package net.kunmc.lab.sharknado.tornado;

import com.google.common.collect.Lists;
import net.kunmc.lab.sharknado.missile.MobMissileConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tornado implements Listener {
    private static final String armorStandTag = "TornadoArmorStand";
    public static final String involvedEntityTag = "TornadoEntity";
    final TornadoConfig config;
    final MobMissileConfig mobMissileConfig;
    final ArmorStand armorStand;
    final List<Entity> involvedEntities = Collections.synchronizedList(new ArrayList<>());
    final List<FallingBlock> involvedBlocks = Collections.synchronizedList(new ArrayList<>());
    private final List<BukkitTask> tasks = new ArrayList<>();

    public Tornado(TornadoConfig config, MobMissileConfig mobMissileConfig, Location location) {
        this.config = config;
        this.mobMissileConfig = mobMissileConfig;
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, x -> {
            ArmorStand as = ((ArmorStand) x);
            as.setVisible(false);
            as.setInvulnerable(true);
            as.addScoreboardTag(armorStandTag);
        });

        tasks.addAll(Lists.newArrayList(InvolveBlockTask.run(this),
                InvolveEntityTask.run(this),
                RaiseTask.run(this),
                MoveTask.run(this),
                EffectTask.run(this)));

        Bukkit.getPluginManager().registerEvents(this, config.plugin());
    }

    public void remove() {
        armorStand.remove();
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
        involvedEntities.forEach(this::releaseEntity);
        involvedBlocks.forEach(this::releaseEntity);
        involvedEntities.clear();
        involvedBlocks.clear();
    }

    public void addEntity(Entity entity) {
        entity.addScoreboardTag(involvedEntityTag);
        involvedEntities.add(entity);
    }

    public void addBlock(Block block) {
        BlockData data = block.getBlockData();
        block.setType(Material.AIR);
        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), data);
        fallingBlock.setGravity(false);
        involvedBlocks.add(fallingBlock);
    }

    public void releaseEntity(Entity entity) {
        involvedEntities.remove(entity);
        entity.setGravity(true);
        entity.removeScoreboardTag(involvedEntityTag);
    }

    public Location location() {
        return armorStand.getLocation();
    }

    public void setAsPassenger(Entity vehicle) {
        vehicle.addPassenger(armorStand);
    }

    public void leaveVehicle() {
        armorStand.leaveVehicle();
    }

    @EventHandler
    private void onArmorStandKilled(EntityDeathEvent e) {
        if (e.getEntity() == armorStand) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onChangeGameMode(PlayerGameModeChangeEvent e) {
        GameMode mode = e.getNewGameMode();
        if (mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR) {
            releaseEntity(e.getPlayer());
        }
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin() == config.plugin()) {
            armorStand.remove();
        }
    }
}
