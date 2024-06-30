package supermemnon.pixelmonperms.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.api.rules.teamselection.TeamSelectionRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;
import supermemnon.pixelmonperms.NBTHandler;
import supermemnon.pixelmonperms.PixelmonPerms;
import supermemnon.pixelmonperms.util.FormattingHelper;
import supermemnon.pixelmonperms.util.RayTraceHelper;

import java.util.UUID;

public class PixelmonPermsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandStructure = Commands.literal("pixelmonperms").requires(source -> source.hasPermission(2));
        commandStructure = appendSetCommand(commandStructure);
        commandStructure = appendGetCommand(commandStructure);
        commandStructure = appendRemoveCommand(commandStructure);
        commandStructure = appendNPCBattleCommand(commandStructure);
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

    private static LiteralArgumentBuilder<CommandSource> appendNPCBattleCommand(LiteralArgumentBuilder<CommandSource> command) {
        return command.then(Commands.literal("npcbattle")
                .then(Commands.argument("player",  StringArgumentType.word())
                        .then(Commands.argument("uuid", StringArgumentType.word())
                            .executes(context -> runNpcBattle(context.getSource(), StringArgumentType.getString(context, "player"), StringArgumentType.getString(context, "uuid")))
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
            if (!NBTHandler.hasFailCommand(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no fail command!!"));
                return 0;
            }
            String[] commandList = NBTHandler.getFailCommands(lookEntity);
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
            NBTHandler.appendFailCommand(lookEntity, command);
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
            if (!NBTHandler.hasFailCommand(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have fail command set!"));
                return 0;
            }
            String[] commands = NBTHandler.getFailCommands(lookEntity);
            if (commands.length < (index + 1)) {
                source.sendFailure(new StringTextComponent("NPC does not have a fail command at that index!"));
                return 0;
            }
            NBTHandler.removeFailCommand(lookEntity, index);
            source.sendSuccess(new StringTextComponent(String.format("Removed NPC's fail command at index %d.", index)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

    private static int runNpcBattle(CommandSource source, String playerName, String npcUUID) throws CommandSyntaxException {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerWorld world = server.overworld().getWorldServer();
        ServerPlayerEntity playerBattler = server.getPlayerList().getPlayerByName(playerName);
        UUID entityUUID = UUID.fromString(npcUUID);
        Entity entity = world.getEntity(entityUUID);
        if (playerBattler == null) {
            source.sendFailure(new StringTextComponent("No player with that name found."));
            return 0;
        }
        else if (entity == null) {
            source.sendFailure(new StringTextComponent("No entity with that UUID found."));
            return 0;
        }
        else if (!(entity instanceof NPCEntity)) {
            source.sendFailure(new StringTextComponent("Entity is not an NPC!"));
            return 0;
        }
        else {
            if (!(entity instanceof NPCTrainer)) {
                source.sendFailure(new StringTextComponent("NPC is not a trainer!"));
                return 0;
            }
            NPCTrainer trainer = (NPCTrainer) entity;
            Pokemon startingPixelmon = StorageProxy.getParty(playerBattler).getSelectedPokemon();
            if (startingPixelmon == null) {
                source.sendFailure(new StringTextComponent("Trainer has no pokemon!!"));
                return 0;
            }
            TeamSelectionRegistry.builder().members(trainer, playerBattler).showRules().showOpponentTeam().closeable(true).battleRules(trainer.battleRules).start();
            PixelmonPerms.getLOGGER().log(Level.INFO, String.format("Started NPC Battle between %s and %s", playerName, trainer.getName().getString()));
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
            if (!NBTHandler.hasRequiredPermission(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no required permission!"));
                return 0;
            }
            String[] permList = NBTHandler.getRequiredPermissions(lookEntity);
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
            NBTHandler.appendRequiredPermission(lookEntity, permission);
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
        String[] perms = NBTHandler.getRequiredPermissions(lookEntity);
        if (!NBTHandler.hasRequiredPermission(lookEntity) || perms.length < 1) {
            source.sendFailure(new StringTextComponent("NPC does not have required permission!!"));
            return 0;
        }
        if (perms.length < (index + 1)) {
            source.sendFailure(new StringTextComponent("NPC does not have a permission at that index!"));
            return 0;
        }
        NBTHandler.removeRequirePermission(lookEntity, index);
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
            if (!NBTHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC has no custom cancel message!"));
                return 0;
            }
            String cancelMessage = NBTHandler.getCancelMessage(lookEntity);
            source.sendSuccess(new StringTextComponent(String.format("Cancel Message: %s", cancelMessage)), true);
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
            NBTHandler.appendCancelMessage(lookEntity, cancelMessage);
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
            if (!NBTHandler.hasCancelMessage(lookEntity)) {
                source.sendFailure(new StringTextComponent("NPC does not have custom cancel message!!"));
                return 0;
            }
//            NBTHandler.removeCancelMessage(lookEntity, index);
//            source.sendSuccess(new StringTextComponent("Removed NPC's custom cancel message."), false);
            String[] messages = NBTHandler.getCancelMessages(lookEntity);
            if (messages.length < (index + 1)) {
                source.sendFailure(new StringTextComponent("NPC does not have a message at that index!"));
                return 0;
            }
            NBTHandler.removeCancelMessage(lookEntity, index);
            source.sendSuccess(new StringTextComponent(String.format("Removed NPC's message at index %d.", index)), true);
        }
        else {
            source.sendFailure(new StringTextComponent("Entity is not NPC!"));
        }
        return 1;
    }

}
