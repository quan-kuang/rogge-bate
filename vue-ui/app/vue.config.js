// noinspection NpmUsedModulesInstalled
const webpack = require('webpack');
const path = require('path');
const CompressionWebpackPlugin = require('compression-webpack-plugin');
const productionGzipExtensions = ['js', 'css'];

function resolve(dirname) {
    return path.join(__dirname, dirname);
}

module.exports = {
    /* 部署生产环境和开发环境下的URL*/
    publicPath: process.env.NODE_ENV === 'production' ? '/app' : '/',
    /* 输出文件目录：在npm run build时，生成文件的目录名称*/
    outputDir: 'dist',
    /* 放置生成的静态资源(js、css、img、fonts)的(相对于outputDir的)目录*/
    assetsDir: 'assets',
    /* 是否在构建生产包时生成 sourceMap 文件，false将提高构建速度*/
    productionSourceMap: false,
    /* 默认情况下，生成的静态资源在它们的文件名中包含了 hash 以便更好的控制缓存，你可以通过将这个选项设为 false 来关闭文件名哈希。(false的时候就是让原来的文件名不改变)*/
    filenameHashing: false,
    /* 代码保存时进行eslint检测*/
    lintOnSave: false,
    /* 配置webpack插件*/
    configureWebpack: {
        /* 设置路径别名*/
        resolve: {
            alias: {
                '@': resolve('src'),
                '@assets': resolve('src/assets'),
                '@components': resolve('src/components'),
            },
        },
        /* 设置插件*/
        plugins: [
            /* 创建一个全局的变量*/
            new webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery',
            }),
            /* 配置compression-webpack-plugin压缩*/
            new CompressionWebpackPlugin({
                /* 压缩算法*/
                algorithm: 'gzip',
                /* 处理所有匹配此 {RegExp} 的资源*/
                test: new RegExp('\\.(' + productionGzipExtensions.join('|') + ')$'),
                /* 只处理比这个值大的资源，按字节计算*/
                threshold: 10240,
                /* 只有压缩率比这个值小的资源才会被处理*/
                minRatio: 0.8,
                /* 是否删除源文件*/
                deleteOriginalAssets: false,
            }),
            /* 忽略解析依赖中没有用到的插件*/
            new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),
        ],
    },
    /* dev-server相关配置、tomcat下发布改用nginx配置*/
    devServer: {
        open: false,
        host: 'loyer.com',
        port: 1001,
        https: true,
        hotOnly: true,
        disableHostCheck: true,
        /* history模式设置*/
        historyApiFallback: true,
        /* 代理设置*/
        proxy: {
            '/apis': {
                /* 设置服务器调用的接口域名和端口号*/
                target: 'https://loyer.wang/',
                /* 允许跨域*/
                changOrigin: true,
                /* 是否验证SSL*/
                secure: false,
                /* 路径重写: 如果请求的路径中包含apis，则自动触发路径重写*/
                pathRewrite: {
                    '^/apis': '/apis',
                },
            },
        },
    },
};
