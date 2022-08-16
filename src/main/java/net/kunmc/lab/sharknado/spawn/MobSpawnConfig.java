package net.kunmc.lab.sharknado.spawn;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import net.kunmc.lab.configlib.value.map.Enum2IntegerMapValue;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MobSpawnConfig extends BaseConfig {
    public final BooleanValue enable = new BooleanValue(true);
    public final Enum2IntegerMapValue<EntityType> entityType2SpawnAmountMap = new Enum2IntegerMapValue<>();
    public final Enum2IntegerMapValue<EntityType> entityType2SpawnFrequencyMap = new Enum2IntegerMapValue<>();

    public MobSpawnConfig(@NotNull Plugin plugin) {
        super(plugin);
        setEntryName("mobSpawn");

        entityType2SpawnAmountMap.put(EntityType.DOLPHIN, 0);
        entityType2SpawnFrequencyMap.put(EntityType.DOLPHIN, 0);
    }
}
