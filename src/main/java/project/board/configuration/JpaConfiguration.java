package project.board.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import project.board.dto.security.UserPrincipal;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || !authentication.isAuthenticated()) {
            return null;
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return () -> Optional.of("this");
//        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
//            .map(SecurityContext::getAuthentication)
//            .filter(Authentication::isAuthenticated)
//            .map(Authentication::getPrincipal)
//            .map(UserPrincipal.class::cast)
//            .map(UserPrincipal::getUsername);
   }
}
