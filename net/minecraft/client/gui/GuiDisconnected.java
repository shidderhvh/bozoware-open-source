package net.minecraft.client.gui;

import bozoware.base.api.GuiAltLogin;
import bozoware.impl.UI.BozoMainMenu;
import bozoware.impl.module.visual.SessionInfo;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    static int initTime;
    static String name;


    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
//        SessionInfo.bruh = (int) System.currentTimeMillis();
        initTime = ((int) System.currentTimeMillis() - SessionInfo.bruh);
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        if (mc.getCurrentServerData() != null) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 24, "Reconnect"));
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 48, "Reconnect With Cracked Alt"));
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 48 + 24, "Alt Manager "));
        } else {
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 24, "Alt Manager "));
        }
    }
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            SessionInfo.bruh = (int) System.currentTimeMillis();
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if(button.id == 1){
            if (mc.getCurrentServerData() != null) mc.displayGuiScreen(new GuiConnecting(new BozoMainMenu(), mc, mc.getCurrentServerData()));
        }
        if(button.id == 2){
            SessionInfo.bruh = (int) System.currentTimeMillis();
            String bozo = "Bozo";
            String numba[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

            for (int i = 0; i < 12; i++){
                bozo += numba[(int) Math.floor(Math.random() * numba.length)];;
            }
            Minecraft.getMinecraft().session =  new Session(bozo, "", "", "mojang");
            if (mc.getCurrentServerData() != null) mc.displayGuiScreen(new GuiConnecting(new BozoMainMenu(), mc, mc.getCurrentServerData()));
        }
        if(button.id == 3){
            SessionInfo.bruh = (int) System.currentTimeMillis();
            mc.displayGuiScreen(new GuiAltLogin());
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
//                if(s.contains("Cheating") || s.contains("KillAura") || s.contains("Fly") || s.contains("Flying") || s.contains("AimBot") || s.contains("Hacking")){
//                }
            }
            this.drawCenteredString(this.fontRendererObj, "Playtime: " + ChatFormatting.RED + initTime / (60 * 60 * 1000) % 24 + "h " + initTime / (60 * 1000) % 60 + "m " + initTime / 1000 % 60 + "s", this.width / 2, i - 200, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
}


