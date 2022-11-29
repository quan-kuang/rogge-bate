package com.loyer.common.core.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 异常响应跳转Control
 *
 * @author kuangq
 * @date 2019-10-15 15:23
 */
@ApiIgnore
@Controller
@RequestMapping("error")
public class ErrorPageControl {

    @RequestMapping("e404")
    public ModelAndView e404() {
        return new ModelAndView("html/404");
    }

    @RequestMapping("e500")
    public ModelAndView e500() {
        return new ModelAndView("html/404");
    }
}