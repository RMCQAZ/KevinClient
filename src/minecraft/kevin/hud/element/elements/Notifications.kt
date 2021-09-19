package kevin.hud.element.elements

import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.*
import kevin.main.KevinClient
import kevin.module.ListValue
import kevin.utils.AnimationUtils
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val notificationMode = ListValue("NotificationMode", arrayOf("LiquidBounce-Kevin","Kevin"),"LiquidBounce-Kevin")
    private val exampleNotification = Notification("Example Notification")
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()
        val hud = KevinClient.hud
        for(i in hud.notifications)
            notifications.add(i)
        for(i in notifications){
            if (mc.currentScreen !is GuiHudDesigner) {
                if (notificationMode.get().equals("liquidbounce-kevin", true))
                    i.drawNotification(animationY).also { animationY += 20 }
                else if (notificationMode.get().equals("kevin", true))
                    i.drawNotificationKevinNew(animationY, i.text1).also { animationY += 40 }
            }else{
                if (notificationMode.get().equals("liquidbounce-kevin", true)) exampleNotification.drawNotification(animationY)
                else if (notificationMode.get().equals("kevin", true)) exampleNotification.drawNotificationKevinNew(animationY,"Example title")
            }
        }
        if ((mc.currentScreen) is GuiHudDesigner) {
            if (!KevinClient.hud.notifications.contains(exampleNotification)) KevinClient.hud.addNotification(exampleNotification)
            exampleNotification.fadeState = Notification.FadeState.STAY
            exampleNotification.x = exampleNotification.textLength + 8F
            if (notificationMode.get().equals("liquidbounce-kevin", true)) return Border(-118.114514191981F, -50F, 0F, -30F)
            else if (notificationMode.get().equals("kevin", true)) return Border(-114.5F, -70F, 0F, -30F)
        }
        GL11.glDisable(GL11.GL_BLEND)
        return null
    }
}

class Notification(private val message: String) {
    var text1 = ""
    var x = 0F
    var textLength = 0

    private var stay = 0F
    private var fadeStep = 0F
    var fadeState = FadeState.IN

    private var stayTimer = MSTimer()
    private var firstY = 0f
    private var animeTime: Long = 0

    enum class FadeState { IN, STAY, OUT, END }

    init {
        stayTimer.reset()
        firstY = 1919F
        textLength = KevinClient.fontManager.font35!!.getStringWidth(message)
    }

    fun drawNotification(animationY: Float) {
        var y = animationY
        if (firstY == 1919.0F) {
            firstY = y
        }
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y) {
                firstY = cacheY
            }
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        // Draw notification
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -20F-y, Color(0,0,0,155).rgb)
        if (message.contains("Enabled")) {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 255, 160,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 255, 160,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 255, 160,225).rgb)
            KevinClient.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(0, 255, 160).rgb)
        }else if (message.contains("Disabled")) {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(255, 0, 80,225).rgb)
            KevinClient.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(255, 0, 80).rgb)
        }else {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 160, 255,225).rgb)
            KevinClient.fontManager.font35!!.drawString(message, -x + 4, -14F-y, Color(0, 160, 255).rgb)
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            FadeState.STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2850L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(3000L))
                    fadeState = FadeState.OUT
            }

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }

        }
    }

    fun drawNotificationKevinNew(animationY: Float,text2:String = "") {
        var y = animationY
        if (firstY == 1919.0F) {
            firstY = y
        }
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y) {
                firstY = cacheY
            }
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        // Draw notification
        val color = if (message.contains("Enabled")) Color(0, 255, 160).rgb else if (message.contains("Disabled")) Color(255, 0, 80).rgb else Color(0, 160, 255).rgb
        textLength = if (textLength > 100) textLength else 100
        val text = if (message.contains("Enabled")||message.contains("Disabled")) "ModuleManager" else text2

        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -40F-y, Color(0,0,0,100).rgb)

        RenderUtils.drawRect(-x, -y, -x - 1, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -39F-y, -x, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -39F-y, color)
        KevinClient.fontManager.font35!!.drawString(message, -x + (8F + textLength)/2 - KevinClient.fontManager.font35!!.getStringWidth(message)/2, -14F-y, color)
        KevinClient.fontManager.font35!!.drawString(text, -x + (8F + textLength)/2 - KevinClient.fontManager.font35!!.getStringWidth(text)/2, -30F-y, color)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            FadeState.STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*2, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(1500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*3, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2000L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*4, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2500L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x/6*5, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(2850L)) {
                    if (message.contains("Enabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 255, 160, 225).rgb)
                    } else if (message.contains("Disabled")) {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(255, 0, 80,225).rgb)
                    } else {
                        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -2F - y, Color(0, 160, 255,225).rgb)
                    }
                }
                if (stayTimer.hasTimePassed(3000L))
                    fadeState = FadeState.OUT
            }

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }

        }
    }
}