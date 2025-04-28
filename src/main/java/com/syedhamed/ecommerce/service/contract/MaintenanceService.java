package com.syedhamed.ecommerce.service.contract;

public interface MaintenanceService {
    void purgeDeactivatedUsers();
    void releaseExpiredLocksOnCartItems();
}
