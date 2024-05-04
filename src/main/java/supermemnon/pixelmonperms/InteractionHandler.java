package supermemnon.pixelmonperms;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public class InteractionHandler {

    static String nbtPermString = "pixelmonperm";
    static String nbtCancelString = "pixelmoncancel";
    static String defaultCancelMessage = "Talk to me later!";
    public static String getRequiredPermission(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getString(nbtPermString);
    }

    public static void setRequiredPermission(Entity entity, String permission) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtPermString, permission);
    }

    public static boolean removeRequirePermission(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtPermString)) {
            return false;
        }
        nbt.remove(nbtPermString);
        return true;
    }

    public static boolean hasRequiredPermission(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtPermString);
    }

    public static void setCancelMessage(Entity entity, String message) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtCancelString, message);
    }

    public static boolean hasCancelMessage(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtCancelString);
    }
    public static String getCancelMessage(Entity entity) {
        if (hasCancelMessage(entity)) {
            CompoundNBT  nbt = entity.getPersistentData();
            return nbt.getString(nbtCancelString);
        }
        else {
            return defaultCancelMessage;
        }
    }

    public static boolean removeCancelMessage(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtCancelString)) {
            return false;
        }
        nbt.remove(nbtCancelString);
        return true;
    }
}
