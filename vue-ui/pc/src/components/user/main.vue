<template>
    <div class="user">
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
import dept from '@components/dept/js/mixin';

export default {
    name: 'user',
    components: {
        list, add, alter,
    },
    mixins: [common, dept],
    data() {
        return {
            params: {
                // 是否刷新列表
                isFlush: false,
                // 性别字典
                genderList: [],
                // 部门信息
                deptList: [],
                // 克隆的部门信息
                cloneDeptList: [],
            },
        };
    },
    created() {
        let self = this;
        // 加载字典信息
        self.selectDict('.user', 'gender').then((result) => {
            self.params.genderList = result;
        });
        // 加载部门信息
        self.initDept({}, '.user').then((result) => {
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
