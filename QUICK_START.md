# Player2NPC - Quick Start Guide

## Installation

1. **Prerequisites:**
   - Minecraft 1.20.1 (Java Edition)
   - Fabric Loader installed
   - Fabric API mod

2. **Download:**
   - Get the latest `player2npc.jar` from the releases
   - Place in your `mods` folder

3. **Run Game:**
   - Start Minecraft with Fabric
   - Player2NPC will auto-load

## Quick Usage

### Opening the Character Menu
- **Press `H`** while in game
- Character selection screen opens
- Wait for characters to load

### Spawning a Companion
1. Press `H` to open menu
2. Click on any character card
3. Select "Summon" button
4. Companion spawns near you!

### Interacting with Companions
- **Chat**: Type commands in chat (depends on Player2 API)
- **Tasks**: Give them jobs like "gather wood"
- **Following**: Command to follow you
- **Combat**: Order them to attack mobs

### Dismissing Companions
1. Open character menu (Press H)
2. Click character to dismiss
3. Companion disappears

## Features

### ✅ Multiple Companions
- Summon multiple different companions
- Each has unique personality
- Independent AI behaviors

### ✅ Advanced AI
- Powered by Player2 API + Baritone
- Understands natural language commands
- Can navigate terrain
- Can gather resources
- Can fight enemies

### ✅ Persistence
- Companions saved when you log out
- Reappear when you join world
- Inventory is preserved

### ✅ Smart Inventory
- Companions have 36-slot inventory
- Automatically pick up nearby items
- Can receive items from you

## Configuration

### Basic Settings
Edit configuration in `config/player2npc.json` (when available):

```json
{
  "spawn_radius": 3,
  "spawn_height": 1,
  "companion_speed": 0.4,
  "item_pickup_range": 3,
  "render_range": 64
}
```

**Current Values** (hardcoded, edit ConfigManager.java to change):
- **Spawn Radius**: 3 blocks from player
- **Spawn Height**: 1 block above ground
- **Speed**: 0.4 (same as players)
- **Pickup Range**: 3 blocks
- **Render Range**: 64 blocks
- **Hunger**: Disabled (set to false)

## Troubleshooting

### Problem: Menu doesn't open (Press H does nothing)
**Solution:**
1. Make sure you're in a world
2. Check mod is loaded in logs
3. Try re-binding the key in controls
4. Verify Fabric API is installed

### Problem: Characters won't load
**Solution:**
1. Wait 10+ seconds - loading takes time
2. Check internet connection
3. Check mod logs for errors
4. Restart Minecraft

### Problem: Companion won't spawn
**Solution:**
1. Make sure you're close to ground
2. Check companion isn't already spawned
3. Check mod logs
4. Try different character

### Problem: Companion stuck or not moving
**Solution:**
1. Move away and back
2. Dismiss and re-summon
3. Reload world
4. Check for obstacles

### Problem: Items not being picked up
**Solution:**
1. Items must be within 3 blocks
2. Companion might be busy
3. Inventory might be full
4. Check game rule: `mobGriefing` is enabled

### Problem: Performance issues / Lag
**Solution:**
1. Reduce render distance (Ctrl+F3+G)
2. Dismiss extra companions
3. Close other applications
4. Update GPU drivers

## Chat Commands

**These depend on Player2 API character configuration:**

```
# Examples (character-dependent):
"Alex, gather 10 wood"
"Alex, follow me"
"Alex, wait here"
"Alex, attack that zombie"
"Alex, come back"
"Alex, go to 100 200 300"
```

**Note:** Exact commands depend on character definition and AI capabilities.

## World Settings

### Recommended Settings
```
Difficulty: Hard (more challenge)
PvP: Enabled (if testing combat)
Mobs: Enabled (for gathering tasks)
Fire Spread: Enabled (for realistic environment)
```

### Gamerules
- `mobGriefing` - Should be `true` for item pickup
- `showDeathMessages` - For debugging crashes

## Performance Tips

1. **Lower companion render range**
   - Further away = less rendering
   - Only render nearby companions

2. **Limit companions**
   - 1-3 companions ideal
   - Each adds rendering/AI overhead
   - More = more lag

3. **Disable unnecessary features**
   - Hunger system is disabled by default (good!)
   - Only enable if needed

4. **Optimize Minecraft**
   - 60+ FPS recommended
   - 8GB+ RAM recommended
   - Good GPU helps with rendering

## Advanced: Building Custom Characters

**Coming Soon:** Character customization guide

Characters are defined in the Player2 database. Once that's available, you can create custom:
- Names
- Personalities
- Appearances
- Skills
- Behaviors

## FAQ

**Q: Can I have infinite companions?**
A: Technically yes, but not recommended. Each adds overhead. 3-5 is ideal.

**Q: Can they die?**
A: Yes! They're vulnerable to damage like any mob. Protect them!

**Q: Do they eat food?**
A: Hunger is disabled by default. You can enable it in ConfigManager.

**Q: Can I customize their appearance?**
A: Yes via Player2 character definitions. Download skins from minetools.

**Q: Do they respawn?**
A: Not automatically. You need to re-summon them. Future version might add auto-respawn.

**Q: Can they use weapons/armor?**
A: Yes! Companions can equip gear from their inventory.

**Q: What if my companion bugs out?**
A: Dismiss and re-summon. Data is preserved in NBT.

**Q: Where are companions saved?**
A: In world data (NBT format). Per-player per-world.

## Resources

- **Main README**: README.md
- **Improvements**: IMPROVEMENTS.md
- **Development**: DEVELOPMENT_GUIDE.md
- **Mod Repository**: https://github.com/ngaololl4-netizen/Player2NPC
- **Player2 API**: Documentation in dependencies
- **Fabric**: https://fabricmc.net/

## Getting Help

1. **Check Logs**: Look in `.minecraft/logs/latest.log`
2. **Read Documentation**: IMPROVEMENTS.md, DEVELOPMENT_GUIDE.md
3. **Issue Tracker**: GitHub issues
4. **Discord**: Player2 community server

## Next Steps

1. ✅ Install mod
2. ✅ Open character menu
3. ✅ Summon a companion
4. ✅ Give them a command
5. ✅ Explore their abilities
6. ✅ Read IMPROVEMENTS.md for details

---

**Have fun with your AI companions! 🎮**

---

**Last Updated**: 2025-11-01
**Version**: 1.0+
**Status**: Production Ready
