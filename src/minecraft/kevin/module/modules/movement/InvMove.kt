package kevin.module.modules.movement

import kevin.event.*
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.MovementUtils
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C16PacketClientStatus
import org.lwjgl.input.Keyboard

class InvMove : Module("InvMove","Allows you to walk while an inventory is opened.",Keyboard.KEY_NONE,ModuleCategory.MOVEMENT){
    private val sprintMode = ListValue("Sprint", arrayOf("Ignore", "AlwaysFake", "StopWhenOpen", "FakeWhenOpen"), "Ignore")
    private val bypass = BooleanValue("Bypass",false)
    private val noMoveClicksValue = BooleanValue("NoMoveClicks", false)

    val needFakeSprint: Boolean
    get() = this.state && (sprintMode equal "AlwaysFake" || (sprintMode equal "FakeWhenOpen" && mc.currentScreen != null))
    val needStopSprint: Boolean
    get() = this.state && sprintMode equal "StopWhenOpen" && mc.currentScreen != null

    private val affectedBindings = arrayOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindJump,
        mc.gameSettings.keyBindSprint
    )

    @EventTarget
    fun onPacket(event: PacketEvent){
        if(bypass.get() && event.packet is C16PacketClientStatus
            && event.packet.status == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT){
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        //if (event.eventState == UpdateState.OnLivingUpdate){

        if (mc.currentScreen !is GuiChat && mc.currentScreen !is GuiIngameMenu)
            for (affectedBinding in affectedBindings) {
                affectedBinding.pressed = GameSettings.isKeyDown(affectedBinding)
            }

        //}

    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (noMoveClicksValue.get() && MovementUtils.isMoving)
            event.cancelEvent()
    }

    override fun onDisable() {
        val isIngame = mc.currentScreen != null
        for (affectedBinding in affectedBindings) {
            if (!GameSettings.isKeyDown(affectedBinding) || isIngame)
                affectedBinding.pressed = false
        }
    }

    override val tag: String
        get() = "Sprint:${sprintMode.get()}" + if (bypass.get()) " & NoPacket" else ""
//if (sprintMode.get()&&bypass.get()) "FakeSprint & NoPacket" else if (sprintMode.get()) "FakeSprint" else if (bypass.get()) "NoPacket" else null
}