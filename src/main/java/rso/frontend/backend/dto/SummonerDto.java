package rso.frontend.backend.dto;

import lombok.Data;

@Data
public class SummonerDto
{
    private String id;
    private String accountId;
    private String puuid;
    private String username;
    private String profileIconUrl;
    private Integer summonerLevel;
    private String division;
    private String tier;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;

}


