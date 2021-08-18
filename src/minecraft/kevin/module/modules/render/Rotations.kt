package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.Render3DEvent
import kevin.main.Kevin
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.modules.combat.KillAura
import kevin.utils.RotationUtils
import net.minecraft.network.play.client.C03PacketPlayer

class Rotations : Module("Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.RENDER){
    private val bodyValue = BooleanValue("Body", true)

    private var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null && !bodyValue.get())
            mc.thePlayer?.rotationYawHead = RotationUtils.serverRotation.yaw
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer

        if (!bodyValue.get() || !shouldRotate() || thePlayer == null)
            return

        val packet = event.packet

        if ((packet) is C03PacketPlayer.C06PacketPlayerPosLook || (packet) is C03PacketPlayer.C05PacketPlayerLook) {
            val packetPlayer = packet as C03PacketPlayer

            playerYaw = packetPlayer.yaw

            thePlayer.renderYawOffset = packetPlayer.yaw
            thePlayer.rotationYawHead = packetPlayer.yaw
        } else {
            if (playerYaw != null)
                thePlayer.renderYawOffset = this.playerYaw!!

            thePlayer.rotationYawHead = thePlayer.renderYawOffset
        }
    }

    private fun getState(module: String) = Kevin.getInstance.moduleManager.getModule(module)!!.getToggle()

    private fun shouldRotate(): Boolean {
        val killAura = Kevin.getInstance.moduleManager.getModule("KillAura") as KillAura
        return (getState("KillAura") && killAura.target != null)
                || getState("Scaffold") || getState("Breaker") /**getState(Tower::class.java) ||
                getState(BowAimbot::class.java) ||
                 || getState(CivBreak::class.java) || getState(Nuker::class.java) ||
                getState(ChestAura::class.java)**/
    }
}