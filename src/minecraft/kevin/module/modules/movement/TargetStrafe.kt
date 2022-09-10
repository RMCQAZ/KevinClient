package kevin.module.modules.movement

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils
import kevin.utils.RotationUtils
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

object TargetStrafe : Module("TargetStrafe","Strafe around your target.", category = ModuleCategory.MOVEMENT) {
    private val radius = FloatValue("Radius", 2.0f, 0.1f, 4.0f)
    private val render = BooleanValue("Render", true)
    private val space = BooleanValue("HoldSpace", false)
    private val safewalk = BooleanValue("SafeWalk", true)
    private val onlySpeed = BooleanValue("OnlySpeed", false)
    private var direction = -1

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState === EventState.PRE) {
            if (mc.thePlayer.isCollidedHorizontally) {
                switchDirection()
            }
            if (mc.gameSettings.keyBindLeft.isKeyDown) {
                direction = 1
            }
            if (mc.gameSettings.keyBindRight.isKeyDown) {
                direction = -1
            }
        }
    }

    private fun switchDirection() {
        direction = if (direction == 1) -1 else 1
    }

    @EventTarget
    fun strafe(event: MoveEvent) {
        val target = KevinClient.combatManager.target
        if (canStrafe(target))
            setSpeed(event, MovementUtils.speed.toDouble(), RotationUtils.getRotationsEntity(target).yaw, direction.toDouble(), if (mc.thePlayer.getDistanceToEntity(target) <= radius.get()) 0.0 else 1.0)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (safewalk.get() && mc.thePlayer.onGround && canStrafe(KevinClient.combatManager.target))
            event.isSafeWalk = true
    }

    private fun setSpeed(moveEvent: MoveEvent, moveSpeed: Double, pseudoYaw: Float, pseudoStrafe: Double, pseudoForward: Double) {
        var forward = pseudoForward
        var strafe = pseudoStrafe
        var yaw = pseudoYaw

        if (forward == .0 && strafe == .0) {
            moveEvent.z = .0
            moveEvent.x = .0
        } else {
            if (forward != .0) {
                if (strafe > .0) {
                    yaw += (if (forward > .0) -45 else 45)
                } else if (strafe < .0) {
                    yaw += (if (forward > .0) 45 else -45)
                }
                strafe = 0.0
                if (forward > .0) {
                    forward = 1.0
                } else if (forward < .0) {
                    forward = -1.0
                }
            }
            val cos = cos(Math.toRadians(yaw + 90.0))
            val sin = sin(Math.toRadians(yaw + 90.0))

            moveEvent.x = (forward * moveSpeed * cos + strafe * moveSpeed * sin)
            moveEvent.z = (forward * moveSpeed * sin - strafe * moveSpeed * cos)
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val target = KevinClient.combatManager.target
        if (canStrafe(target) && render.get()) {
            target?:return
            GL11.glPushMatrix()
            GL11.glDisable(3553)
            GL11.glEnable(2848)
            GL11.glEnable(2881)
            GL11.glEnable(2832)
            GL11.glEnable(3042)
            GL11.glBlendFunc(770, 771)
            GL11.glHint(3154, 4354)
            GL11.glHint(3155, 4354)
            GL11.glHint(3153, 4354)
            GL11.glDisable(2929)
            GL11.glDepthMask(false)
            GL11.glLineWidth(1.0f)

            GL11.glBegin(3)
            val x = target.lastTickPosX + (target.posX - target.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
            val y = target.lastTickPosY + (target.posY - target.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY
            val z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
            for (i in 0..360) {
                val rainbow = Color(Color.HSBtoRGB((mc.thePlayer.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
                GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                GL11.glVertex3d(x + radius.get() * cos(i * 6.283185307179586 / 45.0), y, z + radius.get() * sin(i * 6.283185307179586 / 45.0))
            }
            GL11.glEnd()

            GL11.glDepthMask(true)
            GL11.glEnable(2929)
            GL11.glDisable(2848)
            GL11.glDisable(2881)
            GL11.glEnable(2832)
            GL11.glEnable(3553)
            GL11.glPopMatrix()
        }
    }

    private fun canStrafe(target: EntityLivingBase?): Boolean {
        return this.state && target != null && (!space.get() || mc.thePlayer.movementInput.jump) && (!onlySpeed.get() || KevinClient.moduleManager.getModule(Speed::class.java).state)
    }
}