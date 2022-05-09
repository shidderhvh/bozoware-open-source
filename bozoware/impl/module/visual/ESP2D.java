package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.PlayerUtils;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.property.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@ModuleData(moduleName = "2DESP", moduleCategory = ModuleCategory.VISUAL)
public class ESP2D extends Module {

    @EventListener
    EventConsumer<EventRender3D> onRender3D;

    public final EnumProperty<imgModes> imgMode = new EnumProperty<>("Image Mode", imgModes.Shidder, this);

    String location;

    public ESP2D() {
        setModuleSuffix(imgMode.getPropertyValue().toString());
        onRender3D = (event -> {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e != null) {
                    if (e instanceof EntityLivingBase) {
                        if (e instanceof EntityPlayer) {
                            if(e != mc.thePlayer){
                                if(imgMode.getPropertyValue().equals(imgModes.Shidder)){
                                    location = "BozoWare/shidder.jpg";
                                }
                                if(imgMode.getPropertyValue().equals(imgModes.Posk)){
                                    location = "BozoWare/posk.png";
                                }
                                if(imgMode.getPropertyValue().equals(imgModes.Anth)){
                                    location = "BozoWare/anth.png";
                                }
                                mc.getTextureManager().bindTexture(new ResourceLocation(location));
                                PlayerUtils.drawImageESP(e);
                            }
                        }
                    }
                }
            }
        });
    }
    public static ESP2D getInstance() {
        return (ESP2D) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(ESP2D.class);
    }
    public enum imgModes {
        Shidder,
        Posk,
        Anth
    }
}