package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.PlayerUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.module.combat.AntiBot;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.EnumProperty;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@ModuleData(moduleName = "ImageESP", moduleCategory = ModuleCategory.VISUAL)
public class ImageESP extends Module {

    @EventListener
    EventConsumer<EventRender3D> onRender3D;

    public final EnumProperty<imgModes> imgMode = new EnumProperty<>("Image Mode", imgModes.Shidder, this);

    String location;

    public ImageESP() {
        imgMode.onValueChange = () -> setModuleSuffix(imgMode.getPropertyValue().toString());
        setModuleSuffix(imgMode.getPropertyValue().toString());
        onRender3D = (event -> {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e != null) {
                    if (e instanceof EntityLivingBase) {
                        if (e instanceof EntityPlayer) {
                            if (e != mc.thePlayer) {
                                if (e != null) {
                                    if (e instanceof EntityLivingBase) {
                                        if (e instanceof EntityPlayer && !AntiBot.botList.contains(e.getEntityId())) {
                                            if (!e.isInvisible() && e != mc.thePlayer) {
                                                if (imgMode.getPropertyValue().equals(imgModes.Shidder)) {
                                                    location = "BozoWare/shidder.jpg";
                                                }
                                                if (imgMode.getPropertyValue().equals(imgModes.Posk)) {
                                                    location = "BozoWare/posk.png";
                                                }
                                                if (imgMode.getPropertyValue().equals(imgModes.Anth)) {
                                                    location = "BozoWare/anth.png";
                                                }
                                                if (imgMode.getPropertyValue().equals(imgModes.Phobos)) {
                                                    location = "BozoWare/phobo.png";
                                                }
                                                if (imgMode.getPropertyValue().equals(imgModes.Nusted)) {
                                                    location = "BozoWare/Nusted.png";
                                                }
                                                if (imgMode.getPropertyValue().equals(imgModes.Tear)) {
                                                    location = "BozoWare/Tear.png";
                                                }
                                                mc.getTextureManager().bindTexture(new ResourceLocation(location));
                                                PlayerUtils.drawImageESP(e);
                                            }
                                        }
                                    }
                                }
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
        Nusted,
        Tear,
        Phobos,
        Anth
    }
}