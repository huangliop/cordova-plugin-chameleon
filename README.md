**这个分支是精简一些功能，以让其他项目通用**
 
**安装方法**

    cordova plugin add https://gitee.com/huangliop/chameleon-cordova-plugin.git

- Android版本使用说明

    android版本实现了将工程Assets目录下的www文件夹为web_lib.so.在第一次启动应用时，会自动解压web_lib.so到应用目录(/data/data/your.package.name/files/)。
在复制的过程如果你配置了SplaseScreen的话,界面就会显示你配置的图片,如果没有配置则会是白屏.
同时，启动图片推荐使用白色背景。
- iOS版本使用说明

    iOS版本在运行的时候会把H5的文件拷贝到应用的Documents/www目录下，以便以后覆盖更新H5的源码。

        
    如果想添加启动显示页,请在config.xml根节点下添加 
```
    <preference name="SplashScreen" value="loading" /> 
```
    loading为图片的名称,且必须是在drawable而不是mipmap下面.

    **在PC上打包主要事项**

        windonw下需要依赖7z来压缩目录,所以请提前安装7z，并将其可执行文件配置到环境变量中。

*** 主要功能介绍 ***

    1. removeWebviewCache:清除应用的浏览器缓存,主要应用在更新后,使更新内容被从新加载进浏览器.  
```
navigator.ChameleonUtil.removeWebviewCache(true);//true:退出应用;false:不退出应用
``` 