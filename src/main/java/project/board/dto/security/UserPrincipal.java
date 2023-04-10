package project.board.dto.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import project.board.domain.UserAccount;
import project.board.domain.type.RoleType;
import project.board.dto.UserAccountDto;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record UserPrincipal(
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities,
    String email,
    String nickname,
    String memo,
    Map<String, Object> attribute
) implements UserDetails, OAuth2User {

    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String nickname, String memo) {
        this(username, password, authorities, email, nickname, memo, null);
    }

    public static UserPrincipal of(String username, String password, String email, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.User);

        return new UserPrincipal(
            username,
            password,
            roleTypes.stream()
                .map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet()),
            email,
            nickname,
            memo
        );
    }

    public static UserPrincipal ofSocialLogin(UserAccount userAccount, Map<String, Object> attribute) {
        Set<RoleType> roleTypes = Set.of(RoleType.User);

        return new UserPrincipal(
            userAccount.getUserId(),
            userAccount.getUserPassword(),
            roleTypes.stream()
                .map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet())
            ,
            userAccount.getEmail(),
            userAccount.getNickname(),
            userAccount.getMemo(),
            attribute
        );
    }

    public static UserPrincipal from(UserAccountDto dto) {
        return UserPrincipal.of(
            dto.userId(),
            dto.userPassword(),
            dto.email(),
            dto.nickname(),
            dto.memo()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
            this.username,
            this.password,
            this.email,
            this.nickname,
            this.memo
        );
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attribute;
    }

    // 게시판 프로젝트에서 인증만 존재하며 권한은 없음
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

}
