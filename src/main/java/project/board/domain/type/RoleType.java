package project.board.domain.type;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
    User("ROLE_USER");

    @Getter
    private final String name;
}
