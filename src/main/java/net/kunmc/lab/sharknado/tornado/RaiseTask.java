package net.kunmc.lab.sharknado.tornado;

import net.kunmc.lab.sharknado.missile.MissileMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

class RaiseTask extends BukkitRunnable {
    private final Tornado tornado;
    private final TornadoConfig config;

    public static BukkitTask run(Tornado tornado) {
        return new RaiseTask(tornado).runTaskTimerAsynchronously(tornado.config.plugin(), 0, 0);
    }

    private RaiseTask(Tornado tornado) {
        this.tornado = tornado;
        this.config = tornado.config;
    }

    @Override
    public void run() {
        List<FallingBlock> releasingBlocks;
        synchronized (tornado.involvedBlocks) {
            releasingBlocks = tornado.involvedBlocks.stream()
                    .peek(x -> x.setTicksLived(1))
                    .peek(this::raise)
                    .filter(this::isReleasingBlock)
                    .peek(x -> x.setGravity(true))
                    .collect(Collectors.toList());
        }
        tornado.involvedBlocks.removeAll(releasingBlocks);

        List<Entity> releasingEntities;
        synchronized (tornado.involvedEntities) {
            releasingEntities = tornado.involvedEntities.stream()
                    .peek(this::raise)
                    .filter(this::isReleasingEntity)
                    .collect(Collectors.toList());
        }
        releasingEntities.stream()
                .filter(x -> x instanceof Mob)
                .forEach(x -> new MissileMob(tornado.mobMissileConfig, x));
        releasingEntities.forEach(tornado::releaseEntity);
    }

    private boolean isReleasingBlock(FallingBlock fallingBlock) {
        if (fallingBlock.isDead()) {
            return true;
        }

        return isAtTheTop(fallingBlock);
    }

    private boolean isReleasingEntity(Entity entity) {
        if (entity.isDead()) {
            return true;
        }

        return isAtTheTop(entity);
    }

    private boolean isAtTheTop(Entity entity) {
        return entity.getLocation().getY() >= tornado.location().getY() + config.height.value();
    }

    private void raise(Entity entity) {
        Location center = tornado.location();

        double radian = Math.atan2(entity.getLocation().getZ() - center.getZ(),
                entity.getLocation().getX() - center.getX()) + Math.toRadians(config.angleOfRotationPerTick.value());
        double cos = Math.cos(radian);
        double sin = Math.sin(radian);

        double coefficient = 0.0625 * 10 / config.angleOfRotationPerTick.value();
        double planeDistance = center.toVector().setY(0).subtract(entity.getLocation().toVector().setY(0)).length();
        double fromOrigin = Math.min(planeDistance + coefficient, config.radius.value());

        double nextX = center.getX() + fromOrigin * cos;
        double nextY = entity.getLocation().getY() + coefficient;
        double nextZ = center.getZ() + fromOrigin * sin;
        entity.setVelocity(new Vector(nextX, nextY, nextZ).subtract(entity.getLocation().toVector()));
    }
}
