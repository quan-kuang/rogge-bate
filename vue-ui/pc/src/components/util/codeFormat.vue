<template>
    <div class="code-format">
        <panel-title/>
        <el-tabs type="border-card">
            <el-tab-pane v-for="(item, index) in labelList" :key="index" :label="item.toUpperCase()+'解析'">
                <textarea class="code-textarea" :ref="item" v-model="textareaList[index]"/>
                <div class="button-box">
                    <el-button type="primary" @click="codeFormat(index,'min')">压缩</el-button>
                    <el-button type="success" @click="codeFormat(index)">格式化</el-button>
                    <el-button type="info" @click="copy($event, index)">复制代码</el-button>
                </div>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<!--suppress JSUnresolvedFunction, ThisExpressionReferencesGlobalObjectJS, NpmUsedModulesInstalled, JSUnusedLocalSymbols -->
<script>
import copy from '@assets/js/mixin/copy';
import common from '@assets/js/mixin/common';
import vkbeautify from 'vkbeautify';
// 核心组件和样式
import CodeMirror from 'codemirror/lib/codemirror';
import 'codemirror/lib/codemirror.css';
// 引入主题
import 'codemirror/theme/idea.css';
// 语言模型
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/javascript/javascript';

export default {
    name: 'codeFormat',
    mixins: [common, copy],
    data() {
        return {
            options: {
                theme: 'idea',
                mode: 'application/json',
            },
            labelList: ['json', 'xml', 'sql', 'css'],
            textareaList: [],
            codeMirrorMap: {},
            vkBeautify: vkbeautify,
        };
    },
    mounted() {
        this.setHeight();
    },
    methods: {
        /* 格式化代码*/
        codeFormat(index, type) {
            let self = this;
            const name = self.labelList[index];
            const methodName = name + (type || '');
            const ref = self.$refs[name][0];
            // 校验编辑器是否已经创建
            const source = ref.parentNode.getElementsByClassName('CodeMirror');
            if (source && source.length > 0) {
                const codeMirror = self.codeMirrorMap[name];
                const value = self.format(methodName, codeMirror.getValue());
                self.textareaList[index] = value;
                codeMirror.setValue(value);
                return;
            }
            // 判断输入为空
            const input = self.textareaList[index];
            if (!input || !input.trim()) {
                return;
            }
            // 获取格式化后的代码
            const value = self.format(methodName, input.trim());
            self.textareaList[index] = value;
            // 创建编辑器
            const codeMirror = CodeMirror.fromTextArea(ref, self.options);
            // 设置编辑器样式
            const obj = ref.parentNode.getElementsByClassName('CodeMirror')[0];
            const height = document.getElementsByClassName('code-textarea')[0].style.height;
            obj.setAttribute('style', `width: 100%;height: ${height};border-radius: 4px;border: 1px solid #DCDFE6;`);
            // 处理线上莫名其妙的问题
            $(obj).children('.CodeMirror-scroll').css('max-height', '100%');
            // 编辑器赋值
            codeMirror.setValue(value);
            // 编辑器聚焦事件
            codeMirror.on('focus', function () {
                obj.style.border = '1px solid #409EFF';
            });
            // 编辑器失焦事件
            codeMirror.on('blur', function () {
                obj.style.border = '1px solid #DCDFE6';
            });
            // 编辑器在输入或粘贴时销毁
            codeMirror.on('inputRead', function () {
                self.textareaList.splice(index, 1, codeMirror.getValue());
                delete self.codeMirrorMap[name];
                codeMirror.toTextArea();
            });
            self.codeMirrorMap[name] = codeMirror;
        },
        /* 美化/压缩代码*/
        format(name, value) {
            try {
                // eslint-disable-next-line no-eval
                return eval('this.vkBeautify[name](value)');
            } catch (e) {
                return e.message;
            }
        },
        /* 复制文本*/
        copy(event, index) {
            this.hbdtwx(event, this.textareaList[index], this.labelList[index]);
        },
        /* 文本框高度设置*/
        setHeight() {
            const height = this.tableMaxHeight - 31 + 'px';
            for (let element of document.getElementsByClassName('code-textarea')) {
                element.style.height = height;
            }
            for (let element of document.getElementsByClassName('CodeMirror')) {
                element.style.height = height;
            }
        },
    },
    watch: {
        tableMaxHeight(newValue) {
            this.setHeight();
        },
    },
};
</script>

<style lang="scss">
    .code-format .el-tabs__item {
        font-weight: bold;
    }
</style>

<style scoped lang="scss">
    .code-format {
        .el-tabs__item {
            height: 50px;
            line-height: 50px;
            font-size: large;
            font-weight: bold;
        }

        .button-box {
            margin-top: 10px;
            margin-bottom: 10px;
        }

        .code-textarea {
            resize: none;
            padding: 0;
            width: 100%;
            min-height: 300px;
            border: 1px solid #DCDFE6;
            border-radius: 4px;
            margin-bottom: -4px;
        }

        .code-textarea:focus {
            outline: 0;
            border-color: #409EFF;
        }
    }
</style>
