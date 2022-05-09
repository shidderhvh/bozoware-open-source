package bozoware.base.security.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

public class SecurityUtils {

    public static URL kilLSwitchURL;

    static {
        try {
            kilLSwitchURL = new URL("https://pastebin.com/raw/" + "PiTeCAeC");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL kilLSwitchURL1;

    static {
        try {
            kilLSwitchURL1 = new URL("https://pastebin.com/raw/P" + "i" + "T" + "e" + "C" + "A" + "e" + "C");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public static URL kilLSwitchURL2;

    static {
        try {
            kilLSwitchURL2 = new URL("https://pastebin.com/raw/PiTeCAeC");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String getHWID() {
        try {
            String toEncrypt = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();
            byte[] BD = md.digest();
            for (int i = 0; i < BD.length; ++i) {
                byte aBD = BD[i];
                String hex = Integer.toHexString(255 & aBD);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception var9) {
            var9.printStackTrace();
            return "Error";
        }
    }

    public static String MD5Hash(String toBeHashed) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toBeHashed.getBytes());
            StringBuffer hexString = new StringBuffer();
            byte[] BD = md.digest();
            for (int i = 0; i < BD.length; ++i) {
                byte aBD = BD[i];
                String hex = Integer.toHexString(255 & aBD);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception var9) {
            var9.printStackTrace();
            return "Error";
        }
    }

    public static String grabCurrentIP() {
        try {
            URL URL = new URL("http://checkip.amazonaws.com/");
            InputStreamReader ISR =  new InputStreamReader(URL.openStream());
            BufferedReader br = new BufferedReader(ISR);
            return br.readLine();
        } catch (IOException var2) {
            return null;
        }
    }
}