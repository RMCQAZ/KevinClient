package kevin.module.modules.combat

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.main.KevinClient
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer

class Criticals : Module(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT) {
    val modeValue = ListValue("Mode", arrayOf("Packet", "NcpPacket", "AACPacket", "NoGround", "Hop", "Jump", "LowJump", "Visual", "MineMora"), "Packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)

    val msTimer = MSTimer()

    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true) && mc.thePlayer!!.onGround)
            mc.thePlayer!!.jump()
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.inWeb || thePlayer.isInWater ||
                thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                KevinClient.moduleManager.getModule("Fly")!!.state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ

            when (modeValue.get().toLowerCase()) {
                "aacpacket" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.05250000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.01400000001304, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                }

                "packet" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false))
                    thePlayer.onCriticalHit(entity)
                }

                "ncppacket" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1100013579, z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0000013579, z, false))
                    thePlayer.onCriticalHit(entity)
                }

                "hop" -> {
                    thePlayer.motionY = 0.1
                    thePlayer.fallDistance = 0.1f
                    thePlayer.onGround = false
                }

                "jump" -> thePlayer.motionY = 0.42
                "lowjump" -> thePlayer.motionY = 0.3425
                "visual" -> thePlayer.onCriticalHit(entity)
                "minemora" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.01145141919810,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.0010999999940395355,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.00150000001304,z,false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x,y + 0.0012016413,z,false))
                }
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer && modeValue.get().equals("NoGround", ignoreCase = true))
            packet.isOnGround = false
    }

    override val tag: String
        get() = modeValue.get()
}