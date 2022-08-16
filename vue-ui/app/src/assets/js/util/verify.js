const verify = {};

// 手机号校验
verify.regExpPhone = /^1[34578]\d{9}$/;

// 价格校验
verify.regExpPrice = /(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/;

// 空格校验
verify.regExpBlank = /^$| /;

// 中文校验
verify.regExpChinese = /[\u4e00-\u9fa5]/;

// 身份证号校验
verify.regExpIdCard = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;

/* 必填校验*/
verify.isBlank = (value) => {
    return value === null || value.trim() === '';
};

/* 正则校验*/
verify.regularCheck = (value, regExp) => {
    return regExp.test(value);
};

/* 入参校验*/
verify.notEmptyCheck = (value, name) => {
    const result = {rst: false, msg: '校验成功！'};
    if (!value || !value.trim()) {
        result.msg = '请输入' + name;
    } else if (value.indexOf(' ') >= 0) {
        result.msg = name + '不能包含空格';
    } else {
        result.rst = true;
    }
    return result;
};

export default verify;
