package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
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
                String[] strList = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strList[i] = list.getString(i);
                }
                str = str.concat("\n").concat(formatIndexedStringList(strList));
            }
        }
        return str;
    }
}
