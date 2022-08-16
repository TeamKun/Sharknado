package net.kunmc.lab.sharknado.missile;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import net.kunmc.lab.configlib.value.DoubleValue;
import net.kunmc.lab.configlib.value.FloatValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MobMissileConfig extends BaseConfig {
    public final DoubleValue speed = new DoubleValue(6.0);
    public final FloatValue explosionPower = new FloatValue(1.0F);
    public final DoubleValue distanceToStopHoming = new DoubleValue(4.0);
    public final BooleanValue explodeWhenCollideBlock = new BooleanValue(true);

    public MobMissileConfig(@NotNull Plugin plugin) {
        super(plugin);
        setEntryName("mobMissile");
    }
}
