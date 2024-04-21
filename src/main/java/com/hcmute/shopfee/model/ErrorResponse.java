package com.hcmute.shopfee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {
    @Builder.Default
    private Date timestamp = new Date();
    @Builder.Default
    private boolean success = false;
//    private String message;
    private Integer errorCode;
    private ErrorData error;
    private DevResponse<T> devResponse;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DevResponse<T> {
//        private String stack;
        private String message;
        private T details;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorData {
        private String errorMessage;
        private Integer errorCode;
        private String subErrorMessage;
        private Integer subErrorCode;

        public ErrorData(ShopfeeErrorCode error) {
            this.errorMessage = error.supErrorCode().description();
            this.errorCode = error.supErrorCode().code();
            this.subErrorMessage = error.description();
            this.subErrorCode = error.code();
        }

        public ErrorData(ShopfeeErrorCode.SupErrorCode supError) {
            this.errorMessage = supError.description();
            this.errorCode = supError.code();
            this.subErrorMessage = null;
            this.subErrorCode = null;
        }
    }
}
