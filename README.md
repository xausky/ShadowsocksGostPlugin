# ShadowsocksGostPlugin
Gost 的 Shadowsocks 安卓插件，可以直接在Shadowsocks安卓客户端上连接 Gost 服务器

## 使用方法

* 在Shadowsocks客户端选中本插件，即可在参数配置除了-L 参数之为的 gost 参数以连接远程服务器或者多段代理。
* -L 参数会自动添加，只需要在Shadowsocks界面配置`chacha20`加密和`ss123456`密码即可
* gost的参数配置里面可以用`#SS_HOST`和`#SS_PORT`代替Shadowsocks配置的主机和端口
* 注意使用#SS_HOST参数会先对填写的主机名进行DNS解析后才传递，如果是与主机名相关的远程协议比如ws协议必须之间在参数里面配置
* 在参数里面配置的域名会忽略手机系统的DNS配置固定使用114.114.114.114
