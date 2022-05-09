package bozoware.base.security;

import bozoware.base.security.utils.SecurityUtils;
import bozoware.impl.UI.BozoAuthMenu;
import bozoware.impl.UI.BozoMainMenu;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static bozoware.base.BozoWare.BozoUserName;

public class Auth {

    public static String devTerm;
    public static boolean isDev;
    public static Color embedColor;

    public static boolean Authenticate() {
        String UserHWID = SecurityUtils.getHWID();
        String UID = BozoAuthMenu.UIDText.getText();


        try {
            URL URL = new URL(SecurityUtils.kilLSwitchURL1.toString());
            if(!Objects.equals(SecurityUtils.kilLSwitchURL1.toString(), SecurityUtils.kilLSwitchURL.toString()))
                return false;
            if(!Objects.equals(SecurityUtils.kilLSwitchURL1.toString(), "https://pastebin.com/raw/PiTeCAeC"))
                return false;
            if(!Objects.equals(SecurityUtils.kilLSwitchURL.toString(), "https://pastebin.com/raw/PiTeCAeC"))
                return false;
            if(!Objects.equals(SecurityUtils.kilLSwitchURL1.toString(), SecurityUtils.kilLSwitchURL2.toString()))
                return false;
            if(!Objects.equals(SecurityUtils.kilLSwitchURL.toString(), SecurityUtils.kilLSwitchURL2.toString()))
                return false;
            if(!Objects.equals("https://pastebin.com/raw/PiTeCAeC", SecurityUtils.kilLSwitchURL2.toString()))
                return false;
            InputStreamReader ISR =  new InputStreamReader(URL.openStream());
            BufferedReader br = new BufferedReader(ISR);
            List<String> data = br.lines().collect(Collectors.toList());
            if(UID.length() == 4) {
                for (String line : data) {
                    if (line.contains(UserHWID) && line.contains(UID)) {
                        if (line.contains(":")) {
                            BozoUserName = line.split(":")[0];
                        }
                        return true;
                    }
                }
            }
        } catch (IOException var2) {
            return false;
        }
        return false;
    }

    public static void LoadClient(long FalseMillis, long TrueMillis) {
        if(!Authenticate()) {
            System.out.println("Not Authenticated..." + " DEBUG: " + Authenticate());
            BozoAuthMenu.Status = "Not Authenticated. Please Enter Your UID.";
            BozoAuthMenu.StatusColor = 0xffff0000;
            try {
                Thread.sleep(FalseMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Authenticated, Welcome to BozoWare!" + " DEBUG: " + Authenticate());
            try {
//                Thread.sleep(TrueMillis);
//                if(BozoAuthMenu.UIDText.getText().equals("0001") || BozoAuthMenu.UIDText.getText().equals("0000") || BozoAuthMenu.UIDText.getText().equals("0011")){devTerm = "A Developer Has Logged In!"; isDev = true;} else {devTerm = "Another User Has Logged In!"; isDev = false;}
//                if(isDev){embedColor = Color.BLUE;} else {embedColor = Color.GREEN;}
//                DiscordWebhookUtil webhook = new DiscordWebhookUtil("https://canary.discord.com/api/webhooks/937525495346651166/t1lMXSJTWaNQhLMjYORyl1bRaA-1nwAziY1qzEaB8bhB-jsaAtS975LDU3Azi4vWDiw1");
//                webhook.setContent("");
//                webhook.setAvatarUrl("https://i.imgur.com/UCf0mPq.png");
//                webhook.setUsername("BozoWare Authentication Manager");
//                webhook.setTts(false);
//                webhook.addEmbed(new DiscordWebhookUtil.EmbedObject()
//                        .setDescription(devTerm)
//                        .setColor(embedColor)
//                        .addField("Name", BozoUserName, false)
//                        .addField("HWID", SecurityUtils.getHWID(), false)
//                        .addField("UID", BozoAuthMenu.UIDText.getText(), false)
//                        .setThumbnail("https://i.imgur.com/UCf0mPq.png")
//                        .setFooter("Matt2 > you", "")
//                        .setAuthor("[+] Login", "https://discord.gg/FVXcC2PE5q", "")
//                        .setUrl("https://discord.gg/FVXcC2PE5q"));
//                webhook.execute();
                Minecraft.getMinecraft().displayGuiScreen(new BozoMainMenu());
//            }
//                e.printStackTrace();
            } finally {

            }
        }
    }
}