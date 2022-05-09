package bozoware.base.module;

import bozoware.base.BozoWare;
import bozoware.impl.command.HideCommand;
import bozoware.impl.module.combat.*;
import bozoware.impl.module.movement.*;
import bozoware.impl.module.player.*;
import bozoware.impl.module.visual.*;
import bozoware.impl.module.world.*;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        registerModules(
                // Combat Modules
                new Velocity(),
                new Aura(),
                new FastBow(),
                new Criticals(),
                new TargetStrafe(),
                new AutoClicker(),
                new AntiBot(),
                new Reach(),

                // Player Modules
                new Timer(),
//                new ClickTP(),
                new Speedmine(),
                new KillSays(),
                new NoSlow(),
                new ChatBypass(),
                new NoFall(),
                new FastUse(),
                new Blink(),
                new Invmove(),
                new BlockFly(),
                new Scaffold(),
                new Teleport(),
                new ResetVL(),
                new AutoTool(),
                new ChestStealer(),
                new AutoArmor(),
                new AutoPot(),
                new Spammer(),
                new AutoGapple(),
                new InvManager(),
                new FastPlace(),
                new BedBreaker(),

                // Movement Modules
                new Step(),
                new Sprint(),
                new IceSpeed(),
                new LongJump(),
                new HighJump(),
                new Flight(),
                new Speed(),


                // Visual Modules
                new ChinaHat(),
                new Animations(),
                new TargetHUD(),
                new Chams(),
                new NameHider(),
                new ClickGUI(),
                new HUD(),
                new ESP(),
                new Camera(),
                new ImageESP(),
                new TimeChanger(),
                new SessionInfo(),
                new XRay(),
                new AntiVoid(),


                // World Modules
                new Jesus(),
                new ClickTP(),
                new HackerDetector(),
                new PingSpoofer(),
                new Freecam(),
                new Test(),
                new AutoHypixel(),
                new NoGUIClose(),
                new Disabler()

                //Skyblock Modules
//                new farmStats()
        );
    }

    private void registerModules(Module... module) {
        modules.addAll(Arrays.asList(module));
    }
    public void onKeyPressed(int keyPressed) {
        modules.forEach(module -> {
            if (module.getModuleBind() == keyPressed) {
                module.toggleModule();
//                NotificationManager.show(new Notification(NotificationType.INFO, "Module Toggled", "Toggled " + module.getModuleName() + "!", 1F));
            }
        });
    }

    public ArrayList<Module> getModulesByCategory(ModuleCategory category) {
        ArrayList<Module> sortedModules = new ArrayList<>(modules);
        sortedModules.removeIf(module -> !module.getModuleCategory().equals(category));
        return sortedModules;
    }

    public Function<String, Module> getModuleByName =
            (label -> modules.stream().filter(module -> module.getModuleName().equalsIgnoreCase(label)).findFirst().orElse(null));

    public Function<Class<? extends Module>, Module> getModuleByClass =
            (moduleClass -> modules.stream().filter(module -> module.getClass() == moduleClass).findFirst().orElse(null));

    public ArrayList<Module> getModulesToDraw(boolean vanillaFont) {
        ArrayList<Module> sortedModules = new ArrayList<>(modules);
        sortedModules.removeIf(module -> !module.isModuleToggled());
        sortedModules.removeIf(HideCommand.isHidden::contains);
        MinecraftFontRenderer fontRenderer = BozoWare.getInstance().getFontManager().mediumFontRenderer;
        FontRenderer vanillaFontRenderer = Minecraft.getMinecraft().fontRendererObj;
        sortedModules.sort(Comparator.comparingDouble(module -> (vanillaFont ?
                vanillaFontRenderer.getStringWidth(((Module) module).getModuleDisplayName()) : fontRenderer.getStringWidth(((Module) module).getModuleDisplayName()))).reversed());
        return sortedModules;
    }
    public ArrayList<Module> getModules(boolean vanillaFont) {
        ArrayList<Module> sortedModules = new ArrayList<>(modules);
        sortedModules.removeIf(HideCommand.isHidden::contains);
        MinecraftFontRenderer fontRenderer = BozoWare.getInstance().getFontManager().mediumFontRenderer;
        FontRenderer vanillaFontRenderer = Minecraft.getMinecraft().fontRendererObj;
            sortedModules.sort(Comparator.comparingDouble(module -> (vanillaFont ?
                    vanillaFontRenderer.getStringWidth(((Module) module).getModuleDisplayName()) : fontRenderer.getStringWidth(((Module) module).getModuleDisplayName()))).reversed());
        return sortedModules;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
