package com.example.ecommerce_application.repository;

import com.example.ecommerce_application.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

	List<CartItem> findByCartId(Long cartId);
}
