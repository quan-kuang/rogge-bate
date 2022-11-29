package com.loyer.modules.system.service.impl;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.constant.SuffixConst;
import com.loyer.common.core.utils.common.StringUtil;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.system.entity.FieldExplain;
import com.loyer.modules.system.entity.TableExplain;
import com.loyer.modules.system.mapper.postgresql.UtilMapper;
import com.loyer.modules.system.service.UtilService;
import com.loyer.modules.system.utils.FtpClientUtil;
import com.loyer.modules.system.utils.VelocityUtil;
import com.loyer.modules.system.utils.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具ServiceImpl
 *
 * @author kuangq
 * @date 2020-06-25 18:26
 */
@Service
public class UtilServiceImpl implements UtilService {

    @Resource
    private UtilMapper utilMapper;

    /**
     * 上传vue项目
     *
     * @author kuangq
     * @date 2020-07-08 16:30
     */
    @Override
    public ApiResult putVue(String projectName) {
        //本地项目文件路径
        String localPath = String.format("D:/Work/Project/rogge-bate/vue-ui/%s/dist", projectName);
        //校验需要上传的项目是否存在
        File file = new File(localPath);
        if (!file.exists() || !file.isDirectory()) {
            return ApiResult.hintEnum(HintEnum.HINT_1009, localPath);
        }
        //拼接压缩文件路径
        String localFile = localPath + SpecialCharsConst.SLASH + projectName + SuffixConst.ZIP;
        //压缩项目文件
        ZipUtil.compress(localPath, localFile);
        //操作标示
        boolean flag = false;
        //上传至文件服务器
        if (FtpClientUtil.upload(localFile, SystemConst.VUE_PROJECT_PATH)) {
            //删除临时文件
            flag = FileUtil.deleteFile(localPath);
        }
        return ApiResult.success(flag);
    }

    /**
     * 发布Vue项目
     *
     * @author kuangq
     * @date 2020-09-13 22:05
     */
    @Override
    public ApiResult releaseVue(String projectName) {
        String[] projectNames = new String[]{"pc", "app"};
        if (!Arrays.asList(projectNames).contains(projectName)) {
            return ApiResult.hintEnum(HintEnum.HINT_1080, projectNames);
        }
        String path = SystemConst.VUE_PROJECT_PATH + projectName;
        FileUtil.deleteFile(path);
        String zipFilePath = path + SuffixConst.ZIP;
        int count = ZipUtil.decompress(zipFilePath, path);
        return ApiResult.success(count);
    }

    /**
     * 查询数据库模式
     *
     * @author kuangq
     * @date 2020-07-31 9:54
     */
    @Override
    public ApiResult selectSchemaName() {
        return ApiResult.success(utilMapper.selectSchemaName());
    }

    /**
     * 查询表说明信息
     *
     * @author kuangq
     * @date 2020-07-18 23:22
     */
    @Override
    public ApiResult selectTableExplain(TableExplain tableExplain) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(tableExplain)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<TableExplain> pageResult = PageHelperUtil.pagingQuery(utilMapper, methodName, tableExplain);
            formatTableExplainList(pageResult.getList());
            return ApiResult.success(pageResult);
        } else {
            List<TableExplain> tableExplainList = utilMapper.selectTableExplain(tableExplain);
            //遍历查询表的字段信息
            formatTableExplainList(tableExplainList);
            return ApiResult.success(tableExplainList);
        }
    }

    /**
     * 查询并格式化表字段信息
     *
     * @author kuangq
     * @date 2020-07-31 10:24
     */
    private void formatTableExplainList(List<TableExplain> tableExplainList) {
        for (int i = 0; i < tableExplainList.size(); i++) {
            TableExplain tableExplain = tableExplainList.get(i);
            //如果表存在主键获取主键字段名
            if (tableExplain.getIsHasPkey()) {
                tableExplain.setPrimaryKey(utilMapper.selectPrimaryKey(tableExplain.getOid()));
                tableExplain.setPrimaryName(StringUtil.formatCamelCase(tableExplain.getPrimaryKey()));
            }
            //设置类名名（由表名转换）
            tableExplain.setClassName(StringUtil.firstUpper(StringUtil.formatCamelCase(tableExplain.getTableName())));
            //获取表字段详细说明
            List<FieldExplain> fieldExplainList = utilMapper.selectFieldExplain(tableExplain.getOid());
            //格式化表字段详情
            tableExplain.setFieldExplainList(VelocityUtil.formatFields(fieldExplainList));
            //剔除表名后缀
            String tableText = tableExplain.getTableText();
            if (StringUtils.endsWith(tableText, "表")) {
                tableExplain.setTableText(tableText.substring(0, tableText.length() - 1));
            }
            tableExplainList.set(i, tableExplain);
        }
    }

    /**
     * 代码生成
     *
     * @author kuangq
     * @date 2020-07-31 15:18
     */
    @Override
    public void generateCode(HttpServletResponse httpServletResponse, int[] tableOids) {
        if (tableOids.length == 0) {
            throw new BusinessException(HintEnum.HINT_1014);
        }
        TableExplain tableExplain = new TableExplain();
        Map<String, Object> params = new HashMap<String, Object>(1) {{
            put("tableOids", tableOids);
        }};
        tableExplain.setParams(params);
        List<TableExplain> tableExplainList = utilMapper.selectTableExplain(tableExplain);
        formatTableExplainList(tableExplainList);
        byte[] bytes = VelocityUtil.generateCode(tableExplainList);
        String fileName = "code.zip";
        if (tableOids.length == 1) {
            fileName = tableExplainList.get(0).getTableName() + SuffixConst.ZIP;
        }
        FileUtil.download(httpServletResponse, bytes, fileName);
    }
}