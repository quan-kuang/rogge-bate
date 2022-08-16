package com.loyer.common.quartz.task;

import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuangq
 * @title DemoTask
 * @description 示例task
 * @date 2020-12-18 10:47
 */
@Component("demoTask")
public class DemoTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param
     * @return void
     * @author kuangq
     * @description 无参任务，调用表达式：com.loyer.system.framework.scheduler.task.DemoTask.noParams
     * @date 2020-12-18 11:00
     */
    public void noParams() {
        String currentTime = DateUtil.getTimestamp(DatePattern.YMD_HMS_1);
        logger.info("【当前时间】{}", currentTime);
    }

    /**
     * @param str String类型入参，参数需用''包裹
     * @param I   Integer类型入参，参数后面用大写I结尾
     * @param i   int类型入参，参数后面用小写i结尾
     * @param L   Long类型入参，参数后面用大写L结尾
     * @param l   long类型入参，参数后面用小写l结尾
     * @param F   Float类型入参，参数后面用大写F结尾
     * @param f   float类型入参，参数后面用小写f结尾
     * @param D   Double类型入参，参数后面用大写D结尾
     * @param d   double类型入参，参数后面用小写d结尾
     * @param B   Boolean类型入参，参数后面用大写B结尾
     * @param b   boolean类型入参，参数后面用小写b结尾
     * @return java.util.Map
     * @author kuangq
     * @description 有参有返回任务，调用表达式：demoTask.haveParams('String',10I,10i,1000L,1000l,1.3F,1.3f,10.88D,10.88d,trueB,falseb)
     * @date 2020-12-18 11:01
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Map haveParams(String str, Integer I, int i, Long L, long l, Float F, float f, Double D, double d, Boolean B, boolean b) {
        return new HashMap<String, Object>(16) {{
            put("String", str);
            put("Integer", I);
            put("int", i);
            put("Long", L);
            put("long", l);
            put("Float", F);
            put("float", f);
            put("Double", D);
            put("double", d);
            put("Boolean", B);
            put("boolean", b);
        }};
    }
}