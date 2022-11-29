<template>
    <transition name="fade">
        <keep-alive>
            <router-view/>
        </keep-alive>
    </transition>
</template>

<script>
/* 菜单路由出口*/
export default {
    name: 'right',
};
</script>

<style scoped lang="scss">
    .fade-enter-active {
        transition: opacity 1s;
    }

    .fade-leave-active {
        transition: opacity 0s;
    }

    .fade-enter, .fade-leave-to {
        opacity: 0;
    }
</style>
