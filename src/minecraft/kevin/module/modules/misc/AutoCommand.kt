package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.event.UpdateEvent

//import kevin.event.UpdateState

import kevin.module.*
import kevin.utils.MSTimer
import net.minecraft.network.play.server.S02PacketChat

class AutoCommand : Module("AutoCommand","Send commands automatically.",category = ModuleCategory.MISC) {
    private val autoLoginValue = BooleanValue("AutoLogin",true)
    private val autoRegisterValue = BooleanValue("AutoRegister",true)
    private val registerAndLoginPassword = TextValue("RegisterAndLoginPassword","Password")
    private val autoLoginAndRegisterDelay = IntegerValue("AutoLoginAndRegisterDelay",2500,100,5000)
    private val autoLoginMode = ListValue("AutoLoginMode", arrayOf("/l","/login","Custom"),"/login")
    private val autoLoginCustom = TextValue("AutoLoginCustomCommand","/login")
    private val autoRegisterCommand = TextValue("AutoRegisterCommand","/register")
    private val autoLoginDetectMessage = TextValue("AutoLoginDetectMessage","login")
    private val autoRegisterDetectMessage = TextValue("AutoRegisterDetectMessage","register")

    private val timer = MSTimer()
    private var register = false
    private var login = false

    override fun onDisable() {
        register = false
        login = false
        super.onDisable()
    }

    override val tag: String?
        get() = when{
            autoLoginValue.get()&&autoRegisterValue.get() ->"Auto Login Register"
            autoRegisterValue.get() -> "Auto Register"
            autoLoginValue.get() -> "Auto Login"
            else -> null
        }

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet !is S02PacketChat) return
        val text = packet.chatComponent.formattedText
        if (text.contains(autoRegisterDetectMessage.get(),true)&&autoRegisterValue.get()) {register=true;timer.reset();return}
        if (text.contains(autoLoginDetectMessage.get(),true)&&autoLoginValue.get()) {login=true;timer.reset()}
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        //if (event.eventState == UpdateState.OnUpdate) return

        if (!timer.hasTimePassed(autoLoginAndRegisterDelay.get().toLong())) return
        if (register){
            mc.thePlayer.sendChatMessage("${autoRegisterCommand.get()} ${registerAndLoginPassword.get()} ${registerAndLoginPassword.get()}")
            register = false
        }
        if (login){
            val start = when(autoLoginMode.get()){
                "/l" -> "/l"
                "/login" -> "/login"
                "Custom" -> autoLoginCustom.get()
                else -> ""
            }
            val text = "$start ${registerAndLoginPassword.get()}"
            mc.thePlayer.sendChatMessage(text)
            login = false
        }
    }
}