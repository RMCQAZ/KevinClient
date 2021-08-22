package kevin.module

import kevin.event.EventTarget
import kevin.event.KeyEvent
import kevin.event.Listenable
import kevin.main.Kevin
import kevin.module.modules.*
import kevin.module.modules.combat.*
import kevin.module.modules.exploit.*
import kevin.module.modules.misc.AntiBot
import kevin.module.modules.misc.NameProtect
import kevin.module.modules.misc.Teams
import kevin.module.modules.movement.*
import kevin.module.modules.player.*
import kevin.module.modules.render.*
import kevin.module.modules.world.Breaker
import kevin.module.modules.world.ChestStealer
import kevin.module.modules.world.Scaffold
import kevin.module.modules.world.Timer

class ModuleManager : Listenable {

    private val modules = ArrayList<Module>()

    private var combatList:ArrayList<Module>? = null
    private var exploitList:ArrayList<Module>? = null
    private var miscList:ArrayList<Module>? = null
    private var movementList:ArrayList<Module>? = null
    private var playerList:ArrayList<Module>? = null
    private var renderList:ArrayList<Module>? = null
    private var worldList:ArrayList<Module>? = null

    fun load(){
        combatList = arrayListOf(
            AntiKnockback(),
            AutoArmor(),
            AutoClicker(),
            AutoWeapon(),
            Criticals(),
            HitBox(),
            KillAura(),
            TeleportAttack()
        )
        exploitList = arrayListOf(
            AbortBreaking(),
            MultiActions(),
            TP()
        )
        miscList = arrayListOf(
            AntiBot(),
            NameProtect(),
            Teams()
        )
        movementList = arrayListOf(
            Fly(),
            InvMove(),
            NoSlow(),
            Speed(),
            Sprint(),
            Strafe()
        )
        playerList = arrayListOf(
            AntiCactus(),
            AutoSneak(),
            AutoTool(),
            FastUse(),
            InventoryCleaner(),
            NoFall(),
            Reach()
        )
        renderList = arrayListOf(
            Animations(),
            AntiBlind(),
            BlockOverlay(),
            CameraClip(),
            Chams(),
            ClickGui(),
            ESP(),
            HUD(),
            Rotations(),
            TrueSight()
        )
        worldList = arrayListOf(
            Breaker(),
            ChestStealer(),
            Scaffold(),
            Timer()
        )

        modules.add(Targets())
        modules.addAll(combatList!!)
        modules.addAll(exploitList!!)
        modules.addAll(miscList!!)
        modules.addAll(movementList!!)
        modules.addAll(playerList!!)
        modules.addAll(renderList!!)
        modules.addAll(worldList!!)
        modules.forEach { Kevin.getInstance.eventManager.registerListener(it) }
        Kevin.getInstance.eventManager.registerListener(this)
    }

    fun getModules(): ArrayList<Module>{
        return modules
    }

    fun getModule(name: String): Module?{
        for (module in modules){
            if (module.getName().equals(name,ignoreCase = true))return module
        }
        return null
    }

    @EventTarget
    fun onKey(key: KeyEvent){
        for (module in modules){
            if (module.getKeyBind() == key.key) module.toggle()
        }
    }

    override fun handleEvents(): Boolean {
        return true
    }
}