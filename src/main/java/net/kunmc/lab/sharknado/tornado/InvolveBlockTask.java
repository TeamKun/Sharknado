package net.kunmc.lab.sharknado.tornado;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

class InvolveBlockTask extends BukkitRunnable {
    private final Tornado tornado;
    private final TornadoConfig config;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static BukkitTask run(Tornado tornado) {
        return new InvolveBlockTask(tornado).runTaskTimer(tornado.config.plugin(), 0, 4);
    }

    private InvolveBlockTask(Tornado tornado) {
        this.tornado = tornado;
        this.config = tornado.config;
    }

    @Override
    public void run() {
        Location location = tornado.location();
        getAffectedBlocks(location).stream()
                .filter(x -> random.nextDouble() <= config.involveBlockProbability.value())
                .filter(x -> tornado.involvedBlocks.size() <= config.limitOfBlocks.value())
                .forEach(tornado::addBlock);
    }

    private Set<Block> getAffectedBlocks(Location origin) {
        Set<Block> blocks = new HashSet<>();

        for (int i = 0; i <= config.height.value(); i++) {
            Block center = origin.clone().add(0, i, 0).getBlock();
            double radius = config.radius.value();

            for (double x = -radius; x <= radius; x++) {
                for (double z = -radius; z <= radius; z++) {
                    Block b = center.getRelative((int) x, 0, (int) z);
                    if (center.getLocation().distance(b.getLocation()) > radius) {
                        continue;
                    }

                    if (b.getType().isAir()) {
                        continue;
                    }

                    if (b.isLiquid()) {
                        Levelled data = ((Levelled) b.getBlockData());
                        if (config.excludeFlowing.value() && data.getLevel() >= 1) {
                            continue;
                        }
                        if (config.excludeSource.value() && data.getLevel() == 0) {
                            continue;
                        }
                    }

                    blocks.add(b);
                }
            }
        }

        return blocks;
    }
}
