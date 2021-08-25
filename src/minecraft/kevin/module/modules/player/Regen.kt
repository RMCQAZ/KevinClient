package kevin.module.modules.player

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

class Regen : Module("Regen", "Regenerates your health much faster.", category = ModuleCategory.PLAYER) {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Spartan", "AAC4NoFire"), "Vanilla")
    private val healthValue = IntegerValue("Health", 18, 0, 20)
    private val foodValue = IntegerValue("Food", 18, 0, 20)
    private val speedValue = IntegerValue("Speed", 100, 1, 100)
    private val noAirValue = BooleanValue("NoAir", false)
    private val potionEffectValue = BooleanValue("PotionEffect", false)

    private var resetTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (resetTimer)
            mc.timer.timerSpeed = 1F
        resetTimer = false

        val thePlayer = mc.thePlayer ?: return

        if ((!noAirValue.get() || thePlayer.onGround) && !thePlayer.capabilities.isCreativeMode &&
            thePlayer.foodStats.foodLevel > foodValue.get() && thePlayer.isEntityAlive && thePlayer.health < healthValue.get()) {
            if (potionEffectValue.get() && !thePlayer.isPotionActive(Potion.regeneration))
                return

            when (modeValue.get().toLowerCase()) {
                "vanilla" -> {
                    repeat(speedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }
                }

                "spartan" -> {
                    if (MovementUtils.isMoving || !thePlayer.onGround)
                        return

                    repeat(9) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(thePlayer.onGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }

                "aac4nofire" -> {
                    if(mc.thePlayer!!.isBurning && mc.thePlayer!!.ticksExisted%10==0){
                        repeat(35){
                            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                        }
                    }
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}