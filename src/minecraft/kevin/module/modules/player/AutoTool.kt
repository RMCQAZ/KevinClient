package kevin.module.modules.player

import kevin.event.ClickBlockEvent
import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MSTimer
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos

class AutoTool : Module(name = "AutoTool", description = "Automatically selects the best tool in your inventory to mine a block.", category = ModuleCategory.PLAYER) {
    val silentValue = BooleanValue("Silent", true)
    var nowSlot = 0
    private val switchTimer = MSTimer()
    private var needReset = false

    @EventTarget
    fun onClick(event: ClickBlockEvent) {
        switchSlot(event.clickedBlock ?: return)
    }
    @EventTarget fun onUpdate(event: UpdateEvent){
        if (needReset) {
            if (switchTimer.hasTimePassed(100)){
                needReset = false
                if (nowSlot!=mc.thePlayer!!.inventory.currentItem){
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
                    nowSlot = mc.thePlayer!!.inventory.currentItem
                }
            }
        }
    }
    @EventTarget fun onPacket(event: PacketEvent){
        if (event.packet is C09PacketHeldItemChange) {
            nowSlot = event.packet.slotId
        }
    }

    fun switchSlot(blockPos: BlockPos) {
        var bestSpeed = 1F
        var bestSlot = -1

        val blockState = mc.theWorld!!.getBlockState(blockPos).block

        for (i in 0..8) {
            val item = mc.thePlayer!!.inventory.getStackInSlot(i) ?: continue
            val speed = item.getStrVsBlock(blockState)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }
        if (bestSlot != -1 && bestSlot != nowSlot) {
            if (mc.thePlayer!!.inventory.getStackInSlot(nowSlot)!=null&&mc.thePlayer!!.inventory.getStackInSlot(nowSlot).getStrVsBlock(blockState) >= bestSpeed) return
            if (silentValue.get()) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(bestSlot))
                nowSlot = bestSlot
            } else {
                mc.thePlayer!!.inventory.currentItem = bestSlot
            }
        }
        switchTimer.reset()
        needReset = true
    }
}