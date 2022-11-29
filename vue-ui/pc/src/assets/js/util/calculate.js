const calculate = {};

/* 用来得到精确的加法结果*/
calculate.cusAdd = (arg1, arg2) => {
    try {
        let len1, len2, num1;
        len1 = String(arg1).indexOf('.') > -1 ? arg1.toString().split('.')[1].length : 0;
        len2 = String(arg2).indexOf('.') > -1 ? arg2.toString().split('.')[1].length : 0;
        num1 = Math.pow(10, Math.max(len1, len2)); // 动态控制精度长度
        return (Math.round(arg1 * num1) + Math.round(arg2 * num1)) / num1;
    } catch (e) {
        console.log('cusAdd异常：' + e);
    }
};

/* 用来得到精确的减法结果*/
calculate.cusSub = (arg1, arg2) => {
    try {
        let len1, len2, num1, num2;
        len1 = String(arg1).indexOf('.') > -1 ? arg1.toString().split('.')[1].length : 0;
        len2 = String(arg2).indexOf('.') > -1 ? arg2.toString().split('.')[1].length : 0;
        num1 = Math.pow(10, Math.max(len1, len2)); // 动态控制精度长度
        num2 = len1 >= len2 ? len1 : len2;
        return ((arg1 * num1 - arg2 * num1) / num1).toFixed(num2);
    } catch (e) {
        console.log('cusSub异常：' + e);
    }
};

/* 用来得到精确的乘法结果*/
calculate.cusMul = (arg1, arg2) => {
    try {
        let len1, len2, num1, num2;
        len1 = String(arg1).indexOf('.') > -1 ? arg1.toString().split('.')[1].length : 0;
        len2 = String(arg2).indexOf('.') > -1 ? arg2.toString().split('.')[1].length : 0;
        num1 = Number(arg1.toString().replace('.', ''));
        num2 = Number(arg2.toString().replace('.', ''));
        return num1 * num2 / Math.pow(10, len1 + len2);
    } catch (e) {
        console.log('cusMul异常：' + e);
    }
};

/* 用来得到精确的除法结果*/
calculate.cusDiv = (arg1, arg2) => {
    try {
        let len1, len2, num1, num2;
        len1 = String(arg1).indexOf('.') > -1 ? arg1.toString().split('.')[1].length : 0;
        len2 = String(arg2).indexOf('.') > -1 ? arg2.toString().split('.')[1].length : 0;
        num1 = Number(arg1.toString().replace('.', ''));
        num2 = Number(arg2.toString().replace('.', ''));
        return num1 / num2 * Math.pow(10, len2 - len1);
    } catch (e) {
        console.log('cusDiv异常：' + e);
    }
};

/* 重写四舍五入处理精度损失问题*/
calculate.cusToFixed = (num, digit) => {
    const flag = num < 0 ? -1 : 1;
    num = Math.abs(num);
    num = calculate.cusMul(num, Math.pow(10, digit));
    num = Math.round(num);
    return num / Math.pow(10, digit) * flag;
};

export default calculate;
