package rso.frontend.backend.dto;

import lombok.Data;

@Data
public class ParticipantDto
{
    private String username;
    private String accountId;
    private String profileIconUrl;
    private String champion;
    private String championIconUrl;
    private boolean win;
    private int kills;
    private int deaths;
    private int assists;
    private int largestMultiKill;
    private int teamId;
}
