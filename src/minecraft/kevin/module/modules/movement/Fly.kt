package kevin.module.modules.movement

import kevin.event.*
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.exploit.Disabler
import kevin.utils.*
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin


class Fly : Module("Fly","Allow you fly", Keyboard.KEY_F,ModuleCategory.MOVEMENT) {
    private val speed = FloatValue("Speed",2F,0.5F,5F)

    val mode = ListValue("Mode", arrayOf("Vanilla","Creative","AAC5","NCP","Teleport","VerusAuto"),"Vanilla")

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


    private val resetMotion = BooleanValue("ResetMotion",false)
    private val keepAlive = BooleanValue("KeepAlive",false)

    private val aac5PacketMode = ListValue("AAC5PacketMode", arrayOf("Old", "Rise"), "Old")
    private val aac5UseC04 = BooleanValue("AAC5UseC04", false)
    private val aac5Purse = IntegerValue("AAC5.2.0Purse", 7, 3, 20)

    private val ncpDelay = IntegerValue("NCPDelay",500,100,1000)

    private val renderPath = BooleanValue("RenderPath",true)
    private val glLineWidthValue = FloatValue("RenderPathLineWidth",2F,0.5F,4F)
    private val colorMode = ListValue("ColorMode", arrayOf("Custom","Rainbow"),"Custom")
    private val colorR = IntegerValue("R",255,0,255)
    private val colorG = IntegerValue("G",255,0,255)
    private val colorB = IntegerValue("B",255,0,255)

    //Teleport
    private val teleportLongValue = FloatValue("TeleportLong",10F,0.1F,20F)
    private val teleportTimer = FloatValue("TeleportTimer",0.1F,0.1F,1F)
    private val teleportHighPacket = BooleanValue("TeleportHPacket",true)
    private val teleportHigh = FloatValue("TeleportHigh",-1F,-4F,4F)
    private val teleportYMotion = FloatValue("TeleportYMotion",-0.05F,-1F,1F)
    private val teleportMotion = FloatValue("TeleportMotion",2F,0F,5F)
    private val teleportResetMotion = BooleanValue("TeleportResetMotion",true)
    private val teleportSetPos = BooleanValue("TeleportSetPos",false)

    private val fakeDamageValue = BooleanValue("FakeDamage", true)

    private var isFlying = false

    private val flyTimer = MSTimer()
    private var aac5FlyClip = false
    private var aac5FlyStart = false
    private var aac5nextFlag = false

    private val ncpTimer = MSTimer()

    //private val packetList = arrayListOf<Packet<*>>()

    private fun sendPacketNoEvent(packet: Packet<*>){
        /**
        packetList.add(packet)
        mc.netHandler.addToSendQueue(packet)
        **/
        PacketUtils.sendPacketNoEvent(packet)
    }

