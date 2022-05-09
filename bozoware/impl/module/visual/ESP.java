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
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.combat.AntiBot;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.EnumProperty;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static bozoware.base.util.player.PlayerUtils.get2DVector;
import static org.lwjgl.opengl.GL11.glPushMatrix;

@ModuleData(moduleName = "ESP", moduleCategory = ModuleCategory.VISUAL)
public class ESP extends Module {

    private static final Frustum frustrum = new Frustum();

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventRender3D> onRender3D;
    @EventListener
    EventConsumer<Render2DEvent> onRender2D;

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();

    public final EnumProperty<ESPModes> mode = new EnumProperty<>("Mode", ESPModes.Shader, this);
    private final BooleanProperty renderonSelf = new BooleanProperty("Render on Self", true, this);
    private final ColorProperty color = new ColorProperty("Color", new Color(0xffff0000), this);

    String location;

    public ESP() {
//        ?Suffix("Shader Outline");
        onUpdatePositionEvent = (event -> {

        });
        onRender2D = (event -> {

        });
        onRender3D = (event -> {
//            if(mc.thePlayer != null){
//                double x = mc.thePlayer.posX;
//                double y = mc.thePlayer.posY;
//                double z = mc.thePlayer.posZ;
//            }
            switch(mode.getPropertyValue()){
                case Box:
                    for (Entity e : mc.theWorld.loadedEntityList) {
                        if (e != null) {
                            if (e instanceof EntityLivingBase) {
                                if (e instanceof EntityPlayer && !AntiBot.botList.contains(e.getEntityId())) {
                                    if (!e.isInvisible() && e != mc.thePlayer) {
//                                        if(!renderonSelf.getPropertyValue())
//                                            if(e == mc.thePlayer)
//                                                return;
//                                        if(e instanceof EntityPlayerSP && renderonSelf.getPropertyValue()) {
//                                            if (mc.gameSettings.thirdPersonView == 0) return;
//                                        }
                                        PlayerUtils.draw2DESP(color.getColorRGB(), e);
                                        Vector4d pos = get2DVector(e.getEntityBoundingBox());

//                                        if (pos == null)
//                                            return;
//                                        if(!PlayerUtils.isInViewFrustrum(e.getEntityBoundingBox()))
//                                            return;
//                                        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
//                                        mc.entityRenderer.setupOverlayRendering();
//
//                                        double posX = pos.x, posY = pos.y, endPosX = pos.z, endPosY = pos.w;
//
//                                        GlStateManager.pushMatrix();
//                                        GlStateManager.enableBlend();
//                                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                                        GlStateManager.color(1, 1, 1, 1);
//
//                                        int size = 20;
//                                        float drawX = (float)(endPosX - posX + size),
//                                                drawY = (float)(endPosY - posY + size);
//                                        Gui.drawRect((int) posX - 20, (int) posY, (float) endPosX, (float) endPosY, TargetHUD.getHealthColor(((EntityPlayer) e).getHealth(), ((EntityPlayer) e).getMaxHealth()).getRGB());
//                                        GlStateManager.disableBlend();
//                                        GlStateManager.popMatrix();
//
                                    }
                                }
                            }
                        }
                    }
                    break;
                case Tracer:
                    for (Entity e : mc.theWorld.loadedEntityList) {
                        if (e != null) {
                            if (e instanceof EntityLivingBase) {
                                if (e instanceof EntityPlayer && !AntiBot.botList.contains(e.getEntityId())) {
                                    if(e != mc.thePlayer){
                                        line(e, false, 0);
                                    }
                                } else if (e instanceof EntityPlayer && AntiBot.botList.contains(e.getEntityId())){
                                    if(e != mc.thePlayer){
                                        line(e, true, 0xffff0000);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        });
    }
    public static ESP getInstance() {
        return (ESP) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(ESP.class);
    }
    public enum ESPModes {
        Shader,
        Box,
        Tracer
    }
    private void line(Entity target, boolean CustomColor, int Color) {
        glPushMatrix();
        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        double x = RenderUtil.animate(target.posX, target.lastTickPosX, 10);
        double y = RenderUtil.animate(target.posY, target.lastTickPosY, 10);
        double z = RenderUtil.animate(target.posZ, target.lastTickPosZ, 10);
        x -= RenderManager.renderPosX;
        y -= RenderManager.renderPosY;
        z -= RenderManager.renderPosZ;
        if(!CustomColor)
            RenderUtil.setColorWithAlpha(color.getPropertyValue().getRGB(), 255);
        else
            RenderUtil.setColorWithAlpha(Color, 255);

        GL11.glLineWidth(1.5F);
        GL11.glBegin(3);
        GL11.glVertex3d(0.0D, (double)mc.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0D, 1.0D, 1.0D);
        GL11.glPopMatrix();
    }
    public static void entityESPBox(Entity entity, Color color) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);


        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) mc.timer.renderPartialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) mc.timer.renderPartialTicks;
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.timer.renderPartialTicks;
        GlStateManager.translate(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ);
        GlStateManager.rotate(-yaw, 0, 1, 0);
        GlStateManager.translate(-(x - mc.getRenderManager().renderPosX), -(y - mc.getRenderManager().renderPosY), -(z - mc.getRenderManager().renderPosZ));
        GL11.glEnable(GL11.GL_LINE_SMOOTH);


        GlStateManager.color(Objects.requireNonNull(color).getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        RenderGlobal.func_181561_a(
                new AxisAlignedBB(
                        x - entity.width / 2 - 0.05 - x + (x - mc.getRenderManager().renderPosX),
                        y - y + (y - mc.getRenderManager().renderPosY),
                        z - entity.width / 2 - 0.05 - z + (z - mc.getRenderManager().renderPosZ),
                        x + entity.width / 2 + 0.05 - x + (x - mc.getRenderManager().renderPosX),
                        y + entity.height + 0.1 - y + (y - mc.getRenderManager().renderPosY),
                        z + entity.width / 2 + 0.05 - z + (z - mc.getRenderManager().renderPosZ)
                ));
        GlStateManager.translate(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.translate(-(x - mc.getRenderManager().renderPosX), -(y - mc.getRenderManager().renderPosY), -(z - mc.getRenderManager().renderPosZ));
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }
}