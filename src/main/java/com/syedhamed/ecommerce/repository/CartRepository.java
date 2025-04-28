package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.model.Cart;
import com.syedhamed.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUser_Id(Long id);

}
