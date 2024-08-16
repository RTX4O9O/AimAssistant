package org.redthsgayclub.aimassistant.listener

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.redthsgayclub.aimassistant.config.ModConfig
import net.minecraft.client.renderer.GlStateManager as GL

object EventListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun renderPlayerEvent(event: RenderPlayerEvent.Post) {
        if (!ModConfig.enabled) return
        val player = mc.thePlayer ?: return
        val target = event.entityPlayer
        val partialTicks = event.partialRenderTick
        if (target == player) return

        if (ModConfig.mode) { // range mode
            val distance = target.getDistanceSqToEntity(player)
            if (distance > ModConfig.range * ModConfig.range) return
        } else { //target mode
            val eyes = mc.thePlayer.getPositionEyes(partialTicks)
            val lookVector = mc.thePlayer.getLook(partialTicks) * ModConfig.range.toDouble()
            val intercept = target.actualHitbox.calculateIntercept(eyes, eyes + lookVector)
            if (intercept == null) return
        }

        val lerpedPos = player.getLerpedPos(partialTicks)
        val targetSmoothOffset = (target.prevPos - target.pos) * (1.0 - partialTicks.toDouble())
        val boxAtOrigin = target.actualHitbox.offset(targetSmoothOffset - lerpedPos)
        val camera = Vec3(0.0, player.eyeHeight.toDouble(), 0.0)
        val bestHitPos = camera.coerceInto(boxAtOrigin)
        val tooClose = camera.squareDistanceTo(bestHitPos) < 0.1 * 0.1
        if (tooClose) return
        val halfRange = ModConfig.size / 2.0
        val shrunkenTargetBox = boxAtOrigin.contract(halfRange)
        val shrunkenBestHitPos = camera.coerceInto(shrunkenTargetBox)
        val box = shrunkenBestHitPos.toBox().expand(halfRange)

        if (mc.pointedEntity == event.entityPlayer) {
            renderBox(box, ModConfig.inReachColor)
        } else {
            renderBox(box, ModConfig.boxColor)
        }
    }

    private val Entity.actualHitbox get() = entityBoundingBox.expand(collisionBorderSize.toDouble())
    private val Entity.prevPos get() = Vec3(prevPosX, prevPosY, prevPosZ)
    private val Entity.pos get() = Vec3(posX, posY, posZ)
    private fun Entity.getLerpedPos(partialTicks: Float) = prevPos + (pos - prevPos) * partialTicks.toDouble()
    private fun AxisAlignedBB.offset(vec: Vec3) = this.offset(vec.xCoord, vec.yCoord, vec.zCoord)
    private fun AxisAlignedBB.contract(amount: Double) = this.contract(amount, amount, amount)
    private fun AxisAlignedBB.expand(amount: Double) = this.expand(amount, amount, amount)
    private fun Vec3.toBox() = AxisAlignedBB(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord)
    private operator fun Vec3.plus(other: Vec3) = this.add(other)
    private operator fun Vec3.minus(other: Vec3) = this.subtract(other)
    private operator fun Vec3.unaryMinus() = Vec3(-xCoord, -yCoord, -zCoord)
    private operator fun Vec3.times(scale: Double) = Vec3(xCoord * scale, yCoord * scale, zCoord * scale)
    private fun Vec3.coerceInto(box: AxisAlignedBB) = Vec3(
        xCoord.coerceIn(box.minX, box.maxX),
        yCoord.coerceIn(box.minY, box.maxY),
        zCoord.coerceIn(box.minZ, box.maxZ),
    )

    private fun renderBox(aabb: AxisAlignedBB, color: OneColor) {
        GL.pushMatrix()
        GL.disableAlpha()
        GL.enableBlend()
        GL.tryBlendFuncSeparate(770, 771, 1, 0)
        GL.disableTexture2D()
        GL.depthMask(true)
        GL.disableLighting()
        GL.disableCull()
        GL.disableDepth()
        GL.color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
        val tessellator = Tessellator.getInstance()
        val wr = tessellator.worldRenderer
        wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
        wr.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        wr.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        wr.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        wr.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        wr.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        wr.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        wr.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        wr.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        tessellator.draw()
        wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION)
        wr.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex()
        wr.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex()
        wr.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex()
        wr.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex()
        wr.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex()
        wr.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex()
        wr.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex()
        wr.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex()
        tessellator.draw()
        GL.color(1f, 1f, 1f, 1f)
        GL.enableCull()
        GL.enableDepth()
        GL.enableLighting()
        GL.depthMask(true)
        GL.enableTexture2D()
        GL.disableBlend()
        GL.enableAlpha()
        GL.popMatrix()
    }
}
