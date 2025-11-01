# Player2NPC - Development Guide

## Project Structure

```
Player2NPC/
├── src/main/java/com/goodbird/player2npc/
│   ├── Player2NPC.java                 # Main mod initializer
│   ├── Player2NPCClient.java           # Client-side initializer
│   ├── Player2NPCComponents.java       # Component registration
│   │
│   ├── config/
│   │   └── ConfigManager.java          # Centralized configuration
│   │
│   ├── inventory/
│   │   └── InventoryManager.java       # Inventory management
│   │
│   ├── util/
│   │   └── EventLogger.java            # Event logging utility
│   │
│   ├── companion/
│   │   ├── AutomatoneEntity.java       # AI companion entity (CORE)
│   │   └── CompanionManager.java       # Companion lifecycle management
│   │
│   ├── network/
│   │   ├── AutomatoneSpawnRequestPacket.java      # Client → Server
│   │   ├── AutomatoneDespawnRequestPacket.java    # Client → Server
│   │   └── AutomatonSpawnPacket.java              # Server → Client
│   │
│   ├── client/
│   │   ├── gui/
│   │   │   ├── CharacterSelectionScreen.java   # Main UI
│   │   │   ├── CharacterDetailScreen.java      # Detail view
│   │   │   └── CharacterCardWidget.java        # Card component
│   │   │
│   │   ├── render/
│   │   │   └── RenderAutomaton.java            # Rendering logic
│   │   │
│   │   └── util/
│   │       ├── ImageDownloadAlt.java
│   │       ├── ResourceDownloader.java
│   │       └── SkinManager.java
│   │
│   └── resources/
│       └── assets/
│           └── player2npc/
│               ├── lang/
│               └── textures/
```

## Core Components

### 1. AutomatoneEntity.java (The Heart)
**Implements:**
- `IAutomatone` - Marks as AI-controllable
- `IInventoryProvider` - Provides player-like inventory
- `IInteractionManagerProvider` - Block interaction capability
- `IHungerManagerProvider` - Hunger management

**Key Methods:**
- `init()` - Initialize entity and AI brain
- `tick()` - Update every game tick
- `tickMovement()` - Movement updates
- `pickupItems()` - Collect items from ground
- `tryAttack()` - Combat system

**Important Fields:**
- `controller` - AltoClefController (AI brain)
- `character` - Player2 Character data
- `inventory` - LivingEntityInventory
- `manager` - LivingEntityInteractionManager

### 2. CompanionManager.java (Lifecycle Manager)
**Responsibilities:**
- Spawn companions when player joins
- Despawn companions when player leaves
- Track companion UUIDs per player
- Handle companion respawning

**Key Methods:**
- `ensureCompanionExists(Character)` - Spawn or teleport
- `dismissCompanion(String)` - Despawn specific companion
- `dismissAllCompanions()` - Despawn all
- `getActiveCompanions()` - Get list of active companions

**NBT Persistence:**
- Saves companion UUIDs
- Restores companions on server restart
- Player-specific companion list

### 3. Player2NPC.java (Mod Initialization)
**Registers:**
- Entity type: AUTOMATONE
- Network packets: SPAWN_REQUEST, DESPAWN_REQUEST, SPAWN
- Event listeners: Join, Disconnect, Server Tick

**Endpoints:**
- Network handlers for C2S and S2C communication
- Server tick events for AI updates

## Configuration

### ConfigManager.java
All configuration in one place:

```java
// Spawn settings
ConfigManager.getSpawnRadius()        // 3 blocks
ConfigManager.getSpawnHeight()        // 1 block

// Entity settings
ConfigManager.getCompanionSpeed()     // 0.4f
ConfigManager.getCompanionStepHeight() // 0.6f
ConfigManager.getCompanionRenderRange() // 64 blocks

// Behavior
ConfigManager.getItemPickupRange()    // 3 blocks
ConfigManager.isHungerTickingEnabled() // false
```

**To Add New Configuration:**
1. Add constant to ConfigManager
2. Add getter method
3. Use in code: `ConfigManager.getValue()`

Example:
```java
private static final int MAX_COMPANIONS = 5;

public static int getMaxCompanions() {
    return MAX_COMPANIONS;
}
```

## Error Handling Patterns

### Pattern 1: Null Validation
```java
if (character == null) {
    LOGGER.warn("Character is null");
    return;
}
```

### Pattern 2: Try-Catch Protection
```java
try {
    // risky operation
} catch (Exception e) {
    LOGGER.error("Operation failed", e);
}
```

### Pattern 3: Safe Collection Iteration
```java
for (UUID uuid : companionMap.values()) {
    if (uuid == null) continue;

    Entity entity = world.getEntity(uuid);
    if (entity instanceof AutomatoneEntity) {
        // safe to use
    }
}
```

