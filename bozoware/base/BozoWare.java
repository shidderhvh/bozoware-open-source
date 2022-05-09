package bozoware.base;

import bozoware.base.command.CommandManager;
import bozoware.base.config.ConfigManager;
import bozoware.base.event.Event;
import bozoware.base.event.EventManager;
import bozoware.base.file.FileManager;
import bozoware.base.module.ModuleManager;
import bozoware.base.property.PropertyManager;
import bozoware.base.util.visual.SkiddedBlurUtil;
import bozoware.visual.font.FontManager;
import bozoware.visual.screens.alt.AltManager;
import bozoware.visual.screens.dropdown.GuiDropDown;
import bozoware.visual.screens.ot.GuiOTUI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import viamcp.ViaMCP;

public class BozoWare {

    public final String CLIENT_NAME = "BozoWare";
    public final String CLIENT_VERSION = "220508";
    public static String BozoUserName;

    private static final BozoWare INSTANCE = new BozoWare();
    public static final SkiddedBlurUtil blurrer = new SkiddedBlurUtil();

    private ModuleManager moduleManager;
    private EventManager<Event> eventManager;
    private FontManager fontManager;
    private PropertyManager propertyManager;
    private CommandManager commandManager;
    private FileManager fileManager;
    private ConfigManager configManager;
    private AltManager altManager;
    private final GuiOTUI guiOTUIScreen = new GuiOTUI();

    public final Runnable onClientStart = () -> {
        System.out.println("Starting Client! Hello Bozo!");
        System.out.printf("%s Version %s%n", CLIENT_NAME, CLIENT_VERSION);
        this.eventManager = new EventManager<>();
        this.propertyManager = new PropertyManager();
        this.moduleManager = new ModuleManager();
        this.fontManager = new FontManager();
        this.commandManager = new CommandManager();
        this.fileManager = new FileManager();
        this.configManager = new ConfigManager();
        this.guiOTUIScreen.setupGui();
        this.altManager = new AltManager();
        this.altManager.onStart();

        GuiDropDown.onStartTask.run();
        try
        {
            ViaMCP.getInstance().start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    };

    public final Runnable onClientExit = () -> {
        System.out.println("Closing Client! GoodBye Bozo!");
        System.out.printf("%s Version %s%n", CLIENT_NAME, CLIENT_VERSION);
        this.altManager.onExit();
    };

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public EventManager<Event> getEventManager() {
        return eventManager;
    }

    public void chat(String msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00A7f[\u00A7c\u00A74BozoWare\u00A7f]\u00A7r " + msg));
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GuiOTUI getGuiOTUIScreen() {
        return guiOTUIScreen;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public static BozoWare getInstance() {
        return INSTANCE;
    }

    public AltManager getAltManager() {
        return altManager;
    }
}