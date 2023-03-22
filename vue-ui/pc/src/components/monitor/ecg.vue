<template>
    <div class="ecg">
        <!-- 面板标签-->
        <panel-title>
            <el-button type="info" icon="el-icon-s-promotion" @click="sendImageMsg" v-has-any-permissions="['qianYu:upload','qianYu:send']">发送报告</el-button>
        </panel-title>
        <div class="title">
            <i class="el-icon-caret-left" @click="onLeft"/>
            <span>&emsp;{{ result.dateList[0] }}&emsp;<i class="el-icon-refresh" @click="selectEcgInfo"/>&emsp;{{ result.dateList[2] }}&emsp;</span>
            <i class="el-icon-caret-right" @click="onRigth"/>
        </div>
        <div class="content">
            <el-row>
                <el-col :span="11">
                    <div class="chart" ref="chart_1"/>
                </el-col>
                <el-col :span="11" :push="1">
                    <div class="chart" ref="chart_2"/>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
import format from '@assets/js/util/format';
import popup from '@assets/js/mixin/popup';
import common from '@assets/js/mixin/common';
import {selectEcgInfo, sendImageMsg} from '@assets/js/api/qianYu';

const echarts = require('echarts');

export default {
    name: 'home',
    mixins: [popup, common],
    data() {
        return {
            format: format,
            hours: ['07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'],
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
            const barchartOption = this.getBarChartOption();
            this.drawChart('chart_1', barchartOption);
            const scatterChartOption = this.getScatterChartOption();
            this.drawChart('chart_2', scatterChartOption);
        },
        getBarChartOption() {
            return {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {color: '#999'},
                    },
                },
                toolbox: {
                    feature: {
                        magicType: {show: true, type: ['line', 'bar']},
                        dataView: {show: true, readOnly: false},
                        saveAsImage: {show: true},
                    },
                },
                grid: {
                    left: 10,
                    right: 10,
                    bottom: 10,
                    containLabel: true,
                },
                legend: {
                    left: 10,
                    data: this.result.dateList,
                },
                xAxis: [
                    {
                        data: this.hours,
                        type: 'category',
                        axisPointer: {type: 'shadow'},
                    },
                ],
                yAxis: [
                    {
                        type: 'value',
                    },
                ],
                series: this.getSeries(),
            };
        },
        getScatterChartOption() {
            return {
                tooltip: {
                    position: 'top',
                    formatter: (val) => `beat ${val.value[2]} times`,
                },
                toolbox: {
                    feature: {
                        dataView: {show: true, readOnly: false},
                        saveAsImage: {show: true},
                    },
                },
                grid: {
                    left: 10,
                    right: 10,
                    bottom: 10,
                    containLabel: true,
                },
                xAxis: {
                    type: 'category',
                    data: this.hours,
                    boundaryGap: false,
                    splitLine: {show: true},
                    axisLine: {show: false},
                },
                yAxis: {
                    type: 'category',
                    data: this.result.dateList,
                    axisLine: {show: false},
                },
                series: [
                    {
                        type: 'scatter',
                        data: this.result.dataList,
                        symbolSize: (val) => val[2] * 2,
                        animationDelay: (idx) => idx * 5,
                        itemStyle: {
                            normal: {
                                color: (item) => format.decode(item.value[1], 0, 'rgb(84,112,198)', 1, 'rgb(145,204,117)', 2, 'rgb(250,200,88)', ''),
                            },
                        },
                    },
                ],
            };
        },
        drawChart(ref, option) {
            if (this.$refs[ref].childNodes.length > 0) {
                this[ref].dispose();
            }
            this[ref] = echarts.init(this.$refs[ref]);
            this[ref].setOption(option);
        },
        getSeries() {
            const series = [];
            for (let i = 0; i < this.result.dateList.length; i++) {
                const item = {
                    type: 'bar',
                    name: this.result.dateList[i],
                    data: this.result.dataList.filter((item) => item[1] === i).map((item) => [item[2]][0]),
                };
                series.push(item);
            }
            return series;
        },
        getImageBase64(ref) {
            const opts = {
                type: 'png',
                pixelRatio: 1,
                excludeComponents: ['toolbox'],
            };
            let base64 = this[ref].getDataURL(opts);
            console.log(base64);
            return base64.substring(22);
        },
        sendImageMsg() {
            let self = this;
            const loading = self.loadingOpen('.home');
            const data = {
                mediaInfoList: [
                    {name: 'chart_1.png', type: 'image', base64: self.getImageBase64('chart_1')},
                    {name: 'chart_2.png', type: 'image', base64: self.getImageBase64('chart_2')},
                ],
            };
            sendImageMsg(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            }).finally(() => {
                loading && loading.close();
            });
        },
        onLeft() {
            this.currentDate.setDate(this.currentDate.getDate() - 1);
            this.selectEcgInfo();
        },
        onRigth() {
            if (new Date(this.result.dateList[2] + ' 23:59:59') >= new Date()) {
                return;
            }
            this.currentDate.setDate(this.currentDate.getDate() + 1);
            this.selectEcgInfo();
        },
    },
};
</script>

<style scoped lang="scss">
    .ecg {
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

        .content {
            width: 100%;
            height: 100%;
            margin-top: 20px;

            .chart {
                height: 450px;
            }
        }
    }
</style>
