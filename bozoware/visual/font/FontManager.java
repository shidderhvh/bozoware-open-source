package bozoware.visual.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager {

    private int completed;

    public MinecraftFontRenderer smallFontRenderer;
    private Font smallFontRendererFont;
    public MinecraftFontRenderer mediumFontRenderer;
    private Font mediumFontRendererFont;
    public MinecraftFontRenderer largeFontRenderer;
    private Font largeFontRendererFont;
    public MinecraftFontRenderer largeFontRenderer2;
    private Font largeFontRendererFont2;
    public MinecraftFontRenderer SUPALargeFontRenderer;
    private Font SUPALargeFontRendererFont;
    public MinecraftFontRenderer ArrowIcons;
    private Font ArrowIconsFont;
    public MinecraftFontRenderer novolineFontRenderer;
    private Font novolineFont;
    public MinecraftFontRenderer BasicIcons;
    private Font BasicIconsFont;
    public MinecraftFontRenderer MenuIcons;
    private Font MenuIconsFont;
    public MinecraftFontRenderer MenuIcons2;
    private Font MenuIconsFont2;
    public MinecraftFontRenderer MenuIcons3;
    private Font MenuIconsFont3;
    public MinecraftFontRenderer LargeBasicIcons;
    private Font LargeBasicIconsFont;
    public MinecraftFontRenderer hotMcFontRenderer;
    private Font hotMcFont;
    public MinecraftFontRenderer SkeetIcons;
    private Font SkeetIconsFont;
    public MinecraftFontRenderer smallCSGORenderer;
    private Font smallCSGOFont;
    public MinecraftFontRenderer onetapFontRenderer;
    private Font onetapFont;
    public MinecraftFontRenderer McFontRenderer;
    private Font McFontRendererFont;
    public MinecraftFontRenderer SmallMcFontRenderer;
    private Font SmallMcFontRendererFont;
    public MinecraftFontRenderer onetapIconsRenderer;
    private Font onetapIcons;
    public MinecraftFontRenderer onetapDefaultRenderer;
    private Font onetapDefaultFont;
    public MinecraftFontRenderer BitFontRenderer;
    private Font BitFont;
    public MinecraftFontRenderer rainyHeartsRenderer;
    private Font rainyHearts;

    public FontManager() {
        setupFonts();
    }

    private Font getFont(Map<String, Font> locationMap, String location, int size) {
        Font font;
        try {
            if (locationMap.containsKey(location)) {
                font = locationMap.get(location).deriveFont(Font.PLAIN, size);
            } else {
                InputStream is = Minecraft.getMinecraft().getResourceManager()
                        .getResource(new ResourceLocation("BozoWare/Fonts/" + location)).getInputStream();
                font = Font.createFont(0, is);
                locationMap.put(location, font);
                font = font.deriveFont(Font.PLAIN, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, 10);
        }
        return font;
    }

    private boolean hasLoaded() {
        return completed >= 3;
    }

    private void setupFonts(){
        new Thread(() ->
        {
            Map<String, Font> locationMap = new HashMap<>();
            SmallMcFontRendererFont = getFont(locationMap, "mc.ttf", 12);
            McFontRendererFont = getFont(locationMap, "mc.ttf", 21);
            smallFontRendererFont = getFont(locationMap, "font.ttf", 17);
            mediumFontRendererFont = getFont(locationMap, "font.ttf", 19);
            largeFontRendererFont = getFont(locationMap, "font.ttf", 21);
            largeFontRendererFont2 = getFont(locationMap, "font.ttf", 40);
            hotMcFont = getFont(locationMap, "Minecraft.ttf", 19);
            SUPALargeFontRendererFont = getFont(locationMap, "font.ttf",25);
            ArrowIconsFont = getFont(locationMap, "Arrows.ttf", 21);
            BasicIconsFont = getFont(locationMap, "BasicIcons.ttf", 21);
            MenuIconsFont = getFont(locationMap, "menuicons.ttf", 45);
            MenuIconsFont2 = getFont(locationMap, "menuicons2.ttf", 45);
            novolineFont = getFont(locationMap, "novoline.ttf", 19);
            MenuIconsFont3 = getFont(locationMap, "menuicons3.ttf", 45);
            LargeBasicIconsFont = getFont(locationMap, "BasicIcons.ttf", 40);
            SkeetIconsFont = getFont(locationMap, "SkeetIcons.ttf", 21);
            smallCSGOFont = new Font("tahoma", Font.PLAIN, 15);
            onetapFont = getFont(locationMap,"onetap.ttf", 24);
            onetapIcons = getFont(locationMap, "onetapicon.ttf", 18);
            onetapDefaultFont = new Font("roboto", Font.PLAIN, 16);
            BitFont = getFont(locationMap, "BitFont.ttf", 21);
            rainyHearts = getFont(locationMap, "rainyhearts.ttf", 19);


            completed++;
        }).start();
        new Thread(() -> completed++).start();
        new Thread(() -> completed++).start();
        while (!hasLoaded()) {
            try {
                //noinspection BusyWait
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SmallMcFontRenderer = new MinecraftFontRenderer(SmallMcFontRendererFont, true, true);
        McFontRenderer = new MinecraftFontRenderer(McFontRendererFont, true, true);
        smallFontRenderer = new MinecraftFontRenderer(smallFontRendererFont, true, true);
        mediumFontRenderer = new MinecraftFontRenderer(mediumFontRendererFont, true, true);
        largeFontRenderer = new MinecraftFontRenderer(largeFontRendererFont, true, true);
        BitFontRenderer = new MinecraftFontRenderer(BitFont, true, true);
        novolineFontRenderer = new MinecraftFontRenderer(novolineFont, true, true);
        largeFontRenderer2 = new MinecraftFontRenderer(largeFontRendererFont2, true, true);
        SUPALargeFontRenderer = new MinecraftFontRenderer(SUPALargeFontRendererFont, true, true);
        hotMcFontRenderer = new MinecraftFontRenderer(hotMcFont, true, true);
        ArrowIcons = new MinecraftFontRenderer(ArrowIconsFont, true, true);
        BasicIcons = new MinecraftFontRenderer(BasicIconsFont, true, true);
        MenuIcons = new MinecraftFontRenderer(MenuIconsFont, true, true);
        MenuIcons2 = new MinecraftFontRenderer(MenuIconsFont2, true, true);
        MenuIcons3 = new MinecraftFontRenderer(MenuIconsFont3, true, true);
        LargeBasicIcons = new MinecraftFontRenderer(LargeBasicIconsFont, true, true);
        SkeetIcons = new MinecraftFontRenderer(SkeetIconsFont, true, true);
        smallCSGORenderer = new MinecraftFontRenderer(smallCSGOFont, false, true);
        onetapFontRenderer = new MinecraftFontRenderer(onetapFont, true, true);
        onetapIconsRenderer = new MinecraftFontRenderer(onetapIcons, true, true);
        onetapDefaultRenderer = new MinecraftFontRenderer(onetapDefaultFont, true, true);
        rainyHeartsRenderer = new MinecraftFontRenderer(onetapDefaultFont, true, true);
    }
}