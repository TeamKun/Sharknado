package net.kunmc.lab.sharknado.missile;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MissileMob implements Listener {
    final MobMissileConfig config;
    final Entity entity;
    final Player target;
    private final BukkitTask homingTask;
    public static final String missileMobTag = "MissileMob";

    public MissileMob(MobMissileConfig config, Entity entity) {
        this.config = config;
        this.entity = entity;

        Optional<Player> p = getPlayerRandomly();
        if (p.isPresent()) {
            this.target = p.get();
            this.homingTask = HomingTask.run(this);
        } else {
            this.target = null;
            this.homingTask = null;
        }

        entity.addScoreboardTag(missileMobTag);
        Bukkit.getPluginManager().registerEvents(this, config.plugin());
    }

    private Optional<Player> getPlayerRandomly() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers()).stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE)
                .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                .collect(Collectors.toList());
        if (players.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(players, ThreadLocalRandom.current());
        return Optional.ofNullable(players.get(0));
    }

    public void setVelocity(Vector velocity) {
        entity.setVelocity(velocity);
    }

    public Location location() {
        return entity.getLocation();
    }

    public Location targetLocation() {
        return target.getLocation();
    }

    public void remove() {
        entity.remove();
        homingTask.cancel();
    }

    @EventHandler
    private void onFallingDamage(EntityDamageEvent e) {
        if (e.getEntity() != entity) {
            return;
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }
}
