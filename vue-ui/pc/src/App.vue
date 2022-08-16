<template>
    <div id="app">
        <router-view v-if="isRouterAlive"/>
    </div>
</template>

<script>
export default {
    name: 'app',
    provide() {
        return {
            reload: this.reload,
        };
    },
    data() {
        return {
            isRouterAlive: true,
        };
    },
    methods: {
        reload() {
            this.isRouterAlive = false;
            this.$nextTick(() => {
                this.isRouterAlive = true;
            });
        },
    },
};
</script>

<style lang="scss">
    @import './assets/scss/common.scss';

    #app {
        height: 100%;
    }

    /*滚动条整体部分*/
    ::-webkit-scrollbar {
        width: 8px;
        height: 8px;
    }

    /*滚动条的轨道*/
    ::-webkit-scrollbar-track {
        /*background: rgb(239, 239, 239);*/
        border-radius: 2px;
    }

    /*滚动条里面的小方块*/
    ::-webkit-scrollbar-thumb {
        background: rgba(148, 148, 148, 0.33);
        border-radius: 10px;
    }

    /*鼠标悬停滚动条里面的小方块*/
    ::-webkit-scrollbar-thumb:hover {
        background: rgba(148, 148, 148, 0.66);
    }

    /*两个滚动条的交汇处*/
    ::-webkit-scrollbar-corner {
        background: rgba(148, 148, 148, 0.99);
    }
</style>
