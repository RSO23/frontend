package rso.frontend.ui.views.gameAccounts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import rso.frontend.backend.dto.GameAccountDto;
import rso.frontend.backend.dto.UserDto;
import rso.frontend.backend.feign.UserCatalogueServiceFeign;
import rso.frontend.backend.util.SecurityUtils;
import rso.frontend.ui.MainLayout;

@Route(value = "gameAccounts", layout = MainLayout.class)
@PageTitle("Game accounts")
public class GameAccountsView extends VerticalLayout
{
    private final Grid<GameAccountDto> grid = new Grid<>(GameAccountDto.class);

    private final GameAccountsForm gameAccountsForm;

    private final UserCatalogueServiceFeign userCatalogue;
    private final TextField filterText = new TextField();
    private Button addNewGameAccountButton;

    public GameAccountsView(UserCatalogueServiceFeign userCatalogue)
    {
        this.userCatalogue = userCatalogue;

        addClassName("game-accounts-view");
        setSizeFull();

        configureGrid();

        gameAccountsForm = new GameAccountsForm();
        gameAccountsForm.addListener(GameAccountsForm.SaveEvent.class, this::saveGameAccountDto);
        gameAccountsForm.addListener(GameAccountsForm.DeleteEvent.class, this::deleteGameAccountDto);
        gameAccountsForm.addListener(GameAccountsForm.CloseEvent.class, e -> closeEditor());


        Div content = new Div(grid, gameAccountsForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void deleteGameAccountDto(GameAccountsForm.DeleteEvent event)
    {
        userCatalogue.deleteGameAccount(event.getGameAccountDto().getAccountId());
        updateList();
        closeEditor();
    }

    private void saveGameAccountDto(GameAccountsForm.SaveEvent event)
    {
        userCatalogue.createOrUpdateGameAccount(event.getGameAccountDto());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        addNewGameAccountButton = new Button("Add new game account", click -> createGameAccount());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addNewGameAccountButton);
        addNewGameAccountButton.getStyle().set("margin-left", "auto");
        toolbar.setWidth("100%");
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void createGameAccount()
    {
        grid.asSingleSelect().clear();
        editGameAccount(new GameAccountDto());
    }

    private void configureGrid()
    {
        grid.addClassName("game-account-grid");
        grid.setSizeFull();
        grid.setColumns("username", "accountId");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editGameAccount(event.getValue()));
    }

    private void updateList() {

        UserDto userDto = userCatalogue.getById(SecurityUtils.getUserId());
        grid.setItems(userDto.getGameAccountDtos());

        int size = userDto.getGameAccountDtos().size();
        addNewGameAccountButton.setEnabled(size <= 2);
    }

    private void closeEditor() {
        gameAccountsForm.setGameAccount(null);
        gameAccountsForm.setVisible(false);
        removeClassName("editing");
    }

    private void editGameAccount(GameAccountDto gameAccountDto)
    {
        if (gameAccountDto == null) {
            closeEditor();
        }
        else {
            gameAccountsForm.setGameAccount(gameAccountDto);
            gameAccountsForm.setVisible(true);
            addClassName("editing");
        }
    }
}
