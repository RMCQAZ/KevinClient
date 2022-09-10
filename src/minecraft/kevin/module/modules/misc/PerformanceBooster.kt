package kevin.module.modules.misc

import kevin.module.BooleanValue
import kevin.module.Module

object PerformanceBooster : Module("PerformanceBooster", "Optimize to improve performance.") {
    private val staticParticleColorValue = BooleanValue("StaticParticleColor", false)
    private val fastEntityLightningValue = BooleanValue("FastEntityLightning", false)
    private val fastBlockLightningValue = BooleanValue("FastBlockLightning", false)
    val staticParticleColor
        get() = this.state && staticParticleColorValue.get()
    val fastEntityLightning
        get() = this.state && fastEntityLightningValue.get()
    val fastBlockLightning
        get() = this.state && fastBlockLightningValue.get()
}