package rso.frontend.backend.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import rso.frontend.backend.dto.AuthDto;
import rso.frontend.backend.feign.AuthorizationServiceFeign;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider
{

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
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
        try {
            token.setDetails(authorizationServiceFeign.login(authDto).getToken());
        } catch (FeignException e) {
            log.warn(e.getLocalizedMessage());
            throw new AuthenticationCredentialsNotFoundException(e.getLocalizedMessage());
        }

        log.info(name + " successfully logged in.");
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}