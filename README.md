<h3 align="center">
  PET HORSES
</h3>

<p align="center">
  <a href="https://modrinth.com/plugin/pet-horses">
    <img src="https://img.shields.io/modrinth/dt/pet-horses?color=green&style=for-the-badge&logo=modrinth" alt="Modrinth Downloads"/>
  </a><br>
  <a href="https://www.curseforge.com/minecraft/bukkit-plugins/pet-horses">
    <img src="https://cf.way2muchnoise.eu/full_1347084_downloads.svg" alt="CurseForge Downloads"/>
  </a><br>
  <a href="https://www.codefactor.io/repository/github/iliiasik/pet-horses/overview/main">
    <img src="https://www.codefactor.io/repository/github/iliiasik/pet-horses/badge/main" alt="CodeFactor"/>
  </a>
</p>

## Description

Pet Horses enhances horse gameplay by giving **each player their own personal horse pet**.  
It adds **passive leveling**, **summon/hide functionality**, **customization via GUI**, **passenger permissions**, and a **personal inventory to carry items**.  
Over time, your horse becomes faster, jumps higher, and gains more health.


---

<p align="center">
  <img src="https://github.com/user-attachments/assets/cc6205f3-129a-4f0b-8cd7-2eb907b534e0" width="800" height="400" alt="Horse Level">
</p>



Horses that die can be **respawned** after a configurable cooldown. All settings can be adjusted in the plugin's configuration:

```yaml
# Language for the plugin. Available: 'en', 'ru', 'es', 'fr', 'zh', 'pt', 'de', 'ja', 'ko', 'it', 'hi, 'ar'
language: 'en'

# Database configuration (MariaDB only) false - yaml storage
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

allow_fall_damage: false

# Horse backpack configuration
# IMPORTANT. Size must be between 9 and 54, and must be a multiple of 9
backpack:
  base_size: 9
  size_per_level: 9
  max_size: 54
  armor_slot_enabled: true
  drop_on_death: true # Drop backpack contents on horse death

# Cooldown in minutes before a dead horse can be respawned
respawn_cooldown_minutes: 15
```

<p align="center">
  <img src="https://github.com/user-attachments/assets/94a4f993-d64d-407c-9401-e5c1b49a0f9b" width="800" height="400" alt="Horse Customize">
</p>


Horses can be customized in appearance through a GUI. You can summon or hide your horse at any time, make it follow you, or leave it standing in place. Other players cannot ride your horse without your permission.

---

<p align="center">
  <img src="https://github.com/user-attachments/assets/db600f08-4781-49eb-9b3c-15a3f0ecca1e" width="600" alt="Horse Backpack" />
</p>



Each horse has its own **personal inventory**, which can be opened by pressing <kbd>SHIFT</kbd> + <kbd>Right Click</kbd>.  
The inventory **expands as the horse levels up**.  
All settings can be **customized in the configuration file**, including the option to **enable or disable horse armor saving**.

---

- Supports multiple languages: English (en), Russian (ru), Spanish (es), French (fr), Chinese (zh), Portuguese (pt), German (de), Japanese (ja), Korean (ko), Italian (it), Hindi (hi), Arabic (ar)
- Storage options: YAML (small servers 0-70 players) or MariaDB (large servers 100+ players)
- Easy to add new languages by translating localization files

<p align="center">
<img width="300" height="200" alt="Screenshot 4" src="https://github.com/user-attachments/assets/7e57fccb-b185-4d3b-802d-ad1cc030bea3" />
</p>

## License

MIT © [Iliiasik](https://github.com/Iliiasik)
