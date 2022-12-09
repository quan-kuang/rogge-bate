<template>
    <div class="treeSelect">
        <el-tree ref="treeSelect" node-key="uuid" :props="props" :data="data" :style="treeStyle"
                 :accordion="false" :expand-on-click-node="true" :highlight-current="true"
                 :show-checkbox="showCheckbox" :default-expanded-keys="defaultKeys" :check-strictly="checkStrictly"
                 @node-click="nodeClick" @check-change="checkChange"/>
    </div>
</template>

<script>
import {mapState} from 'vuex';

export default {
    name: 'treeSelect',
    props: {
        props: {
            type: Object,
            default: () => {
                return {
                    value: 'uuid',
                    label: 'label',
                    children: 'children',
                    disabled: 'disabled',
                };
            },
        },
        data: {
            type: Array,
            default: () => {
                return [];
            },
        },
        showCheckbox: {
            type: Boolean,
            default: () => {
                return false;
            },
        },
        checkStrictly: {
            type: Boolean,
            default: () => {
                return false;
            },
        },
        defaultKeys: {
            type: Array,
            default: () => {
                return [];
            },
        },
        treeStyle: {
            type: String,
            default: () => {
                return '';
            },
        },
    },
    computed: {
        ...mapState({
            tableMaxHeight: (state) => state.style.tableMaxHeight,
            isShowPanelTitle: (state) => state.style.isShowPanelTitle,
        }),
    },
    methods: {
        // 设置某个节点的勾选状态
        setChecked(key, checked) {
            this.$refs.treeSelect.setChecked(key, checked);
        },
        // 设置目前勾选的节点
        setCheckedKeys(keys) {
            this.$refs.treeSelect.setCheckedKeys(keys);
        },
        // 节点选中状态发生变化
        checkChange(node, isCheck) {
            this.$emit('checkChange', node, isCheck);
        },
        // 获取被选中的节点的key所组成的数组
        getCheckedKeys() {
            return this.$refs.treeSelect.getCheckedKeys();
        },
        // 设置指定节点的选中状态
        setCurrentKey(key) {
            this.$refs.treeSelect.setCurrentKey(key);
        },
        // 节点被点击时的回调
        nodeClick(node) {
            this.$emit('nodeClick', node);
        },
        /* 设置树的最大高度*/
        setMaxHeight(value) {
            if (!this.isShowPanelTitle) {
                value -= 61;
            }
            this.$refs.treeSelect.$el.style.setProperty('max-height', `${value}px`);
        },
    },
    watch: {
        tableMaxHeight(value) {
            this.setMaxHeight(value);
        },
    },
};
</script>
