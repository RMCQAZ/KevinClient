package kevin.hud.element.elements

import kevin.hud.designer.GuiHudDesigner
import kevin.hud.element.*
import kevin.hud.element.elements.ConnectNotificationType.*
import kevin.hud.element.elements.Notification.FadeState.*
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

    private val notificationMode = ListValue("NotificationMode", arrayOf("Connect","LiquidBounce-Kevin","Kevin"),"Connect")
    private val exampleNotification = Notification("Example Notification", "Example title")
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()
        val hud = KevinClient.hud
        for(i in hud.notifications)
            notifications.add(i)
        for(i in notifications){
            if (mc.currentScreen !is GuiHudDesigner) {
                when (notificationMode.get()) {
                    "LiquidBounce-Kevin" -> i.drawNotification(animationY).also { animationY += 20 }
                    "Kevin" -> i.drawNotificationKevinNew(animationY).also { animationY += 40 }
                    "Connect" -> i.drawConnectNotification(animationY).also { animationY += 24 }
                }
            } else {
                when (notificationMode.get()) {
                    "LiquidBounce-Kevin" -> exampleNotification.drawNotification(animationY)
                    "Kevin" -> exampleNotification.drawNotificationKevinNew(animationY)
                    "Connect" -> exampleNotification.drawConnectNotification(animationY)
                }
            }
        }
        if (mc.currentScreen is GuiHudDesigner) {
            if (!KevinClient.hud.notifications.contains(exampleNotification)) KevinClient.hud.addNotification(exampleNotification)
            exampleNotification.fadeState = STAY

            exampleNotification.x = if (notificationMode equal "Connect") {
                val c = 16.0 / 48.0
                val iconWidth = (48 * c).toFloat() + 4F
                exampleNotification.textLength + KevinClient.fontManager.font35.getStringWidth("[${exampleNotification.title}]: ") + iconWidth
            } else {
                exampleNotification.textLength.toFloat()
            } + 8F

            when (notificationMode.get()) {
                "LiquidBounce-Kevin" -> return Border(-118.114514191981F, -50F, 0F, -30F)
                "Kevin" -> return Border(-114.5F, -70F, 0F, -30F)
                "Connect" -> return Border(-220F, -50F, 0F, -30F) //
            }
        }
        GL11.glDisable(GL11.GL_BLEND)
        return null
    }
}

enum class ConnectNotificationType {
    Connect,Disconnect,OK,Warn,Info,Error
}

class Notification(private val message: String, val title: String = "", val type: ConnectNotificationType = Info) {
    var x = 0F
    var textLength = 0

    private var stay = 0F
    private var fadeStep = 0F
    var fadeState = IN

    private val stayTimer = MSTimer()
    private var timer = 0L
    private var firstY = 0f
    private var animeTime: Long = 0

    enum class FadeState { IN, STAY, OUT, END }

