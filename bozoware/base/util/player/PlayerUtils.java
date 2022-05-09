package bozoware.base.util.player;

import bozoware.base.BozoWare;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.visual.BloomUtil;
import bozoware.base.util.visual.BlurUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.module.visual.TargetHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector4d;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static TimerUtil timer = new TimerUtil();
    private static boolean doneBow = false;
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private static final Frustum frustrum = new Frustum();


    public static void bowSelf() {
        timer.reset();
        int oldSlot = mc.thePlayer.inventory.currentItem;

        Thread thread = new Thread(){
            public void run() {
                int oldSlot = mc.thePlayer.inventory.currentItem;
                ItemStack block = mc.thePlayer.getCurrentEquippedItem();

                if (block != null) {
                    block = null;
                }
                int slot = mc.thePlayer.inventory.currentItem;
                for (short g = 0; g < 9; g++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0 && (block == null || (block.getItem() instanceof ItemBow))) {
                        slot = g;
                        block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
                    }
                }

                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                mc.thePlayer.inventory.currentItem = slot;
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(block));
                Wrapper.sendPacketDirect(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, false));
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                try {
                    Thread.sleep(160);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doneBow = true;

//                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
                if (mc.thePlayer.hurtTime > 0){
                    mc.thePlayer.inventory.currentItem = ThreadLocalRandom.current().nextInt(1, 9);
                }
            }
        };

        thread.start();

        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
        mc.thePlayer.inventory.currentItem = oldSlot;
    }
    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }
    public static void draw2DESP(int color, Entity entity) {
        double x = interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        double y = interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks);
        double z = interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
        double width = entity.width / 1.5;
        double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
        AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
        Vector4d pos = get2DVector(aabb);
        if (pos == null)
            return;
        if(!isInViewFrustrum(aabb))
            return;

        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        mc.entityRenderer.setupOverlayRendering();

        double posX = pos.x, posY = pos.y, endPosX = pos.z, endPosY = pos.w;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1, 1);

        int size = 20;
//        BozoWare.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(entity.getName(), endPosX - BozoWare.getInstance().getFontManager().smallFontRenderer.getStringWidth(entity.getName()) / 2, posY - 10, -1);
//        BlurUtil.blurArea((int) posX, (int) posY, (float) endPosX - posX, (float) endPosY - posY);
        BloomUtil.drawAndBloom(() -> Gui.drawRect((int) posX, (int) posY, (float) endPosX, (float) endPosY, 0x40000000));
        BloomUtil.drawAndBloom(() -> RenderUtil.drawBoxOutline((int) posX, (int) posY, (float) endPosX, (float) endPosY, color, 0.5));

//            mc.fontRendererObj.drawStringWithShadow(entity.getName(), (float) posX, (float) posY - 20, -1);

        final boolean living = entity instanceof EntityLivingBase;

        if (living) {
                float hp = ((EntityLivingBase) entity).getHealth();
                final float maxHealth = ((EntityLivingBase) entity).getMaxHealth();
                if (hp > maxHealth) {
                    hp = maxHealth;
                }

                final double hpPercentage = hp / maxHealth;

                double hpAnimHeight = (endPosY - posY) * hpPercentage;
                final double hpRealHeight = (endPosY - posY) * hpPercentage;
                hpAnimHeight = RenderUtil.animate(hpRealHeight, hpAnimHeight, 0.05D);

                Gui.drawRect(posX - 3.5, posY - .5, posX - 1.5, endPosY + 0.5, 0x40000000);

                if (hp > 0) {
                    int colorrectCode = TargetHUD.getHealthColor(hp, maxHealth).getRGB();
                    Gui.drawRect(posX - 3, endPosY, posX - 2, endPosY - hpAnimHeight, colorrectCode);
                }
        }
