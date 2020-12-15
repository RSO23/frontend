package rso.frontend.ui.views.dashboard;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import rso.frontend.backend.dto.SummonerDto;
import rso.frontend.backend.dto.UserDto;
import rso.frontend.backend.feign.DataCatalogueServiceFeign;
import rso.frontend.backend.feign.UserCatalogueServiceFeign;
import rso.frontend.backend.util.SecurityUtils;
import rso.frontend.ui.MainLayout;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout
{

    private final UserCatalogueServiceFeign userCatalogueServiceFeign;

    private final DataCatalogueServiceFeign dataCatalogueServiceFeign;

    public DashboardView(UserCatalogueServiceFeign userCatalogueServiceFeign, DataCatalogueServiceFeign dataCatalogueServiceFeign)
    {
        this.userCatalogueServiceFeign = userCatalogueServiceFeign;
        this.dataCatalogueServiceFeign = dataCatalogueServiceFeign;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.START);

        HorizontalLayout content = createContent();
        content.setClassName("content");
        add(content);
    }

    private HorizontalLayout createContent() {
        UserDto userDto = userCatalogueServiceFeign.getById(SecurityUtils.getUserId());
        VerticalLayout[] profiles = userDto.getGameAccountDtos().stream()
                .map(gameAccountDto -> {
                    SummonerDto summonerDto = dataCatalogueServiceFeign.getSummonerByUsername(gameAccountDto.getUsername());

                    Image image = new Image(summonerDto.getProfileIconUrl(), "profile_icon");
                    image.setWidth("4em");
                    image.setHeight("4em");

                    HorizontalLayout iconAndUsername = new HorizontalLayout(image, new H2(summonerDto.getUsername()));

                    return new VerticalLayout(
                            iconAndUsername,
                            new H4(String.format("%s %s (%s LP)", summonerDto.getTier(), summonerDto.getDivision(), summonerDto.getLeaguePoints())),
                            createWinsChart(summonerDto.getWins(), summonerDto.getLosses())
                    );


                }).toArray(VerticalLayout[]::new);

        return new HorizontalLayout(profiles);
    }

    private ApexCharts createWinsChart(double win, double lose) {
        ApexCharts chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.pie)
                        .build())
                .withLabels("Win", "Lose")
                .withSeries(win, lose)
                .withColors("#1676F3", "#F53C32")
                .build();

        chart.setWidth("400px");
        chart.setHeight("400px");

        return chart;
    }

}
