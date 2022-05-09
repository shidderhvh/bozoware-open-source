package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.visual.BloomUtil;
import bozoware.base.util.visual.BlurUtil;
import bozoware.base.util.visual.ColorUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.RenderNametagEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import bozoware.visual.font.MinecraftFontRenderer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleData(moduleName = "TargetHUD", moduleCategory = ModuleCategory.VISUAL)
public class TargetHUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;
    @EventListener
    EventConsumer<RenderNametagEvent> onRenderNametagEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final BooleanProperty animBool = new BooleanProperty("Animate", true, this);
    public EnumProperty<targetHUDModes> targetHUDMode = new EnumProperty<>("TargetHUD Mode", targetHUDModes.Bozo, this);
    public final ValueProperty<Integer> xPos = new ValueProperty<>("TargetHUD X", 100, 1, 1920, this);
    public final ValueProperty<Integer> yPos = new ValueProperty<>("TargetHUD Y", 100, 1, 1080, this);

    boolean isTHUDShowing;
    private double healthBarWidth;
    private double hpPercentage, hpWidth, Width;
    public double xPosi;
    public double xPosition;
    private int hp;
    private double armorBarWidth;
    private EntityOtherPlayerMP target;
    private double hudHeight;


    public TargetHUD() {
        onModuleEnabled = () -> {
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            xPosi = sr.getScaledWidth() + 25;
            xPosition = sr.getScaledWidth() + 25;
        };
        onUpdatePositionEvent = (e -> {
            if(Aura.target == null){
                hpPercentage = 0;
                hpWidth = 0;
                ScaledResolution SR = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                xPosi = SR.getScaledWidth() + 25;
                xPosition = SR.getScaledWidth() + 25;
            }
        });
        onRenderNametagEvent = (e -> {
        });
        onRender2DEvent = (e -> {
            MinecraftFontRenderer FR = BozoWare.getInstance().getFontManager().largeFontRenderer;
            MinecraftFontRenderer arrowIcons = BozoWare.getInstance().getFontManager().ArrowIcons;
            MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
            MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
            final ScaledResolution SR = e.getScaledResolution();
            if(Aura.target == null){
                hpPercentage = 0;
                hpWidth = 0;
                xPosi = SR.getScaledWidth() + 25;
            }
            if(mc.thePlayer != null && Aura.target != null && Aura.getInstance().isModuleToggled()){
                switch (targetHUDMode.getPropertyValue()){
                    case Bozo:
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;
//                        RenderUtil.drawRoundedRect(xPos.getPropertyValue() - 5, yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + Aura.target.getName().length(), yPos.getPropertyValue() + 22, 15, 0x40000000);
                        Gui.drawRect(xPosition - 5, yPos.getPropertyValue() - 1, xPosition + 180, yPos.getPropertyValue() - 5 + 49, 0x50000000);
                        hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                        hpWidth = 115.0D * hpPercentage;
                        hp = Math.round(Aura.target.getHealth());
                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                        Gui.drawRect(xPosition + 45, yPos.getPropertyValue() + 27,  xPosition + 55 + healthBarWidth, yPos.getPropertyValue() + 37, getHealthColor(Aura.target.getHealth(), Aura.target.getMaxHealth()).getRGB());
                        mc.fontRendererObj.drawStringWithShadow(Math.round(hpPercentage * 100) + "%", (float) (xPosition + 95), yPos.getPropertyValue() + 28, -1);
                        mc.fontRendererObj.drawStringWithShadow(Aura.target.getName(), (float) (xPosition + 45), yPos.getPropertyValue() + 5, -1);
                        mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 150), yPos.getPropertyValue() + 5, 0xFFFF0000);
                        mc.fontRendererObj.drawStringWithShadow(String.valueOf((int) hp), (float) (xPosition + 160), yPos.getPropertyValue() + 5, -1);
                        NetworkPlayerInfo playerInf2 = mc.getNetHandler().getPlayerInfo(Aura.target.getUniqueID());
                        if (playerInf2 != null)
                        {
                            mc.getTextureManager().bindTexture(playerInf2.getLocationSkin());
                            GL11.glColor4f(1F, 1F, 1F, 1F);

                            Gui.drawScaledCustomSizeModalRect((int) xPosition, (int) yPos.getPropertyValue() + 5, 8F, 8F, 8, 8, 35, 35, 64F, 64F);
                        }
                        break;
                    case Rise:
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;
                        RenderUtil.drawSmoothRoundedRect((float) (xPosition - 5), yPos.getPropertyValue() - 1, (float) (xPosition) + 135 + Aura.target.getName().length(), yPos.getPropertyValue() + 44, 15, 0x40000000);
                        NetworkPlayerInfo playerInf = mc.getNetHandler().getPlayerInfo(Aura.target.getUniqueID());
                        if (playerInf != null)
                        {
                            mc.getTextureManager().bindTexture(playerInf.getLocationSkin());
                            GL11.glColor4f(1F, 1F, 1F, 1F);

                            Gui.drawScaledCustomSizeModalRect((int) xPosition, (int) yPos.getPropertyValue() + 2, 8F, 8F, 8, 8, 30, 30, 64F, 64F);
                        }
                        MFR.drawStringWithShadow("Name " + Aura.target.getName(), (int) xPosition + 35, yPos.getPropertyValue() + 8, -1);
                        DecimalFormat df = new DecimalFormat("0.0");
                        String distance = df.format(Aura.target.getDistanceToEntity(mc.thePlayer));
                        SFR.drawStringWithShadow("Distance " + String.valueOf(distance), (int) xPosition + 35, yPos.getPropertyValue() + 22, -1);
                        SFR.drawStringWithShadow("Hurt " + Aura.target.hurtTime, (int) xPosition + 87, yPos.getPropertyValue() + 22, -1);
                        hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                        hpWidth = 115.0D * hpPercentage;
                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                        Gui.drawRect((int) xPosition, yPos.getPropertyValue() + 34,  (int) xPosition + 15 + healthBarWidth, yPos.getPropertyValue() + 39, HUD.getInstance().bozoColor);
                        DecimalFormat df1 = new DecimalFormat("00.0");
                        String healthFormatted;
                        if(Aura.target.getHealth() > 9.9999999999999999999F) {
                            healthFormatted = df1.format(Aura.target.getHealth());
                        }
                        else {
                            healthFormatted = df.format(Aura.target.getHealth());
                        }
                        if(Aura.target.getHealth() < Aura.target.getMaxHealth()){
                            SFR.drawStringWithShadow(healthFormatted, (int) xPosition + 12 + healthBarWidth + 5, yPos.getPropertyValue() + 34, -1);
                        }
                        isTHUDShowing = true;
                        break;
                    case Novoline:
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;
                        final Aura ka = (Aura) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class);
                        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                        if (Aura.target != null && ka.isModuleToggled()) {
                            float startX = 20;
                            float renderX = ((int) xPosition) + startX;
                            float renderY = (yPos.getPropertyValue()) + 10;
                            int maxX2 = 30;
                            if (ka.target.getCurrentArmor(3) != null) {
                                maxX2 += 15;
                            }
                            if (ka.target.getCurrentArmor(2) != null) {
                                maxX2 += 15;
                            }
                            if (ka.target.getCurrentArmor(1) != null) {
                                maxX2 += 15;
                            }
                            if (ka.target.getCurrentArmor(0) != null) {
                                maxX2 += 15;
                            }
                            if (ka.target.getHeldItem() != null) {
                                maxX2 += 15;
                            }
                            final int healthColor = getHealthColor(Aura.target.getHealth(), Aura.target.getMaxHealth())
                                    .getRGB();
                            float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(Aura.target.getName()) + 30);
                            hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                            hpWidth = maxX * hpPercentage;
                            hp = Math.round(Aura.target.getHealth());
                            healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                            Gui.drawRect(renderX, renderY, renderX + maxX, renderY + 40, new Color(0, 0, 0, 0.3f).getRGB());
                            Gui.drawRect(renderX, renderY + 38, renderX + (healthBarWidth), renderY + 40, healthColor);
                            mc.fontRendererObj.drawStringWithShadow(Aura.target.getName(), renderX + 25, renderY + 7, -1);
                            int xAdd = 0;
                            double multiplier = 0.85;
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(multiplier, multiplier, multiplier);
                            if (Aura.target.getCurrentArmor(3) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(3), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(2) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(2), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(1) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(1), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getCurrentArmor(0) != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getCurrentArmor(0), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
                                xAdd += 15;
                            }
                            if (Aura.target.getHeldItem() != null) {
                                mc.getRenderItem().renderItemAndEffectIntoGUI(Aura.target.getHeldItem(), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue() + 28) / multiplier)));
                            }
                            GlStateManager.popMatrix();
                            GuiInventory.drawEntityOnScreen((int) renderX + 12, (int) renderY + 33, 15, Aura.target.rotationYaw, Aura.target.rotationPitch, Aura.target);
                        }
                        break;
                    case Skeet:
