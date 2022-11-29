package com.loyer.modules.system.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.StringUtil;
import com.loyer.modules.system.entity.FieldExplain;
import com.loyer.modules.system.entity.TableExplain;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成工具类
 *
 * @author kuangq
 * @date 2020-07-20 9:38
 */
public class VelocityUtil {

    public static final String VM_JAVA_CONTROLLER = "vm/java/Controller.java.vm";
    public static final String VM_JAVA_SERVICE = "vm/java/Service.java.vm";
    public static final String VM_JAVA_SERVICE_IMPL = "vm/java/ServiceImpl.java.vm";
    public static final String VM_JAVA_MAPPER = "vm/java/Mapper.java.vm";
    public static final String VM_JAVA_ENTITY = "vm/java/Entity.java.vm";
    public static final String VM_XML_MAPPER = "vm/xml/Mapper.xml.vm";
    public static final String VM_VUE_MAIN = "vm/vue/main.vue.vm";
    public static final String VM_VUE_LIST = "vm/vue/list.vue.vm";
    public static final String VM_VUE_ADD = "vm/vue/add.vue.vm";
    public static final String VM_VUE_ALTER = "vm/vue/alter.vue.vm";
    public static final String VM_JS_API = "vm/js/api.js.vm";
    public static final String VM_JS_MIXIN = "vm/js/mixin.js.vm";
    public static final String VM_JS_RULES = "vm/js/rules.js.vm";
    private static final Logger logger = LoggerFactory.getLogger(VelocityUtil.class);

