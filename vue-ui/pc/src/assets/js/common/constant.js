const constant = {};

// 系统环境
constant.environment = process.env.NODE_ENV === 'production';
// 服务器域名
constant.domainName = process.env.VUE_APP_DOMAIN_NAME;
// 公共接口地址
constant.apiUrl = constant.domainName + 'apis/';
// 本地文件服务器地址
constant.viewLocalUrl = constant.domainName + 'view/local/';
// FTP文件服务器地址
constant.viewFtpUrl = constant.domainName + 'view/ftp/';
// FastDfs文件服务器地址
constant.viewFastDfsUrl = constant.domainName + 'view/fdfs/';
// 私人密匙value值
constant.secretKey = '931851631';
// AES2加密密匙
constant.secretKeyAES = 'o1c2e3a4n5s6o7ft';
// 管理员
constant.admin = 'admin';
// 微信测试公众号的appId
constant.appId = 'wx7a9eb3c9029e9e51';
// 微信测试公众号的appSecret
constant.appSecret = 'ee885ac3cf1e104f5c9fe2ea8ce888d5';
// 腾讯地图组件的开发密匙（https://lbs.qq.com/dev/console/key/manage）
constant.mapKey = 'KV7BZ-W2G3P-SGADM-VAK2K-DFCRZ-LRFBD';
// 腾讯地图组件的调用来源（目前测试乱填无影响）
constant.referer = 'referer';

export default constant;
