<template>
    <div class="menus">
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
    name: 'menus',
    components: {
        list, add, alter,
    },
    mixins: [common],
    data() {
        return {
            params: {isFlush: false},
        };
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
    .menus {
        .search-box {
            .el-input {
                width: 280px;
            }
        }
    }
</style>
