package kevin.module.modules.misc

import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.TextValue
import net.minecraft.entity.EntityLivingBase

class AntiShop : Module("AntiShop", "Attack modules will not attack the store.") {
    private val nameDetect = BooleanValue("NameDetect", false)
    private val nameDetectName = TextValue("NameDetectName", "NPC | SHOP||NPC | UPGRADES")
    private val armorDetect = BooleanValue("NoArmorDetect", true)
    fun isShop(entityLivingBase: EntityLivingBase): Boolean{
        if (!this.state)
            return false
        if (nameDetect.get()) {
            val names = nameDetectName.get().split("||").filter { it.isNotEmpty() && it.isNotBlank() }
            val displayName = entityLivingBase.displayName?.formattedText
            if (displayName != null) {
                var name = displayName
                val set = mutableSetOf<String>()
                for (i in 0 until name.length-1) {
                    if (name[i] == '§')
                        set += "${name[i]}${name[i+1]}"
                }
                set.forEach {
                    name = name!!.replace(it, "")
                }
                if (names.contains(name))
                    return true
            }
        }
        if (armorDetect.get()) {
            val first = entityLivingBase.getCurrentArmor(0)?.item
            val second = entityLivingBase.getCurrentArmor(1)?.item
            val third = entityLivingBase.getCurrentArmor(2)?.item
            val fourth  = entityLivingBase.getCurrentArmor(3)?.item
            if (first == null && second == null && third == null && fourth == null)
                return true
        }
        return false
    }
}