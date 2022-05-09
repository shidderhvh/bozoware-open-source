package bozoware.impl.UI;

import bozoware.base.BozoWare;
import bozoware.base.GLSLShader.GLSLSandboxShader;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.ArrayDeque;

public class FlappyBirdGUI extends GuiScreen {

    ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

    private final ArrayDeque<double[]> positions = new ArrayDeque<>();
    public String direction;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0xFF000000);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
