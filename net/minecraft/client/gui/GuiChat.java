package net.minecraft.client.gui;

import bozoware.base.BozoWare;
import bozoware.base.util.visual.BloomUtil;
import bozoware.base.util.visual.BlurUtil;
import bozoware.base.util.visual.ColorUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.module.visual.TargetHUD;
import bozoware.visual.font.MinecraftFontRenderer;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import static bozoware.impl.module.visual.TargetHUD.getHealthColor;

public class GuiChat extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";
    private int x, y, distX, distY;
    private boolean dragging;
    boolean isTHUDShowing;
    private double healthBarWidth;
    private double hpPercentage, hpWidth, Width;
    public double xPosi;
    public double xPosition;
    private int hp;
    private double armorBarWidth;
    private EntityOtherPlayerMP target;
    private double hudHeight;
    MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
    MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = Lists.<String>newArrayList();

    /** Chat entry field */
    protected GuiTextField inputField;

    /**
     * is the text that appears when you press the chat key and the input box appears pre-filled
     */
    private String defaultInputFieldText = "";

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }
    public void drawScreen(int mouseX, int mouseY, int partialTicks) {
        if(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(TargetHUD.class).isModuleToggled()){
             if(TargetHUD.getInstance().targetHUDMode.equals(TargetHUD.targetHUDModes.Bozo)) {
                 xPosi = TargetHUD.getInstance().xPos.getPropertyValue();
                 xPosition = xPosi;
//                        RenderUtil.drawRoundedRect(xPos.getPropertyValue() - 5, TargetHUD.getInstance().yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + mc.thePlayer.getName().length(), TargetHUD.getInstance().yPos.getPropertyValue() + 22, 15, 0x40000000);
                 BlurUtil.blurArea(xPosition - 5, TargetHUD.getInstance().yPos.getPropertyValue() - 1, 135 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()), 22);
                 hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
                 hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                 hpWidth = 115.0D * hpPercentage;
                 hp = Math.round(mc.thePlayer.getHealth());
                 healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                 ScaledResolution SR = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                 Gui.drawRect(xPosition, TargetHUD.getInstance().yPos.getPropertyValue() + 17, xPosition + 15 + healthBarWidth, TargetHUD.getInstance().yPos.getPropertyValue() + 19, ColorUtil.interpolateColorsDynamic(3, SR.getScaledWidth() * 15, new Color(0xFFFF0000), new Color(0xFF500000)).getRGB());
                 mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), (float) (xPosition + 12), TargetHUD.getInstance().yPos.getPropertyValue() + 5, -1);
                 mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 105 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) - 3), TargetHUD.getInstance().yPos.getPropertyValue() + 5, 0xFFFF0000);
                 mc.fontRendererObj.drawStringWithShadow(String.valueOf((int) hp), (float) (xPosition + 115 + mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) - 3), TargetHUD.getInstance().yPos.getPropertyValue() + 5, -1);
             }
            if(TargetHUD.getInstance().targetHUDMode.equals(TargetHUD.targetHUDModes.Rise)) {
                xPosi = TargetHUD.getInstance().xPos.getPropertyValue();
                xPosition = xPosi;
                RenderUtil.drawSmoothRoundedRect((float) (xPosition - 5), TargetHUD.getInstance().yPos.getPropertyValue() - 1, (float) (xPosition) + 135 + mc.thePlayer.getName().length(), TargetHUD.getInstance().yPos.getPropertyValue() + 44, 15, 0x40000000);
                NetworkPlayerInfo playerInf = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
                if (playerInf != null) {
                    mc.getTextureManager().bindTexture(playerInf.getLocationSkin());
                    GL11.glColor4f(1F, 1F, 1F, 1F);

                    Gui.drawScaledCustomSizeModalRect((int) xPosition, (int) TargetHUD.getInstance().yPos.getPropertyValue() + 2, 8F, 8F, 8, 8, 30, 30, 64F, 64F);
                }
                MFR.drawStringWithShadow("Name " + mc.thePlayer.getName(), (int) xPosition + 35, TargetHUD.getInstance().yPos.getPropertyValue() + 8, -1);
                DecimalFormat df = new DecimalFormat("0.0");
                String distance = df.format(mc.thePlayer.getDistanceToEntity(mc.thePlayer));
                SFR.drawStringWithShadow("Distance " + String.valueOf(distance), (int) xPosition + 35, TargetHUD.getInstance().yPos.getPropertyValue() + 22, -1);
                SFR.drawStringWithShadow("Hurt " + mc.thePlayer.hurtTime, (int) xPosition + 87, TargetHUD.getInstance().yPos.getPropertyValue() + 22, -1);
                hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
                hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                hpWidth = 115.0D * hpPercentage;
                healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                Gui.drawRect((int) xPosition, TargetHUD.getInstance().yPos.getPropertyValue() + 34, (int) xPosition + 15 + healthBarWidth, TargetHUD.getInstance().yPos.getPropertyValue() + 39, HUD.getInstance().bozoColor);
                DecimalFormat df1 = new DecimalFormat("00.0");
                String healthFormatted;
                if (mc.thePlayer.getHealth() > 9.9999999999999999999F) {
                    healthFormatted = df1.format(mc.thePlayer.getHealth());
                } else {
                    healthFormatted = df.format(mc.thePlayer.getHealth());
                }
                if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                    SFR.drawStringWithShadow(healthFormatted, (int) xPosition + 12 + healthBarWidth + 5, TargetHUD.getInstance().yPos.getPropertyValue() + 34, -1);
                }
                isTHUDShowing = true;
            }
            if(TargetHUD.getInstance().targetHUDMode.equals(TargetHUD.targetHUDModes.Novoline)) {
                xPosi = TargetHUD.getInstance().xPos.getPropertyValue();
                xPosition = xPosi;
                final Aura ka = (Aura) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class);
                ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                if (mc.thePlayer != null && ka.isModuleToggled()) {
                    float startX = 20;
                    float renderX = ((int) xPosition) + startX;
                    float renderY = (TargetHUD.getInstance().yPos.getPropertyValue()) + 10;
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
                    final int healthColor = getHealthColor(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth())
                            .getRGB();
                    float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()) + 30);
                    hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
                    hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                    hpWidth = maxX * hpPercentage;
                    hp = Math.round(mc.thePlayer.getHealth());
                    healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                    Gui.drawRect(renderX, renderY, renderX + maxX, renderY + 40, new Color(0, 0, 0, 0.3f).getRGB());
                    Gui.drawRect(renderX, renderY + 38, renderX + (healthBarWidth), renderY + 40, healthColor);
                    mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), renderX + 25, renderY + 7, -1);
                    int xAdd = 0;
                    double multiplier = 0.85;
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(multiplier, multiplier, multiplier);
                    if (mc.thePlayer.getCurrentArmor(3) != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(3), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((TargetHUD.getInstance().yPos.getPropertyValue()) + 28) / multiplier));
                        xAdd += 15;
                    }
                    if (mc.thePlayer.getCurrentArmor(2) != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(2), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((TargetHUD.getInstance().yPos.getPropertyValue()) + 28) / multiplier));
                        xAdd += 15;
                    }
                    if (mc.thePlayer.getCurrentArmor(1) != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(1), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((TargetHUD.getInstance().yPos.getPropertyValue()) + 28) / multiplier));
                        xAdd += 15;
                    }
                    if (mc.thePlayer.getCurrentArmor(0) != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getCurrentArmor(0), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((TargetHUD.getInstance().yPos.getPropertyValue()) + 28) / multiplier));
                        xAdd += 15;
                    }
                    if (mc.thePlayer.getHeldItem() != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.thePlayer.getHeldItem(), (int) ((((xPosition) + startX + 23) + xAdd) / multiplier), (int) (((TargetHUD.getInstance().yPos.getPropertyValue() + 28) / multiplier)));
                    }
                    GlStateManager.popMatrix();
                    GuiInventory.drawEntityOnScreen((int) renderX + 12, (int) renderY + 33, 15, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer);
                }
            }
            if(TargetHUD.getInstance().targetHUDMode.equals(TargetHUD.targetHUDModes.Skeet)) {
//                        RenderUtil.drawSmoothRoundedRect(2, 33, 100, 78, 15, 0xff171717);
                xPosi = TargetHUD.getInstance().xPos.getPropertyValue();
                xPosition = xPosi;
                if (mc.thePlayer.getName().length() <= 4) {
                    Width = -(healthBarWidth);
//                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(mc.thePlayer.getName()))) * 2) + healthBarWidth;
                } else {
                    Width = -(healthBarWidth);
//                            Width = (xPos.getPropertyValue() + ((mc.fontRendererObj.getStringWidth(mc.thePlayer.getName())))) + healthBarWidth + 4;
                }

                Gui.drawRect(xPosition, TargetHUD.getInstance().yPos.getPropertyValue(), xPosition + 147, TargetHUD.getInstance().yPos.getPropertyValue() + 35, 0xFF000000);
                RenderUtil.drawRoundedOutline((float) xPosition, TargetHUD.getInstance().yPos.getPropertyValue(), (float) xPosition + 147, TargetHUD.getInstance().yPos.getPropertyValue() + 35, 0xff000000, 3, 2);
//                        RenderUtil.drawRoundedOutline(xPos.getPropertyValue(), TargetHUD.getInstance().yPos.getPropertyValue(), (float) xPos.getPropertyValue() + 147, TargetHUD.getInstance().yPos.getPropertyValue() + 35, 0xff3c3c3c, 2, 2);
                BloomUtil.drawAndBloom(() -> RenderUtil.drawRoundedOutline((float) xPosition, TargetHUD.getInstance().yPos.getPropertyValue(), (float) xPosition + 147, TargetHUD.getInstance().yPos.getPropertyValue() + 35, 0xff282828, 0.8F, 2));
                RenderUtil.glHorizontalGradientQuad(xPosition + 2, TargetHUD.getInstance().yPos.getPropertyValue(), 145, 2, 0xff37b1da, 0xffc862b5);
                RenderUtil.glHorizontalGradientQuad(xPosition + 2, TargetHUD.getInstance().yPos.getPropertyValue(), 145, 2, 0xffc862b5, 0xffcce236);
                hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
                hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0D, 1.0D);
                hpWidth = 115.0D * hpPercentage;
                hp = (int) Math.floor(mc.thePlayer.getHealth());
                healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                Color bozoColor = new Color(HUD.getInstance().bozoColor, true);
                Color bozoColor2 = new Color(HUD.getInstance().bozoColor2, true);
                Color hpColor;
                int healthColor = getHealthColor(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth()).getRGB();

                Gui.drawGradientRect(xPosition + 12.5, TargetHUD.getInstance().yPos.getPropertyValue() + 22, xPosition + 18 + healthBarWidth, TargetHUD.getInstance().yPos.getPropertyValue() + 33, healthColor, 1);
