package net.kunmc.lab.sharknado.tornado;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import net.kunmc.lab.configlib.value.DoubleValue;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.UUIDValue;
import net.kunmc.lab.configlib.value.collection.EnumSetValue;
import net.kunmc.lab.configlib.value.collection.UUIDSetValue;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TornadoConfig extends BaseConfig {
    public final DoubleValue radius = new DoubleValue(10.0);
    public final DoubleValue height = new DoubleValue(20.0);
    public final DoubleValue angleOfRotationPerTick = new DoubleValue(10.0);
    public final BooleanValue excludeSource = new BooleanValue(false);
    public final BooleanValue excludeFlowing = new BooleanValue(true);
    public final BooleanValue excludePlayers = new BooleanValue(false);
    public final IntegerValue limitOfEntities = new IntegerValue(500);
    public final IntegerValue limitOfBlocks = new IntegerValue(500);
    public final DoubleValue involveEntityProbability = new DoubleValue(0.25, 0.0, 1.0);
    public final DoubleValue involveBlockProbability = new DoubleValue(0.05, 0.0, 1.0);
    public final EnumSetValue<Material> blocksExcludedFromInvolving = new EnumSetValue<>();
    public final BooleanValue automaticMovement = new BooleanValue(true);
    public final DoubleValue followingSpeed = new DoubleValue(3.5);
    public final UUIDValue targetPlayer = new UUIDValue();
    public final UUIDSetValue playersExcludedFromTarget = new UUIDSetValue();
    public final IntegerValue changeTargetInterval = new IntegerValue(600);

    public TornadoConfig(@NotNull Plugin plugin) {
        super(plugin);
        setEntryName("tornado");
        blocksExcludedFromInvolving.add(Material.BEDROCK);
        blocksExcludedFromInvolving.add(Material.COMMAND_BLOCK);
        blocksExcludedFromInvolving.add(Material.COMMAND_BLOCK_MINECART);
        blocksExcludedFromInvolving.add(Material.CHAIN_COMMAND_BLOCK);
        blocksExcludedFromInvolving.add(Material.REPEATING_COMMAND_BLOCK);
    }
}
