package kevin.font.renderer

abstract class AbstractCachedFont(var lastUsage: Long) {
    abstract fun finalize()
}