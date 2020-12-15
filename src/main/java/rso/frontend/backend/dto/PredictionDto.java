package rso.frontend.backend.dto;

import lombok.Data;

@Data
public class PredictionDto
{
    private Long gameId;
    private int team;
    private Boolean predictionResult;
    private double predictionCertainty;
    private long timestamp;
    private Boolean winner;
}
