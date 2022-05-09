package bozoware.base.notifications;

import bozoware.base.BozoWare;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.awt.Color;

import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class Notification {
    private NotificationType type;
    private String title;
    private String messsage;
    private long start;
    private double notifOffset;
    private long fadedIn;
    private long fadeOut;
    private long end;


    public Notification(NotificationType type, String title, String messsage, float length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = (long) (200L * length);
        fadeOut = (long) (fadedIn + 500L * length);
        end = fadeOut + fadedIn;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render() {
        double offset = 0;
        int width = 120;
        int height = 30;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
        } else {
            offset = width;
        }
        MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
        MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        Gui.drawRect(sr.getScaledWidth() - offset, sr.getScaledHeight() - 5 - height, sr.getScaledWidth(), sr.getScaledHeight() - 5, 0xBF000000);
        Gui.drawRect(sr.getScaledWidth() - offset, sr.getScaledHeight() - 5 - height, sr.getScaledWidth() - offset + 4, sr.getScaledHeight() - 5, type.getColor(time));
        RenderUtil.drawBoxOutline(sr.getScaledWidth() - offset, sr.getScaledHeight() - 5 - height, sr.getScaledWidth(), sr.getScaledHeight(), 0xff000000, 0.69);

        MFR.drawString(title, (int) (sr.getScaledWidth() - offset + 8), sr.getScaledHeight() - 2 - height, -1);
        SFR.drawString(messsage, (int) (sr.getScaledWidth() - offset + 12), sr.getScaledHeight() - 17, -1);
    }
}