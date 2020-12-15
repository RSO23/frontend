package rso.frontend.ui.views.matches;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import rso.frontend.backend.dto.MatchDto;
import rso.frontend.backend.dto.ParticipantDto;
import rso.frontend.backend.dto.PredictionDto;
import rso.frontend.backend.dto.SummonerNamesDto;
import rso.frontend.backend.feign.PredictionCatalogueServiceFeign;
import rso.frontend.backend.feign.PredictionServiceFeign;
import rso.frontend.backend.util.SecurityUtils;

public class MatchDetailsLayout extends VerticalLayout
{
    private final Grid<ParticipantDto> gridTeamBlue = new Grid<>(ParticipantDto.class);
    private final Grid<ParticipantDto> gridTeamRed = new Grid<>(ParticipantDto.class);
    private final Grid<PredictionDto> gridPrediction = new Grid<>(PredictionDto.class);

    private final HorizontalLayout blueHorizontal = new HorizontalLayout();
    private final HorizontalLayout redHorizontal = new HorizontalLayout();
    private final HorizontalLayout predictionHorizontal = new HorizontalLayout();

    private H5 blueTeam = new H5("Blue team");
    private H5 redTeam = new H5("Red team");
    private H5 prediction = new H5("Prediction");

    private Div warning;
    private Button predictButton = new Button("Predict");

    private Map<Boolean, List<ParticipantDto>> teams;

    private PredictionCatalogueServiceFeign predictionCatalogueServiceFeign;
    private MatchDto matchDto;

    public MatchDetailsLayout(PredictionServiceFeign predictionServiceFeign, PredictionCatalogueServiceFeign predictionCatalogueServiceFeign)
    {
        this.predictionCatalogueServiceFeign = predictionCatalogueServiceFeign;
        addClassName("match-details-layout");
        this.getStyle().set("padding", "0");
        this.getStyle().set("padding-left", "1em");

        configureGrid(gridTeamBlue, blueHorizontal, blueTeam, "blue");
        configureGrid(gridTeamRed, redHorizontal, redTeam, "red");
        configureGridPrediction(gridPrediction, predictionHorizontal, this.prediction);
        configurePredictButton(predictionServiceFeign);

        add(blueHorizontal, redHorizontal, predictionHorizontal, predictButton);
    }

    private void configurePredictButton(PredictionServiceFeign predictionServiceFeign)
    {
        predictButton.setWidthFull();
        predictButton.addClickListener(event -> {

            List<PredictionDto> predictionDtos = teams.values().stream()
                    .map(participantDtos -> {
                        ParticipantDto participantDto = participantDtos.get(0);
                        int teamId = participantDto.getTeamId();
                        boolean win = participantDto.isWin();

                        List<String> usernames = participantDtos.stream().map(ParticipantDto::getUsername).collect(Collectors.toList());
                        SummonerNamesDto summonerNamesDto = new SummonerNamesDto();
                        summonerNamesDto.setSummonerNames(usernames);
                        summonerNamesDto.setTeam(teamId);
                        summonerNamesDto.setUserId(SecurityUtils.getUserId());
                        summonerNamesDto.setGameId(matchDto.getGameId());
                        summonerNamesDto.setWinner(win);
                        return predictionServiceFeign.makePredictionSummoners(summonerNamesDto);

                    })
                    .collect(Collectors.toList());

            setPredictionDtos(predictionDtos);
        });
    }

    public void setMatchDto(MatchDto matchDto)
    {
        this.matchDto = matchDto;
        teams = this.matchDto.getParticipants().stream().collect(Collectors.partitioningBy(participantDto -> participantDto.getTeamId() == 100));
        gridTeamBlue.setItems(teams.get(true));
        gridTeamRed.setItems(teams.get(false));

        boolean blueWin = teams.get(true).stream().findFirst().map(ParticipantDto::isWin).orElse(false);

        if (blueWin) {
            blueTeam.setText("Blue team - winner");
            redTeam.setText("Red team - loser");
        } else {
            blueTeam.setText("Blue team - loser");
            redTeam.setText("Red team - winner");
        }

        List<PredictionDto> predictionDtos = predictionCatalogueServiceFeign.getByGameIds(List.of(matchDto.getGameId()));
        setPredictionDtos(predictionDtos);

    }

    public void setPredictionDtos(List<PredictionDto> predictionDto)
    {
        if (!predictionDto.isEmpty())
        {
            warning.addClassName("hidden");
        }
        else
        {
            warning.removeClassName("hidden");
        }

        predictButton.setEnabled(predictionDto.isEmpty());
        gridPrediction.setItems(predictionDto);
    }

    private void configureGrid(Grid<ParticipantDto> grid, HorizontalLayout horizontalLayout, H5 team, String color) {
        grid.addClassName("match-details-grid");
        grid.setColumns("username", "champion");

        grid.addColumn(participantDto -> String.format("%s / %s / %s", participantDto.getKills(), participantDto.getDeaths(), participantDto.getAssists()))
                .setComparator(Comparator.comparing(p -> ((p.getKills() + p.getAssists()) / (p.getDeaths() == 0 ? 1 : p.getDeaths()))))
                .setHeader("Score")
                .setKey("Score");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setHeightByRows(true);

        team.setClassName("match-details-grid-toolbar-" + color);

        horizontalLayout.add(grid, team);
        horizontalLayout.setWidthFull();

    }

    private void configureGridPrediction(Grid<PredictionDto> grid, HorizontalLayout horizontalLayout, H5 team) {
        horizontalLayout.addClassName("match-details-grid-prediction");

        grid.addClassName("grid");

        grid.setColumns();

        grid.addColumn(predictionDto -> predictionDto.getTeam() == 100 ? "Blue" : "Red")
                .setHeader("Team")
                .setKey("team")
                .setComparator(Comparator.comparing(PredictionDto::getTeam))
                .setSortable(true);

        grid.addColumn("predictionResult")
                .setHeader("Result");

        grid.addColumn("predictionCertainty")
                .setHeader("Certainty");

        grid.addColumn(new LocalDateTimeRenderer<>(predictionDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(predictionDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setHeader("Timestamp")
                .setKey("timestamp")
                .setComparator(Comparator.comparing(predictionDto -> LocalDateTime.ofInstant(Instant.ofEpochMilli(predictionDto.getTimestamp()), TimeZone.getDefault().toZoneId())))
                .setSortable(true);

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("team"), SortDirection.ASCENDING)));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        Div gridRoot = new Div();
        gridRoot.addClassName("grid-root");

        warning = new Div(new Text("Click predict for predictions."));
        warning.addClassName("warning");

        gridRoot.add(grid, warning);

        team.setClassName("match-details-grid-toolbar-prediction");

        horizontalLayout.add(gridRoot, team);
        horizontalLayout.setWidthFull();
    }
}
