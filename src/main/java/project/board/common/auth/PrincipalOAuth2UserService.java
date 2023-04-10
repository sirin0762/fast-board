package project.board.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project.board.domain.UserAccount;
import project.board.domain.type.ProviderType;
import project.board.dto.security.OAuth2UserInfo;
import project.board.dto.security.OAuth2UserInfoFactory;
import project.board.dto.security.UserPrincipal;
import project.board.repository.UserAccountRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());
        Optional<UserAccount> userAccount = userAccountRepository.findById(userInfo.getId());

        if(userAccount.isEmpty()) {
            userAccount = Optional.of(
                userAccountRepository.save(UserAccount.of(userInfo.getId(), userInfo.getPassword(), userInfo.getEmail(), userInfo.getUsername(), ""))
            );
        }
        return UserPrincipal.ofSocialLogin(userAccount.get(), oAuth2User.getAttributes());
    }

}
