package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;


import java.util.UUID;

public record AuthorityJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("authority")
        Authority authority,
        @JsonProperty("user")
        AuthUserEntity user){

    public static AuthorityJson fromEntity(AuthorityEntity entity) {
        return new AuthorityJson(
                entity.getId(),
                entity.getAuthority(),
                entity.getUser());
    }
}