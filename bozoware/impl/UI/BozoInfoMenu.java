package bozoware.impl.UI;

import bozoware.base.BozoWare;
import bozoware.base.security.utils.SecurityUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class BozoInfoMenu extends GuiScreen {

    public static String UserName = BozoWare.BozoUserName;
    public static String UserUID = BozoAuthMenu.UIDText.getText();
    public static String UserCurrentIP;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MinecraftFontRenderer SLFR = BozoWare.getInstance().getFontManager().largeFontRenderer2;
        MinecraftFontRenderer LFR = BozoWare.getInstance().getFontManager().largeFontRenderer;
        MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0xff171717);

        RenderUtil.drawSmoothRoundedRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 15, 0xff1f1f1f);

        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff000000, 3, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff3c3c3c, 2, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff282828, 0.8F, 2);

        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 108, sr.getScaledHeight() / 2 - 98.5, 108, 2, 0xff37b1da, 0xffc862b5);
        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 2, sr.getScaledHeight() / 2 - 98.5, 110.5, 2, 0xffc862b5, 0xffcce236);

        SLFR.drawCenteredStringWithShadow("Name: " + UserName,sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 75F, -1);
        LFR.drawCenteredStringWithShadow("UID: " + UserUID, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 50F, -1);
        LFR.drawCenteredStringWithShadow("Current Ip: " + UserCurrentIP, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 35F, -1);

        SFR.drawCenteredStringWithShadow("Version " + EnumChatFormatting.RED + BozoWare.getInstance().CLIENT_VERSION, sr.getScaledWidth() / 2, sr.getScaledHeight() - 10, 0x40ffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        int j = this.height / 2 - 55;
        int x = width/2 - 100;
        this.buttonList.add(new GuiButton(1, x + 51, j + 85, 98, 20, "Back"));
        this.buttonList.add(new GuiButton(2, x + 51, j + 60, 98, 20, "Get Current IP"));
        super.initGui();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 1) {
            mc.displayGuiScreen(new BozoMainMenu());
        }
        if(button.id == 2) {
            UserCurrentIP = SecurityUtils.grabCurrentIP();
        }
        super.actionPerformed(button);
    }
}