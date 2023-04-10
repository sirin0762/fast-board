package project.board.dto.security;

import java.util.Map;
import java.util.UUID;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public abstract String getId();
    public abstract String getUsername();
    public abstract String getEmail();
    public abstract String getImageUrl();

}
