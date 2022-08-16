package net.kunmc.lab.sharknado.tornado;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class MoveTask extends BukkitRunnable {
    private final Tornado tornado;
    private final TornadoConfig config;
    private int tick = 0;

    public static BukkitTask run(Tornado tornado) {
        return new MoveTask(tornado).runTaskTimerAsynchronously(tornado.config.plugin(), 4, 0);
    }

    public MoveTask(Tornado tornado) {
        this.tornado = tornado;
        this.config = tornado.config;

        Optional<Player> p = getPlayerRandomly();
        if (p.isPresent() && config.targetPlayer.toPlayer() == null) {
            config.targetPlayer.value(p.map(Player::getUniqueId).get());
        }
    }

    private Optional<Player> getPlayerRandomly() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers()).stream()
                .filter(p -> p.getGameMode() != GameMode.CREATIVE)
                .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                .filter(p -> !p.getScoreboardTags().contains(Tornado.involvedEntityTag))
                .filter(p -> !config.playersExcludedFromTarget.contains(p.getUniqueId()))
                .collect(Collectors.toList());
        if (players.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(players, ThreadLocalRandom.current());
        return Optional.ofNullable(players.get(0));
    }

    private Location calcNextLocation(Location targetLocation) {
        return tornado.location()
                .add(targetLocation.subtract(tornado.location())
                        .toVector()
                        .normalize()
                        .multiply(config.followingSpeed.value() / 20));
    }

    @Override
    public void run() {
        tick++;

        if (!config.automaticMovement.value()) {
            return;
        }

        Player p = config.targetPlayer.toPlayer();
        if (p == null ||
                p.getGameMode() == GameMode.CREATIVE ||
                p.getGameMode() == GameMode.SPECTATOR ||
                p.getScoreboardTags().contains(Tornado.involvedEntityTag)) {
            config.targetPlayer.value(getPlayerRandomly()
                    .map(Player::getUniqueId)
                    .orElse(null));
            return;
        }

        Location to = calcNextLocation(p.getLocation());
        if (!to.getBlock().isSolid()) {
            tornado.armorStand.teleportAsync(to);
        }

        if (tick % config.changeTargetInterval.value() == 0) {
            config.targetPlayer.value(getPlayerRandomly()
                    .map(Player::getUniqueId)
                    .orElse(null));
        }
    }
}
