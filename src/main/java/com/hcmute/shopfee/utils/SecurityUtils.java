package com.hcmute.shopfee.utils;

import com.hcmute.shopfee.enums.EmployeeRole;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityUtils {


    public static String getCurrentUserId() {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            try {
                return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    public static void checkUserId(String userId) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal.getClass() != UserPrincipal.class) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED);
        }
        if(!((UserPrincipal) principal).getUserId().equals(userId)) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.FORBIDDEN);
        }
    }

    public static List<String> getRoleList() {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal.getClass() != UserPrincipal.class) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED);
        }
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(Object::toString).toList();
    }

    public static boolean isOnlyRole(EmployeeRole employeeRoleName) {
        List<String> roles = getRoleList();
        return roles.size() == 1 && roles.get(0).equals(employeeRoleName.name());
    }
}
