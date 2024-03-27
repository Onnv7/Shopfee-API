package com.hcmute.shopfee.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.UpdateEmployeeProfileRequest;
import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeByIdResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeProfileByIdResponse;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = EMPLOYEE_CONTROLLER_TITLE)
@RestController
@RequestMapping(EMPLOYEE_BASE_PATH)
@RequiredArgsConstructor
public class EmployeeController {
    private final IEmployeeService employeeService;

    @Operation(summary = EMPLOYEE_GET_ALL_SUM)
    @GetMapping(path = GET_EMPLOYEE_ALL_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllEmployeeResponse>> getEmployeeList(
            @Parameter(name = "key", description = "Key is employee's username", required = false, example = "nav611")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "status")
            @RequestParam(name = "status", required = false) EmployeeStatus status
    ) {
        GetAllEmployeeResponse resData = employeeService.getEmployeeList(key, page, size, status);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_GET_BY_BRANCH_ID_SUM)
    @GetMapping(path = GET_EMPLOYEE_BY_BRANCH_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllEmployeeResponse>> getEmployeeListByBranchId(
            @PathVariable(BRANCH_ID) String branchId,
            @Parameter(name = "key", description = "Key is employee's username", required = false, example = "nav611")
            @RequestParam(name = "key", required = false) String key,
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size,
            @Parameter(name = "status")
            @RequestParam(name = "status", required = false) EmployeeStatus status
    ) {
        GetAllEmployeeResponse resData = employeeService.getEmployeeListByBranchId(branchId, key, page, size, status);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_GET_PROFILE_BY_ID_SUM)
    @GetMapping(path = GET_EMPLOYEE_PROFILE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetEmployeeProfileByIdResponse>> getEmployeeProfileById(@PathVariable(EMPLOYEE_ID) String employeeId) {
        GetEmployeeProfileByIdResponse resData = employeeService.getEmployeeProfileById(employeeId);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_GET_BY_ID_SUM)
    @GetMapping(path = GET_EMPLOYEE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetEmployeeByIdResponse>> getEmployeeById(@PathVariable(EMPLOYEE_ID) String employeeId) {
        GetEmployeeByIdResponse resData = employeeService.getEmployeeById(employeeId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(resData)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_EMPLOYEE_UPDATE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> updateEmployeeForAdmin(@PathVariable(EMPLOYEE_ID) String id, @RequestBody @Valid UpdateEmployeeRequest body) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        employeeService.updateEmployeeForAdmin(body, id);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
//                .data(resData)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_UPDATE__PROFILE_BY_ID_SUM)
    @PatchMapping(path = PATCH_EMPLOYEE_PROFILE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> updateEmployeeProfile(@PathVariable(EMPLOYEE_ID) String id, @RequestBody @Valid UpdateEmployeeProfileRequest body) {
        employeeService.updateEmployeeProfile(body, id);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
//                .data(resData)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = EMPLOYEE_DELETE_BY_ID_SUM)
    @DeleteMapping(path = DELETE_EMPLOYEE_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> deleteEmployeeById(@PathVariable(EMPLOYEE_ID) String id) {
        employeeService.deleteEmployeeById(id);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.DELETED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }


}