//                        RenderUtil.drawSmoothRoundedRect(2, 33, 100, 78, 15, 0xff171717);
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                        xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;
                        if(Aura.target.getName().length() <= 4){
                            Width =  -(healthBarWidth);
//                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(Aura.target.getName()))) * 2) + healthBarWidth;
                        } else {
                            Width = -(healthBarWidth);
//                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(Aura.target.getName())))) + healthBarWidth + 4;
                        }

                        Gui.drawRect(xPosition, yPos.getPropertyValue(), xPosition + 147, yPos.getPropertyValue() + 35, 0xFF000000);
                        RenderUtil.drawRoundedOutline((float) xPosition, yPos.getPropertyValue(), (float) xPosition + 147, yPos.getPropertyValue() + 35, 0xff000000, 3, 2);
//                        RenderUtil.drawRoundedOutline(xPos.getPropertyValue(), yPos.getPropertyValue(), (float) xPos.getPropertyValue() + 147, yPos.getPropertyValue() + 35, 0xff3c3c3c, 2, 2);
                        BloomUtil.drawAndBloom(() -> RenderUtil.drawRoundedOutline((float) xPosition, yPos.getPropertyValue(), (float) xPosition + 147, yPos.getPropertyValue() + 35, 0xff282828, 0.8F, 2));
                        RenderUtil.glHorizontalGradientQuad(xPosition + 2, yPos.getPropertyValue(), 145, 2, 0xff37b1da, 0xffc862b5);
                        RenderUtil.glHorizontalGradientQuad(xPosition + 2, yPos.getPropertyValue(), 145, 2, 0xffc862b5, 0xffcce236);
                        hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                        hpWidth = 115.0D * hpPercentage;
                        hp = (int) Math.floor(Aura.target.getHealth());
                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                        Color bozoColor = new Color(HUD.getInstance().bozoColor, true);
                        Color bozoColor2 = new Color(HUD.getInstance().bozoColor2, true);
                        Color hpColor;
                        int healthColor = getHealthColor(Aura.target.getHealth(), Aura.target.getMaxHealth()).getRGB();

                        Gui.drawGradientRect(xPosition + 12.5, yPos.getPropertyValue() + 22,  xPosition + 18 + healthBarWidth, yPos.getPropertyValue() + 33, healthColor, 1);
