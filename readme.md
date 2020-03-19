# 小程序 Demo

主要使用了 React Native 的动态化特性，将 bundle 文件下载的手机 sdcard，然后将集成到 native 项目里的 rn 容器去加载 bundle 文件

### 配置环境

* 内部开发同事，快速配置

```shell
// React Native CLI Quickstart 
// macOS

$ brew install node
$ brew install watchman

// 假设你的 Android 开发环境已经 OK
// 检查你的环境变量配置

export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

```

* 详细配置流程，参考官方文档

(https://reactnative.dev/docs/getting-started)[https://reactnative.dev/docs/getting-started]

### 运行项目

```shell
$ git clone git@github.com:phonechan/rn_add_to_native.git
$ cd rn_add_to_native

// 安装依赖，会下载到 node_modules/ 中
$ yarn install

// 至此，目录结构如下

├── android // Android 原生项目，AS 中直接打开这个就行
├── index.js // RN 入口源码文件，根据这个入口可以编译成 bundle，不重要，后面反正从服务端下载
├── node_modules // RN 第三方依赖库合集（包含 Android 的依赖，如：implementation project(':react-native-vector-icons')）
├── package-lock.json // npm lock
├── package.json // 包管理文件
├── readme.md
└── yarn.lock // yarn lock
// npm & yarn 都是包管理工具，RN 以前用 npm ，现在推荐用 yarn

// 用 Android Studio 打开 android/ 目录下的项目，编译运行，就跟原生开发一样的操作

```

### 其他

* 调试阶段，可以先手动添加小程序到 sdcard 

* 一些命令行

```shell

// 项目根目录运行 rn 项目(非 android/ 项目)
$ react-native run-android // debug apk
$ react-native run-android --variant=release // release apk

// 打包 bundle
$ react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/bonus/src/main/assets/index.android.bundle --assets-dest android/bonus/src/main/res/
（1）--entry 入口js文件，android系统就是index.android.js，ios系统就是index.ios.js
（2）--bundle-output 生成的bundle文件路径
（3）--platform 平台
（4）--assets-dest 图片资源的输出目录
（5）--dev 是否为开发版本，打正式版的安装包时我们将其赋值为false

// 打包出来的 bundle 产物大致如下
.
├── drawable-hdpi // 资源文件
├── drawable-mdpi
├── drawable-xhdpi
├── drawable-xxhdpi
├── drawable-xxxhdpi
└── index.android.bundle // 压缩的 JS 代码

// 将当前路径下所有的文件和目录推到手机
$ adb push ./ /sdcard/cil_react/mini02/

```


