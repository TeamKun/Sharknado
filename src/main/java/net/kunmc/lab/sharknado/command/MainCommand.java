package net.kunmc.lab.sharknado.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.sharknado.SharknadoPlugin;

public class MainCommand extends Command {
    public MainCommand(SharknadoPlugin plugin, ConfigCommand configCommand) {
        super("sharknado");
        addChildren(configCommand, new SetTornadoToCommand(plugin), new SummonTornadoCommand(plugin), new RemoveTornadoCommand(plugin));
    }
}
