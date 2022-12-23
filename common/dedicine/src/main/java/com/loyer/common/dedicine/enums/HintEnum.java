package com.loyer.common.dedicine.enums;

/**
 * 业务处理提示信息
 *
 * @author kuangq
 * @date 2019-07-19 9:22
 */
public enum HintEnum {

    HINT_1000(1000, "操作成功"),
    HINT_1001(1001, "服务器内部错误"),
    HINT_1002(1002, "非法访问"),
    HINT_1003(1003, "验签失效"),
    HINT_1004(1004, "验签失败"),
    HINT_1005(1005, "分页参数校验失败"),
    HINT_1006(1006, "未找到查询目标"),
    HINT_1007(1007, "找不到模板ID"),
    HINT_1008(1008, "找不到微信报警接收人"),
    HINT_1009(1009, "该目录不存在"),
    HINT_1011(1011, "关闭流失败"),
    HINT_1012(1012, "获取accessToken失败"),
    HINT_1013(1013, "获取apiTicket失败"),
    HINT_1014(1014, "入参不能为空"),
    HINT_1015(1015, "url不能为空"),
    HINT_1024(1024, "认证已失效"),
    HINT_1025(1025, "实名核身未完成"),
    HINT_1029(1029, "没有定义该方法"),
    HINT_1030(1030, "方法安全权限异常"),
    HINT_1031(1031, "调用方法内部异常"),
    HINT_1032(1032, "没有定义该类"),
    HINT_1033(1033, "正则校验失败"),
    HINT_1034(1034, "操作符错误"),
    HINT_1036(1036, "实例化对象失败"),
    HINT_1041(1041, "用户不存在"),
    HINT_1042(1042, "用户已被停用"),
    HINT_1043(1043, "用户密码输入有误"),
    HINT_1051(1051, "禁止操作管理员账户"),
    HINT_1063(1063, "路径异常，拿到的是目录并非文件"),
    HINT_1069(1069, "文件在服务器上不存在"),
    HINT_1070(1070, "上传FTP文件服务器失败"),
    HINT_1071(1071, "redirectUrl/scope不能为空"),
    HINT_1077(1077, "尚无访问权限"),
    HINT_1080(1080, "参数异常，不在指定范围内"),
    HINT_1082(1082, "旧密码输入有误"),
    HINT_1083(1083, "未识别到人脸"),
    HINT_1086(1086, "验证码已失效"),
    HINT_1087(1087, "验证码有误，请重试"),
    HINT_1088(1088, "安全认证失败"),
    HINT_1089(1089, "访问频率过高，请稍后再试"),
    HINT_1090(1090, "ES索引不存在"),
    HINT_1091(1091, "Corn表达式无效"),
    HINT_1095(1095, "ES查询失败"),
    HINT_1099(1099, "会话已过期，请重新登录"),
    HINT_1100(1100, "自定义提示"),
    ;

    private final Integer code;

    private final String msg;

    HintEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}