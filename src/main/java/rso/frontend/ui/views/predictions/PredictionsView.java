package rso.frontend.ui.views.predictions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import com.vaadin.flow.component.button.Button;
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

import rso.frontend.backend.dto.PredictionDto;
import rso.frontend.backend.dto.SummonerNamesDto;
import rso.frontend.backend.feign.PredictionCatalogueServiceFeign;
import rso.frontend.backend.feign.PredictionServiceFeign;
import rso.frontend.backend.util.SecurityUtils;
import rso.frontend.ui.MainLayout;

@Route(value = "predictions", layout = MainLayout.class)
@PageTitle("Predictions")
public class PredictionsView extends VerticalLayout
{
    private final Grid<PredictionDto> grid = new Grid<>(PredictionDto.class);

    private final PredictionServiceFeign predictionServiceFeign;

    private final PredictionCatalogueServiceFeign predictionCatalogueServiceFeign;

    private final PredictionForm predictionForm;

    private final TextField filterText = new TextField();

    private Button addNewPredictionButton;


    public PredictionsView(PredictionServiceFeign predictionServiceFeign, PredictionCatalogueServiceFeign predictionCatalogueServiceFeign)
    {
        this.predictionServiceFeign = predictionServiceFeign;
        this.predictionCatalogueServiceFeign = predictionCatalogueServiceFeign;

        addClassName("predictions-view");
        setSizeFull();

        configureGrid();

        predictionForm = new PredictionForm();
        predictionForm.addListener(PredictionForm.SaveEvent.class, this::saveSummonerNamesDto);
        predictionForm.addListener(PredictionForm.CloseEvent.class, event -> closeEditor());


        Div content = new Div(grid, predictionForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void saveSummonerNamesDto(PredictionForm.SaveEvent event)
    {
        predictionServiceFeign.makePredictionSummoners(event.getSummonerNamesDto());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        addNewPredictionButton = new Button("New prediction", event -> predictSummonerNamesDto());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addNewPredictionButton);
        addNewPredictionButton.getStyle().set("margin-left", "auto");
        toolbar.setWidth("100%");
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void predictSummonerNamesDto()
    {
        SummonerNamesDto summonerNamesDto = new SummonerNamesDto();
        predictionForm.setSummonerNamesDto(summonerNamesDto);
        predictionForm.setVisible(true);
        addClassName("editing");
    }

    private void configureGrid()
    {
        grid.addClassName("predictions-grid");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn(predictionDto -> predictionDto.getTeam() == 100 ? "Blue" : "Red")
                .setHeader("Team")
                .setKey("team")
                .setComparator(Comparator.comparing(PredictionDto::getTeam))
                .setSortable(true);

        grid.addColumn(predictionDto -> {
            Boolean predictionResult = predictionDto.getPredictionResult();
            if (predictionResult != null) {
                return predictionResult ? "win" : "loss";
            }
            return "unknown";
        }).setHeader("Result").setKey("predictionResult");

        grid.addColumn("predictionCertainty")
                .setHeader("Certainty");

        grid.addColumn(new LocalDateTimeRenderer<>(predictionDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(predictionDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setHeader("Timestamp")
                .setKey("timestamp")
                .setComparator(Comparator.comparing(predictionDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(predictionDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setSortable(true);

        grid.addColumn(predictionDto -> {
            Boolean winner = predictionDto.getWinner();
            if (winner != null) {
                return winner ? "win" : "loss";
            }
            return "unknown";
        }).setHeader("Actual result").setKey("winner");

        grid.addComponentColumn(predictionDto -> {
            Icon icon;
            if (predictionDto.getWinner() == null) {
                icon = VaadinIcon.QUESTION.create();
            }
            else if (predictionDto.getWinner().equals(predictionDto.getPredictionResult())) {
                icon = VaadinIcon.CHECK.create();
                icon.setColor("#0eb549");
            } else {
                icon = VaadinIcon.CLOSE.create();
                icon.setColor("#F5342A");
            }
            return icon;
        }).setHeader("Correct");

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("timestamp"), SortDirection.DESCENDING)));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList()
    {
        List<PredictionDto> predictionsForUser = predictionCatalogueServiceFeign.getPredictionsForUser(SecurityUtils.getUserId());
        grid.setItems(predictionsForUser);
    }

    private void closeEditor()
    {
        predictionForm.setSummonerNamesDto(null);
        predictionForm.setVisible(false);
        removeClassName("editing");
    }


}
