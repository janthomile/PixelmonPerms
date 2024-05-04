package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                            .then(Commands.literal("message")
                                    .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> runSetCancelMessage(context.getSource(), StringArgumentType.getString(context, "message")))))
                            .then(Commands.literal("permission")
                                .then(Commands.argument("permission", PermissionNodeArgument.permissionNode())
                                    .executes(context -> runSetPermission(context.getSource(), PermissionNodeArgument.getPermissionNode(context, "permission"))))))
                        .then(Commands.literal("get")
                            .then(Commands.literal("message")
                                .executes(context -> runGetCancelMessage(context.getSource())))
                            .then(Commands.literal("permission")
                                .executes(context -> runGetPermission(context.getSource()))))
                        .then(Commands.literal("remove")
                            .then(Commands.literal("message")
                                .executes(context -> runRemoveCancelMessage(context.getSource())))
                            .then(Commands.literal("permission")
                                .executes(context -> runRemovePermission(context.getSource()))))
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
                source.sendFailure(new StringTextComponent("NPC has no required permission!"));
                return 0;
            }
            String perm = InteractionHandler.getRequiredPermission(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Required Permission: %s", perm)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runSetPermission(CommandSource source, String permission) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
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

    private static int runGetCancelMessage(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!InteractionHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no custom cancel message!"));
                return 0;
            }
            String cancelMessage = InteractionHandler.getCancelMessage(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Required Permission: %s", cancelMessage)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runSetCancelMessage(CommandSource source, String cancelMessage) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            InteractionHandler.setCancelMessage(lookEntity, cancelMessage);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runRemovePermission(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!InteractionHandler.hasRequiredPermission(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have required permission!!"));
                return 0;
            }
            InteractionHandler.removeRequirePermission(lookEntity);
            source.sendSuccess(new StringTextComponent("Removed NPC's required permission."), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runRemoveCancelMessage(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!InteractionHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have custom cancel message!!"));
                return 0;
            }
            InteractionHandler.removeCancelMessage(lookEntity);
            source.sendSuccess(new StringTextComponent("Removed NPC's custom cancel message."), false);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

}
