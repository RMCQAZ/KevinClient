package kevin.hud.element.elements

import kevin.event.*
import kevin.hud.element.Border
import kevin.hud.element.Element
import kevin.hud.element.ElementInfo
import kevin.hud.element.Side
import kevin.main.Kevin
import kevin.utils.RenderUtils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.server.S03PacketTimeUpdate
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.concurrent.CopyOnWriteArrayList

@ElementInfo("Information")
class Information(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.UP)) : Element(x, y, scale, side),Listenable {

    //TPS
    private var tps = 20.0
    private val packetHistoryTime = arrayListOf<Long>()
    //Kills
    private var kills = 0
    private val attackEntities = CopyOnWriteArrayList<EntityLivingBase>()
    //BPS
    private var bps = 0.0
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    //Playtime
    private val startTime: Long

    override fun drawElement(): Border {
        RenderUtils.drawRectRoundedCorners(10.0,10.0,100.0,70.0,5.0, Color(255,255,255,100))
        GL11.glPushMatrix()
        GL11.glScaled(0.7,0.7,0.7)
        Kevin.getInstance.fontManager.font35!!.drawString("Information",(10+45-Kevin.getInstance.fontManager.font35!!.getStringWidth("Information")/2*0.7f)/0.7f,(10+Kevin.getInstance.fontManager.font35!!.fontHeight/2F)/0.7f,Color(0,60,255).rgb)
        GL11.glPopMatrix()
        var y = 10.0 + Kevin.getInstance.fontManager.font35!!.fontHeight

        GL11.glPushMatrix()
        RenderUtils.drawLineStart(Color(255,255,255,150),2F)
        RenderUtils.drawLine(10.0,y+2.0,100.0,y+2.0)
        RenderUtils.drawLineEnd()
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        GL11.glScaled(0.6,0.6,0.6)
        Kevin.getInstance.fontManager.font40!!.drawString("TPS: $tps",20/0.6f,(y.toFloat()+2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F)/0.6f,if(tps >= 19.0) Color(0,255,0,200).rgb else Color(255,0,0,200).rgb)
        y += 2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F
        Kevin.getInstance.fontManager.font40!!.drawString("HurtTime: ${mc.thePlayer.hurtTime}",20/0.6f,(y.toFloat()+2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F)/0.6f,if (mc.thePlayer.hurtTime>0) Color(255,0,0,200).rgb else Color(0,20,255,200).rgb)
        y += 2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F
        Kevin.getInstance.fontManager.font40!!.drawString("Kills: $kills",20/0.6f,(y.toFloat()+2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F)/0.6f,Color(255,0,0,200).rgb)
        y += 2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F
        Kevin.getInstance.fontManager.font40!!.drawString("Speed: ${bps}BPS",20/0.6f,(y.toFloat()+2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F)/0.6f,Color(0,40,255,200).rgb)
        y += 2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F
        Kevin.getInstance.fontManager.font40!!.drawString("Timeplayed: ${getTime(System.currentTimeMillis()-startTime)}",(10+45-Kevin.getInstance.fontManager.font40!!.getStringWidth("Timeplayed: ${getTime(System.currentTimeMillis()-startTime)}")/2*0.6f)/0.6f,(y.toFloat()+2+Kevin.getInstance.fontManager.font40!!.fontHeight/2F)/0.6f,Color(0,0,0,200).rgb)
        GL11.glPopMatrix()
        return Border(10F,10F,100F,70F)
    }
    init {
        Kevin.getInstance.eventManager.registerListener(this)
        startTime = System.currentTimeMillis()
    }
    override fun handleEvents(): Boolean = true
    @EventTarget
    fun onAttack(event: AttackEvent){
        val entity = event.targetEntity ?: return
        if (entity !is EntityLivingBase || entity.isDead || entity.health == 0F) return
        if (entity !in attackEntities) attackEntities.add(entity)
    }
    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if (packet is S03PacketTimeUpdate){
            packetHistoryTime.add(System.currentTimeMillis())
        }
    }
    @EventTarget
    fun onWorld(event: WorldEvent){
        tps = 20.0
        kills = 0
        attackEntities.clear()
    }
    override fun updateElement() {
        //TPS
        updateTPS()
        //Kills
        attackEntities.forEach {if(it.isDead || it.health == 0F){kills+=1;attackEntities.remove(it)}}
        //BPS
        updateBPS()
    }

    private fun updateTPS(){
        var count = 0.0
        packetHistoryTime.forEach {if(System.currentTimeMillis()<1000L+it)count++}
        if (count == packetHistoryTime.size.toDouble()) return else if (count + 10 < packetHistoryTime.size) packetHistoryTime.removeFirst()
        if (mc.currentServerData == null || mc.isIntegratedServerRunning) count /= 2.0
        tps = count * 20.0
    }

    private fun updateBPS(){
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) bps=0.0
        val distance = mc.thePlayer.getDistance(lastX, lastY, lastZ)
        lastX = mc.thePlayer.posX
        lastY = mc.thePlayer.posY
        lastZ = mc.thePlayer.posZ
        bps = String.format("%.2f",distance * (20 * mc.timer.timerSpeed)).toDouble()
    }

    private fun getTime(time:Long): String{
        val h = (time / 1000) / 60 / 60
        val m = (time / 1000) / 60 % 60
        val s = (time / 1000) % 60
        return "${h}h ${m}m ${s}s"
    }
}