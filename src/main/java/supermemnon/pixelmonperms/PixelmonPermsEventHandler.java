package supermemnon.pixelmonperms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.events.npc.NPCEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;
import supermemnon.pixelmonperms.util.FormattingHelper;
import supermemnon.pixelmonperms.util.PermUtils;

import static supermemnon.pixelmonperms.util.CommandUtils.executeCommandString;


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
        public static void onNPCInteractEvent(NPCEvent.Interact event) throws CommandSyntaxException {
            if (!NBTHandler.hasRequiredPermission(event.npc)) {
                return;
            }
            String[] perms = NBTHandler.getRequiredPermissions(event.npc);
            if (!PermUtils.hasAllRequiredPermissions(event.player, perms)) {
                event.player.sendMessage(new StringTextComponent(FormattingHelper.formatWithAmpersand(NBTHandler.getCancelMessage(event.npc))), event.player.getUUID());
                if (NBTHandler.hasFailCommand(event.npc)) {
                    boolean commandSuccess = executeCommandString(event.player.getServer(), event.player, NBTHandler.getFailCommand(event.npc));
                }
                event.setCanceled(true);
            }
        }
    }
}
