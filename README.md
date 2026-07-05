<h1 align="center">DuskByte</h1>
<h4 align="center">
    <p>
        <b>English</b> |
        <a href="./README_zh.md">中文</a>
    </p>
</h4>

<p align="center">
  <a href="https://github.com/DuskByteDevelopment/DuskByte/actions"><img alt="Build" src="https://img.shields.io/badge/build-gradle-4c1?style=flat-square"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-Apache%202.0-blue?style=flat-square"></a>
  <img alt="Minecraft" src="https://img.shields.io/badge/minecraft-1.20.5+-orange?style=flat-square">
</p>

## 📌 Overview
DuskByte is a modern Minecraft utility client built on Fabric with a focus on Crystal PvP and an advanced rendering system. Features a modular architecture with extensive customization options.

## ✨ Features
- **Combat Modules** — KillAura, CrystalAura, AutoTotem, Surround, Criticals, Hitboxes, TriggerBot, AutoWeapon, and more
- **Movement Modules** — ElytraFly, Velocity, NoSlow, Phase, Scaffold, Spider, AirJump, and more
- **Player Modules** — AntiAFK, AutoEat, AutoFish, AutoTool, ChatSuffix, InvManager, and more
- **Render Modules** — ESP, Tracers, NameTags, Chams, Breadcrumbs, Zoom, Fullbright, NoRender, and more
- **Misc Modules** — Timer, AntiSpam, AutoReconnect
- **Custom GUI** — Material Design 3 themed panel UI with full Chinese localization
- **Lumin Graphics System** — Custom rendering pipeline with TTF font support, blur effects, and shader backgrounds

## 🖥️ Main Menu Backgrounds
- Black Hole / Minecraft Blocks / Planet / Nebula / Cyber Fuji
- Powered by GLSL shaders (Shadertoy-compatible)

## ⚙️ Build & Run

```bash
# Build the mod
./gradlew build

# Run client
./gradlew runClient
```

## 📝 License

This project is distributed under a multi-license model:

- **Project Core**: Licensed under the [Apache License 2.0](LICENSE).
- **Graphics**: The core rendering components (located in `src/main/java/com/github/duskbyte/graphics/`)
  are licensed under the [MIT License](src/main/java/com/github/duskbyte/graphics/LICENSE).

---

Copyright © 2026 DuskByteDevelopment.
