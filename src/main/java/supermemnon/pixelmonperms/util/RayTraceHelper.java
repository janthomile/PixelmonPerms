package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import supermemnon.pixelmonperms.PixelmonPerms;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RayTraceHelper {
    public static Entity getEntityLookingAt(ServerPlayerEntity player, double maxDistance) {
        Vector3d playerPos = player.getEyePosition(1.0F);
        Vector3d lookVec = player.getLookAngle();
        Vector3d targetVec = playerPos.add(lookVec.scale(maxDistance));
        for (Entity entity : player.level.getEntities(player, player.getBoundingBox().inflate(maxDistance))) {
            if (doesLineIntersectAABB(playerPos, targetVec, entity.getBoundingBox())) {
                return entity;
            }
        }
        return null;
    }

    public static boolean doesLineIntersectAABB(Vector3d rayStart, Vector3d rayEnd, AxisAlignedBB aabb) {

        return aabb.intersects(Math.min(rayStart.x, rayEnd.x), Math.min(rayStart.y, rayEnd.y), Math.min(rayStart.z, rayEnd.z), Math.max(rayStart.x, rayEnd.x), Math.max(rayStart.y, rayEnd.y), Math.max(rayStart.z, rayEnd.z));
    }
}
