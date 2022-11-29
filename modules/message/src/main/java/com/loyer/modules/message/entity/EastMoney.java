package com.loyer.modules.message.entity;

import lombok.*;

import java.util.List;

/**
 * 东方财富信息网
 *
 * @author kuangq
 * @date 2021-09-17 10:00
 */
@Data
@NoArgsConstructor
public class EastMoney {

    @Data
    @NoArgsConstructor
    public static class Request {
        /*token*/
        private String ut;
        /*分页页码*/
        private Integer pn;
        /*分页大小*/
        private Integer pz;
        /*data返回数组类型*/
        private Integer np = 1;
        /*保留两位小数*/
        private Integer fltt = 2;
        /* f2 最新，f3 涨幅，f4 涨跌，f8 换手率，f9 市盈率，f10 量比，f12 代码，f14 名称，f15 最高，f16 最低，f17 开盘，f18 昨收，f20 总市值，f23 市净率，f100 行业，f124 时间戳*/
        private String fields = "f1,f2,f3,f4,f8,f9,f10,f12,f14,f15,f16,f17,f18,f20,f23,f100,f124";
        /*板块类型*/
        private String fs = "m:0+t:6+f:!2,m:0+t:7+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:3+f:!2,m:1+t:23+f:!2";

        public Request(String ut, Integer pn, Integer pz, String fs) {
            this.ut = ut;
            this.pn = pn;
            this.pz = pz;
            this.fs = fs;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Response {
        private Integer rc;
        private Integer rt;
        private Integer lt;
        private Integer full;
        private Long svr;
        private Data data;

        public Data getData() {
            return data;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private Integer total;
            private List<Stocks> diff;
        }
    }
}