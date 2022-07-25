package kevin.module.modules.misc

import kevin.module.BooleanValue
import kevin.module.Module

object ChatControl : Module("ChatControl", "Chat settings.") {
    private val noLengthLimit = BooleanValue("NoLengthLimit", true)
    private val noChatClear = BooleanValue("NoChatClear", true)
    fun noLengthLimit() = this.state && noLengthLimit.get()
    fun noChatClear() = this.state && noChatClear.get()
}