//                RenderUtil.glHorizontalGradientQuad(xPos.getPropertyValue() + 2, yPos.getPropertyValue() + 17, 147, 2, 0xffc862b5, 0xffcce236);
                        mc.fontRendererObj.drawStringWithShadow(Aura.target.getName(), (float) (xPosition + 12), yPos.getPropertyValue() + 5, -1);
                        mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 110),  yPos.getPropertyValue() + 5, 0xFFFF0000);
                        mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED + " " + ChatFormatting.WHITE + String.valueOf(hp), (float) (xPosition + 116),  yPos.getPropertyValue() + 5, -1);

                        break;
                    case Astolfo:
                        sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                        hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                        hpWidth = 125.0D * hpPercentage;
                        hp = (int) Math.round(Aura.target.getHealth() / 2);
                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;

                        Gui.drawRect(xPosition + 5, yPos.getPropertyValue(), xPosition + 175, yPos.getPropertyValue() + 50, 0x50000000);
                        GuiInventory.drawEntityOnScreen((int) (xPosition + 25), yPos.getPropertyValue() + 50, 25, 900, 0, Aura.target);
                        mc.fontRendererObj.drawStringWithShadow(Aura.target.getName(), (float) (xPosition + 45), yPos.getPropertyValue() + 5, -1);
                        GL11.glPushMatrix();
                        GL11.glScaled((float)(2), (float)2, (float)2);
                        mc.fontRendererObj.drawStringWithShadow( hp + " ❤", (float) (xPosition - (88 + 45*2)), yPos.getPropertyValue() - 108, HUD.getInstance().bozoColor);
                        GL11.glPopMatrix();
                        Gui.drawRectWithWidth(xPosition + 45, yPos.getPropertyValue() + 40, 125, 7, new Color(HUD.getInstance().bozoColor).darker().darker().darker().darker().darker().getRGB());
                        Gui.drawRectWithWidth(xPosition + 45, yPos.getPropertyValue() + 40,  healthBarWidth, 7, HUD.getInstance().bozoColor);
                        break;
                    case Crazy:
                        hpPercentage = (Aura.target.getHealth()) / (Aura.target.getMaxHealth());
                        hpWidth = 125.0D * hpPercentage;
                        hp = (int) Math.round(Aura.target.getHealth());
                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                        xPosi = xPos.getPropertyValue();
                        if(animBool.getPropertyValue())
                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
                        else
                            xPosition = xPosi;
