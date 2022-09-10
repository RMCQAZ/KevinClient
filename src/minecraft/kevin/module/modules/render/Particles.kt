package kevin.module.modules.render

import kevin.module.BooleanValue
import kevin.module.FloatValue
import kevin.module.Module
import kevin.module.ModuleCategory

object Particles : Module("Particles", "Particles control.", category = ModuleCategory.RENDER) {
    private val noCriticalParticlesValue = BooleanValue("NoCriticalParticles", true)
    val noCriticalParticles
        get() = this.state && noCriticalParticlesValue.get()
    private val noCriticalParticlesFromServerValue = BooleanValue("NoCriticalParticlesFromServer", true)
    val noCriticalParticlesFromServer
        get() = this.state && noCriticalParticlesFromServerValue.get()
    private val noSharpParticlesValue = BooleanValue("NoSharpParticles", true)
    val noSharpParticles
        get() = this.state && noSharpParticlesValue.get()
    private val noSharpParticlesFromServerValue = BooleanValue("NoSharpParticlesFromServer", true)
    val noSharpParticlesFromServer
        get() = this.state && noSharpParticlesFromServerValue.get()
    private val blockParticleSpeedValue = FloatValue("BlockParticleSpeed", 0.4F, 0.4F, 1.5F)
    val blockParticleSpeed
        get() = blockParticleSpeedValue.get()
    private val otherParticleSpeedValue = FloatValue("OtherParticleSpeed", 0.4F, 0.4F, 1.5F)
    val otherParticleSpeed
        get() = otherParticleSpeedValue.get()
}