package bozoware.base.api;

import bozoware.visual.screens.alt.DrilledAPI;
import bozoware.visual.screens.alt.GuiAltManager;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;

import java.io.IOException;
import java.net.Proxy;
import java.util.Objects;

public class GuiAltLogin extends GuiScreen {

    private GuiTextField emailField;
    private GuiTextField passwordField;
    private String status = "Idle...";

    public void keyTyped(char character, int key){
        emailField.textboxKeyTyped(character, key);
        passwordField.textboxKeyTyped(character, key);
    }

    public void initGui(){
        emailField = new GuiTextField(height / 4 + 24, Minecraft.getMinecraft().fontRendererObj, width / 2 - 100, 60, 200, 20);
        passwordField = new GuiTextField(height / 4 + 44, this.mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        ScaledResolution sr = new ScaledResolution(mc, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(1, sr.getScaledWidth()/2 - 100, var3 + 72 - 12, "Import email:pass"));
        buttonList.add(new GuiButton(2, sr.getScaledWidth()/2 - 100, var3 + 72 + 12, "Login"));
        buttonList.add(new GuiButton(5, sr.getScaledWidth()/2 - 100, var3 + 72 + 12 + 24, "Login with Drilled Gen"));
        buttonList.add(new GuiButton(3, sr.getScaledWidth()/2 - 100, var3 + 72 + 12 + 24 + 24, "Generate Cracked"));
        buttonList.add(new GuiButton(4, sr.getScaledWidth()/2 - 100, var3 + 72 + 12 + 24 + 24 + 24, "Back to menu"));
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        emailField.drawTextBox();
        passwordField.drawTextBox();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(status, (float) (width/2.0 - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(status) / 2)), 25, -1);
        if (emailField.getText().length() == 0) {
            mc.fontRendererObj.drawString("\2477Username / Email", (int) (width / 2.0F - 95), 66, -1);
        }
        if (passwordField.getText().length() == 0) {
            mc.fontRendererObj.drawString("\2477Password", (int) (width / 2.0F - 95), 106, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        emailField.mouseClicked(mouseX, mouseY, button);
        passwordField.mouseClicked(mouseX, mouseY, button);
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(GuiButton button){

        switch(button.id){
            case 1:
                String clipBoardText = getClipboardString();
                if (clipBoardText.contains("@") && clipBoardText.contains(":")) {
                    emailField.setText(clipBoardText.split(":")[0]);
                    passwordField.setText(clipBoardText.split(":")[1].replaceAll("\n", ""));
                }
                break;
            case 2:
                try {
                    if(!Objects.equals(passwordField.getText(), ""))
                    loginToAccount(emailField.getText(), passwordField.getText());
                    else
                        Minecraft.getMinecraft().session =  new Session(emailField.getText(), "", "", "mojang");
                    status = "Logged in as: \247e" + mc.getSession().getUsername();
                } catch (AuthenticationException e) {
                    status = "\2474Invalid details!";
                    e.printStackTrace();
                }
                break;
            case 3:
                String bozo = "Bozo";
                String numba[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

                for (int i = 0; i < 16 - bozo.length(); i++){
                    bozo += numba[(int) Math.floor(Math.random() * numba.length)];;
                }
                Minecraft.getMinecraft().session =  new Session(bozo, "", "", "mojang");
                status = "Logged in as: \247e" + mc.getSession().getUsername();
                break;
            case 4:
                mc.displayGuiScreen(new GuiAltManager());
                break;
            case 5:
                try {
                    if(Objects.requireNonNull(DrilledAPI.getAlt()).getUsername() != null && DrilledAPI.getAlt().getPassword() != null)
                    loginToAccount(DrilledAPI.getAlt().getUsername(), DrilledAPI.getAlt().getPassword());
                    status = "Logged in as: \247e" + mc.getSession().getUsername();
                } catch (AuthenticationException e) {
                    status = "\2474drilled alts gave an INVALID ACCOUNT?!";
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }

    private void loginToAccount(String email, String pass) throws AuthenticationException {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(pass);
        auth.logIn();
        Minecraft.getMinecraft().session =  new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
    }


}