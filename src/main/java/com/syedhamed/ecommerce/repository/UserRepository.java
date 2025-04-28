package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByDeletedTrue();

    Page<User> findAllByDeletedFalse(Pageable usersPageable);

    Optional<User> getUserByIdAndDeletedFalse(Long userId);

    List<User> findAllByDeactivatedTrue();
}
