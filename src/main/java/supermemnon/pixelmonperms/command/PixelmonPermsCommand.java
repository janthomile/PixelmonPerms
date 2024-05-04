package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;
import supermemnon.pixelmonperms.InteractionHandler;
import supermemnon.pixelmonperms.PixelmonPerms;
import supermemnon.pixelmonperms.util.RayTraceHelper;

public class PixelmonPermsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("pixelmonperms")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("set")
                            .then(Commands.literal("permission")
                                .then(Commands.argument("permission", PermissionNodeArgument.permissionNode())
                                    .executes(context -> runSetPermission(context.getSource(), PermissionNodeArgument.getPermissionNode(context, "permission"))))))
                        .then(Commands.literal("get")
                            .then(Commands.literal("permission")
                                .executes(context -> runGetPermission(context.getSource()))))

        );
    }


    private static int runGetPermission(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
//        String perm = StringArgumentType.getString(context.getSource());
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!InteractionHandler.hasRequiredPermission(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no required permission!" + lookEntity.toString()));
                return 0;
            }
            String perm = InteractionHandler.getRequiredPermission(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Required Permission: %s", perm)), false);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runSetPermission(CommandSource source, String permission) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
//        String perm = StringArgumentType.getString(context.getSource());
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
                InteractionHandler.setRequiredPermission(lookEntity, permission);
            }
        else {
                source.sendFailure(new StringTextComponent("Entity is not NPC!"));
            }
        return 1;
    }

}
