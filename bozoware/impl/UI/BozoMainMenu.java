package bozoware.impl.UI;

import bozoware.base.BozoWare;
import bozoware.base.GLSLShader.GLSLSandboxShader;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import bozoware.visual.screens.alt.GuiAltManager;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BozoMainMenu extends GuiScreen {

    public static String UserName = BozoWare.BozoUserName;
    public static int hA, hA1, hA2;
    private static final List<String> changeLogEntries = fetchChangeLog().collect(Collectors.toList());
    private GLSLSandboxShader backgroundShader;
    private long initTime = System.currentTimeMillis();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MinecraftFontRenderer Icons = BozoWare.getInstance().getFontManager().MenuIcons;
        MinecraftFontRenderer Icons2 = BozoWare.getInstance().getFontManager().MenuIcons2;
        MinecraftFontRenderer SLFR = BozoWare.getInstance().getFontManager().largeFontRenderer2;
        MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
        MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0xff1f1f1f);

        RenderUtil.drawSmoothRoundedRect(sr.getScaledWidth() / 2F - 110, sr.getScaledHeight() / 2F - 100, sr.getScaledWidth() / 2F + 110, sr.getScaledHeight() / 2F + 75, 15, 0xff171717);

        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2F - 110, sr.getScaledHeight() / 2F - 100, sr.getScaledWidth() / 2F + 110, sr.getScaledHeight() / 2F + 75, 0xff000000, 3, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2F - 110, sr.getScaledHeight() / 2F - 100, sr.getScaledWidth() / 2F + 110, sr.getScaledHeight() / 2F + 75, 0xff3c3c3c, 2, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2F - 110, sr.getScaledHeight() / 2F - 100, sr.getScaledWidth() / 2F + 110, sr.getScaledHeight() / 2F + 75, 0xff282828, 0.8F, 2);

        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2F - 108, sr.getScaledHeight() / 2F - 98.5, 108, 2, 0xff37b1da, 0xffc862b5);
        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2F - 2, sr.getScaledHeight() / 2F - 98.5, 110.5, 2, 0xffc862b5, 0xffcce236);

        Icons.drawStringWithShadow("e", 8, sr.getScaledHeight() - 525, hA1);
        Icons.drawStringWithShadow("d", sr.getScaledWidth() - 25, sr.getScaledHeight() - 525, hA2);
        Icons2.drawStringWithShadow("b", sr.getScaledWidth() / 2F - 475, sr.getScaledHeight() - 525, hA);

        SLFR.drawCenteredStringWithShadow("BozoWare",sr.getScaledWidth() / 2F, sr.getScaledHeight() / 2F - 75F, -1);
        try {
            this.backgroundShader = new GLSLSandboxShader("/bozoware/base/GLSLShader/noise1.fsh");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load shader", e);
        }

        GlStateManager.disableCull();

        this.backgroundShader.useShader(mc.displayWidth - 700, mc.displayHeight, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000f);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);

        GL11.glEnd();

        GL20.glUseProgram(0);
        MFR.drawCenteredStringWithShadow("Authenticated, Welcome To BozoWare " + EnumChatFormatting.RED + UserName, sr.getScaledWidth() / 2F, sr.getScaledHeight() - 12, 0x40ffffff);
        SFR.drawCenteredStringWithShadow("Version " + EnumChatFormatting.RED + BozoWare.getInstance().CLIENT_VERSION, sr.getScaledWidth() / 2F, sr.getScaledHeight() - 22, 0x40ffffff);

//        int yPos = 8;
//        for (String line : changeLogEntries) {
//            char type = line.charAt(1);
//            int col = type == '+' ? 0x00FF59 : type == '-' ? 0xFF0020 : 0xFFFF00;
//
//            int alpha = (mouseX > 10 && mouseX < 10 + SFR.getStringWidth(line) && mouseY > yPos && mouseY < yPos + SFR.getHeight() + 2) ? 0xFF : 0x80;
//
//            SFR.drawStringWithShadow(line, 5, yPos, col | alpha << 24);
//            yPos += SFR.getHeight() + 2;
//        }

//        if(isShidder()) {
//            RenderUtil.drawImage(new ResourceLocation("BozoWare/Uzaki.png"), sr.getScaledWidth() / 1.75F, 235, 256, 315);
//        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    @Override
    public void initGui() {
        int j = this.height / 2 - 55;
        int x = width/2 - 100;
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.buttonList.add(new GuiButton(1, x, j + 10, "Singleplayer"));
        this.buttonList.add(new GuiButton(2, x, j + 35, "Multiplayer"));
        this.buttonList.add(new GuiButton(3, x, j + 60, "Alt Manager"));
        this.buttonList.add(new GuiButton(4, x + 51, j + 85, 98, 20, "Quit"));
        this.buttonList.add(new GuiButton(5, sr.getScaledWidth() - 100, sr.getScaledHeight() - 22, 98, 20, "Credits"));
        super.initGui();
    }
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        if (mouseButton == 0) {
//            for (GuiButton guiButton : this.buttonList) {
//                if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
//                    this.selectedButton = guiButton;
//                    guiButton.playPressSound(this.mc.getSoundHandler());
//                    this.actionPerformed(guiButton);
//                }
//            }
//        }
//        String[] buttons = {"d", "e", "b"};
//        MinecraftFontRenderer Icons = BozoWare.getInstance().getFontManager().MenuIcons;
//        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
//        for (String button : buttons) {
//            float dx = sr.getScaledWidth() - 25;
//            float dy = sr.getScaledHeight() - 525;
//            float ex = sr.getScaledWidth() - 50;
//            float ey = sr.getScaledHeight() - 525;
//            float bx = sr.getScaledWidth() / 2F - 475F;
//            float by = sr.getScaledHeight() - 525;
//
//            if (mouseX >= dx && mouseY >= dy && mouseX < dx + Icons.getStringWidth(button) && mouseY < dy + Icons.getHeight()) {
//                try {
//                    Desktop.getDesktop().browse(new URI("https://discord.gg/FVXcC2PE5q"));
//                } catch (IOException | URISyntaxException e) {
//                    e.printStackTrace();
//                }
//                hA2 = 0xffffffff;
//            }
//            else {
//                hA2 = 0x40ffffff;
//            }
//            if (mouseX >= ex && mouseY >= ey && mouseX < ex + Icons.getStringWidth(button) && mouseY < ey + Icons.getHeight()) {
//                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
//                hA1 = 0xffffffff;
//            }
//            else {
//                hA1 = 0x40ffffff;
//            }
//            if (mouseX >= bx && mouseY >= by && mouseX < bx + Icons.getStringWidth(button) && mouseY < by + Icons.getHeight()) {
//                mc.displayGuiScreen(new BozoInfoMenu());
//                hA = 0xffffffff;
//            }
//            else {
//                hA = 0x40ffffff;
//            }
//        }
//    }
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 1) {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if(button.id == 2) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiAltManager());
        }
        if(button.id == 4) {
            mc.shutdown();
        }
        if (button.id == 5) {
            this.mc.displayGuiScreen(new BozoCreditsMenu());
        }
        super.actionPerformed(button);
    }
    private static Stream<String> fetchChangeLog() {
        try {
            URL url = new URL("https://kobleyauthentication.000webhostapp.com/changelog");
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines();
        } catch (IOException e) {
//            System.out.println("cope");
            return Stream.empty();
        }
    }
    public boolean isShidder() {
        return UserName.contains("Shidder") && BozoAuthMenu.UIDText.getText().contains("0000") && BozoAuthMenu.UIDText.getText().length() == 4;
    }

}