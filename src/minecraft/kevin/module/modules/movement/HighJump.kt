package kevin.module.modules.movement

import kevin.event.EventTarget
import kevin.event.JumpEvent
import kevin.event.MoveEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.BlockUtils.getBlock
import kevin.utils.MovementUtils
import net.minecraft.block.BlockPane
import net.minecraft.util.BlockPos

class HighJump : Module("HighJump", "Allows you to jump higher.", category = ModuleCategory.MOVEMENT) {
    private val heightValue = FloatValue("Height", 2f, 1.1f, 5f)
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Damage", "AACv3", "DAC", "Mineplex"), "Vanilla")
    private val glassValue = BooleanValue("OnlyGlassPane", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return

        when (modeValue.get().toLowerCase()) {
            "damage" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) thePlayer.motionY += 0.42f * heightValue.get()
            "aacv3" -> if (!thePlayer.onGround) thePlayer.motionY += 0.059
            "dac" -> if (!thePlayer.onGround) thePlayer.motionY += 0.049999
            "mineplex" -> if (!thePlayer.onGround) MovementUtils.strafe(0.35f)
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return
        if (!thePlayer.onGround) {
            if ("mineplex" == modeValue.get().toLowerCase()) {
                thePlayer.motionY += if (thePlayer.fallDistance == 0.0f) 0.0499 else 0.05
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (glassValue.get() && (getBlock(BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)))!is BlockPane)
            return
        when (modeValue.get().toLowerCase()) {
            "vanilla" -> event.motion = event.motion * heightValue.get()
            "mineplex" -> event.motion = 0.47f
        }
    }

    override val tag: String
        get() = modeValue.get()
}