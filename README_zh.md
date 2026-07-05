<h1 align="center">DuskByte</h1>
<h4 align="center">
    <p>
        <a href="./README.md">English</a> |
        <b>中文</b>
    </p>
</h4>

<p align="center">
  <a href="https://github.com/DuskByteDevelopment/DuskByte/actions"><img alt="构建" src="https://img.shields.io/badge/build-gradle-4c1?style=flat-square"></a>
  <a href="LICENSE"><img alt="许可证" src="https://img.shields.io/badge/license-Apache%202.0-blue?style=flat-square"></a>
  <img alt="Minecraft" src="https://img.shields.io/badge/minecraft-1.20.5+-orange?style=flat-square">
</p>

## 📌 项目简介
DuskByte 是一款基于 Fabric 的现代化 Minecraft 辅助客户端，专注于 Crystal PvP 并拥有先进的渲染系统。模块化架构，提供丰富的自定义选项。

## ✨ 功能模块

### ⚔️ Combat（战斗）
KillAura、CrystalAura、AutoTotem、Surround、Criticals（暴击）、Hitboxes（碰撞箱）、TriggerBot（自动攻击）、AutoWeapon（自动武器）、PacketMine（数据包挖掘）、MaceAura（重锤光环）等

### 🏃 Movement（移动）
ElytraFly（鞘翅飞行）、Velocity（反击退）、NoSlow（无减速）、Phase（穿墙）、Scaffold（自动搭路）、Spider（爬墙）、AirJump（空中跳跃）、Step（自动上台阶）等

### 🎮 Player（玩家）
AntiAFK（防离线）、AutoEat（自动进食）、AutoFish（自动钓鱼）、AutoTool（自动工具）、ChatSuffix（聊天后缀）、InvManager（背包管理）、FakePlayer（假人）等

### 🎨 Render（渲染）
ESP（透视）、Tracers（追踪线）、NameTags（名字标签）、Chams、Breadcrumbs（足迹）、Zoom（缩放）、Fullbright（全亮）、NoRender（移除渲染）、Hat（帽子）等

### 🔧 Misc（其他）
Timer（变速）、AntiSpam（防刷屏）、AutoReconnect（自动重连）

## 🖥️ 主菜单背景
- 黑洞 / Minecraft 世界 / 行星 / 星云 / 赛博富士山
- 由 GLSL 着色器驱动（兼容 Shadertoy 格式）

## 🌐 多语言支持
- 简体中文（完整汉化）
- English

## ⚙️ 构建与运行

```bash
# 构建模组
./gradlew build

# 运行客户端
./gradlew runClient
```

## 📝 许可证

本项目采用多许可证模式分发：

- **项目核心**: 遵循 [Apache License 2.0](LICENSE) 许可证
- **渲染系统**: 核心渲染组件（位于 `src/main/java/com/github/duskbyte/graphics/`）
  遵循 [MIT License](src/main/java/com/github/duskbyte/graphics/LICENSE) 许可证

---

版权所有 © 2026 DuskByteDevelopment.
