package net.kunmc.lab.sharknado.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.sharknado.SharknadoPlugin;

public class RemoveTornadoCommand extends Command {
    private final SharknadoPlugin plugin;

    public RemoveTornadoCommand(SharknadoPlugin plugin) {
        super("removeTornado");
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandContext ctx) {
        if (plugin.removeTornado()) {
            ctx.sendSuccess("竜巻を消しました");
        } else {
            ctx.sendFailure("竜巻は存在しません");
        }
    }
}
