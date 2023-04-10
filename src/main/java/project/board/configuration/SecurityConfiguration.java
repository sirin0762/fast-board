package project.board.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import project.board.common.auth.LoginSuccessHandler;
import project.board.common.auth.PrincipalOAuth2UserService;
import project.board.dto.UserAccountDto;
import project.board.dto.security.UserPrincipal;
import project.board.repository.UserAccountRepository;

@Configuration
public class SecurityConfiguration {

    private final UserAccountRepository userAccountRepository;

    public SecurityConfiguration(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .mvcMatchers(
                    HttpMethod.GET,
                    "/",
                    "/articles",
                    "/articles/search-hashtag",
                    "/login"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successHandler(new LoginSuccessHandler())
                .and()
            .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                    .userService(new PrincipalOAuth2UserService(userAccountRepository))
                    .and()
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .and()
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return (username -> userAccountRepository.findById(username)
                .map(UserAccountDto::from)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username))
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

