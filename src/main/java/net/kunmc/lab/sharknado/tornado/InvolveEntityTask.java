package net.kunmc.lab.sharknado.tornado;

import net.kunmc.lab.sharknado.missile.MissileMob;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

class InvolveEntityTask extends BukkitRunnable {
    private final Tornado tornado;
    private final TornadoConfig config;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static BukkitTask run(Tornado tornado) {
        return new InvolveEntityTask(tornado).runTaskTimer(tornado.config.plugin(), 0, 4);
    }

    private InvolveEntityTask(Tornado tornado) {
        this.tornado = tornado;
        this.config = tornado.config;
    }

    @Override
    public void run() {
        Location location = tornado.location();

        location.getNearbyEntities(config.radius.value(), config.height.value(), config.radius.value()).stream()
                .filter(x -> x != tornado.armorStand)
                .filter(x -> x.getLocation().getY() >= location.getY() - 2)
                .filter(x -> !(x instanceof FallingBlock))
                .filter(x -> !(x instanceof Item))
                .filter(x -> !x.getScoreboardTags().contains(Tornado.involvedEntityTag))
                .filter(x -> !x.getScoreboardTags().contains(MissileMob.missileMobTag))
                .filter(x -> {
                    if (!(x instanceof Player)) {
                        return true;
                    }

                    if (config.excludePlayers.value()) {
                        return false;
                    }

                    GameMode mode = ((Player) x).getGameMode();
                    return mode.equals(GameMode.SURVIVAL) || mode.equals(GameMode.ADVENTURE);
                })
                .filter(x -> random.nextDouble() <= config.involveEntityProbability.value())
                .filter(x -> tornado.involvedEntities.size() <= config.limitOfEntities.value())
                .peek(x -> x.setGravity(false))
                .forEach(tornado::addEntity);
    }
}
