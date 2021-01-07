package rso.frontend.backend.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rso.frontend.backend.dto.PredictionDto;

@FeignClient(name = "prediction-catalogue")
public interface PredictionCatalogueServiceFeign
{
    @GetMapping("/predictions/user/{userId}")
    List<PredictionDto> getPredictionsForUser(@PathVariable("userId") Long userId);

    @PostMapping("/predictions/gameIds")
    List<PredictionDto> getByGameIds(@RequestBody List<Long> gameIds);
}
