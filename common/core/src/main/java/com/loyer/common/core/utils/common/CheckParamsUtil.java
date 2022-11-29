package com.loyer.common.core.utils.common;

import com.loyer.common.core.enums.RegExp;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数校验
 *
 * @author kuangq
 * @date 2019-09-06 16:47
 */
public class CheckParamsUtil {

    /**
     * Map非空，字段必填校验
     *
     * @author kuangq
     * @date 2019-12-31 10:41
     */
    public static void checkMap(Map<String, Object> params, String[] keyArray) {
        if (params == null || params.isEmpty()) {
            throw new BusinessException(HintEnum.HINT_1014);
        }
        for (String key : keyArray) {
            if (!params.containsKey(key) || params.get(key) == null || StringUtils.isBlank(params.get(key).toString())) {
                throw new BusinessException(String.format("%s不能为空", key), params);
            }
        }
    }

    /**
     * 正则校验
     *
     * @author kuangq
     * @date 2019-12-12 17:54
     */
    public static boolean regularCheck(RegExp regExp, Object... objects) {
        Pattern p = Pattern.compile(regExp.getRegex());
        for (Object object : objects) {
            Matcher matcher = p.matcher(object.toString());
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }
}