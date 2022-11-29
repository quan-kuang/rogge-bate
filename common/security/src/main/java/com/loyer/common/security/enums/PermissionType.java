package com.loyer.common.security.enums;

/**
 * 数据权限类型
 *
 * @author kuangq
 * @date 2020-09-15 20:54
 */
public enum PermissionType {

    //全部数据权限
    ALL("0"),
    //自定义权限
    CUSTOM("1"),
    //部门及以下权限
    DEPT_AND_BELOW("2"),
    //仅所属部门权限
    ONLY_DEPT("3"),
    //仅个人权限
    ONLY_SELF("4"),
    ;

    private final String value;

    PermissionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}