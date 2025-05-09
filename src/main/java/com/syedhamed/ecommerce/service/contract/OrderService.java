package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.payload.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(User user);
}
