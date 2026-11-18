# Windows 上 Codex 电脑操控不可用与 Chrome 扩展安装失败的排查

在 Windows 11 的 Microsoft Store 版 Codex 中，「设置 → 电脑操控」同时包含桌面应用操控和浏览器集成。桌面应用操控依赖 Codex 的 Computer Use 插件。使用 Chrome 的现有标签页和登录状态，还需要 Chrome 用户资料中的官方 ChatGPT 扩展，以及 Codex 和扩展之间的本地通信组件。

## 一、Computer Use 不可用

### 怎么判断问题出在哪

这次排查中，Codex 安装包已经包含 `computer-use`。用户侧缺少对应的插件副本、启用配置和本地插件市场记录。Windows 电脑操控环境变量也未设置。

当时的会话可以导入 `@oai/sky` 并列出 Windows 应用，说明运行时可以连接。旧诊断脚本还会查找 `computer-use-client.mjs`。新版改用 `@oai/sky` 时，应按当前版本的入口验证运行时，避免把旧文件缺失误判成运行时故障。

### 怎么修

1. 备份 Codex 的 `config.toml`。从当前已安装的 Codex 同步内置 `openai-bundled` 插件市场和 Computer Use 插件缓存。
2. 确认 `config.toml` 中的 `[features] computer_use = true` 和 `[plugins."computer-use@openai-bundled"] enabled = true` 已设置，且市场来源指向可读取的内置插件目录。
3. 如果当前 Windows 版本的诊断明确缺少对应环境变量，设置用户级 `CODEX_ELECTRON_ENABLE_WINDOWS_COMPUTER_USE=1`，然后重新启动桌面端。
4. 回到「设置 → 电脑操控」，确认「任意应用」可用。让 Codex 读取一个普通应用的窗口，验证实际操控链路。

排查时先保留现有 Windows 沙盒设置。没有证据表明切换沙盒档位是这类插件缺失故障的必要步骤。[Codex 权限文档](https://learn.chatgpt.com/docs/permissions)

## 二、Chrome 扩展装不上或一直显示未安装

### 先检查 Chrome，随后检查 Codex

- **选对浏览器和用户资料。** 桌面端的安装入口可能把网页交给 Windows 默认浏览器。直接用平时使用的 Google Chrome 用户资料打开官方安装页。
- **用 ID 核对扩展。** 在 Chrome 地址栏打开 `chrome://extensions`，打开扩展详情核对 ID。OpenAI 官方 ChatGPT 扩展的 ID 是 `hehggadaopoacecdllhhajmbjkdcmajg`。搜索结果里名称相近的扩展可能来自其他发布者。[官方扩展页面](https://chromewebstore.google.com/detail/chatgpt/hehggadaopoacecdllhhajmbjkdcmajg)
- **区分商店和下载服务。** 这次访问商品链接曾被重定向到空白商品页，Google 更新服务却可以返回官方 CRX。更新服务可达，只能证明下载端点有响应。相同商品页异常也有公开记录。[Codex 问题记录](https://github.com/openai/codex/issues/45589)

### 恢复连接并安装

1. 如果 Codex 侧缺少 `chrome@openai-bundled`，从当前安装包恢复内置 Chrome 插件及其缓存，再通过桌面端的设置流程恢复原生通信组件 `com.openai.codexextension`。
2. 在目标 Chrome 用户资料中打开官方商品页，选择「添加至 Chrome」，并在 Chrome 的原生弹窗中审阅、确认权限。
3. 安装完成后返回 Codex 的「设置 → 电脑操控」，确认 Google Chrome 一栏显示「管理」。仍显示「安装」时，重启 Chrome 和 Codex，再确认用的是刚安装扩展的用户资料。[OpenAI 扩展安装说明](https://learn.chatgpt.com/docs/chrome-extension)

在本次 Windows 环境中，把 `External Extensions` JSON 放进 Chrome 用户目录后没有安装成功。Google 对这种 JSON 文件方式的说明适用于 macOS 和 Linux。[Chrome 扩展分发文档](https://developer.chrome.com/docs/extensions/how-to/distribute/install-extensions)

如果设备由管理员管理，可以按组织规范评估 Chrome Enterprise 的 `ExtensionInstallForcelist` 策略。本次测试中，管理员策略通过 Google 更新服务安装了官方扩展。Chrome 此时会显示受管理状态。移除强装条目并重启后，策略安装的扩展随即被卸载。若要恢复普通安装，需要等商品页可用后重新从商店安装。[Chrome Enterprise 策略说明](https://chromeenterprise.google/policies/extension-install-forcelist/)

### 分别验证扩展与通信组件

在 Codex 内置 Chrome 插件的 `scripts` 目录运行：

~~~powershell
node check-extension-installed.js --browser chrome --json
node check-native-host-manifest.js --browser chrome --json
~~~

第一条应返回 `installed: true` 和 `enabled: true`，第二条应返回 `correct: true`。两条命令的退出码都应为 `0`。最后用 Codex 在 Chrome 中读取一个用于测试的标签页，验证端到端连接。
