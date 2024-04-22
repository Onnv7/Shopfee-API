package com.hcmute.shopfee.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

public class ExcelUtils {

    public static void setDropList(String[] dataList, Sheet sheet, String errorTitle, String errorMessage, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvCategory = dvHelper.createExplicitListConstraint(dataList);
        CellRangeAddressList categoryDropListAddress = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validationCategoryName = dvHelper.createValidation(dvCategory, categoryDropListAddress);

        validationCategoryName.createErrorBox(errorTitle, errorMessage);
        validationCategoryName.setShowErrorBox(true);
        validationCategoryName.setShowPromptBox(true);
        validationCategoryName.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validationCategoryName.setSuppressDropDownArrow(true);
        sheet.addValidationData(validationCategoryName);
    }

    public static void setIntegerConstraint(Sheet sheet, long minValue, long maxValue, String errorTitle, String errorMessage, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvPrice = dvHelper.createNumericConstraint(
                DataValidationConstraint.ValidationType.INTEGER,
                DataValidationConstraint.OperatorType.GREATER_THAN,
                String.valueOf(minValue),
                String.valueOf(maxValue));

        CellRangeAddressList priceCellAddress = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);

        DataValidation validationPrice = dvHelper.createValidation(dvPrice, priceCellAddress);
        validationPrice.createErrorBox(errorTitle, errorMessage);
        validationPrice.setShowErrorBox(true);
        validationPrice.setShowPromptBox(true);
        validationPrice.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validationPrice.setSuppressDropDownArrow(false);
        sheet.addValidationData(validationPrice);
    }
    public static void setFormulas(Workbook workbook,String rangeName, String reference) {
        Name namedRange = workbook.createName();
        namedRange.setNameName(rangeName);
        namedRange.setRefersToFormula(reference);
    }
    public static void setCustomConstraint(Sheet sheet, String customConstraint, String errorTitle, String errorMessage, int firstRow, int lastRow, int firstCol, int lastCol ) {
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();

        CellRangeAddressList addressList2 = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidationConstraint constraint2 = dvHelper.createCustomConstraint(customConstraint);
        DataValidation validation2 = dvHelper.createValidation(constraint2, addressList2);

        validation2.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation2.setShowErrorBox(true);
        validation2.createErrorBox(errorTitle, errorMessage);

        // Apply the validation to the sheet
        sheet.addValidationData(validation2);
    }
}
