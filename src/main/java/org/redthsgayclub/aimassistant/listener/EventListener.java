package org.redthsgayclub.aimassistant.listener;

import cc.polyfrost.oneconfig.config.core.OneColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;

import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.redthsgayclub.aimassistant.AimAssistant;
import org.redthsgayclub.aimassistant.config.Config;

public class EventListener {
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
        if (!AimAssistant.config.enabled) return;
        if (event.entityPlayer.equals(mc.thePlayer)) return;
        if (Config.mode) {
            //range mode
            double distance = event.entityPlayer.getDistanceSq(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
            if (distance > Config.range * Config.range) return;
            AxisAlignedBB aabb = getAABB(event.entityPlayer, event.partialRenderTick);
            renderBox(aabb, Config.boxColor);

        } else {
            //target mode
            if (!isLooking(event.entityPlayer)) return;
            AxisAlignedBB aabb = getAABB(event.entityPlayer, event.partialRenderTick);
            renderBox(aabb, Config.boxColor);

        }

    }

    private void renderBox(AxisAlignedBB aabb, OneColor color) {
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        if (color.getAlpha() > 0.0F) {
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            renderCube(aabb);
        }
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private static void renderCube(AxisAlignedBB aabb) {
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        tessellator.draw();
    }

    private static AxisAlignedBB getAABB(Entity target, float partialTicks) {

        Vec3 cameraPosRelToEntity = mc.thePlayer.getPositionEyes(partialTicks).subtract(getLerpedPos(mc.thePlayer, partialTicks));
        AxisAlignedBB boxAtOrigin = target.getEntityBoundingBox().offset(-mc.thePlayer.posX, -mc.thePlayer.posY, -mc.thePlayer.posZ);
        float halfRange = Config.size / 2;
        AxisAlignedBB shrunkenTargetBox = boxAtOrigin.contract(halfRange, halfRange, halfRange);
        Vec3 bestHitPos = pointClampedIntoBox(cameraPosRelToEntity, shrunkenTargetBox);

        return new AxisAlignedBB(bestHitPos.xCoord, bestHitPos.yCoord, bestHitPos.zCoord, bestHitPos.xCoord, bestHitPos.yCoord, bestHitPos.zCoord).expand(halfRange, halfRange, halfRange);
    }

    private static boolean isLooking(Entity target) {
        Vec3 vec = new Vec3(mc.thePlayer.getLookVec().xCoord * Config.range, mc.thePlayer.getLookVec().yCoord * Config.range, mc.thePlayer.getLookVec().zCoord * Config.range);
        return target.getEntityBoundingBox().calculateIntercept(mc.thePlayer.getPositionEyes(1.0f), mc.thePlayer.getPositionEyes(1.0F).add(vec)) != null;
    }

    private static Vec3 getLerpedPos(Entity entity, float partialTicks) {
        return new Vec3(entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks, entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks, entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks);
    }

    private static Vec3 pointClampedIntoBox(Vec3 point, AxisAlignedBB box) {
        double x = MathHelper.clamp_double(point.xCoord, box.minX, box.maxX);
        double y = MathHelper.clamp_double(point.yCoord, box.minY, box.maxY);
        double z = MathHelper.clamp_double(point.zCoord, box.minZ, box.maxZ);
        return new Vec3(x, y, z);
    }

}
