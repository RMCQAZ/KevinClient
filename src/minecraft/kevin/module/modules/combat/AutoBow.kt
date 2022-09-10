package kevin.module.modules.combat

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class AutoBow : Module("AutoBow", "Automatically shoots an arrow whenever your bow is fully loaded.", category = ModuleCategory.COMBAT) {
    private val waitForBowAimbot = BooleanValue("WaitForBowAimbot", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val bowAimbot = KevinClient.moduleManager.getModule(BowAimbot::class.java)

        val thePlayer = mc.thePlayer!!

        if (thePlayer.isUsingItem && thePlayer.heldItem?.item is ItemBow &&
            thePlayer.itemInUseDuration > 20 && (!waitForBowAimbot.get() || !bowAimbot.state || bowAimbot.hasTarget())) {
            thePlayer.stopUsingItem()
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
    }
}