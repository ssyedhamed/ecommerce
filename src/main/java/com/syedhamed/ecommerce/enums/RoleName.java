package com.syedhamed.ecommerce.enums;

import org.springframework.beans.factory.annotation.Value;

public enum RoleName {
    @Value("${app.role.admin}")
    ROLE_ADMIN, // Can manage users, orders, products, etc. Often used for internal staff or store owners.
    ROLE_CUSTOMER, //Regular customer (browses products, places orders, updates profile).
    ROLE_SELLER, //Sellers can manage their own products, view orders, earnings, etc. Kept separate from admins.
    ROLE_MODERATOR, //For community-based features (review/report moderation).
    ROLE_SHIPPER, //If integrating with a logistics/warehouse partner.
    ROLE_SUPPORT //To allow support staff to respond to tickets or messages.
}
