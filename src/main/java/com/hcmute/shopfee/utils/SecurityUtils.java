package com.hcmute.shopfee.utils;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

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
            throw new CustomException(ErrorConstant.PRINCIPAL_INVALID);
        }
        if(!((UserPrincipal) principal).getUserId().equals(userId)) {
            throw new CustomException(ErrorConstant.USER_ID_INVALID);
        }
    }
}
