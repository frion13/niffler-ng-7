package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendDao {

    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    void deleteSpend(SpendEntity spend);

    List<SpendEntity> findAll();

    SpendEntity update(SpendEntity spend);

    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    void remove(SpendEntity spend);
}
