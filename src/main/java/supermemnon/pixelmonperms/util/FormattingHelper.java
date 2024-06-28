package supermemnon.pixelmonperms.util;

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
}
