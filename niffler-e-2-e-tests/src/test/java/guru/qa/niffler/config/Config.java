package guru.qa.niffler.config;

import javax.annotation.Nonnull;
import java.util.List;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
            ? DockerConfig.INSTANCE
            : LocalConfig.INSTANCE;
  }
  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String spendUrl();

  @Nonnull
  String spendJdbcUrl();

  @Nonnull
  String currencyJdbcUrl();

  @Nonnull
  String userdataGrpcAddress();

  default int userdataGrpcPort(){
    return 8095;
  }

  @Nonnull
  String currencyGrpcAddress();

  @Nonnull
  String kafkaAddress();

  default int currencyGrpcPort(){
    return 8092;
  }

  default String ghUrl(){
    return "https://api.github.com/";
  };

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String allureDockerServiceUrl();

    default List<String> kafkaTopics(){
      return List.of("users");
    }
}
