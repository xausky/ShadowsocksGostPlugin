<h1 align="center">Welcome to Shadowsocks Gost Plugin</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-2.8.1-blue.svg?cacheSeconds=2592000" />
  <a href="https://github.com/xausky/ShadowsocksGostPlugin">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" target="_blank" />
  </a>
</p>

> [Gost](https://github.com/ginuerzh/gost) 的 [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android) 插件，可以直接在Shadowsocks安卓客户端上连接 Gost 服务器

## 🚀 安装

下载 [Release](https://github.com/xausky/ShadowsocksGostPlugin/releases) 内预编译好的APK安装到设备，同时也要安装 [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android)

## 🔧 使用

* 在Shadowsocks客户端选中本插件，即可在参数配置除了-L 参数之外的 gost 参数以连接远程服务器或者多段代理
* -L 参数会自动添加，只需要在Shadowsocks界面配置`rc4-md5`加密和`gost`密码即可
* Gost 的参数配置里面可以用`#SS_HOST`和`#SS_PORT`代替Shadowsocks配置的主机和端口

## ❗ 注意

* 使用#SS_HOST参数会先对填写的主机名进行DNS解析后才传递
* 如果是与主机名相关的远程协议比如ws协议必须直接在参数里配置域名
* 在参数里面配置的域名会忽略手机系统的DNS配置固定使用 Public DNS+
* 如果插件参数里面使用 `-F=` 形式的参数传递则后续参数不能含有 `=` 号，推荐使用 `-F ` 形式代替

## ❤ 关注我

* Github: [@xausky](https://github.com/xausky)
* BiliBili: [@xausky](https://space.bilibili.com/8419077)

## 🤝 贡献

欢迎各种问题，需求，BUG报告和代码PR!<br />提交到这里就可以 [问题页面](https://github.com/xausky/ShadowsocksGostPlugin/issues).

### ⭐ 如果这个项目帮到你的话欢迎点个星