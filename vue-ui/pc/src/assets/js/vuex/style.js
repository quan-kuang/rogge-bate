import storage from '@assets/js/config/storage';
import watermark from '@assets/js/util/watermark';

/* 获取缓存的主题样式*/
function getStyle() {
    return storage.getItem('style') || state;
}

/* 设置缓存的主题样式*/
function setStyle(style) {
    state.isShowWatermark = style.isShowWatermark;
    state.isShowTabs = style.isShowTabs;
    state.isShowPanelTitle = style.isShowPanelTitle;
    state.tableMaxHeight = style.tableMaxHeight;
    storage.setItem('style', style);
}

/* 获取表格最大高度*/
function getHeight() {
    return document.body.clientHeight - 202 - 35 - 61;
}

/* 设置表格最大高度*/
function setHeight(style, bool, value) {
    if (bool) {
        style.tableMaxHeight -= value;
    } else {
        style.tableMaxHeight += value;
    }
}

const state = {
    // 是否显示水印
    isShowWatermark: true,
    // 是否显示导航标签
    isShowTabs: true,
    // 是否显示面板标签
    isShowPanelTitle: true,
    // 表格最大高度
    tableMaxHeight: getHeight(),
};

const getters = {};

const mutations = {
    /* 设置水印是否显示*/
    setIsShowWatermark(state, value) {
        const style = getStyle();
        style.isShowWatermark = value;
        setStyle(style);
    },
    /* 设置导航标签是否显示*/
    setIsShowTabs(state, value) {
        const style = getStyle();
        style.isShowTabs = value;
        setHeight(style, value, 35);
        setStyle(style);
    },
    /* 设置面板标签是否显示*/
    setIsShowPanelTitle(state, value) {
        const style = getStyle();
        style.isShowPanelTitle = value;
        setHeight(style, value, 61);
        setStyle(style);
    },
    /* 设置表格最大高度*/
    setTableMaxHeight() {
        const style = getStyle();
        style.tableMaxHeight = document.body.clientHeight - 202;
        setHeight(style, style.isShowTabs, 35);
        setHeight(style, style.isShowPanelTitle, 61);
        setStyle(style);
    },
};

const actions = {
    /* 设置缓存的主题样式*/
    setStyle() {
        setStyle(getStyle());
    },
    /* 重置默认的主题样式*/
    resetStyle() {
        const style = {
            isShowWatermark: true,
            isShowTabs: true,
            isShowPanelTitle: true,
            tableMaxHeight: getHeight(),
        };
        setStyle(style);
        watermark.deleteWatermark();
    },
};

export default {
    state,
    getters,
    mutations,
    actions,
};
