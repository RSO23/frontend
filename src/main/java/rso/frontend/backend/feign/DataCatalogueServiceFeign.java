package rso.frontend.backend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import rso.frontend.backend.dto.MatchDto;
import rso.frontend.backend.dto.SummonerDto;

@FeignClient(name = "data-catalogue")
public interface DataCatalogueServiceFeign
{
    @GetMapping("/matches/update/{accountId}")
    void updateMatches(@PathVariable("accountId") String accountId);

    @PostMapping("/matches/{accountId}")
    Page<MatchDto> getByAccountId(@PathVariable("accountId") String accountId, Pageable pageable);

    @GetMapping("/summoner/{username}")
    SummonerDto getSummonerByUsername(@PathVariable("username") String username);
}
