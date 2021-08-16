package kevin.event

import net.minecraft.block.Block
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class AttackEvent(val targetEntity: Entity?) : Event()

class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
    val x = blockPos.x
    val y = blockPos.y
    val z = blockPos.z
}

class ClickBlockEvent(val clickedBlock: BlockPos?, val EnumFacing: EnumFacing?) : Event()

class ClientShutdownEvent : Event()

data class EntityMovementEvent(val movedEntity: Entity) : Event()

class JumpEvent(var motion: Float) : CancellableEvent()

class KeyEvent(val key: Int) : Event()

class MotionEvent(val eventState: EventState) : Event()

class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

class StrafeEvent(val strafe: Float, val forward: Float, val friction: Float) : CancellableEvent()

class MoveEvent(var x: Double, var y: Double, var z: Double) : CancellableEvent() {
    var isSafeWalk = false

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

class PacketEvent(val packet: Packet<INetHandler>,val eventState: PacketMode) : CancellableEvent()

class PushOutEvent : CancellableEvent()

class Render2DEvent(val partialTicks: Float) : Event()

class Render3DEvent(val partialTicks: Float) : Event()

class RenderEntityEvent(val entity: Entity, val x: Double, val y: Double, val z: Double, val entityYaw: Float,
                        val partialTicks: Float) : Event()

class ScreenEvent(val guiScreen: GuiScreen?) : Event()

class StepEvent(var stepHeight: Float) : Event()

class StepConfirmEvent : Event()

class TextEvent(var text: String?) : Event()

class TickEvent : Event()

class UpdateEvent(val eventState: UpdateState) : Event()

class WorldEvent(val worldClient: WorldClient?) : Event()

class ClickWindowEvent(val windowId: Int, val slotId: Int, val mouseButtonClicked: Int, val mode: Int) : CancellableEvent()