    /**
     * 根据表/字段说明生成代码
     *
     * @author kuangq
     * @date 2020-07-20 16:11
     */
    public static byte[] generateCode(List<TableExplain> tableExplainList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        for (TableExplain tableExplain : tableExplainList) {
            generateCode(zipOutputStream, tableExplain);
        }
        closeZipOutputStream(zipOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 格式化代码至压缩文件
     *
     * @author kuangq
     * @date 2020-07-20 16:14
     */
    private static void generateCode(ZipOutputStream zipOutputStream, TableExplain tableExplain) {
        //初始化Velocity对象
        initVelocity();
        //设置模板变量信息
        VelocityContext velocityContext = prepareContext(tableExplain);
        //获取模板文件路径
        List<String> vmPathList = getVmPathList();
        for (String vmPath : vmPathList) {
            //渲染模板
            StringWriter stringWriter = new StringWriter();
            //根据vm文件路径创建template对象
            Template template = Velocity.getTemplate(vmPath, StandardCharsets.UTF_8.name());
            //模板变量赋值
            template.merge(velocityContext, stringWriter);
            try {
                //添加到zip
                zipOutputStream.putNextEntry(new ZipEntry(getFileName(tableExplain.getClassName(), vmPath)));
                IOUtils.write(stringWriter.toString(), zipOutputStream, StandardCharsets.UTF_8);
                closeStringWriter(stringWriter);
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
            } catch (Exception e) {
                logger.error("【代码文件压缩失败】{}", e.getMessage());
            }
        }
    }

    /**
     * 创建文件名称
     *
     * @author kuangq
     * @date 2020-07-20 16:26
     */
    private static String getFileName(String className, String vmPath) {
        //截取文件后缀，拼接文件名称
        String postfix = StringUtils.substringAfterLast(StringUtils.substringBefore(vmPath, ".vm"), SpecialCharsConst.SLASH);
        String fileName = String.format("%s%s", className, postfix);
        //追加文件目录
        if (VM_JAVA_CONTROLLER.equals(vmPath)) {
            return String.format("controller/%s", fileName);
        }
        if (VM_JAVA_SERVICE.equals(vmPath)) {
            return String.format("service/%s", fileName);
        }
        if (VM_JAVA_SERVICE_IMPL.equals(vmPath)) {
            return String.format("service/impl/%s", fileName);
        }
        if (VM_JAVA_MAPPER.equals(vmPath)) {
            return String.format("mapper/%s", fileName);
        }
        if (VM_JAVA_ENTITY.equals(vmPath)) {
            return String.format("entity/%s.java", className);
        }
        if (VM_XML_MAPPER.equals(vmPath)) {
            return String.format("mapper/xml/%s", fileName);
        }
        //追加前端文件目录
        String catalogName = StringUtil.firstLower(className);
        if (VM_VUE_MAIN.equals(vmPath)) {
            return String.format("%s/%s", catalogName, postfix);
        }
        if (VM_VUE_LIST.equals(vmPath)) {
            return String.format("%s/components/%s", catalogName, postfix);
        }
        if (VM_VUE_ADD.equals(vmPath)) {
            return String.format("%s/components/%s", catalogName, postfix);
        }
        if (VM_VUE_ALTER.equals(vmPath)) {
            return String.format("%s/components/%s", catalogName, postfix);
        }
        if (VM_JS_API.equals(vmPath)) {
            return String.format("api/%s.js", catalogName);
        }
        if (VM_JS_MIXIN.equals(vmPath)) {
            return String.format("%s/components/js/%s", catalogName, postfix);
        }
        if (VM_JS_RULES.equals(vmPath)) {
            return String.format("%s/components/js/%s", catalogName, postfix);
        }
        return fileName;
    }

    /**
     * 初始化Velocity对象
     *
     * @author kuangq
     * @date 2020-07-20 16:11
     */
    private static void initVelocity() {
        try {
            Properties properties = new Properties();
            //加载classpath目录下的vm文件
            properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            //设置字符集
            properties.setProperty(Velocity.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
            properties.setProperty(Velocity.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());
            //初始化Velocity引擎，指定配置Properties
            Velocity.init(properties);
        } catch (Exception e) {
            logger.error("【初始化Velocity失败】{}", e.getMessage());
        }
    }

    /**
     * 设置模板变量值
     *
     * @author kuangq
     * @date 2020-07-20 16:12
     */
    private static VelocityContext prepareContext(TableExplain tableExplain) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tableText", tableExplain.getTableText());
        velocityContext.put("tableName", tableExplain.getTableName());
        velocityContext.put("className", tableExplain.getClassName());
        velocityContext.put("variableName", StringUtil.firstLower(tableExplain.getClassName()));
        velocityContext.put("primaryKey", tableExplain.getPrimaryKey());
        velocityContext.put("primaryName", tableExplain.getPrimaryName());
        velocityContext.put("fieldExplainList", tableExplain.getFieldExplainList());
        velocityContext.put("dateTime", DateUtil.getTimestamp(DatePattern.YMD_HM_1));
        return velocityContext;
    }

    /**
     * 格式化表字段说明详情
     *
     * @author kuangq
     * @date 2020-07-20 16:12
     */
    public static List<FieldExplain> formatFields(List<FieldExplain> fieldExplainList) {
        for (int i = 0; i < fieldExplainList.size(); i++) {
            FieldExplain fieldExplain = fieldExplainList.get(i);
            //将数据库字段名称改为驼峰式命名
            fieldExplain.setJavaName(StringUtil.formatCamelCase(fieldExplain.getFieldName()));
            //将数据库字段类型映射成java类型
            fieldExplain.setJavaType(ormJavaType(fieldExplain.getFieldType()));
            fieldExplainList.set(i, fieldExplain);
        }
        return fieldExplainList;
    }

    /**
     * 数据库字段类型映射java类型
     *
     * @author kuangq
     * @date 2020-07-20 16:13
     */
    private static String ormJavaType(String value) {
        String fieldType = StringUtils.substringBefore(value, "(");
        if (Arrays.asList(new String[]{"varchar", "bpchar", "text"}).contains(fieldType)) {
            return "String";
        } else if (Arrays.asList(new String[]{"int2", "int4"}).contains(fieldType)) {
            return "Integer";
        } else if (Arrays.asList(new String[]{"float4", "float8"}).contains(fieldType)) {
            return "Float";
        } else if (Arrays.asList(new String[]{"numeric"}).contains(fieldType)) {
            return "Double";
        } else if (Arrays.asList(new String[]{"bool"}).contains(fieldType)) {
            return "Boolean";
        } else if (Arrays.asList(new String[]{"date"}).contains(fieldType)) {
            return "Date";
        } else if (Arrays.asList(new String[]{"timestamp"}).contains(fieldType)) {
            return "Timestamp";
        } else {
            return "unknown";
        }
    }

    /**
     * 获取vm模板文件路径
     *
     * @author kuangq
     * @date 2020-07-20 16:13
     */
    private static List<String> getVmPathList() {
        List<String> templates = new ArrayList<>();
        templates.add(VM_JAVA_CONTROLLER);
        templates.add(VM_JAVA_SERVICE);
        templates.add(VM_JAVA_SERVICE_IMPL);
        templates.add(VM_JAVA_MAPPER);
        templates.add(VM_JAVA_ENTITY);
        templates.add(VM_XML_MAPPER);
        templates.add(VM_VUE_MAIN);
        templates.add(VM_VUE_LIST);
        templates.add(VM_VUE_ADD);
        templates.add(VM_VUE_ALTER);
        templates.add(VM_JS_API);
        templates.add(VM_JS_MIXIN);
        templates.add(VM_JS_RULES);
        return templates;
    }

    /**
     * 关闭Zip输出流
     *
     * @author kuangq
     * @date 2021-03-06 11:58
     */
    private static void closeZipOutputStream(ZipOutputStream zipOutputStream) {
        if (zipOutputStream != null) {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                //TODO
            }
        }
    }

    /**
     * 关闭StringWriter
     *
     * @author kuangq
     * @date 2021-03-06 12:00
     */
    private static void closeStringWriter(StringWriter stringWriter) {
        if (stringWriter != null) {
            try {
                stringWriter.close();
            } catch (IOException e) {
                //TODO
            }
        }
    }
}