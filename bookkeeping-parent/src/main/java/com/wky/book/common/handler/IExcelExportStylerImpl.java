package com.wky.book.common.handler;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.params.ExcelForEachParams;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

public class IExcelExportStylerImpl implements IExcelExportStyler {
    @Override
    public CellStyle getHeaderStyle(short headerColor) {
        return null;
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        return null;
    }

    @Override
    public CellStyle getStyles(Cell cell, int dataRow, ExcelExportEntity entity, Object obj, Object data) {
        return null;
    }

    @Override
    public CellStyle getTemplateStyles(boolean isSingle, ExcelForEachParams excelForEachParams) {
        return null;
    }

    @Override
    public CellStyle getStyles(boolean parity, ExcelExportEntity entity) {
        return null;
    }
}
