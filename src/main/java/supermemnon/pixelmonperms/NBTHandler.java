package supermemnon.pixelmonperms;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class NBTHandler {

    static String nbtPermString = "pixelmonperm";
    static String nbtCancelString = "pixelmoncancel";
    static String nbtFailCommandString = "pixelpermfailcmd";
    static String defaultCancelMessage = "Talk to me later!";
    static String permListDelimiter = ",";

    public static boolean isStringNbt(Entity entity, String search_nbt) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getTagType(search_nbt) == Constants.NBT.TAG_STRING;
    }

    public static String[] getRequiredPermissions(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return parseStringList(nbt.getString(nbtPermString));
    }

    public static String getRequiredPermissionString(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getString(nbtPermString);
    }

    public static void setRequiredPermission(Entity entity, String permission) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtPermString, permission);
    }

    public static void appendRequiredPermission(Entity entity, String permission) {
        if (!hasRequiredPermission(entity)) {
            setRequiredPermission(entity, permission);
            return;
        }
        setRequiredPermission(entity, getRequiredPermissionString(entity).concat(permListDelimiter).concat(permission));
    }

    public static boolean removeRequirePermission(Entity entity, int index) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtPermString)) {
            return false;
        }
        ArrayList<String> newPermList = new ArrayList<> (Arrays.asList(getRequiredPermissions(entity)));
        newPermList.remove(index);
        setRequiredPermission(entity, String.join(permListDelimiter, newPermList));
//        nbt.remove(nbtPermString);
        return true;
    }

    public static boolean hasRequiredPermission(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtPermString);
    }

    public static boolean removeFailCommand(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtFailCommandString)) {
            return false;
        }
        nbt.remove(nbtFailCommandString);
        return true;
    }

    public static String getFailCommand(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getString(nbtFailCommandString);
    }

    public static void setFailCommand(Entity entity, String command) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtFailCommandString, command);
    }

    public static boolean hasFailCommand(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtFailCommandString);
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

    public static String[] parseStringList(String string) {
        return string.split(permListDelimiter);
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
