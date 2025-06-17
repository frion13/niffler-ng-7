package guru.qa.niffler.test.kafka;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.KafkaService;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@KafkaTest
public class AuthKafkaTest {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataUserRepository userRepository = new UserdataUserRepositorySpringJdbc();

    @Test
    void userShouldBeProducedToKafka() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "123";
        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        UserJson userFromKafka = KafkaService.getUser(username);
        assertEquals(
                username,
                userFromKafka.username()
        );
    }

    @Test
    void shouldSaveUserToDbAfterKafkaMessage() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        UserJson userFromKafka = KafkaService.getUser(username);
        Optional<UserEntity> userFromDb = userRepository.findByUsername(userFromKafka.username());

        assertEquals(username, userFromDb.get().getUsername());
        assertEquals(CurrencyValues.RUB.name(), userFromDb.get().getCurrency().name());
    }
}