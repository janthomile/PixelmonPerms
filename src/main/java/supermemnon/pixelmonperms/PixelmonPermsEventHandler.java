package supermemnon.pixelmonperms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.events.npc.NPCEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;
import supermemnon.pixelmonperms.util.LegacyNBTHandler;
import supermemnon.pixelmonperms.util.PermUtils;
import supermemnon.pixelmonperms.util.PixelmonUtils;

import static supermemnon.pixelmonperms.util.CommandUtils.executeCommandList;


//@Mod.EventBusSubscriber(modid = "pixelmonperms")

public class PixelmonPermsEventHandler {

    @Mod.EventBusSubscriber(modid = "pixelmonperms", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            PixelmonPermsCommand.register(event.getDispatcher());
        }

    }

    //    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
//        PixelmonPerms.getLOGGER().log(Level.INFO, "Interaction!!");
//        if (event.getEntity() instanceof NPCEntity && InteractionHandler.hasRequiredPermission(event.getEntity())) {
//            String perm = InteractionHandler.getRequiredPermission(event.getEntity());
//            PixelmonPerms.getLOGGER().log(Level.INFO, "NPC Interaction!");
//            if (!PermissionAPI.hasPermission(event.getPlayer(), perm)) {
//                PixelmonPerms.getLOGGER().log(Level.INFO, "NPC Interaction Cancelled!");
//                event.getPlayer().sendMessage(new StringTextComponent(InteractionHandler.getCancelMessage(event.getEntity())), null);
//                event.setCanceled(true);
//            }
//        }
//    }

    public static class ModEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onNPCBattleEvent(NPCEvent.StartBattle event) throws CommandSyntaxException {
            if (!LegacyNBTHandler.hasRequiredPermission(event.npc)) {
                return;
            }
            String[] perms = LegacyNBTHandler.getRequiredPermissions(event.npc);
            if (!PermUtils.hasAllRequiredPermissions(event.player, perms)) {
                PixelmonUtils.customNpcChat(event.npc, (ServerPlayerEntity) event.player, LegacyNBTHandler.getCancelMessages(event.npc));
//                event.player.sendMessage(new StringTextComponent(FormattingHelper.formatWithAmpersand(NBTHandler.getCancelMessage(event.npc))), event.player.getUUID());
                if (LegacyNBTHandler.hasFailCommand(event.npc)) {
                    boolean commandSuccess = executeCommandList(event.player.getServer(), event.player, LegacyNBTHandler.getFailCommands(event.npc));
                }
                event.setCanceled(true);
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onNPCInteractEvent(NPCEvent.Interact event) throws CommandSyntaxException {
            if (!LegacyNBTHandler.hasRequiredPermission(event.npc)) {
                return;
            }
            String[] perms = LegacyNBTHandler.getRequiredPermissions(event.npc);
            if (!PermUtils.hasAllRequiredPermissions(event.player, perms)) {
                PixelmonUtils.customNpcChat(event.npc, (ServerPlayerEntity) event.player, LegacyNBTHandler.getCancelMessages(event.npc));
//                event.player.sendMessage(new StringTextComponent(FormattingHelper.formatWithAmpersand(NBTHandler.getCancelMessage(event.npc))), event.player.getUUID());
                if (LegacyNBTHandler.hasFailCommand(event.npc)) {
                    boolean commandSuccess = executeCommandList(event.player.getServer(), event.player, LegacyNBTHandler.getFailCommands(event.npc));
                }
                event.setCanceled(true);
            }
        }
    }
}
