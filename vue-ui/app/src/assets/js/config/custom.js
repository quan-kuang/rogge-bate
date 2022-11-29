/* 类型添加自定义方法，使其调用起来更加方便*/
import calculate from '@assets/js/util/calculate';
import format from '@assets/js/util/format';

/* 给Date类型增加一个cusFormat方法*/
Date.prototype.cusFormat = function (arg) {
    return format.date(this, arg);
};

/* 给Number类型增加一个cusAdd方法*/
Number.prototype.cusAdd = function (arg) {
    return calculate.cusAdd(this, arg);
};

/* 给Number类型增加一个cusSub方法*/
Number.prototype.cusSub = function (arg) {
    return calculate.cusSub(this, arg);
};

/* 给Number类型增加一个cusMul方法*/
Number.prototype.cusMul = function (arg) {
    return calculate.cusMul(this, arg);
};

/* 给Number类型增加一个cusDiv方法*/
Number.prototype.cusDiv = function (arg) {
    return calculate.cusDiv(this, arg);
};

/* 给Number类型增加一个cusToFixed方法*/
Number.prototype.cusToFixed = function (arg) {
    return calculate.cusToFixed(this, arg);
};

/* 给String类型增加一个toBool方法*/
String.prototype.toBool = function () {
    return format.toBool(this);
};
