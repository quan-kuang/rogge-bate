package com.loyer.modules.tools.inherit;


import com.loyer.modules.tools.enums.Lawful;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * 入参合法范围校验
 *
 * @author kuangq
 * @date 2022-09-02 15:17
 */
public class LawfulValidator implements ConstraintValidator<Lawful, Object> {

    private int[] intAry;
    private String[] stringAry;
    private Class<? extends Enum<?>>[] enumClassAry;

    @Override
    public void initialize(Lawful constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        intAry = constraintAnnotation.intAry();
        stringAry = constraintAnnotation.stringAry();
        enumClassAry = constraintAnnotation.enumClassAry();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (enumClassAry.length > 0) {
            if (value instanceof Enum) {
                return Arrays.stream(enumClassAry).anyMatch(aClass -> Arrays.asList(aClass.getEnumConstants()).contains(value));
            }
            return Arrays.stream(enumClassAry).anyMatch(aClass -> Arrays.stream(aClass.getEnumConstants()).anyMatch(item -> item.name().equals(value)));
        }
        if (value instanceof Integer) {
            return Arrays.stream(intAry).anyMatch(item -> item == (Integer) value);
        }
        if (value instanceof String) {
            return Arrays.stream(stringAry).anyMatch(item -> item.equals(value.toString()));
        }
        return false;
    }
}