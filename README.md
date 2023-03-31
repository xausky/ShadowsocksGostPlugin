<h1 align="center">Looking for maintainer 本项目正在寻求接手维护者</h1>

<h1 align="center">Welcome to Shadowsocks Gost Plugin</h1>

## 本项目已经停止维护，建议使用这个 [fork](https://github.com/segfault-bilibili/ShadowsocksGostPlugin)
## This project has stopped maintenance, it is recommended to use this [fork](https://github.com/segfault-bilibili/ShadowsocksGostPlugin)

<p>
  <img alt="GitHub Workflow Status" src="https://img.shields.io/github/workflow/status/xausky/ShadowsocksGostPlugin/Android CI">
  <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/xausky/ShadowsocksGostPlugin">
  <img alt="GitHub All Releases" src="https://img.shields.io/github/downloads/xausky/ShadowsocksGostPlugin/total">
  <img alt="GitHub issues" src="https://img.shields.io/github/issues/xausky/ShadowsocksGostPlugin">
  <img alt="GitHub closed issues" src="https://img.shields.io/github/issues-closed/xausky/ShadowsocksGostPlugin">
</p>

> [Gost](https://github.com/ginuerzh/gost) 的 [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android) 插件，可以直接在Shadowsocks安卓客户端上连接 Gost 服务器

> [Gost](https://github.com/ginuerzh/gost) Plugin for [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android), which allows directly connecting to a Gost server from Shadowsocks-Android client

## 🚀 安装 Install

下载 [Release](https://github.com/xausky/ShadowsocksGostPlugin/releases) 内预编译好的APK安装到设备，同时也要安装 [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android)

Download prebuilt APK here [Release](https://github.com/xausky/ShadowsocksGostPlugin/releases) and then install it to the device, at same time [Shadowsocks Android](https://github.com/shadowsocks/shadowsocks-android) is required to be installed as well

## 🔧 使用 Usage

* 在 Shadowsocks 客户端选中本插件，即可在参数配置除了 `-L` 参数之外的 Gost 参数以连接远程服务器或者多段代理
* `-L` 参数会自动添加，只需要在 Shadowsocks 界面配置 `none` 无加密和空密码即可
* Gost 的命令行参数配置里面可以用`#SS_HOST`和`#SS_PORT`代替 Shadowsocks 配置的主机和端口

* Pick this plugin in Shadowsocks client, then Gost can be configured (except `-L` parameter) to connect to a remote server, or multi-hop proxies
* `-L` will be automatically added, just configure Shadowsocks to use `none` encryption and empty password
* In Gost's command-line parameters, `#SS_HOST`和`#SS_PORT` represents host and port configured in Shadowsocks

## ❗ 注意 Notices

* ~使用#SS_HOST参数会先对填写的主机名进行DNS解析后才传递~ 这应该是旧版Shadowsocks-Android才会存在的问题
* ~如果是与主机名相关的远程协议比如ws协议必须直接在参数里配置域名~ 同上
* 在参数里面配置的域名会忽略手机系统的DNS配置，默认使用 Public DNS+解析
* ~如果插件参数里面使用 `-F=` 形式的参数传递则后续参数不能含有 `=` 号，推荐使用 `-F ` 形式代替~ 使用新版配置格式（CFGBLOB）即可避开这个问题

* ~Host specified by #SS_HOST will be firstly resolved with DNS before being passed on~ Should be a problem specific to older versions of Shadowsocks-Android
* ~If the hostname is tied to the protocol, like WebSocket (ws), you must directly use domain name in configuration parameters~ Same as above
* The domain name(s) appeared in configuration parameters is/are resolved with Public DNS+ by default, in other words, ignoring the OS's DNS configurations
* ~If you configure a parameter in the form of `-F=`, then the subsequent parameters can no longer contain `=`, so it's recommended to use the form of `-F ` instead~ This issue can be avoided simply by using new config format (CFGBLOB)

## ❤ 关注我 Follow me

* Github: [@xausky](https://github.com/xausky)
* BiliBili: [@xausky](https://space.bilibili.com/8419077)

## 🤝 贡献 Contribution

欢迎各种问题，需求，BUG报告和代码PR!<br />提交到这里就可以 [问题页面](https://github.com/xausky/ShadowsocksGostPlugin/issues).

Any kind of questions, feature requests, bug reports or pull requests are welcomed!<br />Simply submit it here [issues](https://github.com/xausky/ShadowsocksGostPlugin/issues).

### ⭐ 如果这个项目帮到你的话欢迎点个星 If you feel this project can help you, you are welcomed to tick a star
