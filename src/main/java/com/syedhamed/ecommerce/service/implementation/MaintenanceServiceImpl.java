package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.model.CartItem;
import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.repository.CartItemRepository;
import com.syedhamed.ecommerce.repository.UserRepository;
import com.syedhamed.ecommerce.service.contract.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional //for atomicity, though saveAll is usually fine
public class MaintenanceServiceImpl implements MaintenanceService {
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2AM
    public void purgeDeactivatedUsers() {
        List<User> toDelete = userRepository.findAllByDeactivatedTrue();

        for (User user : toDelete) {
            if (user.getDeactivatedAt() != null) {
                long daysSinceDeactivation = Duration.between(user.getDeactivatedAt(), LocalDateTime.now()).toDays();
                if (daysSinceDeactivation > 30) {
                    userRepository.delete(user);
                }
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void releaseExpiredLocksOnCartItems() {
        LocalDateTime cartItemExpiry = LocalDateTime.now().minusMinutes(15);
        List<CartItem> lockedCartItems  =
                cartItemRepository.findByInventoryLockedTrueAndLockedAtBefore(cartItemExpiry);
        for(CartItem cartItem : lockedCartItems){
            cartItem.setInventoryLocked(false);
            cartItem.setLockedAt(null);
        }
        cartItemRepository.saveAll(lockedCartItems);
        log.info("Unlocked [{}] expired cart items", lockedCartItems.size());
    }
}
