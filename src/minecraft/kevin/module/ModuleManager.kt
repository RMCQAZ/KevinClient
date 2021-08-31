package kevin.module

import kevin.event.EventTarget
import kevin.event.KeyEvent
import kevin.event.Listenable
import kevin.main.Kevin
import kevin.module.modules.*
import kevin.module.modules.combat.*
import kevin.module.modules.exploit.*
import kevin.module.modules.misc.*
import kevin.module.modules.movement.*
import kevin.module.modules.player.*
import kevin.module.modules.render.*
import kevin.module.modules.world.*

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
            SuperKnockback(),
            TeleportAttack()
        )
        exploitList = arrayListOf(
            AbortBreaking(),
            AntiHunger(),
            Clip(),
            ForceUnicodeChat(),
            Ghost(),
            GhostHand(),
            KeepContainer(),
            Kick(),
            MultiActions(),
            NoPitchLimit(),
            Phase(),
            PingSpoof(),
            Plugins(),
            PortalMenu(),
            ServerCrasher(),
            TP(),
            VehicleOneHit()
        )
        miscList = arrayListOf(
            AntiBot(),
            AutoCommand(),
            ComponentOnHover(),
            NameProtect(),
            NoRotateSet(),
            ResourcePackSpoof(),
            SuperSpammer(),
            Teams()
        )
        movementList = arrayListOf(
            AirJump(),
            AirLadder(),
            AntiVoid(),
            Fly(),
            Freeze(),
            HighJump(),
            InvMove(),
            LongJump(),
            NoClip(),
            NoSlow(),
            NoWeb(),
            Parkour(),
            SafeWalk(),
            Speed(),
            Sprint(),
            Step(),
            Strafe(),
            WallClimb(),
            WaterSpeed()
        )
        playerList = arrayListOf(
            AntiAFK(),
            AntiCactus(),
            AutoFish(),
            AutoRespawn(),
            AutoSneak(),
            AutoTool(),
            Blink(),
            FastUse(),
            InventoryCleaner(),
            NoFall(),
            Reach(),
            Regen()
        )
        renderList = arrayListOf(
            Animations(),
            AntiBlind(),
            BlockESP(),
            BlockOverlay(),
            CameraClip(),
            CapeManager(),
            Chams(),
            ClickGui(),
            ESP(),
            FreeCam(),
            FullBright(),
            HUD(),
            HudDesigner(),
            ItemESP(),
            NameTags(),
            NoBob(),
            NoFOV(),
            NoHurtCam(),
            NoSwing(),
            Projectiles(),
            Rotations(),
            StorageESP(),
            TNTESP(),
            Tracers(),
            TrueSight()
        )
        worldList = arrayListOf(
            Breaker(),
            ChestStealer(),
            FastBreak(),
            FastPlace(),
            NoSlowBreak(),
            Nuker(),
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