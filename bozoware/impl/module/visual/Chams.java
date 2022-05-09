package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.module.combat.AntiBot;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.EnumProperty;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(moduleName = "Chams", moduleCategory = ModuleCategory.VISUAL)
public class Chams extends Module {

    public EnumProperty<chamsMode> chamsXD = new EnumProperty<>("Chams Mode", chamsMode.Colored, this);
    public ColorProperty color = new ColorProperty("Color", Color.red, this);
    public BooleanProperty hudColors = new BooleanProperty("Client Colors", true, this);


    @EventListener
    EventConsumer<EventRender3D> onRender3D;
    String location;

    public Chams() {
        color.setHidden(false);
        hudColors.setHidden(false);
        chamsXD.onValueChange = () -> {
            color.setHidden(!chamsXD.getPropertyValue().equals(chamsMode.Colored));
            hudColors.setHidden(!chamsXD.getPropertyValue().equals(chamsMode.Colored));
        };
        onRender3D = (event -> {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e != null) {
                    if (e instanceof EntityLivingBase) {
                        if (e instanceof EntityPlayer && !AntiBot.botList.contains(e.getEntityId())) {
                            if(e != mc.thePlayer){
                                GL11.glPushMatrix();
                                GL11.glClear(256);
                                RenderHelper.enableStandardItemLighting();
                                mc.getRenderManager().renderEntitySimple(e, mc.timer.renderPartialTicks);
                                RenderHelper.disableStandardItemLighting();
                                GL11.glPopMatrix();
                            }
                        }
                    }
                }
            }
        });
    }
    public static Chams getInstance() {
        return (Chams) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Chams.class);
    }
    public enum chamsMode{
        Magic,
        Colored
    }
}