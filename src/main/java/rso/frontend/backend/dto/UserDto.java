package rso.frontend.backend.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserDto
{
    private String name;

    private String username;

    private String password;

    private String email;

    private Set<GameAccountDto> gameAccountDtos;

}