    @EventTarget fun onMotion(event: MotionEvent){
        when(mode.get()){
            "Teleport" -> {
                mc.thePlayer!!.jumpMovementFactor = 0F
                mc.thePlayer!!.motionY = teleportYMotion.get().toDouble()
                mc.timer.timerSpeed = teleportTimer.get()
                if (mc.thePlayer!!.ticksExisted % 2 == 0){
                    val playerYaw = mc.thePlayer!!.rotationYaw * Math.PI / 180
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer!!.posX + teleportLongValue.get() * -sin(playerYaw),
                            mc.thePlayer!!.posY,
                            mc.thePlayer!!.posZ + teleportLongValue.get() * cos(playerYaw),
                            false
                        )
                    )
                    if(teleportHighPacket.get()) {
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer!!.posX,
                                mc.thePlayer!!.posY + teleportHigh.get(),
                                mc.thePlayer!!.posZ,
                                false
                            )
                        )
                    }
                    mc.thePlayer!!.motionX = teleportMotion.get() * -sin(playerYaw)
                    mc.thePlayer!!.motionZ = teleportMotion.get() * cos(playerYaw)
                    if (teleportSetPos.get())
                        mc.thePlayer.setPosition(
                            mc.thePlayer!!.posX + teleportLongValue.get() * -sin(playerYaw),
                            mc.thePlayer!!.posY,
                            mc.thePlayer!!.posZ + teleportLongValue.get() * cos(playerYaw)
                        )
                }else{
                    if (teleportResetMotion.get()) mc.thePlayer!!.motionY = .0
                    mc.thePlayer!!.motionX = .0
                    mc.thePlayer!!.motionZ = .0
                }
            }
            "VerusAuto" -> if (
                verusMoveJump&&
                !verusVanilla&&
                verusState!=1&&
                event.eventState==EventState.PRE&&
                !mc.gameSettings.keyBindJump.isKeyDown&&
                mc.thePlayer.jumpTicks==0&&
                !mc.thePlayer.isInWater&&
                !mc.thePlayer.isInLava&&
                !mc.thePlayer.isInWeb&&
                !mc.thePlayer.isOnLadder&&
                mc.thePlayer.posY == round(mc.thePlayer.posY)
            ) mc.thePlayer.jump()
        }
    }

    override fun onEnable() {
        isFlying = mc.thePlayer.capabilities.isFlying

        if(mc.thePlayer.onGround&&fakeDamageValue.get()) mc.thePlayer.handleStatusUpdate(2)

        when(mode.get()){
            "Creative" -> mc.thePlayer.capabilities.allowFlying = true
            "AAC5" -> {
                if (mc.isSingleplayer) {
                    KevinClient.hud.addNotification(
                        Notification("Use AAC5 Flys will crash single player"),"Fly")
                    toggle(false)
                    return
                }

                flyTimer.reset()
                aac5FlyClip=false
                aac5FlyStart=false
                aac5nextFlag=false
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ)
            }
            "NCP" -> {
                if (mc.thePlayer.onGround) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ)
                ncpTimer.reset()
            }
            "VerusAuto" -> {
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
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.5, mc.thePlayer.posZ, false))
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false))
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
                            //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ)
                        }
                    }
                    verusState = 1
                    mc.thePlayer.speedInAir = 0F
                    mc.thePlayer.hurtTime = 0
                    mc.thePlayer.onGround = false
                }
            }
        }
    }

    override fun onDisable() {
        mc.thePlayer.capabilities.isFlying = isFlying&&(mc.playerController.isSpectator||mc.playerController.isInCreativeMode)
        verusState = 0
        when(mode.get()){
            "Creative" -> mc.thePlayer.capabilities.allowFlying = mc.playerController.isInCreativeMode || mc.playerController.isSpectatorMode
            "Vanilla" -> {
                if (resetMotion.get()) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
            }
            "AAC5"->{
                if (mc.isSingleplayer) return
                sendAAC5Packets()
                mc.thePlayer.noClip = false
                mc.timer.timerSpeed = 1F
            }
            "NCP" -> {
                mc.thePlayer.speedInAir = 0.02F
            }
            "Teleport" -> {
                mc.timer.timerSpeed = 1F
            }
            "VerusAuto" -> {
                mc.thePlayer.speedInAir = .02F
            }
        }
    }

    @EventTarget fun onRender3D(event: Render3DEvent){
        if (!renderPath.get()||!(mode equal "AAC5")||aac5C03List.isEmpty()) return
        val color = if (colorMode.get() == "Custom") Color(colorR.get(),colorG.get(),colorB.get()) else ColorUtils.rainbow()

        val renderPosX = mc.renderManager.viewerPosX
        val renderPosY = mc.renderManager.viewerPosY
        val renderPosZ = mc.renderManager.viewerPosZ

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDepthMask(false)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)

        aac5C03List.forEach {
            val x = it.x - renderPosX
            val y = it.y - renderPosY
            val z = it.z - renderPosZ
            val width = 0.3
            val height = mc.thePlayer!!.eyeHeight.toDouble()
            GL11.glLoadIdentity()
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2)
            RenderUtils.glColor(color)
            GL11.glLineWidth(glLineWidthValue.get())
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y + height, z - width)
            GL11.glVertex3d(x + width, y + height, z - width)
            GL11.glVertex3d(x + width, y, z - width)
            GL11.glVertex3d(x - width, y, z - width)
            GL11.glVertex3d(x - width, y, z + width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x + width, y, z + width)
            GL11.glVertex3d(x + width, y + height, z + width)
            GL11.glVertex3d(x - width, y + height, z + width)
            GL11.glVertex3d(x - width, y, z + width)
            GL11.glVertex3d(x + width, y, z + width)
            GL11.glVertex3d(x + width, y, z - width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x + width, y + height, z + width)
            GL11.glVertex3d(x + width, y + height, z - width)
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex3d(x - width, y + height, z + width)
            GL11.glVertex3d(x - width, y + height, z - width)
            GL11.glEnd()
        }

        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
        GL11.glColor4f(1F, 1F, 1F, 1F)
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        if (mode equal "AAC5") toggle(false)
    }

    private val verusVanilla: Boolean
    get() = (Disabler.modeValue.get().contains("verusmove",true)&&Disabler.getToggle()) || (verusState == 2&&!verusTimer.hasTimePassed(verusBoostTicks.get()+1))

    @EventTarget fun onBB(event: BlockBBEvent){
        if (mode equal "VerusAuto"&&
            !verusVanilla&&
            verusState!=1&&
            event.block==Blocks.air&&
            if (verusMoveJump) (event.y < y&&event.y < mc.thePlayer!!.posY) else event.y < mc.thePlayer!!.posY&&
            mc.thePlayer.getDistance(event.x.toDouble(),event.y.toDouble(),event.z.toDouble()) < 1.5) event.boundingBox =
            AxisAlignedBB(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x + 1.0,
                (if (verusMoveJump) (if (y.toDouble() > mc.thePlayer.posY) mc.thePlayer.posY.toInt().toDouble() else y.toDouble()) else mc.thePlayer.posY.toInt().toDouble()) - if (verusDown.get()&&GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) 1.0 else .0,
                event.z + 1.0
            )
    }

    @EventTarget fun onStep(event: StepEvent){
        if (mode equal "VerusAuto"&&!verusVanilla&&verusState!=1) event.stepHeight = 0f
    }

    @EventTarget fun onJump(event: JumpEvent) {
        if (mode equal "VerusAuto"&&!verusVanilla&&verusState!=1&&!(verusJump.get()&&MovementUtils.isMoving)) event.cancelEvent()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        //if (event.eventState == UpdateState.OnUpdate) return

        when(mode.get()){
            "VerusAuto" -> {
                if (verusState == 2) verusTimer.update()
                if (verusVanilla){
                    if (keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.capabilities.isFlying = false
                    if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.get()
                    if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.get()
                    MovementUtils.strafe(speed.get())
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
            "Vanilla" -> {
                if (keepAlive.get()) mc.netHandler.addToSendQueue(C00PacketKeepAlive())
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.capabilities.isFlying = false
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.get()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.get()
                MovementUtils.strafe(speed.get())
            }
            "Creative" -> if (!mc.thePlayer.capabilities.allowFlying) mc.thePlayer.capabilities.allowFlying = true
            "AAC5" -> {
                mc.thePlayer.noClip=!MovementUtils.isMoving
                if(!flyTimer.hasTimePassed(1000) || !aac5FlyStart) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.jumpMovementFactor = 0.00f
                    mc.timer.timerSpeed = 0.32F
                    return
                }else {
                    if(!aac5FlyClip) {
                        mc.timer.timerSpeed = 0.19F
                    }else{
                        aac5FlyClip=false
                        mc.timer.timerSpeed = 1.2F
                    }
                }
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.capabilities.isFlying = false
                if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.get()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.get()
                MovementUtils.strafe(speed.get())
            }
            "NCP" -> {
                mc.thePlayer.motionX = .0
                mc.thePlayer.motionY = .0
                mc.thePlayer.motionZ = .0
                mc.thePlayer.speedInAir = 0F
                if (ncpTimer.hasTimePassed(ncpDelay.get().toLong())){
                    val speed = 0.99
                    val radiansYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                    PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX + speed * -sin(radiansYaw),mc.thePlayer.posY, mc.thePlayer.posZ + speed * cos(radiansYaw),mc.thePlayer.onGround))
                    PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX + speed * -sin(radiansYaw),-RandomUtils.nextDouble(160.00000001,370.00000001), mc.thePlayer.posZ + speed * cos(radiansYaw),false))
                    mc.thePlayer.setPosition(mc.thePlayer.posX + speed * -sin(radiansYaw),mc.thePlayer.posY, mc.thePlayer.posZ + speed * cos(radiansYaw))
                    ncpTimer.reset()
                }
            }
        }
    }

    private val aac5C03List = CopyOnWriteArrayList<C03PacketPlayer>()

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        //if (packet in packetList) return
        if (packet is S08PacketPlayerPosLook){
            when(mode.get()){
                "AAC5" -> {
                    aac5FlyStart=true
                    if(flyTimer.hasTimePassed(2000)) {
                        aac5FlyClip=true
                        mc.timer.timerSpeed = 1.3F
                    }
                    aac5nextFlag=true
                }
            }
        }
        if (packet is C03PacketPlayer){
            //if (packet.x == 1.7E+301||packet.y == Double.MAX_VALUE||packet.z == 1.7E+301) return
            when(mode.get()){
                "AAC5" -> {
                    event.cancelEvent()
                    val f = mc.thePlayer.width / 2.0
                    // need to no collide else will flag
                    if(aac5nextFlag || !mc.theWorld.checkBlockCollision(AxisAlignedBB(packet.x - f, packet.y, packet.z - f, packet.x + f, packet.y + mc.thePlayer.height, packet.z + f))){
                        aac5C03List.add(packet)
                        aac5nextFlag=false
                        event.cancelEvent()
                        if(flyTimer.hasTimePassed(1000) && aac5C03List.size > aac5Purse.get()) {
                            sendAAC5Packets()
                        }
                    }
                }
                "VerusAuto" -> {
                    if (!verusVanilla&&mc.thePlayer.posY == round(mc.thePlayer.posY)) packet.onGround = true
                }
            }
        }
    }

    private fun sendAAC5Packets() {
        var yaw = mc.thePlayer.rotationYaw
        var pitch = mc.thePlayer.rotationPitch
        if (aac5PacketMode.get().equals("Old",true)) {
            for (packet in aac5C03List) {
                if (packet.isMoving) {
                    sendPacketNoEvent(packet)
                    if (packet.getRotating()) {
                        yaw = packet.yaw
                        pitch = packet.pitch
                    }
                    if (aac5UseC04.get()) {
                        sendPacketNoEvent(C04PacketPlayerPosition(packet.x, 1e+159, packet.z, true))
                        sendPacketNoEvent(C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
                    } else {
                        sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, 1e+159, packet.z, yaw, pitch, true))
                        sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, packet.y, packet.z, yaw, pitch, true))
                    }
                }
            }
        } else {
            for (packet in aac5C03List) {
                if (packet.isMoving) {
                    sendPacketNoEvent(packet)
                    if (packet.getRotating()) {
                        yaw = packet.yaw
                        pitch = packet.pitch
                    }
                    if (aac5UseC04.get()) {
                        sendPacketNoEvent(C04PacketPlayerPosition(packet.x, -1e+159, packet.z + 10, true))
                        sendPacketNoEvent(C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
                    } else {
                        sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, -1e+159, packet.z + 10, yaw, pitch, true))
                        sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, packet.y, packet.z, yaw, pitch, true))
                    }
                }
            }
        }
        aac5C03List.clear()
    }

    override val tag: String
        get() = mode.get()
}