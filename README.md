<h3>
  PET HORSES
</h3>


[![Modrinth Downloads][modrinth-downloads-shield]][modrinth-downloads-url]

[modrinth-downloads-shield]: https://img.shields.io/modrinth/dt/pet-horses?color=green&style=for-the-badge&logo=modrinth
[modrinth-downloads-url]: https://modrinth.com/plugin/pet-horses

[![CurseForge Downloads][curseforge-downloads-shield]][curseforge-downloads-url]

[curseforge-downloads-shield]: https://cf.way2muchnoise.eu/full_1347084_downloads.svg
[curseforge-downloads-url]: https://www.curseforge.com/minecraft/bukkit-plugins/pet-horses

## Description

Pet Horses enhances horse gameplay by giving **each player their own personal horse pet**.  
It adds **passive leveling**, **summon/hide functionality**, **customization via GUI**, **passenger permissions**, and a **personal inventory to carry items**.  
Over time, your horse becomes faster, jumps higher, and gains more health.


---

<p align="center">
  <img src="https://github.com/user-attachments/assets/3b0104be-6d4d-4662-99e6-d74c9bc09344" width="400" height="200" alt="Horse Level">
</p>

Horses that die can be **respawned** after a configurable cooldown. All settings can be adjusted in the plugin's configuration:

```yaml
# Language for the plugin. Available: 'en', 'ru', 'es', 'fr', 'zh', 'pt', 'de', 'ja', 'ko', 'it'
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

# Horse backpack configuration
# IMPORTANT. Size must be between 9 and 54, and must be a multiple of 9
backpack:
  base_size: 9
  size_per_level: 9
  max_size: 54
  armor_slot_enabled: true

# Cooldown in minutes before a dead horse can be respawned
respawn_cooldown_minutes: 15
```

<p align="center">
  <img src="https://github.com/user-attachments/assets/6209fd9c-4c8c-4054-81bd-13d4089fc516" width="400" height="200" alt="Customize Horse">
  <img src="https://github.com/user-attachments/assets/e78bb678-6543-4ff5-8a22-25d8eef61dd3" width="417" height="217" alt="Horse GUI">
</p>

Horses can be customized in appearance through a GUI. You can summon or hide your horse at any time, make it follow you, or leave it standing in place. Other players cannot ride your horse without your permission.

<p align="center">
  <img src="https://github.com/user-attachments/assets/f8a24ea0-2c50-4bf1-9609-c7d2bb6c91f3" width="400" height="200" alt="Commands">
</p>

---

<p align="center">
<img width="600" alt="Inventory" src="https://github.com/user-attachments/assets/7c5255c6-ad0d-4b35-9ad8-5632ddffdbe0" />
</p>


Each horse has its own **personal inventory**, which can be opened by pressing <kbd>SHIFT</kbd> + <kbd>Right Click</kbd>.  
The inventory **expands as the horse levels up**.  
All settings can be **customized in the configuration file**, including the option to **enable or disable horse armor saving**.

---

- Supports multiple languages: English (en), Russian (ru), Spanish (es), French (fr), Chinese (zh), Portuguese (pt), German (de), Japanese (ja), Korean (ko), Italian (it)
- Storage options: YAML (small servers 0-70 players) or MariaDB (large servers 100+ players)
- Easy to add new languages by translating localization files

<p align="center">
<img width="300" height="200" alt="Screenshot 4" src="https://github.com/user-attachments/assets/7e57fccb-b185-4d3b-802d-ad1cc030bea3" />
</p>

## License

MIT © [Iliiasik](https://github.com/Iliiasik)
