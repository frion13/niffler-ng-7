package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();

    @Override
    @Nonnull
    public AuthUserEntity createUser(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @Nonnull
    public AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" " +
                        "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());
            ps.executeUpdate();
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("", e);
        }
    }

    @Override
    @Nonnull
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?")
        ) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
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
    @Nonnull
    public Optional<AuthUserEntity> findByUserName(String username) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement("""
                SELECT * FROM "user" WHERE username = ?
        """)) {
            ps.setString(1, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
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
    @Nonnull
    public List<AuthUserEntity> findAll() {
        List<AuthUserEntity> authUserEntities = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity result = new AuthUserEntity();
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserEntities.add(result);
                }
                return authUserEntities;

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthUserEntity authUser) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE ID = ?"
        )) {
            ps.setObject(1, authUser.getId());
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}