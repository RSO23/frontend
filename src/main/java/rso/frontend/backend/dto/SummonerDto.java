package rso.frontend.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SummonerDto
{
    @JsonProperty("id")
    private String id;
    @JsonProperty("accountId")
    private String accountId;
    @JsonProperty("puuid")
    private String puuid;
    @JsonProperty("username")
    private String username;
    @JsonProperty("profileIconId")
    private Integer profileIconId;
    @JsonProperty("summonerLevel")
    private Integer summonerLevel;

}


