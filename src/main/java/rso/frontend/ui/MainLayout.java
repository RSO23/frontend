package rso.frontend.ui;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import rso.frontend.ui.views.dashboard.DashboardView;
import rso.frontend.ui.views.gameAccounts.GameAccountsView;
import rso.frontend.ui.views.matches.MatchesView;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout
{
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader()
    {
        H1 logo = new H1("League of Legends Predictor");
        logo.addClassName("logo");

        Button logout = new Button("Logout", event -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().navigate("login");
        });
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        logout.getStyle().set("margin-left", "auto");

        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer()
    {
        RouterLink dashboard = new RouterLink("Dashboard", DashboardView.class);
        dashboard.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink gameAccounts = new RouterLink("Game accounts", GameAccountsView.class);
        gameAccounts.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink matches = new RouterLink("Matches", MatchesView.class);
        gameAccounts.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                dashboard,
                gameAccounts,
                matches
        ));

    }
}
