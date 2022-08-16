/* WebStorm设置webpack配置文件，处理不识别@路径问题*/
const path = require('path');

function resolve(dir) {
    return path.join(__dirname, '.', dir);
}

module.exports = {
    context: path.resolve(__dirname, './'),
    resolve: {
        alias: {
            '@': resolve('src'),
            '@assets': resolve('src/assets'),
            '@components': resolve('src/components'),
        },
    },
};
