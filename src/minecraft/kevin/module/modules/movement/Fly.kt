package kevin.module.modules.movement

import kevin.event.*
import kevin.hud.element.elements.Notification
import kevin.main.Kevin
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.input.Keyboard


class Fly : Module("Fly","Allow you fly", Keyboard.KEY_F,ModuleCategory.MOVEMENT) {
    private val speed = FloatValue("Speed",2F,0.5F,5F)
    val mode = ListValue("Mode", arrayOf("Vanilla","Creative","AAC5"),"Vanilla")
    private val resetMotion = BooleanValue("ResetMotion",false)
    private val keepAlive = BooleanValue("KeepAlive",false)

    private val aac5PacketMode = ListValue("AAC5PacketMode", arrayOf("Old", "Rise"), "Old")
    private val aac5UseC04 = BooleanValue("AAC5UseC04", false)
    private val aac5Purse = IntegerValue("AAC5.2.0Purse", 7, 3, 20)

    private val fakeDamageValue = BooleanValue("FakeDamage", true)

    private var isFlying = false

    private val flyTimer = MSTimer()
    private var aac5FlyClip = false
    private var aac5FlyStart = false
    private var aac5nextFlag = false

    private val packetList = arrayListOf<Packet<*>>()

    private fun sendPacketNoEvent(packet: Packet<*>){
        /**
        packetList.add(packet)
        mc.netHandler.addToSendQueue(packet)
        **/
        PacketUtils.sendPacketNoEvent(packet)
    }

    override fun onEnable() {
        isFlying = mc.thePlayer.capabilities.isFlying

        if(mc.thePlayer.onGround&&fakeDamageValue.get()) mc.thePlayer.handleStatusUpdate(2)

        when(mode.get()){
            "Creative" -> mc.thePlayer.capabilities.allowFlying = true
            "AAC5" -> {
                if (mc.isSingleplayer) {
                    Kevin.getInstance.hud.addNotification(
                        Notification("Use AAC5.2.0 Flys will crash single player"),"Fly")
                    toggle(false)
                    return
                }

                flyTimer.reset()
                aac5FlyClip=false
                aac5FlyStart=false
                aac5nextFlag=false
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ)
            }
        }
    }

    override fun onDisable() {
        mc.thePlayer.capabilities.isFlying = isFlying
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
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        if (mode.get().equals("AAC5",true)) toggle(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (event.eventState == UpdateState.OnUpdate) return
        when(mode.get()){
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
        }
    }

    private val aac5C03List = arrayListOf<C03PacketPlayer>()

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