package kevin.module.modules.combat

import kevin.event.AttackEvent
import kevin.event.EventTarget
import kevin.module.IntegerValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction

class SuperKnockback : Module("SuperKnockback", "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT) {

    private val modeValue = ListValue("PacketMode", arrayOf("PlayerPacket","Sprint"),"Sprint")
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val playerPackets = IntegerValue("PlayerPackets",50,10,100)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if ((event.targetEntity)is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get()) return

            val player = mc.thePlayer ?: return
            val mode = modeValue.get()
            if (mode.equals("Sprint",true)) {
                if (player.isSprinting) mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING))

                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING))
                player.isSprinting = true
                player.serverSprintState = true
            }
            if (mode.equals("PlayerPacket",true)){
                repeat(playerPackets.get()){
                    mc.netHandler.addToSendQueue(C03PacketPlayer(player.onGround))
                }
            }
        }
    }
}