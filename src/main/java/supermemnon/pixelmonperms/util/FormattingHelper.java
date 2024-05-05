package supermemnon.pixelmonperms.util;

public class FormattingHelper {
    public static String formatWithAmpersand(String message) {
        message = message.replace("&&", "__DOUBLE_AMPERSAND__");
        message = message.replace("&", "§");
        message = message.replace("__DOUBLE_AMPERSAND__", "&");
        return message;
    }
}
