package rso.frontend.ui.views.matches;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import rso.frontend.backend.dto.GameAccountDto;
import rso.frontend.backend.dto.MatchDto;
import rso.frontend.backend.dto.ParticipantDto;
import rso.frontend.backend.feign.DataCatalogueServiceFeign;
import rso.frontend.backend.feign.UserCatalogueServiceFeign;
import rso.frontend.backend.util.SecurityUtils;
import rso.frontend.ui.MainLayout;

@Route(value = "matches", layout = MainLayout.class)
@PageTitle("Matches")
public class MatchesView extends VerticalLayout
{
    private final Grid<MatchDto> grid = new Grid<>(MatchDto.class);

    private final DataCatalogueServiceFeign dataCatalogue;
    private final UserCatalogueServiceFeign userCatalogue;

    private final TextField filterText = new TextField();
    private final ComboBox<GameAccountDto> gameAccountComboBox = new ComboBox<>();
    private final Button updateButton = new Button("Update");

    private final MatchDetailsLayout matchDetailsLayout;

    public MatchesView(DataCatalogueServiceFeign dataCatalogue, UserCatalogueServiceFeign userCatalogue) {
        this.dataCatalogue = dataCatalogue;
        this.userCatalogue = userCatalogue;

        addClassName("matches-view");
        setSizeFull();

        HorizontalLayout toolbar = getToolbar();
        configureGrid();

        matchDetailsLayout = new MatchDetailsLayout();

        Div content = new Div(grid, matchDetailsLayout);
        content.addClassName("content");
        content.setSizeFull();

        add(toolbar, content);
        updateList();
        closeDetails();
    }

    public void updateList() {

        Page<MatchDto> matchesPage = dataCatalogue.getByAccountId(gameAccountComboBox.getValue().getAccountId(), Pageable.unpaged());
        grid.setItems(matchesPage.getContent());
    }

    private void configureGrid() {
        grid.addClassName("matches-grid");
        grid.setSizeFull();
        grid.setColumns();


        grid.addComponentColumn(matchDto -> matchDto.getParticipants().stream()
                .filter(participantDto -> participantDto.getAccountId().equals(gameAccountComboBox.getValue().getAccountId()))
                .findFirst()
                .map(participantDto -> {
                    Icon icon;
                    if (participantDto.isWin()) {
                        icon = VaadinIcon.CHECK.create();
                        icon.setColor("#0eb549");
                    } else {
                        icon = VaadinIcon.CLOSE.create();
                        icon.setColor("#F5342A");
                    }
                    return icon;
                })
                .orElse(VaadinIcon.QUESTION.create()))
                .setHeader("Result");


        grid.addColumn(matchDto -> matchDto.getParticipants().stream()
                .filter(participantDto -> participantDto.getAccountId().equals(gameAccountComboBox.getValue().getAccountId()))
                .findFirst()
                .map(ParticipantDto::getChampion)
                .orElse("Unknown"))
                .setHeader("Champion");

        grid.addColumn(matchDto -> {
            String lane = matchDto.getLane();
            String role = matchDto.getRole();
            if (role.equals("NONE")) {
                role = "";
            }
            return lane.toLowerCase() + " " + String.join(" ", role.toLowerCase().split("_"));
        }).setHeader("Role");

        grid.addColumn(new LocalDateTimeRenderer<>(matchDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(matchDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setHeader("Timestamp")
                .setKey("timestamp")
                .setComparator(Comparator.comparing(matchDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(matchDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setSortable(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("timestamp"), SortDirection.DESCENDING)));

        grid.asSingleSelect().addValueChangeListener(event -> showMatchDetails(event.getValue()));

    }

    private HorizontalLayout getToolbar()
    {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Set<GameAccountDto> gameAccountDtos = userCatalogue.getById(SecurityUtils.getUserId()).getGameAccountDtos();
        gameAccountComboBox.setItems(gameAccountDtos);
        gameAccountComboBox.setItemLabelGenerator(GameAccountDto::getUsername);
        gameAccountComboBox.setValue(gameAccountDtos.iterator().next());
        gameAccountComboBox.addValueChangeListener(event -> {
            closeDetails();
            updateList();
        });

        updateButton.addClickListener(event -> updateMatches());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, gameAccountComboBox, updateButton);
        toolbar.setWidth("100%");
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void closeDetails()
    {
        matchDetailsLayout.setParticipants(new ArrayList<>());
        matchDetailsLayout.setVisible(false);
        removeClassName("editing");
    }

    private void showMatchDetails(MatchDto matchDto)
    {
        if (matchDto == null) {
            closeDetails();
        }
        else {
            matchDetailsLayout.setParticipants(matchDto.getParticipants());
            matchDetailsLayout.setVisible(true);
            addClassName("editing");
        }
    }

    public void updateMatches() {
        dataCatalogue.updateMatches(gameAccountComboBox.getValue().getAccountId());
        updateList();
    }


}
