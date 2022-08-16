package net.kunmc.lab.sharknado.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.sharknado.SharknadoPlugin;

public class UnsetTornadoCommand extends Command {
    private final SharknadoPlugin plugin;

    public UnsetTornadoCommand(SharknadoPlugin plugin) {
        super("unsetTornado");
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandContext ctx) {
        if (plugin.unsetTornado()) {
            ctx.sendSuccess("竜巻をプレイヤーから解放しました");
        } else {
            ctx.sendSuccess("竜巻は存在しません");
        }
    }
}