//                RenderUtil.glHorizontalGradientQuad(xPos.getPropertyValue() + 2, TargetHUD.getInstance().yPos.getPropertyValue() + 17, 147, 2, 0xffc862b5, 0xffcce236);
                mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), (float) (xPosition + 12), TargetHUD.getInstance().yPos.getPropertyValue() + 5, -1);
                mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 110), TargetHUD.getInstance().yPos.getPropertyValue() + 5, 0xFFFF0000);
                mc.fontRendererObj.drawStringWithShadow(ChatFormatting.RED + " " + ChatFormatting.WHITE + String.valueOf(hp), (float) (xPosition + 116), TargetHUD.getInstance().yPos.getPropertyValue() + 5, -1);
            }
            if(TargetHUD.getInstance().targetHUDMode.equals(TargetHUD.targetHUDModes.Crazy)) {
                hpPercentage = (mc.thePlayer.getHealth()) / (mc.thePlayer.getMaxHealth());
                hpWidth = 125.0D * hpPercentage;
                hp = (int) Math.round(mc.thePlayer.getHealth());
                healthBarWidth = RenderUtil.animate(hpWidth, this.healthBarWidth, 0.05D);
                xPosi = TargetHUD.getInstance().xPos.getPropertyValue();
                xPosition = xPosi;
//                        if(xPosition == xPosi)
//                            xPosition = xPos.getPropertyValue();
//                        RenderUtil.drawSmoothRoundedRect(xPos.getPropertyValue() - 5, TargetHUD.getInstance().yPos.getPropertyValue() - 1, xPos.getPropertyValue() + 135 + mc.thePlayer.getName().length(), TargetHUD.getInstance().yPos.getPropertyValue() + 44, 15, 0x40000000);

                NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
                BlurUtil.blurArea(xPosition - (playerInfo == null ? -20 : 10), TargetHUD.getInstance().yPos.getPropertyValue() + 10, 170 - xPosition - (playerInfo == null ? 10 : 0), 40);
                BloomUtil.bloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -20 : 10), TargetHUD.getInstance().yPos.getPropertyValue() + 10, xPosition + 170, TargetHUD.getInstance().yPos.getPropertyValue() + 49.5, HUD.getInstance().bozoColor));
                BloomUtil.drawAndBloom(() -> Gui.drawRect(xPosition - (playerInfo == null ? -20 : 10), TargetHUD.getInstance().yPos.getPropertyValue() + 10, xPosition + 170, TargetHUD.getInstance().yPos.getPropertyValue() + 50, 0x70000000));
                if (playerInfo != null) {
                    mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
                    GL11.glColor4f(1F, 1F, 1F, 1F);

                    BloomUtil.drawAndBloom(() -> Gui.drawScaledCustomSizeModalRect((int) (xPosition - 5), (int) TargetHUD.getInstance().yPos.getPropertyValue() + 15, 8F, 8F, 8, 8, 30, 30, 64F, 64F));
                }
                //                        Gui.drawRect(xPos.getPropertyValue() + 35, TargetHUD.getInstance().yPos.getPropertyValue() + 40, xPos.getPropertyValue() + healthBarWidth + getDamage(mc.thePlayer.getHeldItem()) / 3 - 5, TargetHUD.getInstance().yPos.getPropertyValue() + 47, HUD.getInstance().getColor2());
