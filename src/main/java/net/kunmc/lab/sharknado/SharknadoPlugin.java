package net.kunmc.lab.sharknado;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.kunmc.lab.sharknado.command.MainCommand;
import net.kunmc.lab.sharknado.missile.MissileMob;
import net.kunmc.lab.sharknado.missile.MobMissileConfig;
import net.kunmc.lab.sharknado.spawn.MobSpawnConfig;
import net.kunmc.lab.sharknado.spawn.MobSpawnTask;
import net.kunmc.lab.sharknado.tornado.Tornado;
import net.kunmc.lab.sharknado.tornado.TornadoConfig;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Optional;

public final class SharknadoPlugin extends JavaPlugin {
    private final TornadoConfig tornadoConfig;
    private final MobMissileConfig mobMissileConfig;
    private final MobSpawnConfig mobSpawnConfig;
    private Tornado tornado;

    public SharknadoPlugin() {
        tornadoConfig = new TornadoConfig(this);
        mobMissileConfig = new MobMissileConfig(this);
        mobSpawnConfig = new MobSpawnConfig(this);
    }

    @Override
    public void onEnable() {
        ConfigCommand configCommand = new ConfigCommandBuilder(tornadoConfig)
                .addConfig(mobMissileConfig)
                .addConfig(mobSpawnConfig)
                .build();
        CommandLib.register(this, new MainCommand(this, configCommand));

        MobSpawnTask.run(mobSpawnConfig);

        try {
            DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
            Field f = MinecraftServer.class.getDeclaredField("allowFlight");
            f.setAccessible(true);
            f.set(server, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e").forEach(x -> {
            x.setGravity(true);
            x.setInvulnerable(false);
            x.removeScoreboardTag(Tornado.involvedEntityTag);
            x.removeScoreboardTag(MissileMob.missileMobTag);
        });
    }

    public boolean summonTornado(Location location) {
        if (tornado != null) {
            return false;
        }

        tornado = new Tornado(tornadoConfig, mobMissileConfig, location);
        return true;
    }

    public boolean removeTornado() {
        if (tornado == null) {
            return false;
        }

        tornado.remove();
        tornado = null;
        return true;
    }

    public boolean setTornadoTo(Entity target) {
        if (tornado == null) {
            return false;
        }

        tornado.setAsPassenger(target);
        return true;
    }

    public boolean unsetTornado() {
        if (tornado == null) {
            return false;
        }

        tornado.leaveVehicle();
        return true;
    }

    public Optional<Tornado> getTornado() {
        return Optional.ofNullable(tornado);
    }
}
