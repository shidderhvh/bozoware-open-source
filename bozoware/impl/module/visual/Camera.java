package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.HurtShakeEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleData(moduleName = "Camera", moduleCategory = ModuleCategory.VISUAL)
public class Camera extends Module {
    private final BooleanProperty hurtCam = new BooleanProperty("No Hurt Cam", true, this);
    private final BooleanProperty motionBlurBool = new BooleanProperty("Motion Blur", false, this);

    @EventListener
    EventConsumer<HurtShakeEvent> hurtShakeEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Camera(){
        hurtShakeEvent = (e -> {
            if(hurtCam.getPropertyValue()) {
                e.setCancelled(true);
            }
        });
        onUpdatePositionEvent = (e -> {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1604400, 2));
            if(motionBlurBool.getPropertyValue()) {
                final EntityRenderer er = this.mc.entityRenderer;
                this.mc.entityRenderer.useShader = true;
                if (this.mc.theWorld != null && (this.mc.entityRenderer.theShaderGroup == null || !this.mc.entityRenderer.theShaderGroup.getShaderGroupName().contains("phosphor"))) {
                    if (er.theShaderGroup != null) {
                        er.theShaderGroup.deleteShaderGroup();
                    }
                    er.loadShader(EntityRenderer.shaderResourceLocations[12]);
                }
            }
        });
        onModuleDisabled = () -> {
            mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
            this.mc.entityRenderer.useShader = true;
            if (this.mc.entityRenderer.theShaderGroup != null) {
                this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            }
        };
        motionBlurBool.onValueChange = () -> {
            if(motionBlurBool.getPropertyValue()) {
                final EntityRenderer er = this.mc.entityRenderer;
                er.activateNextShader();
            } else {
                this.mc.entityRenderer.useShader = true;
                if (this.mc.entityRenderer.theShaderGroup != null) {
                    this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
                }
            }
        };
    }
    public static Camera getInstance() {
        return (Camera) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Camera.class);
    }
}
