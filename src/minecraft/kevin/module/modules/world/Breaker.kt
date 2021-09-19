package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent

//import kevin.event.UpdateState

import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.combat.KillAura
import kevin.module.modules.misc.Teams
import kevin.module.modules.player.AutoTool
import kevin.utils.BlockUtils.getBlock
import kevin.utils.BlockUtils.getBlockName
import kevin.utils.BlockUtils.getCenterDistance
import kevin.utils.BlockUtils.isFullBlock
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import kevin.utils.RotationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.awt.Color

class Breaker : Module("Breaker",description = "Destroys selected blocks around you.", category = ModuleCategory.WORLD) {

    /**
     * SETTINGS
     */

    private val blockValue = BlockValue("Block", 26)
    private val throughWallsValue = ListValue("ThroughWalls", arrayOf("None", "Raycast", "Around"), "None")
    private val rangeValue = FloatValue("Range", 5F, 1F, 7F)
    private val actionValue = ListValue("Action", arrayOf("Destroy", "Use"), "Destroy")
    private val instantValue = BooleanValue("Instant", false)
    private val switchValue = IntegerValue("SwitchDelay", 250, 0, 1000)
    private val swingValue = BooleanValue("Swing", true)
    private val rotationsValue = BooleanValue("Rotations", true)
    private val surroundingsValue = BooleanValue("Surroundings", true)
    private val noHitValue = BooleanValue("NoHit", false)


    /**
     * VALUES
     */

    private var pos: BlockPos? = null
    private var oldPos: BlockPos? = null
    private var blockHitDelay = 0
    private val switchTimer = MSTimer()
    var currentDamage = 0F

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        //if (event.eventState == UpdateState.OnUpdate) return

        val thePlayer = mc.thePlayer ?: return

        if (noHitValue.get()) {
            val killAura = KevinClient.moduleManager.getModule("KillAura") as KillAura

            if (killAura.getToggle() && killAura.target != null)
                return
        }

        val targetId = blockValue.get()

        if (pos == null || Block.getIdFromBlock(getBlock(pos!!)!!) != targetId ||
            getCenterDistance(pos!!) > rangeValue.get())
            pos = find(targetId)

        // Reset current breaking when there is no target block
        if (pos == null) {
            currentDamage = 0F
            return
        }

        // BedCheck
        val teams = KevinClient.moduleManager.getModule("Teams") as Teams
        if (Block.getBlockById(targetId)==Blocks.bed&&teams.bedCheckValue.get()&&pos in teams.teamBed){
            pos = null
            currentDamage = 0F
            return
        }

        var currentPos = pos ?: return
        var rotations = RotationUtils.faceBlock(currentPos) ?: return

        // Surroundings
        var surroundings = false

        if (surroundingsValue.get()) {
            val eyes = thePlayer.getPositionEyes(1F)
            val blockPos = mc.theWorld!!.rayTraceBlocks(eyes, rotations.vec, false,
                false, true)?.blockPos

            if (blockPos != null && (blockPos) !is BlockAir) {
                if (currentPos.x != blockPos.x || currentPos.y != blockPos.y || currentPos.z != blockPos.z)
                    surroundings = true

                pos = blockPos
                currentPos = pos ?: return
                rotations = RotationUtils.faceBlock(currentPos) ?: return
            }
        }

        // Reset switch timer when position changed
        if (oldPos != null && oldPos != currentPos) {
            currentDamage = 0F
            switchTimer.reset()
        }

        oldPos = currentPos

        if (!switchTimer.hasTimePassed(switchValue.get().toLong()))
            return

        // Block hit delay
        if (blockHitDelay > 0) {
            blockHitDelay--
            return
        }

        // Face block
        if (rotationsValue.get())
            RotationUtils.setTargetRotation(rotations.rotation)

