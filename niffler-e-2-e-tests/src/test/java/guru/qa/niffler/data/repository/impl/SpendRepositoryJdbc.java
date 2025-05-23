package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

  private static final Config CFG = Config.getInstance();

  private final String url = CFG.spendJdbcUrl();
  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  @NotNull
  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null && categoryDao.findCategoryById(categoryId).isEmpty()) {
      spend.setCategory(
              categoryDao.create(spend.getCategory())
      );
    }
    return spendDao.create(spend);
  }

  @NotNull
  @Override
  public SpendEntity update(SpendEntity spend) {
    spendDao.update(spend);
    categoryDao.update(spend.getCategory());
    return spend;
  }

  @NotNull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

  @NotNull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findCategoryById(id);
  }

  @NotNull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "SELECT * FROM category WHERE username = ? and name = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, name);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
                  CategoryEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return spendDao.findSpendById(id);
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findSpendByUsernameAndSpendDescription(String username, String description) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "SELECT * FROM spend WHERE username = ? and description = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
                  SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}