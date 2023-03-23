package com.loyer.common.quartz.task;

import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例task
 *
 * @author kuangq
 * @date 2020-12-18 10:47
 */
@Component("demoTask")
public class DemoTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 无参任务，调用表达式：com.loyer.system.framework.scheduler.task.DemoTask.noParams
     *
     * @author kuangq
     * @date 2020-12-18 11:00
     */
    public void noParams() {
        String currentTime = DateUtil.getTimestamp(DatePattern.YMD_HMS_1);
        logger.info("【当前时间】{}", currentTime);
    }

    /**
     * 有参有返回任务，调用表达式：demoTask.haveParams('String',10I,10i,1000L,1000l,1.3F,1.3f,10.88D,10.88d,trueB,falseb)
     *
     * @author kuangq
     * @date 2020-12-18 11:01
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Map<String, Object> haveParams(String str, Integer I, int i, Long L, long l, Float F, float f, Double D, double d, Boolean B, boolean b) {
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