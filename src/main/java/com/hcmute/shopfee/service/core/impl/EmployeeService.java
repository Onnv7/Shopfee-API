package com.hcmute.shopfee.service.core.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpdateEmployeeProfileRequest;
import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeByIdResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeProfileByIdResponse;
import com.hcmute.shopfee.dto.response.GetSaleStatisticTodayResponse;
import com.hcmute.shopfee.dto.sql.GetEmployeeOrderStatisticDto;
import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.EmployeeFCMTokenEntity;
import com.hcmute.shopfee.enums.EmployeeRole;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.EmployeeFCMTokenRepository;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.core.IEmployeeService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.RegexUtils;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapperService modelMapperService;
    private final BranchRepository branchRepository;
    private final EmployeeFCMTokenRepository employeeFCMTokenRepository;
    private final OrderBillRepository orderBillRepository;

    public Optional<EmployeeEntity> findByUsername(String username) {
        return employeeRepository.findByUsernameAndIsDeletedFalse(username);
    }

    @Override
    public GetAllEmployeeResponse getEmployeeList(String key, int page, int size, EmployeeStatus status) {
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.name() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
        GetAllEmployeeResponse response = new GetAllEmployeeResponse();
        List<GetAllEmployeeResponse.Employee> employeeListResponse = new ArrayList<>();
        Page<EmployeeEntity> employeePage = null;
        if (key == null) {
            employeePage = employeeRepository.getEmployeeList(statusRegex, pageable);
        } else {
            employeePage = employeeRepository.searchEmployee(key, statusRegex, pageable);
        }

        if (employeePage == null) {
            response.setTotalPage(0);
            response.setEmployeeList(new ArrayList<>());
            return response;
        }

        employeePage.getContent().forEach(it -> {
            GetAllEmployeeResponse.Employee employee = GetAllEmployeeResponse.Employee.fromEmployeeEntity(it);
            employeeListResponse.add(employee);
        });
        response.setEmployeeList(employeeListResponse);
        response.setTotalPage(employeePage.getTotalPages());
        return response;
    }

    @Override
    public GetAllEmployeeResponse getEmployeeListByBranchId(String branchId, String key, int page, int size, EmployeeStatus status) {
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.name() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
        GetAllEmployeeResponse response = new GetAllEmployeeResponse();
        List<GetAllEmployeeResponse.Employee> employeeListResponse = new ArrayList<>();
        Page<EmployeeEntity> employeePage = null;
        if (key == null) {
            employeePage = employeeRepository.getEmployeeListByBranchId(branchId, statusRegex, pageable);
        } else {
            employeePage = employeeRepository.searchEmployeeByBranchId(branchId, key, statusRegex, pageable);
        }

        if (employeePage == null) {
            response.setTotalPage(0);
            response.setEmployeeList(new ArrayList<>());
            return response;
        }

        employeePage.getContent().forEach(it -> {
            GetAllEmployeeResponse.Employee employee = GetAllEmployeeResponse.Employee.fromEmployeeEntity(it);
            employeeListResponse.add(employee);
        });
        response.setEmployeeList(employeeListResponse);
        response.setTotalPage(employeePage.getTotalPages());
        return response;
    }

    @Override
    public void updateEmployeeForAdmin(UpdateEmployeeRequest body, String employeeId) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + employeeId));
        BranchEntity branchEntity = branchRepository.findById(body.getBranchId())
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND + body.getBranchId()));
        if (SecurityUtils.isOnlyRole(EmployeeRole.ROLE_MANAGER)) {
            EmployeeEntity manager = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + SecurityUtils.getCurrentUserId()));
            if (!manager.getBranch().getId().equals(employee.getBranch().getId())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.FORBIDDEN, "Manager cannot update an employee account belonging to another branch");
            }
        }



        String oldBranchId = employee.getBranch().getId();
        if (oldBranchId != null && !oldBranchId.equals(body.getBranchId())){
            try {
                List<EmployeeFCMTokenEntity> employeeFCMTokenList = employeeFCMTokenRepository.findByEmployeeId(employeeId);
                List<String> deviceTokenList = employeeFCMTokenList.stream().map(EmployeeFCMTokenEntity::getToken).toList();
                // unsubscribe all device which employee use
                FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(
                        deviceTokenList,
                        oldBranchId
                ).get();
                // subscribe new branch
                FirebaseMessaging.getInstance().subscribeToTopic(
                        deviceTokenList,
                        body.getBranchId()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        modelMapperService.mapNotNull(body, employee);
        employee.setBranch(branchEntity);

        employeeRepository.save(employee);
    }

    @Override
    public void updateEmployeeProfile(UpdateEmployeeProfileRequest data, String id) {
        SecurityUtils.checkUserId(id);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + id));
        modelMapperService.mapNotNull(data, employee);
        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(String id) {
        List<String> roleList = SecurityUtils.getRoleList();

        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + id));

        if (SecurityUtils.isOnlyRole(EmployeeRole.ROLE_MANAGER)) {
            EmployeeEntity manager = employeeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + SecurityUtils.getCurrentUserId()));
            if (!manager.getBranch().getId().equals(employee.getBranch().getId())) {
                throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.FORBIDDEN, "Manager cannot delete an employee account belonging to another branch");
            }
        }
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public GetEmployeeProfileByIdResponse getEmployeeProfileById(String employeeId) {
        SecurityUtils.checkUserId(employeeId);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + employeeId));
        return modelMapperService.mapClass(employee, GetEmployeeProfileByIdResponse.class);
    }

    @Override
    public GetEmployeeByIdResponse getEmployeeById(String employeeId) {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + employeeId));
        return GetEmployeeByIdResponse.fromEmployeeEntity(employee);
    }

    @Override
    public GetSaleStatisticTodayResponse getStatisticToday(String employeeId, Date startDate, Date endDate) {
        SecurityUtils.checkUserId(employeeId);
        if(!DateUtils.isWithin31Days(startDate, endDate)) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The selected time period exceeds 31 days");
        }
        List<GetEmployeeOrderStatisticDto>  dataList = orderBillRepository.getEmployeeOrderStatistic(employeeId, startDate, endDate);
        return GetSaleStatisticTodayResponse.fromEmployeeOrderStatisticRecordList(dataList, startDate, endDate);
    }


}
