<template>
    <div class="home">
        <div class="title">
            <i class="el-icon-caret-left" @click="onLeft"/>
            <span>&emsp;{{ result.dateList[0] }}&emsp;<i class="el-icon-refresh" @click="selectEcgInfo"/>&emsp;{{ result.dateList[2] }}&emsp;</span>
            <i class="el-icon-caret-right" @click="onRigth"/>
        </div>
        <el-row>
            <el-col :span="24">
                <div class="chart" ref="chart_1"/>
            </el-col>
            <el-col :span="24">
                <div class="chart" ref="chart_2"/>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import popup from '@assets/js/mixin/popup';
import {selectEcgInfo} from '@assets/js/api/qianYu';

const echarts = require('echarts');

export default {
    name: 'home',
    mixins: [popup],
    data() {
        return {
            hours: ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'],
            currentDate: new Date(),
            result: {
                dateList: [],
                dataList: [],
            },
        };
    },
    mounted() {
        this.selectEcgInfo();
    },
    methods: {
        selectEcgInfo() {
            let self = this;
            const data = {
                currentDate: self.currentDate.cusFormat('yyyy-mm-dd'),
                openId: 'or7WYt5P62MmL3Un3ecWgENFqFTQ',
                num: 3,
            };
            const loading = self.loadingOpen('.home');
            selectEcgInfo(data).then((response) => {
                if (response.flag) {
                    self.result = response.data;
                    self.initCharts();
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        initCharts() {
            let self = this;
            const option_1 = {
                legend: {
                    left: 'right',
                },
                tooltip: {
                    position: 'top',
                    formatter: function (params) {
                        return (
                            params.value[2] +
                                ' commits in ' +
                                self.hours[params.value[0]] +
                                ' of ' +
                                self.result.dateList[params.value[1]]
                        );
                    },
                },
                grid: {
                    bottom: 10,
                    right: 10,
                    left: 10,
                    containLabel: true,
                },
                xAxis: {
                    type: 'category',
                    data: self.hours,
                    boundaryGap: false,
                    splitLine: {
                        show: true,
                    },
                    axisLine: {
                        show: false,
                    },
                },
                yAxis: {
                    type: 'category',
                    data: self.result.dateList,
                    axisLine: {
                        show: false,
                    },
                },
                series: [
                    {
                        name: '浅予',
                        type: 'scatter',
                        symbolSize: function (val) {
                            return val[2] * 2;
                        },
                        data: self.result.dataList,
                        animationDelay: function (idx) {
                            return idx * 5;
                        },
                    },
                ],
            };
            if (self.$refs.chart_1.childNodes.length > 0) {
                self.chart_1.dispose();
            }
            self.chart_1 = echarts.init(self.$refs.chart_1);
            self.chart_1.setOption(option_1);


            const option_2 = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                            color: '#999',
                        },
                    },
                },
                toolbox: {
                    feature: {
                        dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['line', 'bar']},
                        restore: {show: true},
                        saveAsImage: {show: true},
                    },
                },
                grid: {
                    bottom: 10,
                    right: 10,
                    left: 10,
                    containLabel: true,
                },
                legend: {
                    left: 10,
                    data: self.result.dateList,
                },
                xAxis: [
                    {
                        type: 'category',
                        data: self.hours,
                        axisPointer: {type: 'shadow'},
                    },
                ],
                yAxis: [{type: 'value'},
                ],
                series: self.getSeries(),
            };
            if (self.$refs.chart_2.childNodes.length > 0) {
                self.chart_2.dispose();
            }
            self.chart_2 = echarts.init(self.$refs.chart_2);
            self.chart_2.setOption(option_2);

            // const opts = {
            //     type: 'png',
            //     pixelRatio: 1,
            //     excludeComponents: ['toolbox'],
            // };
            // let resBase64 = self.chart_2.getDataURL(opts);
            // console.log(resBase64);
        },
        getSeries() {
            const series = [];
            for (let i = 0; i < this.result.dateList.length; i++) {
                const item = {
                    type: 'bar',
                    name: this.result.dateList[i],
                    tooltip: {valueFormatter: (value) => value},
                    data: this.result.dataList.filter((item) => item[1] === i).map((item) => [item[2]][0]),
                };
                series.push(item);
            }
            return series;
        },
        onLeft() {
            let self = this;
            self.currentDate.setDate(self.currentDate.getDate() - 3);
            self.selectEcgInfo();
        },
        onRigth() {
            let self = this;
            if (new Date(self.result.dateList[2] + ' 23:59:59') >= new Date()) {
                return;
            }
            self.currentDate.setDate(self.currentDate.getDate() + 3);
            self.selectEcgInfo();
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

        .chart {
            height: 450px;
        }
    }
</style>
