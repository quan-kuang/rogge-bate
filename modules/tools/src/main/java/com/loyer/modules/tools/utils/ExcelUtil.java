package com.loyer.modules.tools.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.constant.SuffixConst;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.OracleUtil;
import com.loyer.modules.tools.entity.ExcelData;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 *
 * @author kuangq
 * @date 2019-09-23 22:45
 */
public class ExcelUtil {

    //单元格字段长度的最大限制，便于输出限值缩小百倍
    private static final int MAX_LENGTH = 32767 / 100;

    /**
     * 获取Excel字节数组
     *
     * @author kuangq
     * @date 2022-04-01 19:10
     */
    @SneakyThrows
    public static byte[] getExcelBytes(ExcelData excelData) {
        //创建字节输出流，保证最后关闭
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); Workbook workbook = createExcel(excelData.getFileName(), excelData.getColumns(), excelData.getDataList())) {
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * 根据数据源创建Excel
     *
     * @author kuangq
     * @date 2019-12-15 19:53
     */
    private static Workbook createExcel(String name, Map<String, String> columns, List<Map<String, Object>> dataList) {
        //拆分文件名称和后缀
        int beginIndex = name.indexOf(SpecialCharsConst.PERIOD);
        String suffix = name.substring(beginIndex);
        String sheetName = name.substring(0, beginIndex);
        //创建Excel
        Workbook workbook = SuffixConst.XLS.equals(suffix) ? new HSSFWorkbook() : new XSSFWorkbook();
        //创建sheet
        Sheet sheet = workbook.createSheet(sheetName);
        //设置表头
        Row row = sheet.createRow(0);
        //获取表头样式
        CellStyle headerStyle = getHeaderStyle(workbook);
        //遍历字段名
        int index = 0;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            //设置列宽
            sheet.autoSizeColumn(index);
            sheet.setColumnWidth(index, sheet.getColumnWidth(index) * 18 / 10);
            //设置字段值
            setCellValue(row, index++, entry.getValue(), headerStyle);
        }
        //遍历表数据
        for (int i = 0; i < dataList.size(); i++) {
            //创建行，表头占据一行，表数据下标从1开始
            row = sheet.createRow(i + 1);
            //根据表字段顺序填充单元格
            index = 0;
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                //根据字段的key从行数据map对象中获取value
                Object value = dataList.get(i).get(entry.getKey());
                //如果字段名为时间，将时间戳转换为字符串
                if (entry.getValue().endsWith("时间") && Long.class.equals(value.getClass())) {
                    value = DateUtil.getTimestamp(Long.parseLong(value.toString()), DatePattern.YMD_HMS_1);
                }
                //设置字段值
                setCellValue(row, index++, value, null);
            }
        }
        return workbook;
    }

    /**
     * 获取表头样式
     *
     * @author kuangq
     * @date 2021-03-01 13:56
     */
    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        //设置水平对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置填充模式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置单元格背景色
        cellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        Font font = workbook.createFont();
        //设置字体
        font.setFontName("Arial");
        //字体加粗
        font.setBold(true);
        //设置行高
        font.setFontHeightInPoints((short) 10);
        //设置字体颜色
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 填充单元格
     *
     * @author kuangq
     * @date 2021-02-25 22:48
     */
    private static void setCellValue(Row row, int index, Object value, CellStyle cellStyle) {
        //创建单元格
        Cell cell = row.createCell(index);
        //创建值
        String cellValue = OracleUtil.nvl(value, "").toString();
        //判断字段值的最大长度限制
        if (cellValue.length() >= MAX_LENGTH) {
            cellValue = "大字段省略";
        }
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);
    }
}