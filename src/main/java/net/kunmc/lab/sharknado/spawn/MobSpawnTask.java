package net.kunmc.lab.sharknado.spawn;

import net.kunmc.lab.sharknado.SharknadoPlugin;
import net.kunmc.lab.sharknado.tornado.Tornado;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Optional;

public class MobSpawnTask extends BukkitRunnable {
    private final MobSpawnConfig config;
    private final SharknadoPlugin plugin;
    private int tick = 0;

    public static BukkitTask run(MobSpawnConfig config) {
        return new MobSpawnTask(config).runTaskTimer(config.plugin(), 0, 0);
    }

    private MobSpawnTask(MobSpawnConfig config) {
        this.config = config;
        this.plugin = JavaPlugin.getPlugin(SharknadoPlugin.class);
    }

    @Override
    public void run() {
        Optional<Tornado> tornado = plugin.getTornado();
        if (!config.enable.value() || !tornado.isPresent()) {
            tick = 0;
            return;
        }

        config.entityType2SpawnFrequencyMap.entrySet().stream()
                .filter(x -> tick % x.getValue() == 0)
                .map(Map.Entry::getKey)
                .forEach(this::spawn);

        tick++;
    }

    private void spawn(EntityType type) {
        Optional<Tornado> tornado = plugin.getTornado();

        int amount = config.entityType2SpawnAmountMap.value().getOrDefault(type, 0);
        for (int i = 0; i < amount; i++) {
            tornado.ifPresent(x -> {
                Entity entity = x.location().getWorld().spawnEntity(x.location(), type);
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).setMaximumAir(Integer.MAX_VALUE);
                    ((LivingEntity) entity).setRemainingAir(Integer.MAX_VALUE);
                }
            });
        }
    }
}
