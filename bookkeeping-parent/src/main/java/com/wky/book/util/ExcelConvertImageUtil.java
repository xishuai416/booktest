package com.wky.book.util;

/**
 * excel转图片
 */

import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

public class ExcelConvertImageUtil {

    /**
     * excel转图片
     *
     * @param wb          要转成图片的excel对象
     * @param urlFileName 不带后缀的文件名
     * @return 返回生成的图片对象
     * @throws IOException
     */
    public static File wbToImg(XSSFWorkbook wb, String urlFileName) throws IOException {

        changeCellWidth(wb);

        // 创建文本实体
        File file = new File(urlFileName);
        // 把excel保存到硬盘
        wb.write(new FileOutputStream(file));

        Workbook workbook = new Workbook();
        //加载Excel文档
        workbook.loadFromFile(file.getAbsolutePath());

        //获取第一张工作表
        Worksheet sheet2 = workbook.getWorksheets().get(0);

        //保存到图片
        BufferedImage bufferedImage = sheet2.toImage(1, 1, sheet2.getLastRow(), sheet2.getLastColumn());

        File fileImg = new File(urlFileName + ".png");
        //写出图片到文件
        ImageIO.write(bufferedImage, "PNG", fileImg);

        file.delete();

        return fileImg;
    }

    /**
     * 改变单元格的宽度使得内容全部显示出来
     *
     * @param wb
     */
    private static void changeCellWidth(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheetAt(0);
        // 固定首行，下拉时实现首行固定不动
        sheet.createFreezePane(0, 1, 0, 1);

        for (int columnIndex = 0; columnIndex < 21; columnIndex++) {
            int columnWidth = sheet.getColumnWidth(columnIndex) / 256;
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowIndex) == null) {
                    currentRow = sheet.createRow(rowIndex);
                } else {
                    currentRow = sheet.getRow(rowIndex);
                }
                if (currentRow.getCell(columnIndex) != null) {
                    XSSFCell currentCell = currentRow.getCell(columnIndex);

                    int length = 0;
                    try {
                        length = getCellValue(currentCell).toString().getBytes().length;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (columnWidth < length) {
                        columnWidth = length;
                    }

                }
            }

            sheet.setColumnWidth(columnIndex, (columnWidth) * 256);
        }
    }

    private static Object getCellValue(XSSFCell cell) {
        Object o = null;

        if (cell.getCellType().equals(CellType.BLANK)) {
            o = "";
        } else if (cell.getCellType().equals(CellType.BOOLEAN)) {
            o = cell.getBooleanCellValue();
        } else if (cell.getCellType().equals(CellType.ERROR)) {
            o = "Bad value!";
        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
            o = getValueOfNumericCell(cell);
        } else if (cell.getCellType().equals(CellType.FORMULA)) {
            try {
                o = getValueOfNumericCell(cell);
            } catch (IllegalStateException e) {
                    o = cell.getRichStringCellValue().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            o = cell.getRichStringCellValue().getString();
        }

        return o;
    }

    // 获取数字类型的cell值
    private static Object getValueOfNumericCell(XSSFCell cell) {
        Boolean isDate = DateUtil.isCellDateFormatted(cell);
        Double d = cell.getNumericCellValue();
        Object o = null;
        if (isDate) {
            o = DateFormat.getDateTimeInstance()
                    .format(cell.getDateCellValue());
        } else {
            o = getRealStringValueOfDouble(d);
        }
        return o;
    }

    // 处理科学计数法与普通计数法的字符串显示，尽最大努力保持精度
    private static String getRealStringValueOfDouble(Double d) {
        String doubleStr = d.toString();
        boolean b = doubleStr.contains("E");
        int indexOfPoint = doubleStr.indexOf('.');
        if (b) {
            int indexOfE = doubleStr.indexOf('E');
            // 小数部分
            BigInteger xs = new BigInteger(doubleStr.substring(indexOfPoint
                    + BigInteger.ONE.intValue(), indexOfE));
            // 指数
            int pow = Integer.valueOf(doubleStr.substring(indexOfE
                    + BigInteger.ONE.intValue()));
            int xsLen = xs.toByteArray().length;
            int scale = xsLen - pow > 0 ? xsLen - pow : 0;
            doubleStr = String.format("%." + scale + "f", d);
        } else {
            Pattern compile = Pattern.compile(".0$");
            java.util.regex.Matcher m = compile.matcher(doubleStr);
            if (m.find()) {
                doubleStr = doubleStr.replace(".0", "");
            }
        }
        return doubleStr;
    }

    public static void main(String[] args) {

//        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("test.xlsx")));

//        wbToImg(wb, "D:\\Desktop\\HOME\\reportToImg\\test2");
    }
}
