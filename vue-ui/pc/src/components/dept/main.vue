<template>
    <div>
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
import mixin from './js/mixin';

export default {
    name: 'dept',
    components: {
        list, add, alter,
    },
    mixins: [common, mixin],
    data() {
        return {
            params: {
                // 是否刷新列表
                isFlush: false,
                // 部门信息
                deptList: [],
                // 克隆的部门信息
                cloneDeptList: [],
            },
        };
    },
    created() {
        let self = this;
        // 加载部门信息
        self.initDept({status: true}, '.dept').then((result) => {
            self.params.deptList = result;
            self.params.cloneDeptList = JSON.parse(JSON.stringify(result));
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
