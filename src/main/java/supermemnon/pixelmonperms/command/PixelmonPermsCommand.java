package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import supermemnon.pixelmonperms.util.LegacyNBTHandler;
import supermemnon.pixelmonperms.util.FormattingHelper;
import supermemnon.pixelmonperms.util.NBTHandler;
import supermemnon.pixelmonperms.util.RayTraceHelper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PixelmonPermsCommand {
    private static final String[] entryCommandOptions = {"eval", "permission", "message", "command"};
    private static final String[] entryListOptions = {"permission", "message", "command"};
    private static final String[] evalCommandOptions = {"and", "or", "not"};
    private static CompletableFuture<Suggestions> getSuggestionsFromList(SuggestionsBuilder builder, String[] options) {
        for (String suggestion : options) {
            builder.suggest(suggestion);
        }
        return builder.buildFuture();
    }
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandStructure = Commands.literal("pixelmonperms").requires(source -> source.hasPermission(2));
        commandStructure = appendSetCommand(commandStructure);
        commandStructure = appendGetCommand(commandStructure);
        commandStructure = appendRemoveCommand(commandStructure);
        commandStructure = appendDupeNPCCommand(commandStructure);
        dispatcher.register(commandStructure);
    }

    private static LiteralArgumentBuilder<CommandSource> appendEntryCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("entry")
                .then(Commands.literal("list")
                        .executes(context -> runGetEntryList(context.getSource()))
                )
                .then(Commands.argument("index", IntegerArgumentType.integer())
                        .then(Commands.literal("get")
                                .then(Commands.argument("property", StringArgumentType.word())
                                        .suggests(((context, builder) -> getSuggestionsFromList(builder, entryCommandOptions)))
                                        .executes(context -> runGetEntryProperty(context.getSource(), IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "property")))
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("property", StringArgumentType.word())
                                        .suggests(((context, builder) -> getSuggestionsFromList(builder, entryListOptions)))
                                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                                .executes(context -> runAddEntryProperty(context.getSource(), IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "property"), StringArgumentType.getString(context, "value")))
                                        )
                                )
                                .then(Commands.literal("eval")
                                        .then(Commands.argument("value", StringArgumentType.word())
                                                .suggests(((context, builder) -> getSuggestionsFromList(builder, evalCommandOptions)))
                                                .executes(context -> runAddEntryProperty(context.getSource(), IntegerArgumentType.getInteger(context, "index"), "eval", StringArgumentType.getString(context, "value")))
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("property", StringArgumentType.word())
                                        .suggests(((context, builder) -> getSuggestionsFromList(builder, entryListOptions)))
                                        .executes(context -> runGetEntryProperty(context.getSource(), IntegerArgumentType.getInteger(context, "index"), StringArgumentType.getString(context, "property")))
                                )
                        )
                )

        );
    }

    private static int runGetEntryList(CommandSource source) {
        return 1;
    }

    private static int runGetEntryProperty(CommandSource source, int entryIndex, String property) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (!(lookEntity instanceof NPCEntity)) {
            source.sendFailure(new StringTextComponent("Invalid NPC selected!"));
            return 0;
        }
        String result = FormattingHelper.getEntryPropertyString(lookEntity, entryIndex, property);
        if (result == "") {
            source.sendFailure(new StringTextComponent("Entry or property not found!"));
            return 0;
        }
        source.sendSuccess(new StringTextComponent(result), true);
        return 1;
    }

    private static int runAddEntryProperty(CommandSource source, int entryIndex, String property, String value) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        Entity lookEntity = RayTraceHelper.getEntityLookingAt(player, 8.0);
        if (!(lookEntity instanceof NPCEntity)) {
            source.sendFailure(new StringTextComponent("Invalid NPC selected!"));
            return 0;
        }
        boolean success = false;
        switch (property) {
            case  "eval": {
                int eval = NBTHandler.EVAL.getValueFromName(value);
                if (eval == -1) {
                    source.sendFailure(new StringTextComponent("Invalid eval input!"));
                    return 0;
                }
                success = NBTHandler.setEntryEval(lookEntity, entryIndex, eval);
            }
            case  "permission": case "message": case "command": {
                success = NBTHandler.appendEntryPropertyItem(lookEntity, entryIndex, property, value);
            }
        }
        if (!success) {
            source.sendFailure(new StringTextComponent("Entry or property not found!"));
            return 0;
        }
        return 1;
    }

    private static LiteralArgumentBuilder<CommandSource> appendDupeNPCCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("duplicatenpc")
                .executes(context -> runDupeNPCCommand(context.getSource()))
        );
    }


    private static int runSweepReformat(CommandSource source) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        for (NPCEntity npc : world.getEntities().filter(entity -> entity instanceof NPCEntity).map(entity -> (NPCEntity) entity).collect(Collectors.toList())) {
            if (LegacyNBTHandler.entityHasLegacyFormat(npc)) {
                LegacyNBTHandler.refactorLegacyFormat(npc, false);
            }
        }
        return 1;
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
        dupeEntity.setUUID(UUID.randomUUID());
        dupeEntity.setPose(lookEntity.getPose());
        lookEntity.level.addFreshEntity(dupeEntity);
        return 1;
    }

    ///  ///  ///  ///  ///
    ///LEGACY FORMAT///
    ///  ///  ///  ///  ///

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
