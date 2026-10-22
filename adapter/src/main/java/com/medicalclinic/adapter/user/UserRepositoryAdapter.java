package com.medicalclinic.adapter.user;

import com.medicalclinic.adapter.AdapterControllerCommon;
import internalCommands.PageQuery;
import internalCommands.PageResult;
import lombok.RequiredArgsConstructor;
import models.UserPOJO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import user.UserRepositoryPort;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper mapper;

    @Override
    public PageResult<UserPOJO> findAll(PageQuery pageQuery) {
        Page<UserEntity> page = userJpaRepository.findAll(AdapterControllerCommon.toPageable(pageQuery));
        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public UserPOJO save(UserPOJO userPOJO) {
        UserEntity user = mapper.toEntity(userPOJO);
        UserEntity saved = userJpaRepository.save(user);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserPOJO> findById(Long id) {
        return userJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void delete(Long id) {
        userJpaRepository.deleteById(id);
    }

}
