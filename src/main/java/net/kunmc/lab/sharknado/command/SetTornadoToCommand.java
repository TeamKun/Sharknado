package net.kunmc.lab.sharknado.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.sharknado.SharknadoPlugin;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * 竜巻の中心にあるアーマースタンドを特定のエンティティに乗っけるコマンド
 */
public class SetTornadoToCommand extends Command {
    public SetTornadoToCommand(SharknadoPlugin plugin) {
        super("setTornadoTo");

        argument(builder -> {
            builder.entityArgument("target", true, true, null, ctx -> {
                Entity entity = ((List<Entity>) ctx.getParsedArg(0)).get(0);
                if (plugin.setTornadoTo(entity)) {
                    ctx.sendSuccess(String.format("竜巻を%sに乗せました", entity.getName()));
                } else {
                    ctx.sendFailure("竜巻は存在しません");
                }
            });
        });
    }
}
