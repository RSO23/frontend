package rso.frontend.backend.service;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import rso.frontend.backend.dto.AuthDto;
import rso.frontend.backend.feign.AuthorizationServiceFeign;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider
{

    private final AuthorizationServiceFeign authorizationServiceFeign;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        AuthDto authDto = new AuthDto();

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        authDto.setEmail(name);
        authDto.setPassword(password);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        token.setDetails(authorizationServiceFeign.login(authDto).getToken());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}