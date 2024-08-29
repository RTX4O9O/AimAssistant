package org.redthsgayclub.aimassistant.listener

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Angle(var yaw: Float, var pitch: Float)

fun Double.square() = this * this

fun Vec3.getPitchYawFromPos(pos: Vec3): Angle = getPitchYawFromPos(pos.xCoord, pos.yCoord, pos.zCoord)

fun Vec3.getPitchYawFromPos(x: Double, y: Double, z: Double): Angle {
    val xDifference = x - xCoord
    val zDifference = z - zCoord
    val horizontalDistance = sqrt(xDifference.square() + zDifference.square())
    val verticalDistance = y - yCoord
    val pitch = Math.toDegrees(atan2(-verticalDistance, horizontalDistance)).toFloat()
    val yaw = Math.toDegrees(-atan2(xDifference, zDifference)).toFloat()
    return Angle(yaw, pitch)
}

fun rayTrace(blockReachDistance: Double, pitch: Float, yaw: Float, partialTicks: Float = 1f): MovingObjectPosition? {
    val vec3 = mc.thePlayer.getPositionEyes(partialTicks)
    val vec31 = getVectorForRotation(pitch, yaw)
    val vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance)
    return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true)
}

fun getVectorForRotation(pitch: Float, yaw: Float): Vec3 {
    val f = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f)
    val f1 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f)
    val f2 = -MathHelper.cos(-pitch * 0.017453292f)
    val f3 = MathHelper.sin(-pitch * 0.017453292f)
    return Vec3((f1 * f2).toDouble(), f3.toDouble(), (f * f2).toDouble())
}

data class Area(val left: Float, val right: Float, val top: Float, val bottom: Float)

fun Vec3.getArea(box: AxisAlignedBB): Area {
    val points = ArrayList<Vec3>()
    with(box) {
        points.add(Vec3(minX, minY, minZ))
        points.add(Vec3(minX, minY, maxZ))
        points.add(Vec3(minX, maxY, minZ))
        points.add(Vec3(minX, maxY, maxZ))
        points.add(Vec3(maxX, minY, minZ))
        points.add(Vec3(maxX, minY, maxZ))
        points.add(Vec3(maxX, maxY, minZ))
        points.add(Vec3(maxX, maxY, maxZ))
    }
    val angles = points.map { getPitchYawFromPos(it) }
    var yawA = angles.minOf { it.yaw }
    val yawB = angles.maxOf { it.yaw }
    if (yawA + 180 < yawB) yawA += 360
    return Area(min(yawA, yawB), max(yawA, yawB), angles.minOf { it.pitch }, angles.maxOf { it.pitch })
}