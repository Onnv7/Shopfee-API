package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetAllEmployeeResponse {
    private int totalPage;
    private List<Employee> employeeList;


    @Data
    public static class Employee {
        private String id;
//        private String code;
        private String firstName;
        private String lastName;
        private String username;
        private Date birthDate;
        private Gender gender;
        private EmployeeStatus status;

        public static Employee fromEmployeeEntity(EmployeeEntity entity) {
            Employee employee = new Employee();
            employee.setId(entity.getId());
            employee.setFirstName(entity.getFirstName());
            employee.setLastName(entity.getLastName());
            employee.setUsername(entity.getUsername());
            employee.setBirthDate(entity.getBirthDate());
            employee.setGender(entity.getGender());
            employee.setStatus(entity.getStatus());
            return employee;
        }
    }

}
