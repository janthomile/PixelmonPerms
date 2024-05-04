package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;

public class PermissionNodeArgument {
    public static String getPermissionNode(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    public static StringArgumentType permissionNode() {
        return StringArgumentType.word();
    }
}
