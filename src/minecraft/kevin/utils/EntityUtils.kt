package kevin.utils

import kevin.main.Kevin
import kevin.module.modules.misc.Teams
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object EntityUtils : MinecraftInstance() {

    @JvmField
    var targetInvisible = true

    @JvmField
    var targetPlayer = true

    @JvmField
    var targetMobs = true

    @JvmField
    var targetAnimals = false

    @JvmField
    var targetDeath = false

    @JvmStatic
    fun isSelected(entity: Entity?, canAttackCheck: Boolean): Boolean {
        if (entity is EntityLivingBase && (targetDeath || entity.isEntityAlive) && entity != mc.thePlayer) {
            if (targetInvisible || !entity.isInvisible) {
                if (targetPlayer && entity is EntityPlayer) {

                    if (canAttackCheck) {
                        /***if (isBot(entityPlayer))
                            return false***/

                        if (entity.isClientFriend() /***&& !LiquidBounce.moduleManager.getModule(NoFriends::class.java).state***/ )
                            return false

                        if (entity.isSpectator) return false
                        val teams = Kevin.getInstance.moduleManager.getModule("Teams") as Teams
                        return !teams.getToggle() || !teams.isInYourTeam(entity)
                    }
                    return true
                }

                return targetMobs && entity.isMob() || targetAnimals && entity.isAnimal()
            }
        }
        return false
    }
}