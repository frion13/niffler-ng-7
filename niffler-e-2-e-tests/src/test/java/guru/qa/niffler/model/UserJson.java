package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData) {

    public static UserJson fromEntity(UserEntity entity, FriendshipStatus friendshipStatus) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getCurrency(),
                entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0 ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                friendshipStatus,
                null
        );
    }

    public UserJson(@Nonnull String username) {
        this(username, null);
    }

    public UserJson(@Nonnull String username, @Nullable TestData testData) {
        this(null, username, null, null, null, null, null, null, null, testData);
    }

    public @Nonnull UserJson addTestData(@Nonnull TestData testData) {
        return new UserJson(
                id, username, firstname, surname, fullname, currency, photo, photoSmall, friendshipStatus, testData
        );
    }
}