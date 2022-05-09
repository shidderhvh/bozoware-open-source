package bozoware.impl.module.combat;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;


@ModuleData(moduleName = "AntiBot", moduleCategory = ModuleCategory.COMBAT)
public class AntiBot extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onLoadWorldEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.Hypixel, this);

    public static List<Integer> botList = new ArrayList<Integer>();

    public AntiBot() {
        setModuleSuffix(Mode.getPropertyValue().toString());
        onModuleEnabled = () -> {
            botList.clear();
        };
        onLoadWorldEvent = (e -> {
            botList.clear();
        });
        onUpdatePositionEvent = (e -> {
            switch (Mode.getPropertyValue().toString()){
                case "Hypixel":
                        for (Entity o : (List<Entity>) mc.theWorld.loadedEntityList) {
                            if (o != mc.thePlayer && o.getName().startsWith("§") && o.getName().contains("§c") && o instanceof EntityPlayer && !o.getDisplayName().getFormattedText().contains("NPC")) {
                                botList.add(o.getEntityId());
                                mc.theWorld.removeEntityFromWorld(o.getEntityId());
                            }
                        }
                    break;
                case "Basic":

                    break;
            }
        });
        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }
    private enum Modes{
        Hypixel,
        Funcraft
    }
}
