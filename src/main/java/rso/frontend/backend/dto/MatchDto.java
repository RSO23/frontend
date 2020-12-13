package rso.frontend.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class MatchDto
{
    private long gameId;

    private String platformId;

    private Integer champion;

    private Integer queue;

    private Integer season;

    private Long timestamp;

    private String role;

    private String lane;

    private Long duration;

    private List<ParticipantDto> participants;
}

