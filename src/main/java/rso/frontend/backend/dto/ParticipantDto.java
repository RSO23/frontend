package rso.frontend.backend.dto;

import lombok.Data;

@Data
public class ParticipantDto
{
    public String username;
    public String accountId;
    public int profileIcon;
    public String champion;
    public boolean win;
    public int kills;
    public int deaths;
    public int assists;
    public int largestMultiKill;
}
