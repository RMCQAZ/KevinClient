package kevin.via

import com.viaversion.viaversion.api.platform.PlatformTask
import java.util.concurrent.Future

class FutureTaskId(`object`: Future<*>) : PlatformTask<Future<*>> {
    private val `object`: Future<*>
    override fun getObject(): Future<*> {
        return `object`
    }
    override fun cancel() {
        `object`.cancel(false)
    }
    init {
        this.`object` = `object`
    }
}