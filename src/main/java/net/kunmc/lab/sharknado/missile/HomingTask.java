package net.kunmc.lab.sharknado.missile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

class HomingTask extends BukkitRunnable {
    private final MissileMob missileMob;
    private final MobMissileConfig config;
    private Location targetLocation;

    public static BukkitTask run(MissileMob missileMob) {
        return new HomingTask(missileMob).runTaskTimerAsynchronously(missileMob.config.plugin(), 0, 0);
    }

    private HomingTask(MissileMob missileMob) {
        this.missileMob = missileMob;
        this.config = missileMob.config;
        this.targetLocation = missileMob.targetLocation();
    }

    @Override
    public void run() {
        if (missileMob.entity.isDead()) {
            this.cancel();
            return;
        }

        if (missileMob.target == null) {
            this.cancel();
            return;
        }

        Location currentLocation = missileMob.location();
        if (!currentLocation.getWorld().equals(targetLocation.getWorld())) {
            this.cancel();
            return;
        }

        if (currentLocation.distance(targetLocation) > config.distanceToStopHoming.value()) {
            targetLocation = missileMob.targetLocation();
        }
        missileMob.setVelocity(targetLocation.clone()
                .subtract(currentLocation)
                .toVector()
                .normalize()
                .multiply(config.speed.divide(20)));

        if (shouldExplosion()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    currentLocation.createExplosion(config.explosionPower.value());
                    missileMob.remove();
                }
            }.runTask(missileMob.config.plugin());
        }
    }

    private boolean shouldExplosion() {
        Location currentLocation = missileMob.location();
        if (currentLocation.distance(targetLocation) < 1.0) {
            return true;
        }

        BoundingBox boundingBox = missileMob.entity.getBoundingBox().expand(0.125);
        if (boundingBox.overlaps(missileMob.target.getBoundingBox())) {
            return true;
        }

        if (missileMob.config.explodeWhenCollideBlock.isTrue()) {
            return getNearBlocks().stream()
                    .filter(Block::isSolid)
                    .anyMatch(x -> boundingBox.overlaps(x.getBoundingBox()));
        }

        return false;
    }

    private List<Block> getNearBlocks() {
        List<Block> list = new ArrayList<>();
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    list.add(missileMob.location().getBlock().getRelative(x, y, z));
                }
            }
        }
        return list;
    }
}
