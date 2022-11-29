<template>
    <div class="crontab">
        <keep-alive>
            <list v-if="action===LIST" @nextStep="nextStep" :params="params"/>
            <add v-if="action===ADD" @nextStep="nextStep" :params="params"/>
            <alter v-if="action===ALTER" @nextStep="nextStep" :params="params"/>
        </keep-alive>
    </div>
</template>

<script>
import list from './components/list';
import add from './components/add';
import alter from './components/alter';
import common from '@assets/js/mixin/common';

export default {
    name: 'crontab',
    components: {
        list, add, alter,
    },
    mixins: [common],
    data() {
        return {
            params: {
                // 是否刷新列表
                isFlush: false,
                // 类型字典
                typeList: [],
            },
        };
    },
    created() {
        let self = this;
        // 加载定时任务字典信息
        self.selectDict('.crontab', 'crontab-type').then((result) => {
            self.params.typeList = result;
        });
    },
    methods: {
        /* 页面跳转*/
        nextStep(action) {
            this.action = action;
        },
    },
};
</script>

<style lang="scss">
    .crontab {
        .add, .alter {
            /*表单输入框*/
            .el-form {
                .el-input, .el-select, .el-date-editor .el-input__inner {
                    width: 100%;
                }
            }
        }
    }
</style>
