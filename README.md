<h1>
  NOMAD HORSES
  <img width="27" height="27" alt="HORSE" src="https://github.com/user-attachments/assets/b505c936-97ca-459b-b605-0b8fc1a792ee"/>
</h1>



[![SpigotMC Downloads][spigot-downloads-shield]][spigot-downloads-url]
[![Modrinth Downloads][modrinth-downloads-shield]][modrinth-downloads-url]

[github-downloads-shield]: https://img.shields.io/github/downloads/Iliiasik/Nomad-Horses/total.svg?style=for-the-badge&logo=github
[github-downloads-url]: https://github.com/Iliiasik/Nomad-Horses/releases

[spigot-downloads-shield]: https://img.shields.io/badge/SpigotMC-Downloads-blue?style=for-the-badge&logo=spigot
[spigot-downloads-url]: https://www.spigotmc.org/resources/nomad-horses.128733/

[modrinth-downloads-shield]: https://img.shields.io/badge/Modrinth-Downloads-green?style=for-the-badge&logo=modrinth
[modrinth-downloads-url]: https://modrinth.com/plugin/nomad-horses

<div style="display: flex; justify-content: center; gap: 8px; margin-top: 6px; font-size: 1.2em;">
  <code>1.20</code>
  <code>1.20.1</code>
  <code>1.20.2</code>
  <code>1.20.3</code>
  <code>1.20.4</code>
  <code>1.20.5</code>
  <code>1.20.6</code>
  <code>1.21</code>
</div>

## Description

Nomad Horses enhances horse gameplay by adding **passive leveling**, **summon/hide functionality**, **customization via GUI**, and **passenger permissions**. Over time, your horse becomes faster, jumps higher, and gains more health.

---

<p align="center">
  <img src="https://github.com/user-attachments/assets/3b0104be-6d4d-4662-99e6-d74c9bc09344" width="400" height="200" alt="Horse Level">
</p>

Horses that die can be **respawned** after a configurable cooldown. All settings can be adjusted in the plugin's configuration:

```yaml
# Language for the plugin: 'en', 'ru', 'es'
language: 'en'

# Database configuration (MariaDB only)
database:
  enabled: false
  host: "localhost"
  port: 3306
  name: "minecraft"
  user: "root"
  password: ""

# Leveling system
leveling:
  base_xp: 100
  xp_increment: 50

# Horse stats
stats:
  speed_base: 0.18
  speed_max_bonus: 0.225
  health_base: 15.0
  health_max_bonus: 15.0
  jump_base: 0.6
  jump_max_bonus: 0.6

# Cooldown (minutes) before a dead horse can respawn
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

This plugin was developed for nomadic-style servers focused on constant movement without elytra.

- Supports English, Spanish, and Russian
- Storage options: YAML (small servers) or MariaDB (large servers)
- Easy to add new languages by translating localization files

<p align="center">
  <a href="https://github.com/Iliiasik/Nomad-Horses" target="_blank">
    <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="80" height="80" alt="GitHub">
  </a>
</p>

<p align="center">
<img width="300" height="200" alt="Screenshot 4" src="https://github.com/user-attachments/assets/7e57fccb-b185-4d3b-802d-ad1cc030bea3" />
</p>

## License

MIT © [Iliiasik](https://github.com/Iliiasik)
