package kevin.module

import kevin.event.EventTarget
import kevin.event.KeyEvent
import kevin.event.Listenable
import kevin.main.KevinClient
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
            BowAura(),
            Criticals(),
            FastBow(),
            HitBox(),
            KillAura(),
            SuperKnockback(),
            TeleportAttack()
        )
        exploitList = arrayListOf(
            AbortBreaking(),
            AntiHunger(),
            BoatJump(),
            Clip(),
            Disabler,
            ForceUnicodeChat(),
            Ghost(),
            GhostHand(),
            IllegalItems,
            KeepContainer(),
            Kick(),
            Log4j2(),
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
            AdminDetector,
            AntiBot,
            AntiInvalidBlockPlacement(),
            AntiShop(),
            AutoCommand(),
            AutoDisable,
            AutoL(),
            ChatControl,
            ComponentOnHover(),
            Diastimeter(),
            HideAndSeekHack,
            KillerDetector(),
            NameProtect(),
            NoAchievement,
            NoCommand,
            NoRotateSet(),
            NoScoreboard,
            ResourcePackSpoof(),
            SuperSpammer(),
            Teams(),
            Translator
        )
        movementList = arrayListOf(
            AirJump(),
            AirLadder(),
            AntiVoid(),
            Fly(),
            Freeze(),
            HighJump(),
            InvMove(),
            KeepSprint(),
            LiquidWalk(),
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
            TargetStrafe,
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
            Crosshair,
            DamageParticle(),
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
            Renderer,
            Rotations(),
            StorageESP(),
            TNTESP(),
            Tracers(),
            Trajectories(),
            TrueSight(),
            XRay()
        )
        worldList = arrayListOf(
            Breaker(),
            ChestStealer(),
            FastBreak(),
            FastPlace(),
            LightningDetector,
            NoSlowBreak(),
            Nuker(),
            Scaffold(),
            TeleportUse(),
            Timer(),
            World
        )

        modules.add(Targets())
        modules.addAll(combatList!!)
        modules.addAll(exploitList!!)
        modules.addAll(miscList!!)
        modules.addAll(movementList!!)
        modules.addAll(playerList!!)
        modules.addAll(renderList!!)
        modules.addAll(worldList!!)
        modules.forEach { KevinClient.eventManager.registerListener(it) }
        KevinClient.eventManager.registerListener(this)
    }

    fun getModules(): ArrayList<Module>{
        return modules
    }

    fun getModule(name: String): Module?{
        for (module in modules){
            if (module.name.equals(name,ignoreCase = true))return module
        }
        return null
    }

    @EventTarget
    fun onKey(key: KeyEvent){
        for (module in modules){
            if (module.keyBind == key.key) module.toggle()
        }
    }

    override fun handleEvents(): Boolean {
        return true
    }

    fun registerModule(module: Module) {
        modules += module
        KevinClient.eventManager.registerListener(module)
    }

    fun unregisterModule(module: Module) {
        modules.remove(module)
        KevinClient.eventManager.unregisterListener(module)
    }
}