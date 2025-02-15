package edu.hawaii.its.casdemo.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    //
    // You need to replace this with real look ups.
    //

    public RoleHolder fetchRoles(String uhUuid) {
        return fetchRoles(uhUuid, true);
    }

    public RoleHolder fetchRoles(String uhUuid, boolean isAuthenticated) {

        logger.debug("fetchRoles; look up roles for {}", uhUuid);

        RoleHolder roleHolder = new RoleHolder();

        if (!isAuthenticated) {
            // User not logged in.
            roleHolder.add(Role.ANONYMOUS);
            return roleHolder;
        }

        roleHolder.add(Role.USER);
        roleHolder.add(Role.ADMIN);

        return roleHolder;
    }

}