    init {
        stayTimer.reset()
        firstY = 1919F
        textLength = KevinClient.fontManager.font35.getStringWidth(message)
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
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(0, 255, 160).rgb)
        }else if (message.contains("Disabled")) {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(255, 0, 80,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(255, 0, 80,225).rgb)
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(255, 0, 80).rgb)
        }else {
            RenderUtils.drawRect(-x, -y, -x - 5, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -19F-y, -x, -20F-y, Color(0, 160, 255,225).rgb)
            RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -19F-y, Color(0, 160, 255,225).rgb)
            KevinClient.fontManager.font35.drawString(message, -x + 4, -14F-y, Color(0, 160, 255).rgb)
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            STAY -> {
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
                    fadeState = OUT
            }

            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = END

            END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }

        }
    }

    fun drawNotificationKevinNew(animationY: Float) {
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

        RenderUtils.drawRect(-x + 8 + textLength, -y, -x, -40F-y, Color(0,0,0,100).rgb)

        RenderUtils.drawRect(-x, -y, -x - 1, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -39F-y, -x, -40F-y, color)
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x + 7 + textLength, -39F-y, color)
        KevinClient.fontManager.font35.drawString(message, -x + (8F + textLength)/2 - KevinClient.fontManager.font35.getStringWidth(message)/2, -14F-y, color)
        KevinClient.fontManager.font35.drawString(
            this.title,
            -x + (8F + textLength) / 2 - KevinClient.fontManager.font35.getStringWidth(this.title) / 2,
            -30F - y,
            color
        )
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = textLength + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
            }

            STAY -> {
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
                    fadeState = OUT
            }

            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else
                fadeState = END

            END -> {
                val hud = KevinClient.hud
                hud.removeNotification(this)
            }
        }
    }

    fun drawConnectNotification(animationY: Float) {
        var y = animationY
        if (firstY == 1919.0F)
            firstY = y
        if (firstY > y) {
            val cacheY = firstY - (firstY - y) * ((System.currentTimeMillis() - animeTime).toFloat() / 300.0f)
            if (cacheY <= y)
                firstY = cacheY
            y = cacheY
        } else {
            firstY = y
            animeTime = System.currentTimeMillis()
        }
        val c = 16.0 / 48.0
        val iconWidth = ((if (type == Connect || type == Disconnect) 117 else 48) * c).toFloat() + 4F
        val long = textLength + KevinClient.fontManager.font35.getStringWidth("[$title]: ") + iconWidth
        // Draw notification
        val fontColor = when(type) {
            Connect,OK -> Color(0, 255, 160)
            Disconnect,Error -> Color(255, 0, 80)
            Warn -> Color(240, 240, 80)
            Info -> Color(0, 160, 255)
        }
        RenderUtils.drawRect(8 + long - x, -y, -x, -20F - y, Color(0, 0, 0, 155).rgb)
        RenderUtils.drawShadow(-x, -20F-y,  8F + long, 20F)
        KevinClient.fontManager.font35.drawString("[$title]: ", iconWidth + 4 - x, -14F-y, Color(240, 240, 240).rgb)
        KevinClient.fontManager.font35.drawString(message, iconWidth + 4 + KevinClient.fontManager.font35.getStringWidth("[$title]: ") - x, -14F-y, fontColor.rgb)
        if (fadeState != IN) {
            var pec = (System.currentTimeMillis() - timer).toDouble() / 3000
            if (pec > 1.0) pec = 1.0
            RenderUtils.drawRect(((8 + long)*pec - x).toFloat(), -y, -x, -2F - y, Color(255, 144, 71, 255).rgb)
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        when(type) { // Draw icon
            Connect -> {
                when (fadeState) {
                    IN -> {
                        RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                        RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                    }
                    STAY -> {
                        val pec = (System.currentTimeMillis() - timer).toFloat() / 600F
                        if (pec > 1F) {
                            RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                            RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                        } else {
                            val value = 4.5F * pec
                            RenderUtils.drawIcon(4 + value - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - value - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        }
                    }
                    OUT,END -> {
                        RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                        RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                    }
                }
            }
            Disconnect -> {
                when (fadeState) {
                    IN -> {
                        RenderUtils.drawIcon(8.5F - x, -20F - y, 16F, 16F, "ConnectedLeft")
                        RenderUtils.drawIcon(15.5F - x, -20F - y, 23F, 16F, "ConnectedRight")
                    }
                    STAY -> {
                        val pec = (System.currentTimeMillis() - timer).toFloat() / 600F
                        if (pec > 1F) {
                            RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        } else {
                            val value = 4.5F - 4.5F * pec
                            RenderUtils.drawIcon(4 + value - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                            RenderUtils.drawIcon(20 - value - x, -20F - y, 23F, 16F, "DisconnectedRight")
                        }
                    }
                    OUT,END -> {
                        RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "DisconnectedLeft")
                        RenderUtils.drawIcon(20 - x, -20F - y, 23F, 16F, "DisconnectedRight")
                    }
                }
            }
            OK -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Done")
            Warn -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Warn")
            Info -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Info")
            Error -> RenderUtils.drawIcon(4 - x, -20F - y, 16F, 16F, "Error")
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        // Animation
        val delta = RenderUtils.deltaTime - 4
        val width = long + 8F
        when (fadeState) {
            IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = STAY
                    timer = System.currentTimeMillis()
                    x = width
                    fadeStep = width
                }
            }
            STAY -> {
                if (System.currentTimeMillis() - timer >= 3000)
                    fadeState = OUT
            }
            OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 4F
            } else fadeState = END
            END -> KevinClient.hud.removeNotification(this)
        }
    }
}