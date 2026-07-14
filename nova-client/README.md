# Nova Mobile Client

A Minecraft Bedrock Edition companion client for Android, built with Kotlin + Jetpack Compose.

---

## What it does

- **HUD Overlay** — draws directly over Minecraft using Android's system overlay permission (`TYPE_APPLICATION_OVERLAY`)
- **Server Ping** — pings any Bedrock server using RakNet UDP Unconnected Ping packets
- **15 Features** — see the full list below

---

## Features

| Feature | How it works |
|---|---|
| Custom HUD Editor | Drag-and-drop HUD elements over Minecraft via overlay |
| FPS Booster | Stores optimized render/graphics settings as JSON config |
| Theme Engine | Dark, Neon, AMOLED, Minimal — live Compose theme switching |
| Crosshair Customizer | 12 styles, fully configurable, drawn in overlay |
| Keystrokes Display | Touch-event tracking in HUD overlay |
| CPS Counter | Tap timestamp ring buffer, updates in real time |
| Armor Status | Canvas bars in overlay (hook into MovePlayerPacket for real data) |
| Potion Effects HUD | Timer countdown in overlay |
| Coordinates Overlay | Canvas text in overlay (hook into MovePlayerPacket) |
| Screenshot Mode | Hides all overlay elements instantly |
| Waypoints | Saved locations with X/Y/Z + dimension |
| Resource Pack Manager | Enable/disable/reorder packs saved to config |
| Chat Improvements | Timestamps, filters, copy, quick replies |
| Macro Buttons | One-tap chat command buttons |
| Server Ping | RakNet UDP ping → MOTD, player count, latency |

---

## Setup

### Requirements

- **Android Studio Hedgehog** (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34** (compileSdk) with minSdk 26 (Android 8.0+)

### Build steps

```bash
# 1. Clone or extract the project
cd nova-client

# 2. Open in Android Studio
#    File → Open → select the nova-client folder

# 3. Let Gradle sync (downloads ~200 MB of dependencies first time)

# 4. Connect your Android device (enable USB debugging)
#    OR create an AVD (Android Virtual Device) in Android Studio

# 5. Run → Run 'app'
```

### Overlay permission

The very first time you tap **Start HUD Overlay**, Android will redirect you to
`Settings → Display over other apps`. Enable Nova Client there, then come back
and tap the button again.

---

## Project structure

```
nova-client/
├── app/src/main/kotlin/com/nova/client/
│   ├── MainActivity.kt          — entry point, Compose host
│   ├── NovaApplication.kt       — app singleton, notification channel
│   ├── data/models/Models.kt    — all data classes and enums
│   ├── data/repository/         — JSON config persistence
│   ├── network/BedrockPing.kt   — RakNet UDP ping
│   ├── overlay/
│   │   ├── OverlayService.kt    — foreground service, WindowManager
│   │   ├── HudOverlayView.kt    — Canvas view rendered over Minecraft
│   │   └── elements/            — individual HUD renderers
│   ├── ui/
│   │   ├── theme/               — Compose colors, typography, theming
│   │   ├── navigation/NavGraph.kt
│   │   ├── components/          — shared Compose components
│   │   └── screens/             — one file per screen
│   └── viewmodel/MainViewModel.kt
```

---

## Integrating with real Bedrock game data

The HUD elements (coordinates, armor, potions) have `updateXxx()` methods you
call from a protocol layer. To get live data you need to either:

### Option A — Overlay companion (current approach)
The app runs alongside Minecraft. Hook the HUD by reading game memory (root
only) or by using the Minecraft Scripting API bridge over a local TCP socket.

### Option B — Bedrock Protocol client
Add a full Bedrock protocol library (e.g. adapted from
[CloudburstMC/Protocol](https://github.com/CloudburstMC/Protocol)) to connect
as a proxy. Packets you care about:

| Packet | Use |
|---|---|
| `MovePlayerPacket` | X/Y/Z coordinates, yaw for facing direction |
| `MobEffectPacket` | Potion effects (add / remove / update) |
| `InventorySlotPacket` | Armor slot durability |
| `TextPacket` | Chat messages |
| `PlayStatusPacket` | Login / spawn |

Wire these into `CoordinatesElement.updatePosition()`,
`ArmorStatusElement.updateArmor()`, and `PotionEffectsElement.addEffect()`.

---

## Config persistence

All settings are stored in `files/nova_config.json` (internal storage).
The file is human-readable JSON — you can hand-edit it or reset it by deleting.

---

## License

Open-source. Built for educational purposes. Not affiliated with Mojang or Microsoft.
