<p align="center">
  <img alt="image" src="assets/soulbound-banner.svg" />
</p>

<p align="center">
  <a href="https://github.com/SoulBound-Java/SoulBound"><img alt="license" src="https://shieldcn.dev/github/SoulBound-Java/SoulBound/license.svg" /></a>
  <a href="https://github.com/SoulBound-Java/SoulBound/releases"><img alt="release" src="https://shieldcn.dev/github/SoulBound-Java/SoulBound/release.svg" /></a>
  <a href="https://github.com/SoulBound-Java/SoulBound/commits"><img alt="last commit" src="https://shieldcn.dev/github/SoulBound-Java/SoulBound/last-commit.svg" /></a>
  <a href="https://github.com/SoulBound-Java/SoulBound/actions"><img alt="CI" src="https://shieldcn.dev/github/SoulBound-Java/SoulBound/ci.svg?variant=destructive" /></a>
</p>

## Overview

A lightweight, scalable, and optimized plugin for Paper/Spigot (Java 21+) that adds advanced RPG mechanics to Minecraft.
With support for persistent storage in SQLite/MySQL and zero impact on TPS, SoulBound integrates unique combat, mining,
and survival skills with seamless integration and instant configuration.

- ⚙️ Customizable
- ⚡ Fast

## Getting Started

Follow these steps to get **SoulBound** up and running on your Minecraft server in less than two minutes.

### Prerequisites

Ensure your server environment meets the following minimum requirements:

* **Server Software:** Paper, Purpur, or Spigot (`1.20.4+` / compatible with modern Paper builds)
* **Java Version:** Java 21 or higher
* **Database:** SQLite (default, zero-setup) or MySQL 8.0+

### Installation

1. **Download the Plugin:** Grab the latest `SoulBound-x.x.x.jar` release from
   the [Releases Tab](https://github.com/SoulBound-Java/SoulBound/releases).
2. **Deploy the File:** Place the downloaded `.jar` file into your server's `plugins/` directory.
3. **Start the Server:** Launch your server to generate the configuration files and default SQLite database.

## Skills Included

* **1. Seismic Impact:** Critical hits generate a shockwave on the ground, knocking back nearby enemies.
* **2. Streak of Good Luck:** Mining ores triggers a dynamic chance to chain-mine adjacent blocks or double drops.
* **3. Adrenaline in the Blood:** Falling below 30% HP triggers a frenzy state with bonus speed and damage.
* **4. Piercing Arrow:** Charged bow shots can pierce through initial targets to hit enemies behind them.
* **5. Obsidian Skin:** Passive chance to block incoming damage completely or cut negative status effects in half.

## Commands & Permissions

| Command                     | Permission        | Description                                        |
|:----------------------------|:------------------|:---------------------------------------------------|
| `/skills`                   | `soulbound.user`  | Opens the main skill selection and upgrade menu.   |
| `/soulbound reload`         | `soulbound.admin` | Reloads the `config.yml` file and skill values.    |
| `/soulbound reset <player>` | `soulbound.admin` | Resets all skills and stats for a specific player. |

<p align="center">
  <a href="https://github.com/SoulBound-Java/SoulBound/graphs/contributors"><img alt="contributors" src="https://shieldcn.dev/contributors/SoulBound-Java/SoulBound.svg?mode=dark" /></a>
</p>
