package rso.frontend.backend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rso.frontend.backend.dto.GameAccountDto;
import rso.frontend.backend.dto.UserDto;

@FeignClient(name = "user-catalogue")
public interface UserCatalogueServiceFeign
{
    @GetMapping("/user/{id}")
    UserDto getById(@PathVariable("id") Long id);

    @PostMapping("/gameAccount")
    GameAccountDto createOrUpdateGameAccount(@RequestBody GameAccountDto gameAccountDto);

    @PutMapping("/gameAccount")
    GameAccountDto updateGameAccount(@RequestBody GameAccountDto gameAccountDto);

    @DeleteMapping("/gameAccount/{accountId}")
    void deleteGameAccount(@PathVariable String accountId);
}
