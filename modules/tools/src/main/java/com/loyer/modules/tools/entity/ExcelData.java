package com.loyer.modules.tools.entity;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.constant.SuffixConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * Excel数据实体类
 *
 * @author kuangq
 * @date 2022-04-01 16:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelData {

    private String name = "one";

    @NotEmpty(message = "columns：列名不能为空")
    private Map<String, String> columns;

    @NotEmpty(message = "dataList：表数据不能为空")
    private List<Map<String, Object>> dataList;

    public String getFileName() {
        String suffix = (!name.contains(SpecialCharsConst.PERIOD) ? SuffixConst.XLSX : "");
        return name + suffix;
    }
}