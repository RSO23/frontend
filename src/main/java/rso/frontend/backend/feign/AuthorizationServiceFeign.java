package rso.frontend.backend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rso.frontend.backend.dto.AuthDto;
import rso.frontend.backend.dto.TokenDto;

@FeignClient(name = "authorization")
public interface AuthorizationServiceFeign
{
    @PostMapping("/login")
    TokenDto login(@RequestBody AuthDto authDto);
}
