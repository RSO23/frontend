package rso.frontend.ui.views.dashboard;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import rso.frontend.ui.MainLayout;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout
{
    public DashboardView()
    {
        addClassName("dashboard");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

}