//                        if(xPosition == xPosi)
//                            xPosition = xPos.getPropertyValue();
//                        RenderUtil.drawSmoothRoundedRect(xPos.getPropertyValue() - 5, yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + Aura.target.getName().length(), yPos.getPropertyValue() + 44, 15, 0x40000000);

                        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(Aura.target.getUniqueID());
                        BlurUtil.blurArea(xPosition - (playerInfo == null ? -23 : 10), yPos.getPropertyValue() + 10, 170 - xPosition - (playerInfo == null ? 10 : 0), 40);
                        BloomUtil.bloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -23 : 10), yPos.getPropertyValue() + 10, xPosition + 170, yPos.getPropertyValue() + 49.5, HUD.getInstance().bozoColor));
                        BloomUtil.drawAndBloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -23 : 10), yPos.getPropertyValue() + 10, xPosition + 170, yPos.getPropertyValue() + 50, 0x70000000));
                        if (playerInfo != null)
                        {
                            mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
                            GL11.glColor4f(1F, 1F, 1F, 1F);

                            BloomUtil.drawAndBloom(() -> Gui.drawScaledCustomSizeModalRect((int) (xPosition - 5), (int) yPos.getPropertyValue() + 15, 8F, 8F, 8, 8, 30, 30, 64F, 64F));
                        }
                        //                        Gui.drawRect(xPos.getPropertyValue() + 35, yPos.getPropertyValue() + 40, xPos.getPropertyValue() + healthBarWidth + getDamage(mc.thePlayer.getHeldItem()) / 3 - 5, yPos.getPropertyValue() + 47, HUD.getInstance().getColor2());
//                        mc.fontRendererObj.drawStringWithShadow(Aura.target.getName(), xPos.getPropertyValue() + 34, yPos.getPropertyValue() + 16, -1);
//                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(hp / 2) + "❤", xPos.getPropertyValue() + 145, yPos.getPropertyValue() + 16, -1);
//                        mc.fontRendererObj.drawStringWithShadow("Hurt Time: " + Aura.target.hurtTime, xPos.getPropertyValue() + 34, yPos.getPropertyValue() + 26, -1);
//                        mc.fontRendererObj.drawStringWithShadow("Distance: " + Math.round(Aura.target.getDistanceToEntity(mc.thePlayer)), xPos.getPropertyValue() + 120 - mc.fontRendererObj.getStringWidth(String.valueOf(Math.round(Aura.target.getDistanceToEntity(mc.thePlayer)))), yPos.getPropertyValue() + 26, -1);
                                                mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 157), yPos.getPropertyValue() + 15, -1);
                        MFR.drawStringWithShadow(Aura.target.getName(), xPosition + 34, yPos.getPropertyValue() + 16, -1);
                        MFR.drawStringWithShadow(String.valueOf(hp / 2), xPosition + 155 - MFR.getStringWidth(String.valueOf(hp / 2)), yPos.getPropertyValue() + 16, -1);
                        MFR.drawStringWithShadow(ChatFormatting.GRAY + "Hurt Time: " + Aura.target.hurtTime, xPosition + 34, yPos.getPropertyValue() + 28, -1);
                        MFR.drawStringWithShadow(ChatFormatting.GRAY + "Distance: " + Math.round(Aura.target.getDistanceToEntity(mc.thePlayer)), xPosition + 120 - MFR.getStringWidth(String.valueOf(Math.round(Aura.target.getDistanceToEntity(mc.thePlayer)))), yPos.getPropertyValue() + 28, -1);