        when {
            // Destory block
            actionValue.get().equals("destroy", true) || surroundings -> {
                // Auto Tool
                val autoTool = KevinClient.moduleManager.getModule("AutoTool") as AutoTool
                if (autoTool.getToggle())
                    autoTool.switchSlot(currentPos)

                // Break block
                if (instantValue.get()) {
                    // CivBreak style block breaking
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        currentPos, EnumFacing.DOWN))

                    if (swingValue.get())
                        thePlayer.swingItem()

                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        currentPos, EnumFacing.DOWN))
                    currentDamage = 0F
                    return
                }

                // Minecraft block breaking
                val block = getBlock(currentPos) ?: return

                if (currentDamage == 0F) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        currentPos, EnumFacing.DOWN))

                    if (thePlayer.capabilities.isCreativeMode ||
                        block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, pos!!) >= 1.0F) {
                        if (swingValue.get())
                            thePlayer.swingItem()
                        mc.playerController.onPlayerDestroyBlock(pos!!, EnumFacing.DOWN)

                        currentDamage = 0F
                        pos = null
                        return
                    }
                }

                if (swingValue.get())
                    thePlayer.swingItem()

                currentDamage += block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, currentPos)
                mc.theWorld!!.sendBlockBreakProgress(thePlayer.entityId, currentPos, (currentDamage * 10F).toInt() - 1)

                if (currentDamage >= 1F) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        currentPos, EnumFacing.DOWN))
                    mc.playerController.onPlayerDestroyBlock(currentPos, EnumFacing.DOWN)
                    blockHitDelay = 4
                    currentDamage = 0F
                    pos = null
                }
            }

            // Use block
            actionValue.get().equals("use", true) -> if (mc.playerController.onPlayerRightClick(
                    thePlayer, mc.theWorld!!, thePlayer.heldItem!!, pos!!, EnumFacing.DOWN,
                    Vec3(currentPos.x.toDouble(), currentPos.y.toDouble(), currentPos.z.toDouble()))) {
                if (swingValue.get())
                    thePlayer.swingItem()

                blockHitDelay = 4
                currentDamage = 0F
                pos = null
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(pos ?: return, Color.RED, true)
    }

    /**
     * Find new target block by [targetID]
     */
    /*private fun find(targetID: Int) =
        searchBlocks(rangeValue.get().toInt() + 1).filter {
                    Block.getIdFromBlock(it.value) == targetID && getCenterDistance(it.key) <= rangeValue.get()
                            && (isHitable(it.key) || surroundingsValue.get())
                }.minBy { getCenterDistance(it.key) }?.key*/

    //Removed triple iteration of blocks to improve speed
    /**
     * Find new target block by [targetID]
     */
    private fun find(targetID: Int): BlockPos? {
        val thePlayer = mc.thePlayer ?: return null

        val radius = rangeValue.get().toInt() + 1

        var nearestBlockDistance = Double.MAX_VALUE
        var nearestBlock: BlockPos? = null

        for (x in radius downTo -radius + 1) {
            for (y in radius downTo -radius + 1) {
                for (z in radius downTo -radius + 1) {
                    val blockPos = BlockPos(thePlayer.posX.toInt() + x, thePlayer.posY.toInt() + y,
                        thePlayer.posZ.toInt() + z)
                    val block = getBlock(blockPos) ?: continue

                    if (Block.getIdFromBlock(block) != targetID) continue

                    val distance = getCenterDistance(blockPos)
                    if (distance > rangeValue.get()) continue
                    if (nearestBlockDistance < distance) continue
                    if (!isHitable(blockPos) && !surroundingsValue.get()) continue

                    nearestBlockDistance = distance
                    nearestBlock = blockPos
                }
            }
        }

        return nearestBlock
    }

    /**
     * Check if block is hitable (or allowed to hit through walls)
     */
    private fun isHitable(blockPos: BlockPos): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        return when (throughWallsValue.get().toLowerCase()) {
            "raycast" -> {
                val eyesPos = Vec3(thePlayer.posX, thePlayer.entityBoundingBox.minY +
                        thePlayer.eyeHeight, thePlayer.posZ)
                val movingObjectPosition = mc.theWorld!!.rayTraceBlocks(eyesPos,
                    Vec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5), false,
                    true, false)

                movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
            }
            "around" -> !isFullBlock(blockPos.down()) || !isFullBlock(blockPos.up()) || !isFullBlock(blockPos.north())
                    || !isFullBlock(blockPos.east()) || !isFullBlock(blockPos.south()) || !isFullBlock(blockPos.west())
            else -> true
        }
    }

    override val tag: String
        get() = getBlockName(blockValue.get())
}