package supermemnon.pixelmonperms;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.events.npc.NPCEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;
import supermemnon.pixelmonperms.util.*;

public class PixelmonPermsEventHandler {

    @Mod.EventBusSubscriber(modid = "pixelmonperms", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            PixelmonPermsCommand.register(event.getDispatcher());
        }

    }

    public static class ModEvents {
        public static void handlePermEvent(Event event, NPCEntity entity, PlayerEntity player) throws CommandSyntaxException {
            ListNBT entryList = NBTHandler.getEntryList(entity);
            if (entryList == null) {
                return;
            }
            int firstValidEntry = PermUtils.findFirstValidEntry(entryList, player);
            if (firstValidEntry == -1) {
                return;
            }
            event.setCanceled(true);
            ListNBT messageListNbt = NBTHandler.getEntryListProperty(entity, firstValidEntry, NBTHandler.msgListKey);
            ListNBT commandListNbt = NBTHandler.getEntryListProperty(entity, firstValidEntry, NBTHandler.cmdListKey);
            if (messageListNbt != null) {
                PixelmonUtils.customNpcChat(entity, (ServerPlayerEntity) player, NBTHandler.propertyListToArray(messageListNbt));
            }
            if (commandListNbt != null) {
                CommandUtils.executeCommandList(player.getServer(), player, NBTHandler.propertyListToArray(commandListNbt));
            }
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onNPCBattleEvent(NPCEvent.StartBattle event) throws CommandSyntaxException {
            handlePermEvent(event, event.npc, event.player);
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onNPCInteractEvent(NPCEvent.Interact event) throws CommandSyntaxException {
            handlePermEvent(event, event.npc, event.player);
        }
    }
}
