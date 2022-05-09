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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BozoCreditsMenu extends GuiScreen {
    private static final List<String> credits = fetchChangeLog().collect(Collectors.toList());

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MinecraftFontRenderer SLFR = BozoWare.getInstance().getFontManager().largeFontRenderer2;
        MinecraftFontRenderer LFR = BozoWare.getInstance().getFontManager().largeFontRenderer;
        MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);


        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0xff171717);

        Gui.drawRect(50, 50, sr.getScaledWidth() - 50, sr.getScaledHeight() - 50, 0x70171717);

//        RenderUtil.drawSmoothRoundedRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 15, 0xff1f1f1f);

//        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff000000, 3, 2);
//        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff3c3c3c, 2, 2);
//        RenderUtil.drawRoundedOutline(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 75, 0xff282828, 0.8F, 2);

//        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 108, sr.getScaledHeight() / 2 - 98.5, 108, 2, 0xff37b1da, 0xffc862b5);
//        RenderUtil.glHorizontalGradientQuad(sr.getScaledWidth() / 2 - 2, sr.getScaledHeight() / 2 - 98.5, 110.5, 2, 0xffc862b5, 0xffcce236);


        SFR.drawCenteredStringWithShadow("Version " + EnumChatFormatting.RED + BozoWare.getInstance().CLIENT_VERSION, sr.getScaledWidth() / 2, sr.getScaledHeight() - 10, 0x40ffffff);
        int yPos = sr.getScaledHeight() / 2 -120;
        int xPos = sr.getScaledWidth() / 2 - 120;
        for (String line : credits) {
            char type = line.charAt(1);
            int col = type == '+' ? 0x00FF59 : type == '-' ? 0xFF0020 : 0xFFFF00;

            int alpha = (mouseX > xPos && mouseX < xPos + SFR.getStringWidth(line) && mouseY > yPos && mouseY < yPos + SFR.getHeight() + 2) ? 0xFF : 0x80;

            SFR.drawStringWithShadow(line, xPos, yPos, col | alpha << 24);
            yPos += SFR.getHeight() + 15;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    private static Stream<String> fetchChangeLog() {
        try {
            URL url = new URL("https://pastebin.com/raw/hUg4NJvu");
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines();
        } catch (IOException e) {
//            System.out.println("cope");
            return Stream.empty();
        }
    }
    @Override
    public void initGui() {
        int j = this.height / 2 - 55;
        int x = width/2 - 100;
        super.initGui();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 1) {
            mc.displayGuiScreen(new BozoMainMenu());
        }
        super.actionPerformed(button);
    }
}