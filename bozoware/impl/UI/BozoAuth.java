package bozoware.impl.UI;

import bozoware.base.security.Auth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class BozoAuth extends GuiScreen {

    private GuiTextField uidField;

    public void keyTyped(char character, int key) {
        uidField.textboxKeyTyped(character, key);
    }
        @Override
    public void initGui() {
        int j = this.height / 2 - 55;
        int x = width/2 - 100;
        uidField = new GuiTextField(height / 4 + 24, Minecraft.getMinecraft().fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.buttonList.add(new GuiButton(1, x, j + 10, "Login"));
            super.initGui();
    }
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        super.drawScreen(mouseX, mouseY, partialTicks);
        uidField.drawTextBox();
        if (uidField.getText().length() == 0) {
            mc.fontRendererObj.drawString("\247TType your UID", (int) (width / 2.0F - 95), 66, -1);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        uidField.mouseClicked(mouseX, mouseY, button);
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void actionPerformed(GuiButton button) throws IOException {
//        if (button.id == 1) {
//            try {
//                URL url = new URL("https://pastebin.com/raw/YrGKFGEw");
//                Scanner scanner = new Scanner(url.openStream());
//                while (scanner.hasNext()) {
//                    if (scanner.next().contains("00" + uidField.getText())) {
//                        continue;
//                    }
//                }
//            } catch (IOException var2) {
//                return;
//            }
//            return;
//        }
        if (button.id == 1) {
            if (!Auth.Authenticate()) {
                int j = this.height / 2 - 55;
                int x = width / 2 - 100;
                mc.fontRendererObj.drawStringWithShadow("Not Authenticated..." + " DEBUG: " + Auth.Authenticate(), x, j - 20, -1);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                mc.fontRendererObj.drawStringWithShadow("Authenticating", width, height + 50, -1);
                System.out.println("Authenticated, Welcome to BozoWare!" + " DEBUG: " + Auth.Authenticate());
                mc.displayGuiScreen(new BozoMainMenu());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.actionPerformed(button);
    }
}
