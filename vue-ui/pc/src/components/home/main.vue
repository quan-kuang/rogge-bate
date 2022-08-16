<template>
    <div class="home">
        <div class="title">
            <i class="el-icon-caret-left" @click="onLeft"/>
            <span>&emsp;{{ result.dateList[0] }}&emsp;<i class="el-icon-refresh" @click="logWeekStat"/>&emsp;{{ result.dateList[6] }}&emsp;</span>
            <i class="el-icon-caret-right" @click="onRigth"/>
        </div>
        <div class="charts">
            <div class="chart" ref="chart_1"/>
            <div class="chart" ref="chart_2"/>
        </div>
    </div>
</template>

<script>
const echarts = require('echarts');
import popup from '@assets/js/mixin/popup';
import {logWeekStat} from '@assets/js/api/log';

export default {
    name: 'home',
    mixins: [popup],
    data() {
        return {
            currentDate: new Date(),
            result: {
                dateList: [],
                weekList: [],
                countList: [],
            },
            chart2Data: [],
        };
    },
    mounted() {
        this.logWeekStat();
    },
    methods: {
        logWeekStat() {
            let self = this;
            const data = {
                currentDate: self.currentDate.cusFormat('yyyy-mm-dd'),
            };
            const loading = self.loadingOpen('.home');
            logWeekStat(data).then((response) => {
                if (response.flag) {
                    const dateList = [];
                    const weekList = [];
                    const countList = [];
                    const chart2Data = [];
                    for (const item of response.data) {
                        dateList.push(item.date);
                        weekList.push(item.week);
                        countList.push(item.count);
                        chart2Data.push({value: item.count, name: item.week});
                    }
                    self.result.dateList = dateList;
                    self.result.weekList = weekList;
                    self.result.countList = countList;
                    self.chart2Data = chart2Data;
                    self.initCharts();
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        initCharts() {
            let self = this;
            const option_1 = {
                title: {
                    text: '接口调用统计图',
                    left: 'center',
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['bar', 'tiled']},
                        restore: {show: true},
                        saveAsImage: {show: true},
                    },
                },
                tooltip: {
                    trigger: 'axis',
                    formatter: function (params) {
                        const item = params[0];
                        let result = `${self.result.dateList[item.dataIndex]}`;
                        result += `<br/> ${item.seriesName}：${item.value}`;
                        return result;
                    },
                },
                legend: {
                    left: 'center',
                    top: 'bottom',
                    data: ['调用次数'],
                },
                xAxis: {
                    data: self.result.weekList,
                },
                yAxis: {
                    type: 'value',
                },
                series: [
                    {
                        name: '调用次数',
                        type: 'line',
                        data: self.result.countList,
                    },
                ],
                optionToContent: (option) => {
                    return self.getTable(option);
                },
            };
            if (self.$refs.chart_1.childNodes.length > 0) {
                self.chart_1.dispose();
            }
            self.chart_1 = echarts.init(self.$refs.chart_1);
            self.chart_1.setOption(option_1);
            const option_2 = {
                title: {
                    text: '南丁格尔玫瑰图',
                    left: 'center',
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['tiled']},
                        restore: {show: true},
                        saveAsImage: {show: true},
                    },
                },
                tooltip: {
                    trigger: 'item',
                    show: true,
                    formatter: function (params) {
                        const item = params;
                        let result = `${self.result.dateList[item.dataIndex]}`;
                        result += `<br/> ${item.seriesName}：${item.value}`;
                        result += `<br/> 百分比：${item.percent}%`;
                        return result;
                    },
                },
                legend: {
                    left: 'center',
                    top: 'bottom',
                    data: self.result.weekList,
                },
                series: [
                    {
                        name: '调用次数',
                        type: 'pie',
                        roseType: 'area',
                        data: self.chart2Data,
                    },
                ],
                optionToContent: (option) => {
                    return self.getTable(option);
                },
            };
            if (self.$refs.chart_2.childNodes.length > 0) {
                self.chart_2.dispose();
            }
            self.chart_2 = echarts.init(self.$refs.chart_2);
            self.chart_2.setOption(option_2);
        },
        getTable(option) {
            let self = this;
            // 调用次数求和
            const sum = self.result.countList.reduce((prev, cur, index, array) => {
                return prev + cur;
            });
            // 获取option中的数据，此处只为拿到遍历次数
            const seriesData = option.series[0].data;
            let html = '<table border="1px solid black" cellspacing="0" style="user-select:text;text-align: center"><tbody><tr><td >日期</td><td >星期</td><td>调用次数</td><td>百分比</td></tr>';
            seriesData.forEach((item, index) => {
                let tbody = '';
                // 遍历map
                for (const key in self.result) {
                    tbody += `<td>${self.result[key][index]}</td>`;
                }
                // 自定义计算高精度求百分比
                const percent = self.result.countList[index].cusDiv(sum).cusToFixed(4);
                html += `<tr>${tbody}<td>${percent}%</td></tr>`;
            });
            html += `<tr><td>合计</td><td></td><td>${sum}</td><td>100%</td></tr>`;
            html += '</tbody></table>';
            return html;
        },
        onLeft() {
            let self = this;
            self.currentDate.setDate(self.currentDate.getDate() - 7);
            self.logWeekStat();
        },
        onRigth() {
            let self = this;
            if (new Date(self.result.dateList[6]) >= new Date()) {
                return;
            }
            self.currentDate.setDate(self.currentDate.getDate() + 7);
            self.logWeekStat();
        },
    },
};
</script>

<style scoped lang="scss">
    .home {
        .title {
            text-align: center;
            width: 100%;
            height: 30px;
            line-height: 30px;

            i {
                font-size: 16px;
            }

            i:hover {
                cursor: pointer;
            }
        }

        .charts {
            display: flex;
            margin-top: 25px;

            .chart {
                width: 50%;
                height: 500px;
            }
        }
    }
</style>