//                        mc.fontRendererObj.drawStringWithShadow(mc.thePlayer.getName(), xPos.getPropertyValue() + 34, TargetHUD.getInstance().yPos.getPropertyValue() + 16, -1);
//                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(hp / 2) + "❤", xPos.getPropertyValue() + 145, TargetHUD.getInstance().yPos.getPropertyValue() + 16, -1);
//                        mc.fontRendererObj.drawStringWithShadow("Hurt Time: " + mc.thePlayer.hurtTime, xPos.getPropertyValue() + 34, TargetHUD.getInstance().yPos.getPropertyValue() + 26, -1);
//                        mc.fontRendererObj.drawStringWithShadow("Distance: " + Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)), xPos.getPropertyValue() + 120 - mc.fontRendererObj.getStringWidth(String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)))), TargetHUD.getInstance().yPos.getPropertyValue() + 26, -1);
                mc.fontRendererObj.drawStringWithShadow("❤", (float) (xPosition + 157), TargetHUD.getInstance().yPos.getPropertyValue() + 15, -1);
                MFR.drawStringWithShadow(mc.thePlayer.getName(), xPosition + 34, TargetHUD.getInstance().yPos.getPropertyValue() + 16, -1);
                MFR.drawStringWithShadow(String.valueOf(hp / 2), xPosition + 155 - MFR.getStringWidth(String.valueOf(hp / 2)), TargetHUD.getInstance().yPos.getPropertyValue() + 16, -1);
                MFR.drawStringWithShadow("Hurt Time: " + mc.thePlayer.hurtTime, xPosition + 34, TargetHUD.getInstance().yPos.getPropertyValue() + 26, -1);
                MFR.drawStringWithShadow("Distance: " + Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)), xPosition + 120 - MFR.getStringWidth(String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(mc.thePlayer)))), TargetHUD.getInstance().yPos.getPropertyValue() + 26, -1);

