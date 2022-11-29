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
import dept from '@components/dept/js/mixin';
import {mapState} from 'vuex';

export default {
    name: 'user',
    components: {
        list, add, alter,
    },
    mixins: [common, dept],
    computed: {
        ...mapState({
            user: (state) => state.user.user,
        }),
    },
    data() {
        return {
            params: {
                // 是否刷新列表
                isFlush: false,
                // 数据权限类型
                permissionTypeList: [],
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
        self.selectDict('.role', 'permissionType').then((result) => {
            self.params.permissionTypeList = result.filter((item) => {
                return Number(item.value) >= self.user.permissionScope;
            });
        });
        // 加载部门信息
        self.initDept({}, '.role').then((result) => {
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
