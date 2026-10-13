# FreedomPlus

[![](https://img.shields.io/github/v/release/Kuoopllio/FreedomPlus)](https://github.com/Kuoopllio/FreedomPlus/releases)  [![](https://img.shields.io/github/downloads/Kuoopllio/FreedomPlus/total?color=g)](https://github.com/Kuoopllio/FreedomPlus/releases)

依赖于抖音运行的开源 Xposed 模块

----



## 下载安装

**[⬇️ 点此前往 Releases 下载最新 APK](https://github.com/Kuoopllio/FreedomPlus/releases/latest)**

1. 手机需为 **arm64-v8a（64 位）**
2. 安装下载到的 `app-debug.apk`（debug 签名，LSPosed 可直接加载）
3. 在 LSPosed 里启用 **Freedom+**，作用域勾选**抖音**
4. 强停抖音后重新打开
5. 进入模块设置，打开需要的开关（「视频/图文/音乐下载」「评论区图片保存」等**默认是关闭的**）

> APK 由 GitHub Actions 自动构建，构建日志见 [Actions](https://github.com/Kuoopllio/FreedomPlus/actions) 页面。



## 功能介绍

- 视频无水印下载
- 评论区静态图保存
- 表情包保存（⚠️ 抖音 16.4.15 不支持，见下方「已知限制」）
- 顶部 Tab 栏自定义隐藏



## 支持版本

| 抖音版本 | 包名 | 状态 |
| --- | --- | --- |
| 24.4.0、24.5.0 | `com.ss.android.ugc.aweme` | 支持 |
| 16.4.15（Google Play 版） | `com.ss.android.ugc.aweme.mobile` | 支持（不含表情包保存） |

### 已知限制

- **抖音 16.4.15 不支持表情包保存**：该版本抖音已移除整个 emoji 模块（`emoji.model.Emoji`、`EmojiDetailDialogNew`、`EmojiBottomSheetDialog`），没有可挂载的入口，不是适配遗漏。
- **「隐藏顶部 Tab」的关键字需要自己填**：默认值是「探索, 商城」，抖音 16.4.15 的 Tab 名称可能不同，请在模块设置里改成该版本实际的 Tab 名称。



## 重要说明

- 本项目作为一个工具，代码开源，供开发者学习参考使用；
- 开发人员可能会在任何时间**停止更新**或**删除项目**源码，同时也欢迎 issues、pull requests；
- **请、并且：禁止用于非法用途，否则将立即停止本项目的更新**
