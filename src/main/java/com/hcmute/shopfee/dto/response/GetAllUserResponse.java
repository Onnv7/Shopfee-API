package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.UserEntity;
import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.enums.UserStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetAllUserResponse {
    private Integer totalPage;
    private List<UserInfo> userList;
    @Data
    public static class UserInfo {
        private String id;
        private String firstName;
        private String lastName;
        private Gender gender;
        private Date birthDate;
        private String email;
        private String phoneNumber;
        private UserStatus status;
//    private Date updatedAt;

        public static UserInfo fromUserEntity(UserEntity entity) {
            UserInfo response = new UserInfo();
            response.setId(entity.getId());
            response.setFirstName(entity.getFirstName());
            response.setLastName(entity.getLastName());
            response.setGender(entity.getGender());
            response.setBirthDate(entity.getBirthDate());
            response.setEmail(entity.getEmail());
            response.setPhoneNumber(entity.getPhoneNumber());
            response.setStatus(entity.getStatus());
            return response;
        }
    }


}
