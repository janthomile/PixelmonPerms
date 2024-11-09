package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class LegacyNBTHandler {
    static String nbtPermString = "pixelmonperm";
    static String nbtCancelString = "pixelmoncancel";
    static String nbtFailCommandString = "pixelpermfailcmd";
    static String defaultCancelMessage = "Talk to me later!";
    static String permListDelimiter = ",";
    static String altListDelimiter = "||";
    static String altListDelimiterRegex = "\\|\\|";

    public static boolean refactorLegacyFormat(Entity entity, boolean forceRefactor) {
        if (!NBTHandler.hasEntryList(entity)) {
            NBTHandler.initEntryList(entity);
        }
        else if (!forceRefactor) {
            return false;
        }

        CompoundNBT entry = NBTHandler.createEntry(
                NBTHandler.EVAL.NOT.value,
                createListNbt(getRequiredPermissions(entity)),
                createListNbt(getCancelMessages(entity)),
                createListNbt(getFailCommands(entity))
        );

        NBTHandler.appendEntry(entity, entry);

        return true;
    }

    public static ListNBT createListNbt(String[] array) {
        ListNBT list = new ListNBT();
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals("")) {
                continue;
            }
            list.add(StringNBT.valueOf(array[i]));
        }
        return list;
    }

    public static void removeLegacyData(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.remove(nbtPermString);
        nbt.remove(nbtCancelString);
        nbt.remove(nbtFailCommandString);
    }

    public static boolean entityHasLegacyFormat(Entity entity) {
        return hasRequiredPermission(entity) || hasCancelMessage(entity) || hasFailCommand(entity);
    }

    public static boolean isStringNbt(Entity entity, String search_nbt) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getTagType(search_nbt) == Constants.NBT.TAG_STRING;
    }

    public static String[] getRequiredPermissions(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return parseStringList(nbt.getString(nbtPermString), permListDelimiter);
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
        if (newPermList.size() < 1) {
            nbt.remove(nbtPermString);
        }
        return true;
    }

    public static boolean hasRequiredPermission(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtPermString);
    }

    public static boolean removeFailCommand(Entity entity, int index) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtFailCommandString)) {
            return false;
        }
        ArrayList<String> newCommandList = new ArrayList<> (Arrays.asList(getFailCommands(entity)));
        newCommandList.remove(index);
        setFailCommand(entity, String.join(altListDelimiter, newCommandList));
        if (newCommandList.size() < 1) {
        nbt.remove(nbtFailCommandString);
        }
        return true;
    }

    public static String[] getFailCommands(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return parseAltList(nbt.getString(nbtFailCommandString));
    }

    public static String getFailCommand(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.getString(nbtFailCommandString);
    }

    public static void setFailCommand(Entity entity, String command) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtFailCommandString, command);
    }
    public static void appendFailCommand(Entity entity, String command) {
        if (!hasFailCommand(entity)) {
            setFailCommand(entity, command);
            return;
        }
        setFailCommand(entity, getFailCommand(entity).concat(altListDelimiter).concat(command));
    }

    public static boolean hasFailCommand(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtFailCommandString);
    }
    public static void appendCancelMessage(Entity entity, String message) {
        if (!hasCancelMessage(entity)) {
            setCancelMessage(entity, message);
            return;
        }
        setCancelMessage(entity,getCancelMessage(entity).concat(altListDelimiter).concat(message));
    }

    public static void setCancelMessage(Entity entity, String message) {
        CompoundNBT  nbt = entity.getPersistentData();
        nbt.putString(nbtCancelString, message);
    }

    public static boolean hasCancelMessage(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return nbt.contains(nbtCancelString);
    }
    public static String[] getCancelMessages(Entity entity) {
        CompoundNBT  nbt = entity.getPersistentData();
        return parseAltList(nbt.getString(nbtCancelString));
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

    public static boolean removeCancelMessage(Entity entity, int index) {
        CompoundNBT  nbt = entity.getPersistentData();
        if (!nbt.contains(nbtCancelString)) {
            return false;
        }
        ArrayList<String> newMessageList = new ArrayList<> (Arrays.asList(getCancelMessages(entity)));
        newMessageList.remove(index);
        setCancelMessage(entity, String.join(altListDelimiter, newMessageList));
        if (newMessageList.size() < 1) {
            nbt.remove(nbtCancelString);
        }
        return true;
    }
    public static String[] parseStringList(String string, String delimiter) {
        return string.split(delimiter);
    }
    public static String[] parseAltList(String string) {
        return string.split(altListDelimiterRegex);
    }
}
