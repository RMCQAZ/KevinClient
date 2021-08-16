package kevin.event

open class Event

open class CancellableEvent : Event(){
    var isCancelled: Boolean = false
        private set
    fun cancelEvent() {
        isCancelled = true
    }
}

enum class EventState(val stateName: String) {
    PRE("PRE"), POST("POST")
}
enum class UpdateState(val stateName: String) {
    OnUpdate("OnUpdate"), OnLivingUpdate("OnLivingUpdate")
}
enum class PacketMode(val stateName: String) {
    SEND("Send"), RECEIVE("Receive")
}