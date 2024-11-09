package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class FormattingHelper {
    public static String formatWithAmpersand(String message) {
        message = message.replace("&&", "__DOUBLE_AMPERSAND__");
        message = message.replace("&", "§");
        message = message.replace("__DOUBLE_AMPERSAND__", "&");
        return message;
    }

    public static String formatIndexedStringList(String[] list) {
        String returnString = "";
        for (int i = 0; i < list.length; i++) {
            returnString = returnString.concat(String.format("%d: %s\n", i, list[i]));
        }
        return returnString;
    }

    public static String getEntryQuickFormat(CompoundNBT entry) {
        String str = "{%s Permissions, %s Messages, %s Commands}";
        str = String.format(str, entry.getList(NBTHandler.permListKey, NBTHandler.STRING_NBT_TYPE).size(),
                entry.getList(NBTHandler.msgListKey, NBTHandler.STRING_NBT_TYPE).size(),
                entry.getList(NBTHandler.cmdListKey, NBTHandler.STRING_NBT_TYPE).size());
        return str;
    }

    public static String getEntryListFormatted(Entity entity) {
        ListNBT entryList = NBTHandler.getEntryList(entity);
        if (entryList == null) {
            return "";
        }
        String[] strList = new String[entryList.size()];
        for (int i = 0; i < entryList.size(); i++) {
            strList[i] = getEntryQuickFormat(entryList.getCompound(i));
        }
        String str = String.format("Entries:\n%s", formatIndexedStringList(strList));
        return str;
    }

    public static String getEntryPropertyString(Entity entity, int entryIndex, String property) {
        String str = String.format("%s:\n", property.toUpperCase());
        switch (property) {
            case "eval": {
                int eval = NBTHandler.getEntryEval(entity, entryIndex);
                if (eval == -1) {
                    return "";
                }
                str = str.concat("\n").concat( NBTHandler.EVAL.getNameFromValue(eval));
            }
            case "permission": case "message": case "command": {
                ListNBT list = NBTHandler.getEntryListProperty(entity, entryIndex, property);
                if (list == null) {
                    return "";
                }
                String[] strList = NBTHandler.propertyListToArray(list);
                str = str.concat("\n").concat(formatIndexedStringList(strList));
            }
        }
        return str;
    }
}
