<template>
    <div>
        <template v-if="!readonly">
            <el-input placeholder="请输入关键字" v-model="filterValue" clearable/>
            <br/>
            <el-checkbox v-model="isCheckAll" @change="checkAll">全选</el-checkbox>
            <i class="el-icon-minus" @click="allExpand(false)">&emsp;折叠</i>
            <i class="el-icon-delete" @click="reset">&emsp;重置</i>
        </template>
        <el-tree ref="tree" node-key="uuid" :style="treeStyle" :data="data" :props="props"
                 :check-strictly="checkStrictly" :default-expanded-keys="defaultKeys"
                 :filter-node-method="filterNode" :highlight-current="true" :show-checkbox="true"/>
    </div>
</template>

<script>
import {mapState} from 'vuex';

export default {
    name: 'tree',
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
        readonly: {
            type: Boolean,
            default: () => {
                return false;
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
    data() {
        return {
            filterValue: '',
            isCheckAll: false,
        };
    },
    mounted() {
        this.setMaxHeight(this.tableMaxHeight);
    },
    methods: {
        /* 关键字过滤*/
        filterNode(value, data) {
            if (!value) {
                return true;
            }
            return data[this.props.label].indexOf(value) !== -1;
        },
        /* 全/反选选*/
        checkAll() {
            const data = this.isCheckAll ? this.data : [];
            if (this.checkStrictly) {
                this.setCheckedKeys(data, false);
            } else {
                this.setCheckedNodes(data);
            }
        },
        /* 重置清空选中节点*/
        reset() {
            this.isCheckAll = false;
            this.checkAll();
        },
        /* 设置选中节点*/
        setCheckedNodes(nodes) {
            this.$refs.tree.setCheckedNodes(nodes);
        },
        /* 通过keys设置目前勾选的节点*/
        setCheckedKeys(data, leafOnly) {
            // 默认false，true仅设置叶子节点的选中状态
            leafOnly = leafOnly === null ? false : leafOnly;
            // 传入数组长度为1并且包含子节点判定为树形结构，需要格式化处理
            const isTreeData = data.length === 1 && data[0].hasOwnProperty('children');
            const keys = isTreeData ? this.getKeysByTree(data) : data;
            this.$refs.tree.setCheckedKeys(keys, leafOnly);
        },
        /* 从树形数据中获取所有节点的key*/
        getKeysByTree(treeData) {
            let stack = JSON.parse(JSON.stringify(treeData));
            let result = [];
            while (stack.length > 0) {
                let pop = stack.pop();
                let children = pop.children;
                if (children) {
                    for (let item of children) {
                        stack.push(item);
                    }
                }
                result.push(pop.uuid);
            }
            return result;
        },
        /* 获取选中节点ID*/
        getCheckedKeys() {
            let checkedKeys = this.$refs.tree.getCheckedKeys();
            if (this.checkStrictly) {
                return checkedKeys;
            }
            for (let item of checkedKeys) {
                const node = this.$refs.tree.getNode(item);
                if (node.childNodes.length === 0) {
                    const parentKes = this.getParentKes(node, []);
                    checkedKeys = checkedKeys.concat(parentKes);
                }
            }
            const set = new Set(checkedKeys);
            return Array.from(set);
        },
        /* 获取父节点*/
        getParentKes(node, keys) {
            if (node.level > 1) {
                keys.push('@' + node.parent.data[this.props.value]);
                this.getParentKes(node.parent, keys);
            }
            return keys;
        },
        /* 展开或收缩所有节点*/
        allExpand(isExpand) {
            this.data.forEach((item) => {
                this.$refs.tree.store.nodesMap[item[this.props.value]].expanded = isExpand;
            });
        },
        /* 设置所有未选中的禁用*/
        setDisable(tree) {
            tree = tree || this.data;
            const checkedKeys = this.$refs.tree.getCheckedKeys();
            for (let item of tree) {
                item.disabled = !checkedKeys.includes(item.uuid);
                if (item.hasOwnProperty('children')) {
                    this.setDisable(item.children);
                }
            }
        },
        /* 设置树的最大高度*/
        setMaxHeight(value) {
            if (!this.isShowPanelTitle) {
                value -= 61;
            }
            this.$refs.tree.$el.style.setProperty('--max-height', `${value}px`);
        },
    },
    watch: {
        filterValue(value) {
            this.$refs.tree.filter(value);
        },
        tableMaxHeight(value) {
            this.setMaxHeight(value);
        },
    },
};
</script>

<style scoped lang="scss">
    .el-checkbox__input .el-checkbox__inner {
        height: 16px;
        width: 16px;
    }

    .el-checkbox__label {
        font-size: 16px;
    }

    .el-tree {
        background-color: var(--main-base);
        height: var(--max-height);
        overflow-x: auto;

        .el-tree-node {
            margin-top: 5px;
        }

        .el-checkbox__inner {
            height: 16px;
            width: 16px;
        }

        .el-tree-node__label {
            font-size: 16px;
        }
    }

    .el-button {
        margin-top: 10px;
    }

    i {
        margin-left: 5%;
        cursor: pointer;
        color: #606266;
    }
</style>