//                        GuiInventory.drawEntityOnScreen((int) (xPosition + 15), TargetHUD.getInstance().yPos.getPropertyValue() + 48, 17, -180, mc.thePlayer.rotationPitch, mc.thePlayer);
                RenderUtil.glHorizontalGradientQuad(xPosition + 35, TargetHUD.getInstance().yPos.getPropertyValue() + 40, 125, 7, HUD.getInstance().bozoColorDarker, HUD.getInstance().bozoColorDarker);
                BloomUtil.drawAndBloom(() -> RenderUtil.glHorizontalGradientQuad(xPosition + 35, TargetHUD.getInstance().yPos.getPropertyValue() + 40, healthBarWidth, 7, HUD.getInstance().bozoColor, HUD.getInstance().bozoColor2));
            }
        }
        if(dragging){
            x = (int) mouseX - distX;
            y = (int) mouseY - distY;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHoveringFrame(mouseX, mouseY)) {
            if (mouseButton == 0) {
                distX = mouseX - x;
                distY = mouseY - y;
                dragging = true;
            }
        }
    }
    private boolean isHoveringFrame(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= x + 170 && mouseY >= y && mouseY <= y + 250;
    }
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15)
        {
            this.autocompletePlayerNames();
        }
        else
        {
            this.playerNamesFound = false;
        }

        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 200)
            {
                this.getSentHistory(-1);
            }
            else if (keyCode == 208)
            {
                this.getSentHistory(1);
            }
            else if (keyCode == 201)
            {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            }
            else if (keyCode == 209)
            {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            }
            else
            {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (s.length() > 0)
            {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent))
            {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames()
    {
        if (this.playerNamesFound)
        {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames)
            {
                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText((String)this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_)
    {
        if (p_146405_1_.length() >= 1)
        {
            BlockPos blockpos = null;

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                blockpos = this.mc.objectMouseOver.getBlockPos();
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, blockpos));
            this.waitingOnAutocomplete = true;
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null)
        {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onAutocompleteResponse(String[] p_146406_1_)
    {
        if (this.waitingOnAutocomplete)
        {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            for (String s : p_146406_1_)
            {
                if (s.length() > 0)
                {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(p_146406_1_);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
            {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            }
            else if (this.foundPlayerNames.size() > 0)
            {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
