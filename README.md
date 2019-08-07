<h1 align="center">Welcome to Shadowsocks Gost Plugin 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-2.8.1-blue.svg?cacheSeconds=2592000" />
  <a href="https://github.com/xausky/ShadowsocksGostPlugin">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" target="_blank" />
  </a>
</p>

> [Gost](https://github.com/ginuerzh/gost) 的 [Shadowsocks安卓](https://github.com/shadowsocks/shadowsocks-android) 插件，可以直接在Shadowsocks安卓客户端上连接 Gost 服务器

### 🏠 [Homepage](https://github.com/xausky/ShadowsocksGostPlugin)

## Install

根据CPU构架下载Release内预编译好的APK安装到设备，如果不清楚构架可以下载Universal版本

## Usage

* 在Shadowsocks客户端选中本插件，即可在参数配置除了-L 参数之为的 gost 参数以连接远程服务器或者多段代理。
* -L 参数会自动添加，只需要在Shadowsocks界面配置`rc4-md5`加密和`gost`密码即可
* gost的参数配置里面可以用`#SS_HOST`和`#SS_PORT`代替Shadowsocks配置的主机和端口
* 注意使用#SS_HOST参数会先对填写的主机名进行DNS解析后才传递，如果是与主机名相关的远程协议比如ws协议必须之间在参数里面配置
* 在参数里面配置的域名会忽略手机系统的DNS配置固定使用114.114.114.114

## Author

* Github: [@xausky](https://github.com/xausky)
* BiliBili: [@xausky](https://space.bilibili.com/8419077)

## 🤝 Contributing

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/xausky/ShadowsocksGostPlugin/issues).

## Show your support

Give a ⭐️ if this project helped you!

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_