package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Level;
import supermemnon.pixelmonperms.NBTHandler;
import supermemnon.pixelmonperms.PixelmonPerms;


public class PermUtils {
    public static boolean hasAllRequiredPermissions(PlayerEntity player, String[] permList) {
        for (String perm : permList) {
            if (!PermissionAPI.hasPermission(player, perm)) {
                return false;
            }
            else {
            }
        }
        return true;
    }
}
