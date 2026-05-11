# AI吐槽日记 - 项目说明

## 👋 这是个啥？

一个能让你对着手机吐槽，然后AI自动把怨气变成搞笑段子的App。

你受了一天气 → 对着手机骂几句 → AI一键生成吐槽文案 → 分享朋友圈笑死所有人 → 裂变 🚀

## 🚀 快速开始

1. 用 **Android Studio** 打开这个文件夹
2. 等 Gradle 同步完
3. 跑起来！

> 如果 Gradle Wrapper 有问题，在 Android Studio 里 `File → Sync Project with Gradle Files` 它就会自动生成。

## ⚙️ 配置 AI

打开 App → 设置页面 → 填入：

- **API 地址**：比如 `https://api.moonshot.cn/v1`
- **模型名称**：比如 `moonshot-v1-8k`
- **API Key**：你自己的 Key

支持的 AI 服务（只要是 OpenAI 兼容接口就行）：
- OpenAI (GPT-3.5/GPT-4)
- Moonshot / Kimi（便宜好用）
- DeepSeek（超便宜）
- 智谱 AI (GLM)
- 通义千问
- 百川

## 💰 变现思路

- **免费版**：每天3次吐槽
- **会员版**：每月6块，无限吐槽+更多风格
- **广告**：在日记列表里插原生广告（AdMob）
- **定制风格商店**：卖出圈了的风格模板

## 🏗️ 技术栈

- Kotlin + Jetpack Compose
- Room 本地数据库
- Retrofit + OkHttp 网络请求
- DataStore 偏好设置
- Material 3 暗黑主题
