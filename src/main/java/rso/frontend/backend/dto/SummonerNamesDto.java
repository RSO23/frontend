package rso.frontend.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class SummonerNamesDto
{
    private List<String> summonerNames;
    private int team;
    private Long userId;
    private Long gameId;
    private Boolean winner;
}
