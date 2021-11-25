package kevin.module.modules.movement.flys.verus

import kevin.event.*
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.modules.exploit.Disabler
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MovementUtils
import kevin.utils.TickTimer
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import kotlin.math.round

object VerusAuto : FlyMode("VerusAuto") {

    private val verusMoveMode = ListValue("VerusMoveMode", arrayOf("Walk","Jump"),"Walk")
    private val verusMoveJump: Boolean
        get() = verusMoveMode equal "Jump"
    private val verusBoost = ListValue("VerusBoost", arrayOf("None","Clip","Packet"),"Packet")
    private val verusBoostTicks = IntegerValue("VerusBoostTicks",10,1,15)
    private val verusBoostOnlyFirst = BooleanValue("VerusBoostOnlyFirst",true)
    private val verusJump = BooleanValue("VerusJump",true)
    private val verusDown = BooleanValue("VerusDown",true)
    private val verusDownNoSneak = BooleanValue("VerusDownNoSneak",true)

    private var verusState = 0
    private val verusTimer = TickTimer()
    private var playerY = .0
    private var y = 0

    override fun onEnable() {
        y = round(mc.thePlayer.posY).toInt()
        if (!(verusBoost equal "None")&&mc.thePlayer.onGround&&!verusVanilla&&mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,mc.thePlayer.entityBoundingBox.offset(.0, 3.5, .0).expand(.0, .0, .0)).isEmpty()) {
            playerY = mc.thePlayer.posY
            when(verusBoost.get()){
                "Clip" -> {
                    mc.thePlayer.setPositionAndUpdate(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 3.5,
                        mc.thePlayer.posZ
                    )
                    mc.thePlayer.motionX = .0
                    mc.thePlayer.motionZ = .0
                }
                "Packet" -> {
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 3.5,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ)
                }
            }
            verusState = 1
            mc.thePlayer.speedInAir = 0F
            mc.thePlayer.hurtTime = 0
            mc.thePlayer.onGround = false
        }
    }

    override fun onDisable() {
        verusState = 0
        mc.thePlayer.speedInAir = .02F
    }

    override fun onMotion(event: MotionEvent) {
        if (
            verusMoveJump&&
            !verusVanilla&&
            verusState!=1&&
            event.eventState== EventState.PRE&&
            !mc.gameSettings.keyBindJump.isKeyDown&&
            mc.thePlayer.jumpTicks==0&&
            !mc.thePlayer.isInWater&&
            !mc.thePlayer.isInLava&&
            !mc.thePlayer.isInWeb&&
            !mc.thePlayer.isOnLadder&&
            mc.thePlayer.posY == round(mc.thePlayer.posY)
        ) mc.thePlayer.jump()
    }

    override fun onBB(event: BlockBBEvent) {
        if (!verusVanilla&&
            verusState!=1&&
            event.block== Blocks.air&&
            if (verusMoveJump) (event.y < y&&event.y < mc.thePlayer!!.posY) else event.y < mc.thePlayer!!.posY&&
                    mc.thePlayer.getDistance(event.x.toDouble(),event.y.toDouble(),event.z.toDouble()) < 1.5) event.boundingBox =
            AxisAlignedBB(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x + 1.0,
                (if (verusMoveJump) (if (y.toDouble() > mc.thePlayer.posY) mc.thePlayer.posY.toInt().toDouble() else y.toDouble()) else mc.thePlayer.posY.toInt().toDouble()) - if (verusDown.get()&& GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) 1.0 else .0,
                event.z + 1.0
            )
    }

    override fun onStep(event: StepEvent) {
        if (!verusVanilla&&verusState!=1) event.stepHeight = 0f
    }

    override fun onJump(event: JumpEvent) {
        if (!verusVanilla&&verusState!=1&&!(verusJump.get()&& MovementUtils.isMoving)) event.cancelEvent()
    }

    override fun onUpdate(event: UpdateEvent) {
        if (verusState == 2) verusTimer.update()
        if (verusVanilla){
            if (fly.keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.capabilities.isFlying = false
            if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += fly.speed.get()
            if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= fly.speed.get()
            MovementUtils.strafe(fly.speed.get())
            y = round(mc.thePlayer.posY).toInt()
        }

        if (mc.thePlayer.motionY > 0.43) y = round(mc.thePlayer.posY).toInt()

        if (verusDown.get()&&GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            y = round(mc.thePlayer.posY).toInt()

        if (verusJump.get()&&mc.gameSettings.keyBindJump.isKeyDown)
            y = round(mc.thePlayer.posY).toInt()

        if (mc.thePlayer.onGround) y = round(mc.thePlayer.posY).toInt()

        if(mc.gameSettings.keyBindSneak.pressed&&verusDown.get()&&verusDownNoSneak.get()&&!verusVanilla)
            mc.gameSettings.keyBindSneak.pressed = false

        if (verusState == 1&&mc.thePlayer.posY < playerY) {
            KevinClient.hud.addNotification(Notification("Try fake ground damage boost!"),"Fly")
            verusState = 3
            mc.thePlayer.speedInAir = .02F
        }
        if (verusState == 1&&mc.thePlayer.posY > playerY&&mc.thePlayer.onGround) {
            KevinClient.hud.addNotification(Notification("Boost failed!"),"Fly")
            mc.thePlayer.speedInAir = .02F
            verusState = 2
            repeat(20){verusTimer.update()}
        }
        if (mc.thePlayer.hurtTime > 0&&((verusState == 1||verusState == 3)||(!verusBoostOnlyFirst.get()&&verusState!=0))) {
            verusState = 2
            mc.thePlayer.speedInAir = .02F
            verusTimer.reset()
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer){
            if (!verusVanilla&&mc.thePlayer.posY == round(mc.thePlayer.posY)) packet.onGround = true
        }
    }

    private val verusVanilla: Boolean
        get() = (Disabler.modeValue.get().contains("verusmove",true)&& Disabler.state) || (verusState == 2&&!verusTimer.hasTimePassed(verusBoostTicks.get()+1))
}