package kevin.module.modules.render

import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import net.minecraft.init.Blocks

class XRay : Module(name = "XRay", description = "Allows you to see through walls.", category = ModuleCategory.RENDER) {
    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }
    val mode = ListValue("Mode", arrayOf("Simple","Translucent"),"Simple")
    override val tag: String
        get() = mode.get()
    val xrayBlocks = mutableListOf(
        Blocks.coal_ore,
        Blocks.iron_ore,
        Blocks.gold_ore,
        Blocks.redstone_ore,
        Blocks.lapis_ore,
        Blocks.diamond_ore,
        Blocks.emerald_ore,
        Blocks.quartz_ore,
        Blocks.clay,
        Blocks.glowstone,
        Blocks.crafting_table,
        Blocks.torch,
        Blocks.ladder,
        Blocks.tnt,
        Blocks.coal_block,
        Blocks.iron_block,
        Blocks.gold_block,
        Blocks.diamond_block,
        Blocks.emerald_block,
        Blocks.redstone_block,
        Blocks.lapis_block,
        Blocks.fire,
        Blocks.mossy_cobblestone,
        Blocks.mob_spawner,
        Blocks.end_portal_frame,
        Blocks.enchanting_table,
        Blocks.bookshelf,
        Blocks.command_block,
        Blocks.lava,
        Blocks.flowing_lava,
        Blocks.water,
        Blocks.flowing_water,
        Blocks.furnace,
        Blocks.lit_furnace
    )
}