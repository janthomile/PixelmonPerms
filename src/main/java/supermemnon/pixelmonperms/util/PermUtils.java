package supermemnon.pixelmonperms.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.entities.npcs.NPCEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.server.permission.PermissionAPI;

import static supermemnon.pixelmonperms.util.CommandUtils.executeCommandList;


public class PermUtils {
    public static boolean isValidEntry(CompoundNBT entry, PlayerEntity player) {
        int eval = entry.getInt(NBTHandler.evalKey);
        String[] perms = NBTHandler.propertyListToArray(entry.getList(NBTHandler.permListKey, NBTHandler.STRING_NBT_TYPE));
        switch(NBTHandler.EVAL.getFromValue(eval)) {
            case AND:
                return hasAllRequiredPermissions(player, perms);
            case OR:
                return hasAnyRequiredPermissions(player, perms);
            case NOT:
                return !hasAnyRequiredPermissions(player, perms);
        }
        return false;
    }

    public static int findFirstValidEntry(ListNBT entryList, PlayerEntity player) {
        for (int i = 0; i < entryList.size(); i++) {
            if (isValidEntry(entryList.getCompound(i), player)) {
                return i;
            }
        }
        return -1;
    }


    public static boolean hasAnyRequiredPermissions(PlayerEntity player, String[] permList) {
        for (int i = 0; i < permList.length; i++) {
            if (PermissionAPI.hasPermission(player, permList[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllRequiredPermissions(PlayerEntity player, String[] permList) {
        if (permList.length < 1) {
            return false;
        }
        for (int i = 0; i < permList.length; i++) {
            if (!PermissionAPI.hasPermission(player, permList[i])) {
                return false;
            }
        }
        return true;
    }
}
