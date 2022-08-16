<template>
    <div class="add">
        <panel-title title="新增菜单"/>
        <el-form ref="form" label-width="80px" :model="form" :rules="rules" lstatus-icon>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="菜单类型" prop="type" class="required">
                        <el-select v-model="form.type" placeholder="请选择菜单类型">
                            <el-option v-for="(item, index) in typeList" :key="index" :value="item.value" :label="item.text" :disabled="!item.status"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="上级目录" prop="parentId">
                        <el-select ref="parent" v-model="form.parentId" placeholder="请选择上级目录" filterable :filter-method="filterMethod" clearable @clear="clear">
                            <el-option style="background-color: white" :value="option.uuid" :label="option.label">
                                <tree-select ref="treeSelect" :data="menuDirTreeData" :defaultKeys="defaultKeys" @nodeClick="nodeClick"/>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="菜单排序" prop="sort">
                        <el-input v-model="form.sort" placeholder="请输入排序" maxlength="2" type="number" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="7">
                    <el-form-item label="菜单名称" prop="title">
                        <el-input v-model="form.title" placeholder="请输入菜单名称" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <template v-if="type!=='2'">
                    <el-col :span="7">
                        <el-form-item label="路由地址" prop="path">
                            <el-input v-model="form.path" placeholder="请输入路由地址" maxlength="36" clearable/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="组件路径" prop="url">
                            <el-input v-model="form.url" placeholder="请输入组件路径" maxlength="36" clearable :disabled="isDisabled"/>
                        </el-form-item>
                    </el-col>
                </template>
                <template v-else>
                    <el-col :span="7">
                        <el-form-item label="权限标识" prop="permission">
                            <el-input v-model="form.permission" placeholder="请输入权限标识" maxlength="36" clearable/>
                        </el-form-item>
                    </el-col>
                </template>
            </el-row>
            <el-row v-if="type!=='2'">
                <el-col :span="7">
                    <el-form-item label="菜单图标" prop="icon">
                        <el-select v-model="form.icon" placeholder="请选择菜单图标" clearable filterable>
                            <el-option v-for="(item,index) in iconfont.glyphs" :key="index" :label="item.name" :value="`${iconfont.font_family} ${iconfont.css_prefix_text}${item.font_class}`">
                                <i :class="`${iconfont.font_family} ${iconfont.css_prefix_text}${item.font_class}`"/> <span style="margin-left: 20%">{{ item.name }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="路由名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入路由名称" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="回调地址" prop="redirect">
                        <el-input v-model="form.redirect" placeholder="请输入回调地址" maxlength="36" clearable/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row v-if="type!=='2'">
                <el-col :span="7">
                    <el-form-item label="启用状态" prop="status" :required="true">
                        <el-switch v-model="form.status"/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="是否隐藏" prop="hide" :required="true">
                        <el-switch v-model="form.hide"/>
                    </el-form-item>
                </el-col>
                <el-col :span="7">
                    <el-form-item label="需要认证" prop="requireAuth" :required="true">
                        <el-switch v-model="form.requireAuth"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="20">
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark" maxlength="36"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item>
                <el-button type="success" @click="submit">保存</el-button>
                <el-button type="warning" @click="reset" :disabled="isDisabled">重置</el-button>
                <el-button type="primary" @click="goBack">返回</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import global from '@assets/js/util/global';
import mixin from '../js/mixin';
import {saveMenu} from '@assets/js/api/menu';

export default {
    name: 'alter',
    mixins: [mixin],
    props: ['params'],
    data() {
        return {
            form: {
                level: 1,
                uuid: null,
                type: '1',
                parentId: '',
                sort: null,
                title: '',
                url: '',
                path: '',
                icon: '',
                name: '',
                redirect: '',
                permission: '',
                status: true,
                requireAuth: false,
                hide: false,
                remark: '',
            },
        };
    },
    activated() {
        this.params.isFlush = false;
        this.menuDirTreeData = this.menuDirTree;
        const item = this.params.item;
        if (!item) {
            this.option = global.deepClone({uuid: 'root', label: '根节点'});
            this.form.parentId = 'root';
            this.form.type = '0';
            this.defaultKeys = [];
            this.$refs.treeSelect.setCurrentKey(null);
        } else {
            this.form.type = (Number(item.type) + 1).toString();
            this.option.uuid = item.uuid;
            this.option.label = item.title;
            this.form.parentId = item.uuid;
            this.form.level = item.level;
            this.defaultKeys = [item.uuid];
            this.$refs.treeSelect.setCurrentKey(item.uuid);
        }
    },
    methods: {
        /* 清空表单输入框*/
        reset() {
            this.$refs.form.resetFields();
            this.$refs.treeSelect.setCurrentKey([]);
        },
        /* 保存菜单信息*/
        saveMenu() {
            let self = this;
            const data = global.deepClone(self.form);
            data.uuid = global.getUuid();
            data.parentName = self.$refs.parent.selected.label;
            const loading = self.loadingOpen('.add');
            saveMenu(data).then((response) => {
                if (response.flag) {
                    self.params.isFlush = true;
                    self.confirm(`${response.msg}，继续添加？`
                        , function () {
                            self.reset();
                            self.form.type = data.type;
                        }, function () {
                            self.reset();
                            self.goBack();
                        });
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
    },
};
</script>