### Pattern 4: Graceful Degradation
```java
if (player.getServer() == null) {
    LOGGER.debug("Server unavailable, skipping operation");
    return companions; // return empty list
}
```

## Logging Best Practices

### Using EventLogger
```java
// Spawn event
EventLogger.logCompanionSpawn("Alex", "Player1", "world");

// Despawn event
EventLogger.logCompanionDespawn("Alex", "Player1", "player disconnect");

// Command event
EventLogger.logCompanionCommand("Alex", "Player1", "gather 10 oak_log");

// Error event
EventLogger.logError("ItemPickup", exception);
```

### Using LOGGER Directly
```java
// Different log levels
LOGGER.debug("Detailed debug info");
LOGGER.info("Important event occurred");
LOGGER.warn("Something unexpected happened");
LOGGER.error("Error message", exception);
```

## Network Communication

### Client to Server (C2S)
```
Player presses H → CharacterSelectionScreen opens
→ User clicks character → AutomatoneSpawnRequestPacket sent
→ Server receives → CompanionManager.ensureCompanionExists()
→ Companion spawns
```

### Server to Client (S2C)
```
Companion spawned on server → AutomatonSpawnPacket sent
→ Client receives → RenderAutomaton registers renderer
→ Companion rendered on client
```

**Packet Validation:**
1. Null checks in handle method
2. Character validation
3. Player existence check
4. Server-side execution

## Adding New Features

### Example: New Companion Command

**Step 1: Add Config (Optional)**
```java
// ConfigManager.java
private static final int NEW_FEATURE_RANGE = 10;

public static int getNewFeatureRange() {
    return NEW_FEATURE_RANGE;
}
```

**Step 2: Add to AutomatoneEntity**
```java
public void newFeature() {
    try {
        int range = ConfigManager.getNewFeatureRange();
        // implementation
        EventLogger.logCompanionCommand(character.name(), "new_command");
    } catch (Exception e) {
        LOGGER.error("Error in newFeature", e);
    }
}

@Override
public void tick() {
    // ... existing code
    newFeature();
    super.tick();
}
```

**Step 3: Add Logging**
```java
EventLogger.logCompanionCommand(character.name(), owner.getName().getString(), "new_feature");
```

**Step 4: Test**
1. Verify no NPE crashes
2. Check logs are informative
3. Test error conditions

## Testing Guidelines

### Unit Testing (Pseudocode)
```java
@Test
void testConfigManager() {
    assertEquals(0.4f, ConfigManager.getCompanionSpeed());
}

@Test
void testInventoryManager() {
    InventoryManager inv = new InventoryManager();
    inv.addItem(new ItemStack(...));
    assertEquals(1, inv.getItems().size());
}

@Test
void testEventLogger() {
    // Verify logs are produced
    EventLogger.logCompanionSpawn("test", "player", "world");
}
```

### Manual Testing Checklist
- [ ] Single companion spawn/despawn
- [ ] Multiple companions
- [ ] Player disconnect/reconnect
- [ ] Network disconnection
- [ ] Character loading errors
- [ ] Logs are correct
- [ ] No crashes or stack traces

### Performance Testing
- Spawn 10+ companions
- Monitor memory usage
- Check network traffic
- Verify no lag spikes

## Common Issues & Solutions

### Issue: Null Pointer Exception
**Solution:**
1. Find the line causing NPE
2. Add null check before that line
3. Log the null condition
4. Return early or use safe default

### Issue: Companion Not Spawning
**Solution:**
1. Check CompanionManager logs
2. Verify character is not null
3. Check player world is loaded
4. Check network packet arrives

### Issue: Slow Character Loading
**Solution:**
1. Check network speed
2. Verify timeout is sufficient (10s)
3. Check server response time
4. Add loading indicator

### Issue: High Memory Usage
**Solution:**
1. Clear old companion references
2. Use weak maps if applicable
3. Monitor inventory size
4. Clean up NBT data

## Code Review Checklist

Before committing code:
- [ ] No System.out.println() - use LOGGER
- [ ] All public methods have null checks
- [ ] Exception handling with logging
- [ ] ConfigManager used for constants
- [ ] EventLogger used for important events
- [ ] Meaningful commit message
- [ ] No credentials in code
- [ ] Documentation updated

## Performance Tips

1. **Use concurrent collections** for thread-safe operations
2. **Cache frequently accessed data** like ConfigManager values
3. **Use ArrayList over other lists** for better cache performance
4. **Lazy load resources** when possible
5. **Batch network operations** to reduce packet overhead

## Resources

- **Minecraft Wiki:** https://wiki.minecraft.net/
- **Fabric API:** https://github.com/FabricMC/fabric
- **Baritone:** https://github.com/cabaletta/baritone
- **PlayerEngine:** https://github.com/Ladysnake/Automatone
- **Player2 API:** Documentation in mod dependencies

## Questions?

Check the IMPROVEMENTS.md for detailed feature documentation.
