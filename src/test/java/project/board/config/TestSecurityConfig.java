package project.board.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import project.board.configuration.SecurityConfiguration;
import project.board.domain.UserAccount;
import project.board.repository.UserAccountRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfiguration.class)
public class TestSecurityConfig {

    @MockBean private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetup() {
        given(userAccountRepository.findById(anyString())).willReturn(
            Optional.of(
                UserAccount.of("sirinTest", "pw", "sirin-test@email.com", "sirin", "test memo")
        ));
    }
}
