package supermemnon.pixelmonperms;

import com.pixelmonmod.pixelmon.api.events.npc.NPCEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;


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
        @SubscribeEvent(priority =  EventPriority.HIGHEST)
        public static void onNPCInteractEvent(NPCEvent.Interact event) {
            PixelmonPerms.getLOGGER().log(Level.INFO, "NPC Interaction!");
            if (InteractionHandler.hasRequiredPermission(event.npc)) {
                PixelmonPerms.getLOGGER().log(Level.INFO, "NPC Has required Permission!");
                String perm = InteractionHandler.getRequiredPermission(event.npc);
                if (!PermissionAPI.hasPermission(event.player, perm)) {
                    PixelmonPerms.getLOGGER().log(Level.INFO, "NPC Interaction Cancelled!");
                    event.player.sendMessage(new StringTextComponent(InteractionHandler.getCancelMessage(event.npc)), event.player.getUUID());
                    event.setCanceled(true);
                }
                else {
                    PixelmonPerms.getLOGGER().log(Level.INFO, "Player has permission!!");
                }

            }
        }
    }

}
