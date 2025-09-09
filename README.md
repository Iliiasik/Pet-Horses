<h2 align="center">
  <img width="20" height="20" alt="HORSE" src="https://github.com/user-attachments/assets/b505c936-97ca-459b-b605-0b8fc1a792ee" style="vertical-align:middle;" />
  NOMAD HORSES
</h2>


<p align="center">
  <a href="https://github.com/Iliiasik/Nomad-Horses/stargazers">
    <img src="https://img.shields.io/github/stars/Iliiasik/Nomad-Horses.svg?style=flat&logo=github" alt="GitHub stars">
  </a>
  <br>
  <a href="https://github.com/Iliiasik/Nomad-Horses/releases">
    <img src="https://img.shields.io/github/downloads/Iliiasik/Nomad-Horses/total.svg?style=flat&logo=github" alt="Downloads">
  </a>
</p>


## Description

Nomad Horses is a simple Minecraft plugin that enhances horse gameplay and adds a small passive leveling system.  
Over time, your horse will become faster, jump higher, and gain more health.  

<p align="center">
  <img width="506" height="273" alt="Screenshot 1" src="https://github.com/user-attachments/assets/3b0104be-6d4d-4662-99e6-d74c9bc09344" />
</p>
When a horse dies, it can be respawned after a configurable cooldown. All settings can be adjusted in the plugin's config:

```bash
# Language for the plugin. Available: 'en', 'ru', 'es'
language: 'en'

# Database configuration (MariaDB only)
database:
  enabled: false
  host: "localhost"
  port: 3306
  name: "minecraft"
  user: "root"
  password: ""

# Leveling system configuration
leveling:
  base_xp: 100
  xp_increment: 50

# Horse stats configuration
stats:
  speed_base: 0.18
  speed_max_bonus: 0.225
  health_base: 15.0
  health_max_bonus: 15.0
  jump_base: 0.6
  jump_max_bonus: 0.6

# Cooldown in minutes before a dead horse can be respawned
respawn_cooldown_minutes: 15
```

Horses can be fully customized with colors and names.
<p align="center">
<img width="645" height="320" alt="Screenshot 1" src="https://github.com/user-attachments/assets/6209fd9c-4c8c-4054-81bd-13d4089fc516" />
</p>
<p align="center">
<img width="645" height="320" alt="Screenshot 2" src="https://github.com/user-attachments/assets/e78bb678-6543-4ff5-8a22-25d8eef61dd3" />
</p>

You can hide or summon your horse at any time, make it follow you, or stay in place.
Other players cannot ride someone else's horse without the owner's permission.

<p align="center">
<img width="645" height="320" alt="Screenshot 3" src="https://github.com/user-attachments/assets/f8a24ea0-2c50-4bf1-9609-c7d2bb6c91f3" />
</p>

This plugin was developed for a nomadic-style server focused on constant movement without elytra.

The plugin supports 3 languages – English, Spanish, and Russian.
Adding a new language is simple: translate one of the localization files and include it in the project.

The plugin can use either YAML as storage (suitable for small servers) or a full MariaDB database (better for large servers).

I decided to share this plugin hoping it will be useful for someone

<p align="center">
<img width="300" height="200" alt="Снимок экрана 2025-09-10 005123" src="https://github.com/user-attachments/assets/7e57fccb-b185-4d3b-802d-ad1cc030bea3" />
</p>
