package net.kunmc.lab.sharknado.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.sharknado.SharknadoPlugin;
import org.bukkit.Location;

public class SummonTornadoCommand extends Command {
    public SummonTornadoCommand(SharknadoPlugin plugin) {
        super("summonTornado");

        argument(builder -> {
            builder.locationArgument("location", null, ctx -> {
                Location location = ctx.getParsedArg(0, Location.class);
                if (plugin.summonTornado(location)) {
                    ctx.sendSuccess("竜巻を出しました");
                } else {
                    ctx.sendFailure("竜巻はすでに存在しています");
                }
            });
        });
    }
}
