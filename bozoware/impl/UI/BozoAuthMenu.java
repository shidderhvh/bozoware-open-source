package bozoware.impl.UI;

import bozoware.base.BozoWare;
import bozoware.base.GLSLShader.GLSLSandboxShader;
import bozoware.base.security.Auth;
import bozoware.base.security.utils.SecurityUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;


public class BozoAuthMenu extends GuiScreen {

    public static GuiTextField UIDText;
    public static String Status = "Idle...";
    public static int StatusColor = -1;
    private GLSLSandboxShader backgroundShader;
    private long initTime = System.currentTimeMillis();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MinecraftFontRenderer Icons = BozoWare.getInstance().getFontManager().MenuIcons;
        MinecraftFontRenderer SLFR = BozoWare.getInstance().getFontManager().largeFontRenderer2;
        MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

//        Wrapper.sendPacketDirect(new C01PacketChatMessage());
//        this.mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/Uzaki.png"));
//        drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, (float)this.width, (float)this.height);
//        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0xff1f1f1f);

        try {
            this.backgroundShader = new GLSLSandboxShader("/bozoware/base/GLSLShader/noise2.fsh");
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

        RenderUtil.drawSmoothRoundedRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 15, 0xff171717);

        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff000000, 3, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff3c3c3c, 2, 2);
        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff282828, 0.8F, 2);

        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 108, sr.getScaledHeight() / 2 - 98.5, 108, 2, 0xff37b1da, 0xffc862b5);
        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 2, sr.getScaledHeight() / 2 - 98.5, 110, 2, 0xffc862b5, 0xffcce236);

        SLFR.drawSmoothString("BozoWare",sr.getScaledWidth() / 2 - 45, sr.getScaledHeight() / 2 - 75F, -1, true);

        MFR.drawCenteredStringWithShadow(Status, sr.getScaledWidth() / 2, 10, StatusColor);

        UIDText.drawTextBox();
        if (UIDText.getText().length() == 0 && !UIDText.isFocused()) {
            MFR.drawString("\2477Enter UID", width/2 - 100 + 53, this.height / 2 - 55 + 56, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void keyTyped(char character, int key){
        UIDText.textboxKeyTyped(character, key);
    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        UIDText.mouseClicked(mouseX, mouseY, button);
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        int j = this.height / 2 - 55;
        int x = width/2 - 100;
        UIDText = new GuiTextField(69420, mc.fontRendererObj, x + 51, j + 50, 98, 20);
        this.buttonList.add(new GuiButton(69420, x + 51, j + 85, 98, 20, "Login"));
        this.buttonList.add(new GuiButton(69, x + 51, j + 85 + 19, 98, 20, "Copy HWID"));
        super.initGui();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        if(button.id == 69420) {
            Auth.LoadClient(1, 1);
        }
        if(button.id == 69) {
            final StringSelection stringSelection = new StringSelection(SecurityUtils.getHWID());
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            StatusColor = 0xFF707020;
            Status = "HWID Copied! DM shidder to get whitelisted.";
        }
        super.actionPerformed(button);
    }
}