//                        GuiInventory.drawEntityOnScreen((int) (xPosition + 15), yPos.getPropertyValue() + 48, 17, -180, Aura.target.rotationPitch, Aura.target);
                        RenderUtil.glHorizontalGradientQuad(xPosition + 35, yPos.getPropertyValue() + 40, 125, 7, HUD.getInstance().bozoColorDarker, HUD.getInstance().bozoColorDarker);
                        BloomUtil.drawAndBloom(() -> RenderUtil.glHorizontalGradientQuad(xPosition + 35, yPos.getPropertyValue() + 40, healthBarWidth, 7, HUD.getInstance().bozoColor, HUD.getInstance().bozoColor));
                        break;
                }
            } else {
                hpPercentage = 0;
                hpWidth = 0;
                xPosi = SR.getScaledWidth() + 25;
            }
//            if(mc.currentScreen instanceof GuiChat) {
//                switch (targetHUDMode.getPropertyValue()) {
//                    case Bozo:
//                        xPosi = xPos.getPropertyValue();
//                        if (animBool.getPropertyValue())
//                            xPosition = xPosi;
//                        else
//                            xPosition = xPosi;
////                        RenderUtil.drawRoundedRect(xPos.getPropertyValue() - 5, yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + mc.thePlayer.getName().length(), yPos.getPropertyValue() + 22, 15, 0x40000000);
//                        BlurUtil.blurArea(xPosition - 5, yPos.getPropertyValue() - 1, 135 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()), 22);
//                        hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
//                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
//                        hpWidth = 115.0D * hpPercentage;
//                        hp = Math.round(mc.thePlayer.getHealth());
//                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
//                        Gui.drawRect(xPosition, yPos.getPropertyValue() + 17, xPosition + 15 + healthBarWidth, yPos.getPropertyValue() + 19, ColorUtil.interpolateColorsDynamic(3, SR.getScaledWidth() * 15, new Color(0xFFFF0000), new Color(0xFF500000)).getRGB());
//                        mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), (float) (xPosition + 12), yPos.getPropertyValue() + 5, -1);
//                        mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 105 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) - 3), yPos.getPropertyValue() + 5, 0xFFFF0000);
//                        mc.fontRendererObj.drawStringWithShadow(String.valueOf((int) hp), (float) (xPosition + 115 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) - 3), yPos.getPropertyValue() + 5, -1);
//
//                        break;
//                    case Rise:
//                        xPosi = xPos.getPropertyValue();
//                        if (animBool.getPropertyValue())
//                            xPosition = xPosi;
//                        else
//                            xPosition = xPosi;
//                        RenderUtil.drawSmoothRoundedRect((float) (xPosition - 5), yPos.getPropertyValue() - 1, (float) (xPosition) + 135 + mc.thePlayer.getName().length(), yPos.getPropertyValue() + 44, 15, 0x40000000);
//                        NetworkPlayerInfo playerInf = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
//                        if (playerInf != null) {
//                            mc.getTextureManager().bindTexture(playerInf.getLocationSkin());
//                            GL11.glColor4f(1F, 1F, 1F, 1F);
//
//                            Gui.drawScaledCustomSizeModalRect((int) xPosition, (int) yPos.getPropertyValue() + 2, 8F, 8F, 8, 8, 30, 30, 64F, 64F);
//                        }
//                        MFR.drawStringWithShadow("Name " + mc.thePlayer.getName(), (int) xPosition + 35, yPos.getPropertyValue() + 8, -1);
//                        DecimalFormat df = new DecimalFormat("0.0");
//                        String distance = df.format(mc.thePlayer.getDistanceToEntity(mc.thePlayer));
//                        SFR.drawStringWithShadow("Distance " + String.valueOf(distance), (int) xPosition + 35, yPos.getPropertyValue() + 22, -1);
//                        SFR.drawStringWithShadow("Hurt " + mc.thePlayer.hurtTime, (int) xPosition + 87, yPos.getPropertyValue() + 22, -1);
//                        hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
//                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
//                        hpWidth = 115.0D * hpPercentage;
//                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
//                        Gui.drawRect((int) xPosition, yPos.getPropertyValue() + 34, (int) xPosition + 15 + healthBarWidth, yPos.getPropertyValue() + 39, HUD.getInstance().bozoColor);
//                        DecimalFormat df1 = new DecimalFormat("00.0");
//                        String healthFormatted;
//                        if (mc.thePlayer.getHealth() > 9.9999999999999999999F) {
//                            healthFormatted = df1.format(mc.thePlayer.getHealth());
//                        } else {
//                            healthFormatted = df.format(mc.thePlayer.getHealth());
//                        }
//                        if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
//                            SFR.drawStringWithShadow(healthFormatted, (int) xPosition + 12 + healthBarWidth + 5, yPos.getPropertyValue() + 34, -1);
//                        }
//                        isTHUDShowing = true;
//                        break;
//                    case Novoline:
//                        xPosi = xPos.getPropertyValue();
//                        if (animBool.getPropertyValue())
//                            xPosition = xPosi;
//                        else
//                            xPosition = xPosi;
//                        final Aura ka = (Aura) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class);
//                        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
//                        if (mc.thePlayer != null && ka.isModuleToggled()) {
//                            float startX = 20;
//                            float renderX = ((int) xPosition) + startX;
//                            float renderY = (yPos.getPropertyValue()) + 10;
//                            int maxX2 = 30;
//                            if (ka.target.getCurrentArmor(3) != null) {
//                                maxX2 += 15;
//                            }
//                            if (ka.target.getCurrentArmor(2) != null) {
//                                maxX2 += 15;
//                            }
//                            if (ka.target.getCurrentArmor(1) != null) {
//                                maxX2 += 15;
//                            }
//                            if (ka.target.getCurrentArmor(0) != null) {
//                                maxX2 += 15;
//                            }
//                            if (ka.target.getHeldItem() != null) {
//                                maxX2 += 15;
//                            }
//                            final int healthColor = getHealthColor(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth())
//                                    .getRGB();
//                            float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) + 30);
//                            hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
//                            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
//                            hpWidth = maxX * hpPercentage;
//                            hp = Math.round(mc.thePlayer.getHealth());
//                            healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
//                            Gui.drawRect(renderX, renderY, renderX + maxX, renderY + 40, new Color(0, 0, 0, 0.3f).getRGB());
//                            Gui.drawRect(renderX, renderY + 38, renderX + (healthBarWidth), renderY + 40, healthColor);
//                            mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), renderX + 25, renderY + 7, -1);
//                            int xAdd = 0;
//                            double multiplier = 0.85;
//                            GlStateManager.pushMatrix();
//                            GlStateManager.scale(multiplier, multiplier, multiplier);
//                            if (mc.thePlayer.getCurrentArmor(3) != null) {
//                                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(3), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
//                                xAdd += 15;
//                            }
//                            if (mc.thePlayer.getCurrentArmor(2) != null) {
//                                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(2), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
//                                xAdd += 15;
//                            }
//                            if (mc.thePlayer.getCurrentArmor(1) != null) {
//                                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(1), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
//                                xAdd += 15;
//                            }
//                            if (mc.thePlayer.getCurrentArmor(0) != null) {
//                                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(0), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue()) + 28) / multiplier));
//                                xAdd += 15;
//                            }
//                            if (mc.thePlayer.getHeldItem() != null) {
//                                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getHeldItem(), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((yPos.getPropertyValue() + 28) / multiplier)));
//                            }
//                            GlStateManager.popMatrix();
//                            GuiInventory.drawEntityOnScreen((int) renderX + 12, (int) renderY + 33, 15, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer);
//                        }
//                        break;
//                    case Skeet:
////                        RenderUtil.drawSmoothRoundedRect(2, 33, 100, 78, 15, 0xff171717);
//                        xPosi = xPos.getPropertyValue();
//                        if (animBool.getPropertyValue())
//                            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.075D);
//                        else
//                            xPosition = xPosi;
//                        if (mc.thePlayer.getName().length() <= 4) {
//                            Width = -(healthBarWidth);
////                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()))) * 2) + healthBarWidth;
//                        } else {
//                            Width = -(healthBarWidth);
////                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(mc.thePlayer.getName())))) + healthBarWidth + 4;
//                        }
//
//                        Gui.drawRect(xPosition, yPos.getPropertyValue(), xPosition + 147, yPos.getPropertyValue() + 35, 0xFF000000);
//                        RenderUtil.drawRoundedOutline((float) xPosition, yPos.getPropertyValue(), (float) xPosition + 147, yPos.getPropertyValue() + 35, 0xff000000, 3, 2);
////                        RenderUtil.drawRoundedOutline(xPos.getPropertyValue(), yPos.getPropertyValue(), (float) xPos.getPropertyValue() + 147, yPos.getPropertyValue() + 35, 0xff3c3c3c, 2, 2);
//                        BloomUtil.drawAndBloom(() -> RenderUtil.drawRoundedOutline((float) xPosition, yPos.getPropertyValue(), (float) xPosition + 147, yPos.getPropertyValue() + 35, 0xff282828, 0.8F, 2));
//                        RenderUtil.glHorizontalGradientQuad(xPosition + 2, yPos.getPropertyValue(), 145, 2, 0xff37b1da, 0xffc862b5);
//                        RenderUtil.glHorizontalGradientQuad(xPosition + 2, yPos.getPropertyValue(), 145, 2, 0xffc862b5, 0xffcce236);
//                        hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
//                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
//                        hpWidth = 115.0D * hpPercentage;
//                        hp = (int) Math.floor(mc.thePlayer.getHealth());
//                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
//                        Color bozoColor = new Color(HUD.getInstance().bozoColor, true);
//                        Color bozoColor2 = new Color(HUD.getInstance().bozoColor2, true);
//                        Color hpColor;
//                        int healthColor = getHealthColor(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth()).getRGB();
//
//                        Gui.drawGradientRect(xPosition + 12.5, yPos.getPropertyValue() + 22, xPosition + 18 + healthBarWidth, yPos.getPropertyValue() + 33, healthColor, 1);
////                RenderUtil.glHorizontalGradientQuad(xPos.getPropertyValue() + 2, yPos.getPropertyValue() + 17, 147, 2, 0xffc862b5, 0xffcce236);
//                        mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), (float) (xPosition + 12), yPos.getPropertyValue() + 5, -1);
//                        mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 110), yPos.getPropertyValue() + 5, 0xFFFF0000);
//                        mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED + " " + ChatFormatting.WHITE + String.valueOf(hp), (float) (xPosition + 116), yPos.getPropertyValue() + 5, -1);
//
//                        break;
//                    case Crazy:
//                        hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
//                        hpWidth = 125.0D * hpPercentage;
//                        hp = (int) Math.round(mc.thePlayer.getHealth());
//                        healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
//                        xPosi = xPos.getPropertyValue();
//                        if (animBool.getPropertyValue())
//                            xPosition = xPosi;
//                        else
//                            xPosition = xPosi;
////                        if(xPosition == xPosi)
////                            xPosition = xPos.getPropertyValue();
////                        RenderUtil.drawSmoothRoundedRect(xPos.getPropertyValue() - 5, yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + mc.thePlayer.getName().length(), yPos.getPropertyValue() + 44, 15, 0x40000000);
//
//                        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
//                        BlurUtil.blurArea(xPosition - (playerInfo == null ? -20 : 10), yPos.getPropertyValue() + 10, 170 - xPosition - (playerInfo == null ? 10 : 0), 40);
//                        BloomUtil.bloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -20 : 10), yPos.getPropertyValue() + 10, xPosition + 170, yPos.getPropertyValue() + 49.5, HUD.getInstance().bozoColor));
//                        BloomUtil.drawAndBloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -20 : 10), yPos.getPropertyValue() + 10, xPosition + 170, yPos.getPropertyValue() + 50, 0x70000000));
//                        if (playerInfo != null) {
//                            mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
//                            GL11.glColor4f(1F, 1F, 1F, 1F);
//
//                            BloomUtil.drawAndBloom(() -> Gui.drawScaledCustomSizeModalRect((int) (xPosition - 5), (int) yPos.getPropertyValue() + 15, 8F, 8F, 8, 8, 30, 30, 64F, 64F));
//                        }
//                        //                        Gui.drawRect(xPos.getPropertyValue() + 35, yPos.getPropertyValue() + 40, xPos.getPropertyValue() + healthBarWidth + getDamage(mc.thePlayer.getHeldItem()) / 3 - 5, yPos.getPropertyValue() + 47, HUD.getInstance().getColor2());
////                        mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), xPos.getPropertyValue() + 34, yPos.getPropertyValue() + 16, -1);
////                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(hp / 2) + "❤", xPos.getPropertyValue() + 145, yPos.getPropertyValue() + 16, -1);
////                        mc.fontRendererObj.drawStringWithShadow("Hurt Time: " + mc.thePlayer.hurtTime, xPos.getPropertyValue() + 34, yPos.getPropertyValue() + 26, -1);
////                        mc.fontRendererObj.drawStringWithShadow("Distance: " + Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)), xPos.getPropertyValue() + 120 - mc.fontRendererObj.getStringWidth(String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)))), yPos.getPropertyValue() + 26, -1);
//                        mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 157), yPos.getPropertyValue() + 15, -1);
//                        MFR.drawStringWithShadow(mc.thePlayer.getName(), xPosition + 34, yPos.getPropertyValue() + 16, -1);
//                        MFR.drawStringWithShadow(String.valueOf(hp / 2), xPosition + 155 - MFR.getStringWidth(String.valueOf(hp / 2)), yPos.getPropertyValue() + 16, -1);
//                        MFR.drawStringWithShadow("Hurt Time: " + mc.thePlayer.hurtTime, xPosition + 34, yPos.getPropertyValue() + 26, -1);
//                        MFR.drawStringWithShadow("Distance: " + Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)), xPosition + 120 - MFR.getStringWidth(String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)))), yPos.getPropertyValue() + 26, -1);
//
////                        GuiInventory.drawEntityOnScreen((int) (xPosition + 15), yPos.getPropertyValue() + 48, 17, -180, mc.thePlayer.rotationPitch, mc.thePlayer);
//                        RenderUtil.glHorizontalGradientQuad(xPosition + 35, yPos.getPropertyValue() + 40, 125, 7, HUD.getInstance().bozoColorDarker, HUD.getInstance().bozoColorDarker);
//                        BloomUtil.drawAndBloom(() -> RenderUtil.glHorizontalGradientQuad(xPosition + 35, yPos.getPropertyValue() + 40, healthBarWidth, 7, HUD.getInstance().bozoColor, HUD.getInstance().bozoColor2));
//                        break;
//                }
//            }
        });
    }
    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
        Color[] colors = new Color[]{new Color(108, 30, 0), new Color(255, 51, 0), Color.GREEN};
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }
    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
            return color;
        } else {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
    }
    public static int[] getFractionIndices(float[] fractions, float progress) {
        int[] range = new int[2];

        int startPoint;
        for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0F - r;
        float[] rgb1 = color1.getColorComponents(new float[3]);
        float[] rgb2 = color2.getColorComponents(new float[3]);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0F) {
            red = 0.0F;
        } else if (red > 255.0F) {
            red = 255.0F;
        }
        if (green < 0.0F) {
            green = 0.0F;
        } else if (green > 255.0F) {
            green = 255.0F;
        }

        if (blue < 0.0F) {
            blue = 0.0F;
        } else if (blue > 255.0F) {
            blue = 255.0F;
        }

        Color color3 = null;

        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException var13) {
        }

        return color3;
    }
    private double getDamage (ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (stack != null) {
            if (item instanceof ItemTool) {
                ItemTool tool = (ItemTool) item;
                damage += tool.getDamage();
            }
            if (item instanceof ItemSword) {
                ItemSword sword = (ItemSword) item;
                damage += sword.getAttackDamage();
            }
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
                    + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
            return damage;
        }
        return damage;
    }
    public static TargetHUD getInstance() {
        return (TargetHUD) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(TargetHUD.class);
    }
    public enum targetHUDModes {
        Bozo,
        Rise,
        Novoline,
        Skeet,
        Astolfo,
        Crazy
    }
}
