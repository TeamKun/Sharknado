package net.kunmc.lab.sharknado.tornado;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EffectTask extends BukkitRunnable {
    private final Tornado tornado;

    public static BukkitTask run(Tornado tornado) {
        return new EffectTask(tornado).runTaskTimerAsynchronously(tornado.config.plugin(), 0, 60);
    }

    private EffectTask(Tornado tornado) {
        this.tornado = tornado;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            private double degree = 0;
            private double heightOffset = 0;
            private double currentRadius = 3;

            @Override
            public void run() {
                Location center = tornado.location();
                double radian = Math.toRadians(degree);
                double x = center.getX() + Math.cos(radian) * currentRadius;
                double y = center.getY() + heightOffset;
                double z = center.getZ() + Math.sin(radian) * currentRadius;

                World world = center.getWorld();
                world.spawnParticle(Particle.REDSTONE, x, y, z, 3, new Particle.DustOptions(Color.WHITE, 10));

                degree += 20;
                heightOffset += 0.25;
                currentRadius = Math.min(currentRadius + 0.125, tornado.config.radius.value());

                if (heightOffset > tornado.config.height.value()) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(tornado.config.plugin(), 0, 0);
    }
}
