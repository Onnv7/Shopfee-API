package com.hcmute.shopfee.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class CreateProductFromFileErrorResponse {
    private int rowIndex;
    private List<CellDataError> errorList;

    @Data
    public static class CellDataError {
        private int colIndex;
        private Integer errorCode;

        public CellDataError() {
        }

        public CellDataError(int colIndex, int errorCode) {
            this.colIndex = colIndex;
            this.errorCode = errorCode;
        }
    }
}
