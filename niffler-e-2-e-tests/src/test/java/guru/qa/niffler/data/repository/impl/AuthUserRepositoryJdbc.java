package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        authUserDao.createUser(user);
        authAuthorityDao.createAuthority(user.getAuthorities().toArray(AuthorityEntity[]::new));
        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        authUserDao.update(user);
        authAuthorityDao.update(user.getAuthorities().toArray(new AuthorityEntity[0]));
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return findById(id);
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return authUserDao.findAll();
    }

    @Override
    public Optional<AuthUserEntity> findByUserName(String username) {
        return authUserDao.findByUserName(username);
    }

    @Override
    public void remove(AuthUserEntity user) {
        authUserDao.remove(user);
        authAuthorityDao.remove(user.getAuthorities().toArray(new AuthorityEntity[0]));
    }
}