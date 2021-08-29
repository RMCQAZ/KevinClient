package kevin.module.modules.render

import kevin.module.*

class Animations : Module("Animations","Changes animations.", category = ModuleCategory.RENDER) {
    val animations = ListValue(
        "Preset", arrayOf(
            "Akrien", "Avatar", "ETB", "Exhibition", "Push", "Reverse",
            "Shield", "SigmaNew", "SigmaOld", "Slide", "SlideDown", "HSlide", "Swong", "VisionFX",
            "Swank", "Jello", "LiquidBounce","Rotate"
        ),
        "SlideDown"
    )

    var translateX = FloatValue("TranslateX", 0.0f, 0.0f, 1.5f)
    var translateY = FloatValue("TranslateY", 0.0f, 0.0f, 0.5f)
    var translateZ = FloatValue("TranslateZ", 0.0f, 0.0f, -2.0f)
    val itemPosX = FloatValue("ItemPosX", 0.56F, -1.0F, 1.0F)
    val itemPosY = FloatValue("ItemPosY", -0.52F, -1.0F, 1.0F)
    val itemPosZ = FloatValue("ItemPosZ", -0.71999997F, -1.0F, 1.0F)
    var itemScale = FloatValue("ItemScale", 0.4f, 0.0f, 2.0f)
    val onlyOnBlock = BooleanValue("OnlyBlock",false)
    val animationSpeed = IntegerValue("AnimationSpeed",6,1,30)

    override val tag: String
        get() = animations.get()
}