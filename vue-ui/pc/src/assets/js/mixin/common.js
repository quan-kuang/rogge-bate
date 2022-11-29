import popup from '@assets/js/mixin/popup';
import {selectDict} from '@assets/js/api/dict';
import {download} from '@assets/js/util/download';
import {ADD, ALTER, DELETE, DOWNLOAD, EXECUTE, LIST, RESET} from '@assets/js/common/action';
import {mapGetters, mapState} from 'vuex';
import {panelTitle} from '@assets/js/common/components';
import global from '@assets/js/util/global';

const copy = {
    mixins: [popup],
    components: {
        panelTitle,
    },
    computed: {
        ...mapState({
            tableMaxHeight: (state) => state.style.tableMaxHeight,
        }),
        ...mapGetters(['isHasAllPermissions']),
    },
    data() {
        return {
            // 新增
            ADD: ADD,
            // 编辑
            ALTER: ALTER,
            // 列表
            LIST: LIST,
            // 删除
            DELETE: DELETE,
            // 下载
            DOWNLOAD: DOWNLOAD,
            // 执行
            EXECUTE: EXECUTE,
            // 重置
            RESET: RESET,
            // 行为
            action: LIST,

            // 结果集总条数
            dataTotal: 0,
            // 分页结果
            dataList: [],
            // 当前页
            pageNum: 1,
            // 页数大小
            pageSize: 10,
            // 分页数
            pageSizes: [10, 25, 50, 100],
            // 选中行
            selected: [],
            // 查询条件
            queryText: '',

            // 删除操作提示
            deleteHint: '确认删除选中项？',
        };
    },
    methods: {
        /* 页面跳转*/
        nextStep(action) {
            this.params.item = this.selected.length > 0 ? global.deepClone(this.selected[this.selected.length - 1]) : null;
            this.$emit('nextStep', action);
        },
        /* 格式化时间*/
        formatDate(row, column, cellValue) {
            return !cellValue ? '' : cellValue.toDate();
        },
        /* 格式化时间*/
        formatDate2(row, column, cellValue) {
            return !cellValue ? '' : cellValue.toDate('yyyy-mm-dd hh:MM');
        },
        /* 选中行变更*/
        selectionChange(selection) {
            this.selected = selection;
        },
        /* 设置选中行*/
        setSelected(row, action) {
            // 先清除所有复选状态
            this.$refs.table.clearSelection();
            // 设置选中行
            this.$refs.table.toggleRowSelection(row);
            if (action === this.DELETE) {
                this.deleteConfirm(row);
            } else {
                this.nextStep(action);
            }
        },
        /* 更改分页大小*/
        sizeChange(pageSize) {
            this.pageSize = pageSize;
            this.selectEvent();
        },
        /* 更改当前页*/
        currentChange(pageNum) {
            this.pageNum = pageNum;
            this.selectEvent();
        },
        /* 删除确认*/
        deleteConfirm() {
            this.confirm(this.deleteHint, this.deleteEvent, null);
        },
        /* 查询字典信息*/
        selectDict(selector, parentId) {
            let self = this;
            return new Promise((resolve) => {
                const data = {
                    status: true,
                    parentId: parentId,
                };
                const loading = self.loadingOpen(selector);
                selectDict(data).then((response) => {
                    if (response.flag) {
                        resolve(response.data);
                    } else {
                        self.messageError(response.msg);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            });
        },
        /* 导出Excel*/
        exportExcel(name, selector, ref) {
            let self = this;
            // ref默认为table
            ref = ref || 'table';
            const table = self.$refs[ref];
            // 校验表数据是否为空
            const dataList = table.tableData;
            if (!dataList || dataList.length === 0) {
                self.messageError('暂无数据导出');
                return;
            }
            // 遍历出字段名
            let columns = {};
            table.columns.forEach((item) => {
                if (!!item.property) {
                    columns[item.property] = item.label;
                }
            });
            const data = {
                name: name,
                columns: columns,
                dataList: dataList,
            };
            const url = 'tools/util/exportExcel';
            const loading = self.loadingOpen(selector);
            download('post', url, data, 'xlsx').finally(() => {
                loading && loading.close();
            });
        },
    },
};

export default copy;
