package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import supermemnon.pixelmonperms.util.LegacyNBTHandler;
import supermemnon.pixelmonperms.util.FormattingHelper;
import supermemnon.pixelmonperms.util.RayTraceHelper;

public class PixelmonPermsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandStructure = Commands.literal("pixelmonperms").requires(source -> source.hasPermission(2));
        commandStructure = appendSetCommand(commandStructure);
        commandStructure = appendGetCommand(commandStructure);
        commandStructure = appendRemoveCommand(commandStructure);
        dispatcher.register(commandStructure);
    }

    private static LiteralArgumentBuilder<CommandSource> appendSetCommand(LiteralArgumentBuilder<CommandSource> command) {
           return command.then(Commands.literal("set")
                .then(Commands.literal("message")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> runSetCancelMessage(context.getSource(), StringArgumentType.getString(context, "message"))
                                )
                        )
                )
                .then(Commands.literal("permission")
                        .then(Commands.argument("permission", PermissionNodeArgument.permissionNode())
                                .executes(context -> runSetPermission(context.getSource(), PermissionNodeArgument.getPermissionNode(context, "permission"))
                                )
                        )
                )
               .then(Commands.literal("failcommand")
                       .then(Commands.argument("command", StringArgumentType.greedyString())
                               .executes(context -> runSetFailCommand(context.getSource(), StringArgumentType.getString(context, "command"))
                               )
                       )
               )
           );
    }

    private static LiteralArgumentBuilder<CommandSource> appendGetCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("get")
                .then(Commands.literal("message")
                        .executes(context -> runGetCancelMessage(context.getSource()))
                )
                .then(Commands.literal("permission")
                        .executes(context -> runGetPermission(context.getSource()))
                )
                .then(Commands.literal("failcommand")
                        .executes(context -> runGetFailCommand(context.getSource()))
                )
        );
    }

    private static LiteralArgumentBuilder<CommandSource> appendRemoveCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("remove")
                .then(Commands.literal("message")
                        .then(Commands.argument("index", IntegerArgumentType.integer())
                                .executes(context -> runRemoveCancelMessage(context.getSource(), IntegerArgumentType.getInteger(context, "index"))
                                )
                        )
                )
                .then(Commands.literal("permission")
                        .then(Commands.argument("index", IntegerArgumentType.integer())
                            .executes(context -> runRemovePermission(context.getSource(), IntegerArgumentType.getInteger(context, "index"))
                            )
                    )
                )
                .then(Commands.literal("failcommand")
                        .then(Commands.argument("index", IntegerArgumentType.integer())
                                .executes(context -> runRemoveFailCommand(context.getSource(), IntegerArgumentType.getInteger(context, "index"))
                                )
                        )
                )
        );
    }

    private static LiteralArgumentBuilder<CommandSource> appendDupeNPCCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("duplicatenpc")
                .executes(context -> runDupeNPCCommand(context.getSource()))
        );
    }

    private static int runDupeNPCCommand(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
            return 0;
        }
        Entity dupeEntity = lookEntity.getType().create(lookEntity.level);
        if (dupeEntity == null) {
            return 0;
        }
        CompoundNBT entityNbt = new CompoundNBT();
        lookEntity.saveWithoutId(entityNbt);
        dupeEntity.load(entityNbt);
        dupeEntity.setPose(lookEntity.getPose());
        lookEntity.level.addFreshEntity(dupeEntity);
        return 1;
    }

    private static int runGetFailCommand(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!LegacyNBTHandler.hasFailCommand(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no fail command!!"));
                return 0;
            }
            String[] commandList = LegacyNBTHandler.getFailCommands(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Interact Fail commands:\n%s", FormattingHelper.formatIndexedStringList(commandList))), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runSetFailCommand(CommandSource source, String command) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            LegacyNBTHandler.appendFailCommand(lookEntity, command);
            source.sendSuccess(new StringTextComponent(String.format("Added fail command: %s", command)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runRemoveFailCommand(CommandSource source, int index) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!LegacyNBTHandler.hasFailCommand(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have fail command set!"));
                return 0;
            }
            String[] commands = LegacyNBTHandler.getFailCommands(lookEntity);
            if (commands.length < (index + 1)) {
                source.sendFailure(new StringTextComponent("NPC does not have a fail command at that index!"));
                return 0;
            }
            LegacyNBTHandler.removeFailCommand(lookEntity, index);
            source.sendSuccess(new StringTextComponent(String.format("Removed NPC's fail command at index %d.", index)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }


    private static int runGetPermission(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
//        String perm = StringArgumentType.getString(context.getSource());
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!LegacyNBTHandler.hasRequiredPermission(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no required permission!"));
                return 0;
            }
            String[] permList = LegacyNBTHandler.getRequiredPermissions(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Required permissions:\n%s", FormattingHelper.formatIndexedStringList(permList))), true);
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
            LegacyNBTHandler.appendRequiredPermission(lookEntity, permission);
            source.sendSuccess(new StringTextComponent(String.format("Added required permission: %s", permission)), true);
        }
        else {
                source.sendFailure(new StringTextComponent("Entity is not NPC!"));
            }
        return 1;
    }

    private static int runRemovePermission(CommandSource source, int index) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
            return 0;
        }
        else if (!(lookEntity instanceof NPCEntity)) {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
            return 0;
        }
        String[] perms = LegacyNBTHandler.getRequiredPermissions(lookEntity);
        if (!LegacyNBTHandler.hasRequiredPermission(lookEntity) || perms.length < 1) {
            source.sendFailure(new StringTextComponent("NPC does not have required permission!!"));
            return 0;
        }
        if (perms.length < (index + 1)) {
            source.sendFailure(new StringTextComponent("NPC does not have a permission at that index!"));
            return 0;
        }
        LegacyNBTHandler.removeRequirePermission(lookEntity, index);
        source.sendSuccess(new StringTextComponent("Removed NPC's required permission."), true);
        return 1;
    }

    private static int runGetCancelMessage(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!LegacyNBTHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no custom cancel message!"));
                return 0;
            }
            String[] cancelMessages = LegacyNBTHandler.getCancelMessages(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Cancel Messages:\n%s", FormattingHelper.formatIndexedStringList(cancelMessages))), true);
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
            LegacyNBTHandler.appendCancelMessage(lookEntity, cancelMessage);
            source.sendSuccess(new StringTextComponent(String.format("Added cancel message: %s", cancelMessage)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }



    private static int runRemoveCancelMessage(CommandSource source, int index) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (lookEntity == null) {
            source.sendFailure(new StringTextComponent("No entity found."));
        }
        else if (lookEntity instanceof NPCEntity) {
            if (!LegacyNBTHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have custom cancel message!!"));
                return 0;
            }
//            NBTHandler.removeCancelMessage(lookEntity, index);
//            source.sendSuccess(new StringTextComponent("Removed NPC's custom cancel message."), false);
            String[] messages = LegacyNBTHandler.getCancelMessages(lookEntity);
            if (messages.length < (index + 1)) {
                source.sendFailure(new StringTextComponent("NPC does not have a message at that index!"));
                return 0;
            }
            LegacyNBTHandler.removeCancelMessage(lookEntity, index);
            source.sendSuccess(new StringTextComponent(String.format("Removed NPC's message at index %d.", index)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

}
