package com.loyer.modules.tools.enums;


import com.loyer.modules.tools.inherit.LawfulValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 枚举校验注解
 *
 * @author kuangq
 * @date 2022-09-02 15:16
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {LawfulValidator.class})
public @interface Lawful {

    String message() default "param out of range";

    int[] intAry() default {};

    String[] stringAry() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>>[] enumClassAry() default {};
}