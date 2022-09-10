package kevin.font

import kevin.event.EventTarget
import kevin.event.Listenable
import kevin.event.TickEvent

object FontGC : Listenable {
    private val activeFontRenderers: ArrayList<GameFontRenderer> = ArrayList()

    private var gcTicks: Int = 0
    const val GC_TICKS = 600 // Start garbage collection every 600 ticks
    const val CACHED_FONT_REMOVAL_TIME = 30000 // Remove cached texts after 30s of not being used

    @EventTarget
    fun onTick(event: TickEvent) {
        if (gcTicks++ > GC_TICKS) {
            activeFontRenderers.forEach { it.collectGarbage() }
            gcTicks = 0
        }
    }

    fun register(fontRender: GameFontRenderer) {
        activeFontRenderers.add(fontRender)
    }

    fun unregister(fontRender: GameFontRenderer) {
        if (!activeFontRenderers.contains(fontRender))
            return
        fontRender.close()
        activeFontRenderers.remove(fontRender)
    }

    override fun handleEvents() = true
}