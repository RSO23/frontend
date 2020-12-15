package rso.frontend.backend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import rso.frontend.backend.dto.MatchDto;
import rso.frontend.backend.dto.PredictionDto;
import rso.frontend.backend.dto.SummonerNamesDto;

@FeignClient(name = "prediction", url = "http://localhost:8083/")
public interface PredictionServiceFeign
{
    @PostMapping("/predict")
    PredictionDto makePrediction(@RequestBody MatchDto matchDto, @RequestParam Long userId);

    @PostMapping("/predict/summoners")
    PredictionDto makePredictionSummoners(@RequestBody SummonerNamesDto summonerNamesDto);
}
