package supermemnon.pixelmonperms.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;


public class PermUtils {
    public static boolean hasAllRequiredPermissions(PlayerEntity player, String[] permList) {
        for (int i = 0; i < permList.length; i++) {
            if (!PermissionAPI.hasPermission(player, permList[i])) {
                return false;
            }
            else {
            }
        }
        return true;
    }
}