//            Gui.drawRect(posX, posY, endPosX, endPosY, 0x40000000);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
    }
    public static void drawImageESP(Entity entity) {
        double x = interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        double y = interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks);
        double z = interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
        double width = entity.width / 1.5;
        double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
        AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
        Vector4d pos = get2DVector(aabb);
        if (aabb == null)
            return;
        if(pos == null)
            return;
        if(!isInViewFrustrum(aabb))
            return;

        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        mc.entityRenderer.setupOverlayRendering();

        double posX = pos.x, posY = pos.y, endPosX = pos.z, endPosY = pos.w;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1, 1);

        int size = 1;
        float drawX = (float)(endPosX - posX + size),
                drawY = (float)(endPosY - posY + size);

        Gui.drawModalRectWithCustomSizedTexture((int) posX, (int) (posY), 0, 0, (int)drawX, (int)drawY - 1, drawX, drawY);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static Vector4d get2DVector(AxisAlignedBB bb) {
        List<Vector3d> vectors = Arrays.asList(new Vector3d(bb.minX, bb.minY, bb.minZ), new Vector3d(bb.minX, bb.maxY, bb.minZ), new Vector3d(bb.maxX, bb.minY, bb.minZ), new Vector3d(bb.maxX, bb.maxY, bb.minZ), new Vector3d(bb.minX, bb.minY, bb.maxZ), new Vector3d(bb.minX, bb.maxY, bb.maxZ), new Vector3d(bb.maxX, bb.minY, bb.maxZ), new Vector3d(bb.maxX, bb.maxY, bb.maxZ));
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        Vector4d position = null;
        final int scale = new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight).getScaleFactor();;
        for (Vector3d vector : vectors) {
            vector = project2D(scale, vector.field_181059_a - mc.getRenderManager().viewerPosX, vector.field_181060_b - mc.getRenderManager().viewerPosY, vector.field_181061_c - mc.getRenderManager().viewerPosZ);
            if (vector != null && vector.field_181061_c >= 0.0 && vector.field_181061_c < 1.0) {
                if (position == null) {
                    position = new Vector4d(vector.field_181059_a, vector.field_181060_b, vector.field_181061_c, 0.0);
                }
                position.x = Math.min(vector.field_181059_a, position.x);
                position.y = Math.min(vector.field_181060_b, position.y);
                position.z = Math.max(vector.field_181059_a, position.z);
                position.w = Math.max(vector.field_181060_b, position.w);
            }
        }
        return position;
    }
    public static Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / scaleFactor, (Display.getHeight() - vector.get(1)) / scaleFactor, vector.get(2));
        }

        return null;
    }
    public static boolean isOnSameTeam(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        if (((EntityLivingBase) entity).getTeam() != null && mc.thePlayer.getTeam() != null) {
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

    public static void damageVerusAdvanced() {
        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        double val1 = 0;

        for (int i = 0; i <= 6; i++) {
            val1 += 0.5;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY + val1, mc.thePlayer.posZ, true));
        }

        double val2 = mc.thePlayer.posY + val1;

        ArrayList<Float> vals = new ArrayList<>();

        vals.add(0.07840000152587834f);
        vals.add(0.07840000152587834f);
        vals.add(0.23052736891295922f);
        vals.add(0.30431682745754074f);
        vals.add(0.37663049823865435f);
        vals.add(0.44749789698342113f);
        vals.add(0.5169479491049742f);
        vals.add(0.5850090015087517f);
        vals.add(0.6517088341626192f);
        vals.add(0.1537296175885956f);

        for (float value : vals) {
            val2 -= value;
        }
        mc.thePlayer.sendQueue.addToSendQueue(
                new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, val2, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));

        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        mc.thePlayer.jump();
    }
    public static void damageVerusBasic() {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }
    //prob dorts raycast method from retard client
    public static Entity raycast(Minecraft mc, double r3, Entity entity) {
        if (entity == null)
            return null;
        Entity var2 = mc.thePlayer;
        Vec3 var9 = entity.getPositionVector().add(new Vec3(0, entity.getEyeHeight(), 0));
        Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0, mc.thePlayer.getEyeHeight(), 0));
        Vec3 var10 = null;
        float var11 = 1.0F;
        AxisAlignedBB a = mc.thePlayer.getEntityBoundingBox()
                .addCoord(var9.xCoord - var7.xCoord, var9.yCoord - var7.yCoord, var9.zCoord - var7.zCoord)
                .expand(var11, var11, var11);
        List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, a);
        double var13 = r3 + 0.5f;
        Entity b = null;
        for (int var15 = 0; var15 < var12.size(); ++var15) {
            Entity var16 = (Entity) var12.get(var15);

            if (var16.canBeCollidedWith()) {
                float var17 = var16.getCollisionBorderSize();
                AxisAlignedBB var18 = var16.getEntityBoundingBox().expand((double) var17, (double) var17,
                        (double) var17);
                MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);

                if (var18.isVecInside(var7)) {
                    if (0.0D < var13 || var13 == 0.0D) {
                        b = var16;
                        var10 = var19 == null ? var7 : var19.hitVec;
                        var13 = 0.0D;
                    }
                } else if (var19 != null) {
                    double var20 = var7.distanceTo(var19.hitVec);

                    if (var20 < var13 || var13 == 0.0D) {
                        b = var16;
                        var10 = var19.hitVec;
                        var13 = var20;
                    }
                }
            }
        }
        return b;
    }
    public static AxisAlignedBB interpolateAxis(final AxisAlignedBB bb) {
        return new AxisAlignedBB(RenderUtil.mc.getRenderManager().viewerPosX - bb.minX, mc.getRenderManager().viewerPosY - bb.minY, mc.getRenderManager().viewerPosZ - bb.minZ, RenderUtil.mc.getRenderManager().viewerPosX - bb.maxX, RenderUtil.mc.getRenderManager().viewerPosY - bb.maxY, RenderUtil.mc.getRenderManager().viewerPosZ - bb.maxZ);
    }
    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }
}
