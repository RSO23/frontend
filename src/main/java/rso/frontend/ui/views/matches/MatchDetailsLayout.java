package rso.frontend.ui.views.matches;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import rso.frontend.backend.dto.ParticipantDto;

public class MatchDetailsLayout extends VerticalLayout
{
    private final Grid<ParticipantDto> gridTeamOne = new Grid<>(ParticipantDto.class);
    private final Grid<ParticipantDto> gridTeamTwo = new Grid<>(ParticipantDto.class);

    public MatchDetailsLayout()
    {
        addClassName("match-details-layout");
        this.getStyle().set("padding", "0");
        this.getStyle().set("padding-left", "1em");

        configureGrid(gridTeamOne);
        configureGrid(gridTeamTwo);

        add(gridTeamOne, gridTeamTwo);
    }

    public void setParticipants(List<ParticipantDto> participants)
    {
        Map<Boolean, List<ParticipantDto>> sorted = participants.stream().collect(Collectors.partitioningBy(participantDto -> participantDto.getTeamId() == 100));
        gridTeamOne.setItems(sorted.get(true));
        gridTeamTwo.setItems(sorted.get(false));
    }

    public void configureGrid(Grid<ParticipantDto> grid) {
        grid.addClassName("match-details-grid");
        grid.setColumns("username", "champion");

        grid.addColumn(participantDto -> String.format("%s / %s / %s", participantDto.getKills(), participantDto.getDeaths(), participantDto.getDeaths()))
                .setComparator(Comparator.comparing(p -> (p.getKills() + p.getAssists()) / (p.getDeaths() + 0.001)))
                .setHeader("Score")
                .setKey("Score");


        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }
}
