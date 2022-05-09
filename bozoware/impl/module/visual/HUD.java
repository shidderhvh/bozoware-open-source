package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.module.ModuleManager;
import bozoware.base.util.Wrapper;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.visual.*;
import bozoware.base.util.visual.Animate.Direction;
import bozoware.impl.command.WatermarkCommand;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.property.*;
import bozoware.visual.font.MinecraftFontRenderer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ModuleData(moduleName = "HUD", moduleCategory = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final EnumProperty<watermarkModes> watermarkMode = new EnumProperty<>("HUD Mode", watermarkModes.CSGO, this);
    private final EnumProperty<colorModes> colorMode = new EnumProperty<>("Color Mode", colorModes.CustomFade, this);
    private final EnumProperty<lineModes> lineMode = new EnumProperty<>("Line Mode", lineModes.Right, this);
    private final BooleanProperty cfontBool = new BooleanProperty("CFont", true, this);
    private final BooleanProperty lineBool = new BooleanProperty("Lines", true, this);
    private final BooleanProperty bgBool = new BooleanProperty("BackGround", true, this);
    public final BooleanProperty hideSuffixes = new BooleanProperty("Hide Suffixes", true, this);
    private final ValueProperty<Integer> bgOpacity = new ValueProperty<>("BackGround Opacity", 100, 0, 255, this);
    private final ColorProperty colorProperty = new ColorProperty("Color", new Color(0xffff0000), this);
    private final ColorProperty colorProperty2 = new ColorProperty("Color 2", new Color(0xffff0000), this);
    private final ValueProperty<Integer> offSetValue = new ValueProperty<>("Offset", 6, 0, 12, this);
    private final ValueProperty<Integer> spacingValue = new ValueProperty<>("Spacing", 3, 0, 3, this);
    private final StringProperty theWatermark = new StringProperty("Watermark", WatermarkCommand.watermark, this);
//    public final EnumProperty<arrayListPos> arrayListPosition = new EnumProperty("Array List Position", arrayListPos.Top, this);
    static int bruh = (int) System.currentTimeMillis();

    public int getColor1() {return colorProperty.getPropertyValue().getRGB();}
    public int getColor2Gradient() {return colorProperty2.getPropertyValue().getRGB();}
    public int getColor2() {return colorProperty.getPropertyValue().darker().darker().darker().darker().darker().getRGB();}
    public int getColor2NotAsDark() {return colorProperty.getPropertyValue().darker().darker().getRGB();}

    //    public int animPosX;
    public static int bozoColor2;
    public int bozoColor;
    public int bozoColorDarker;
    float posY, posX;
    public Color bozoColorColor = new Color(bozoColor, true);
    public Color bozoColor2Color = new Color(bozoColor2, true);
    public int bozoColorColorFinal = bozoColorColor.darker().darker().getRGB();
    public int bozoColorDarkerTest = new Color(Math.max((int)(bozoColorColor.getRed()  * 0.7), 0), Math.max((int)(bozoColorColor.getGreen() * 0.7), 0), Math.max((int)(bozoColorColor.getBlue() * 0.7), 0), bozoColorColor.getAlpha()).getRGB();
    public int bozoColorX;
    public String serverIp;
    public Iterator<PotionEffect> iterator2;
    public PotionEffect effect;
    public String effectName;
    String getServerIp;
    float offset = 0;

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 0.85f, 1f).getRGB();
    }

    public static int rainbow(int idk, float bright, float st) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (idk * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0))	 < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright).getRGB();
    }

    public HUD() {
        theWatermark.setHidden(true);
        colorProperty2.setHidden(true);
        colorMode.onValueChange = () -> {
            if(colorMode.getPropertyValue().equals(colorModes.Gradient)){
                colorProperty2.setHidden(false);
            } else {
                colorProperty2.setHidden(true);
            }
        };
        onRender2DEvent = (render2DEvent -> {
            theWatermark.setPropertyValue(WatermarkCommand.watermark);
            theWatermark.setPropertyValue(theWatermark.getPropertyValue().replaceAll("_", " "));
            MinecraftFontRenderer SFR = BozoWare.getInstance().getFontManager().smallFontRenderer;
            MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
            MinecraftFontRenderer SLFR = BozoWare.getInstance().getFontManager().SUPALargeFontRenderer;
            MinecraftFontRenderer MCFR = BozoWare.getInstance().getFontManager().McFontRenderer;
            MinecraftFontRenderer SMCFR = BozoWare.getInstance().getFontManager().SmallMcFontRenderer;
            MinecraftFontRenderer skeetIcons = BozoWare.getInstance().getFontManager().SkeetIcons;
            MinecraftFontRenderer LbasicIcons = BozoWare.getInstance().getFontManager().LargeBasicIcons;
            MinecraftFontRenderer arrowIcons = BozoWare.getInstance().getFontManager().ArrowIcons;
            getServerIp = Wrapper.getCurrentServerIP();
            final ScaledResolution sr = render2DEvent.getScaledResolution();

            float yaw = (System.currentTimeMillis() / 5) % 360;
            float bounce = Math.abs(((System.currentTimeMillis() / 15) % 100) - 50);
            float rotationYaw = MathHelper.clamp_float(mc.thePlayer.rotationYaw, -45, 45);
            GlStateManager.color(1f,1f,1f,1f);

//            Gui.drawRect(sr.getScaledWidth() - 955, sr.getScaledHeight() - 5 - anim.getValue(), sr.getScaledWidth() - 930, sr.getScaledHeight() - 5, -1);
//            if(mc.thePlayer.onGround){
//                mc.thePlayer.motionY = anim.getValue();
//            }
//            if(!mc.thePlayer.onGround)
//                anim.reset();


            final String watermark = String.format("%s \2477|\247r v%s \2477|\247r %s FPS \2477|\247r %s BPS",
                    theWatermark.getPropertyValue(), BozoWare.getInstance().CLIENT_VERSION, Minecraft.getDebugFPS(), MovementUtil.getBPS());

            final String OTWatermark = String.format("%s | %s | %s FPS | %s BPS",
                    theWatermark.getPropertyValue(), getServerIp, Minecraft.getDebugFPS(), MovementUtil.getBPS());
            switch (colorMode.getPropertyValue()){
                case CustomFade:
                    bozoColor2 = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE * 1500, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                    break;
                case Gradient:
                    bozoColor2 = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE * 1500, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                    break;
                case Rainbow:
//                    bozoColor2 = getRainbow(1, (int) (sr.getScaledWidth() * Integer.MAX_VALUE * 5));
                    break;
//                case Astolfo:
//                    bozoColor2 = rainbow((int) -sr.getScaledWidth() * Integer.MAX_VALUE, 1f, 0.47f);
//                    break;
                default:
                    bozoColor2 = -1;
            }
            switch (watermarkMode.getPropertyValue().toString()) {
                case "Bozoware":
                    char firstLetter = theWatermark.getPropertyValue().charAt(0);
                    String lettersBeyond = theWatermark.getPropertyValue().replaceFirst(String.valueOf(firstLetter), "");
                    MCFR.drawStringWithShadow(String.valueOf(firstLetter), 5, 6, bozoColor2);
                    mc.fontRendererObj.drawStringWithShadow(lettersBeyond, 13, 8, -1);
//                    SMCFR.drawStringWithShadow("b" + BozoWare.getInstance().CLIENT_VERSION, !lettersBeyond.equals("") ? mc.fontRendererObj.getStringWidth(lettersBeyond) - MCFR.getStringWidth(firstLetter + "") : MCFR.getStringWidth(firstLetter + "") * 2, 5, -1);
                    break;
                case "CSGO":
                    if (cfontBool.getPropertyValue()) {
                        RenderUtil.drawSmoothRoundedRect(2, 2, (float) (2 + MFR.getStringWidth(watermark) + 5), 2 + MFR.getHeight() + 5, 8, 0x90000000);
                        MFR.drawStringWithShadow(watermark, 4, 5, -1);
                    } else {
                        RenderUtil.drawSmoothRoundedRect(2, 2, (float) (2 + mc.fontRendererObj.getStringWidth(watermark) + 5), 2 + mc.fontRendererObj.FONT_HEIGHT + 5, 8, 0x90000000);
                        mc.fontRendererObj.drawStringWithShadow(watermark, 4, 5, -1);
                    }
                    break;
                case "idkwhat2call":
                    if (!(mc.currentScreen instanceof GuiChat)) {
//                        GuiInventory.drawEntityOnScreen(sr.getScaledWidth() - 30, sr.getScaledHeight() - 5, 50, 45, 0, mc.thePlayer);
                    } else {
//                        GuiInventory.drawEntityOnScreen(sr.getScaledWidth() - 30, sr.getScaledHeight() - 15, 50, 45, 0, mc.thePlayer);
                    }
                    if (cfontBool.getPropertyValue()) {
                        String dateFormat = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());;
                        String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                        String direction = directions[wrapAngleToDirection(mc.thePlayer.rotationYaw, directions.length)];
                        firstLetter = theWatermark.getPropertyValue().charAt(0);
                        lettersBeyond = theWatermark.getPropertyValue().replaceFirst(String.valueOf(firstLetter), "");
                        int x = (int) mc.thePlayer.posX;
                        int y = (int) mc.thePlayer.posY;
                        int z = (int) mc.thePlayer.posZ;
                        MFR.drawStringWithShadow(ChatFormatting.WHITE + "Location: " + ChatFormatting.GRAY + x + " " + y + " " + z, 4, 18, bozoColor2);
                        MFR.drawStringWithShadow(String.valueOf(firstLetter) + ChatFormatting.WHITE + lettersBeyond + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + BozoWare.getInstance().CLIENT_VERSION  + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + getServerIp + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + dateFormat + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + direction + ChatFormatting.GRAY + "]", 4, 6, bozoColor2);
//                        SFR.drawStringWithShadow("b" + BozoWare.getInstance().CLIENT_VERSION, 48, 4, -1);
                    }
                    else {
                        int x = (int) mc.thePlayer.posX;
                        int y = (int) mc.thePlayer.posY;
                        int z = (int) mc.thePlayer.posZ;
                        String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                        String direction = directions[wrapAngleToDirection(mc.thePlayer.rotationYaw, directions.length)];
                        String dateFormat = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        firstLetter = theWatermark.getPropertyValue().charAt(0);
                        lettersBeyond = theWatermark.getPropertyValue().replaceFirst(String.valueOf(firstLetter), "");
                        mc.fontRendererObj.drawStringWithShadow(ChatFormatting.WHITE + "Location: " + ChatFormatting.GRAY + x + " " + y + " " + z, 4, 18, bozoColor2);
                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(firstLetter) + ChatFormatting.WHITE + lettersBeyond + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + BozoWare.getInstance().CLIENT_VERSION  + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + getServerIp + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + dateFormat + ChatFormatting.GRAY + "]" + ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + direction + ChatFormatting.GRAY + "]", 4, 6, bozoColor2);
                    }
//                        mc.fontRendererObj.drawStringWithShadow("B", 4, 4, bozoColor2);
//                        mc.fontRendererObj.drawStringWithShadow("ozoware", 10, 4, -1);
//                        mc.fontRendererObj.drawStringWithShadow("Build: " + ChatFormatting.WHITE + "012122", sr.getScaledWidth() - 70, sr.getScaledHeight() - 15, bozoColor2);
//                        SMCFR.drawStringWithShadow("b" + BozoWare.getInstance().CLIENT_VERSION, 55, 4, -1);
//                    }
                    break;
                case "Basic":
                    firstLetter = theWatermark.getPropertyValue().charAt(0);
                    lettersBeyond = theWatermark.getPropertyValue().replaceFirst(String.valueOf(firstLetter), "");
                    if(cfontBool.getPropertyValue()){
                        MFR.drawStringWithShadow(String.valueOf(firstLetter) + ChatFormatting.WHITE + lettersBeyond, 4, 6, bozoColor2);
                    } else {
                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(firstLetter) + ChatFormatting.WHITE + lettersBeyond, 4, 5, bozoColor2);
                    }
//                    Gui.drawRect(4, 15, 75, 75, 0x40000000);
                    break;
                case "Onetap":
//                    if (cfontBool.getPropertyValue()) {
//                        Gui.drawRect(2, 2, (float) (2 + BozoWare.getInstance().getFontManager().smallCSGORenderer.getStringWidth(OTWatermark) + 4), 2 + MFR.getHeight() + 4, 0x30202020);
//                        Gui.drawRect(2, 2, (float) (2 + BozoWare.getInstance().getFontManager().smallCSGORenderer.getStringWidth(OTWatermark) + 4), 3, bozoColor2);
                    BloomUtil.drawAndBloom(() -> BlurUtil.blurArea((double) 2, (double) 2, BozoWare.getInstance().getFontManager().smallCSGORenderer.getStringWidth(OTWatermark) + 4, MFR.getHeight() + 5));
                    RenderUtil.glHorizontalGradientQuad(2, 2, (float) (BozoWare.getInstance().getFontManager().smallCSGORenderer.getStringWidth(OTWatermark) + 4), 1, bozoColor2, bozoColor);
                    BozoWare.getInstance().getFontManager().smallCSGORenderer.drawStringWithShadow(OTWatermark, 4, 6.5, -1);
//                    BloomUtil.drawAndBloom(() -> BlurUtil.blurArea(2, 2, BozoWare.getInstance().getFontManager().smallCSGORenderer.getStringWidth(OTWatermark) + 4, MFR.getHeight() + 4));
//                    } else {
//                        BlurUtil.blurArea(2, 2, mc.fontRendererObj.getStringWidth(OTWatermark) + 4, mc.fontRendererObj.FONT_HEIGHT + 4 + 5);
//                        Gui.drawRect(2, 2, (float) (2 + mc.fontRendererObj.getStringWidth(OTWatermark) + 4), 5 + mc.fontRendererObj.FONT_HEIGHT + 4, 0x30202020);
//                        RenderUtil.glHorizontalGradientQuad(2, 2, (float) (mc.fontRendererObj.getStringWidth(OTWatermark) + 4), 1, bozoColor2, bozoColor);
//                        mc.fontRendererObj.drawStringWithShadow(OTWatermark, 4, 6.5F, -1);
//                    }
                    break;

            }

            AtomicInteger moduleCounter = new AtomicInteger();
            if(cfontBool.getPropertyValue()) {
                offset = 0;
                BozoWare.getInstance().getModuleManager().getModules(false).forEach(module -> {
                    if(colorMode.getPropertyValue().equals(colorModes.Category)){
                        switch (module.getModuleCategory()){
                            case VISUAL:
                                bozoColor = 0xFFFFFF99;
                                bozoColor2 = 0xFFFFFF00;
                                break;
                            case PLAYER:
                                bozoColor = 0xFFFF5050;
                                bozoColor2 = 0xFFFF5050;
                                break;
                            case WORLD:
                                bozoColor = 0xFF009030;
                                bozoColor2 = 0xFF009030;
                                break;
                            case COMBAT:
                                bozoColor = 0xFFFF0020;
                                bozoColor2 = 0xFFFF0020;
                                break;
                            case MOVEMENT:
                                bozoColor = 0xFF8040FF;
                                bozoColor2 = 0xFF8040FF;
                                break;
                            default:
                                bozoColor = 0xFF005080;
                                bozoColor2 = 0xFF005080;
                                break;
                        }
                    }
                    if(!lineBool.getPropertyValue())
                        posX = (float) ((sr.getScaledWidth() - (module.isModuleToggled() ? 1.5 + offSetValue.getPropertyValue() : 0)) - MFR.getStringWidth(module.getModuleDisplayName()) * module.animation.getOutput());
                    else
                        posX = (float) ((sr.getScaledWidth() - (module.isModuleToggled() ? 3.5 + offSetValue.getPropertyValue() : 0)) - MFR.getStringWidth(module.getModuleDisplayName()) * module.animation.getOutput());

                    posY = 4 + offSetValue.getPropertyValue() + (offset * (13 - spacingValue.getPropertyValue()));


                    if(module.isModuleToggled()){
                        if(bgBool.getPropertyValue()){
                            RenderUtil.drawRoundedRect((float) posX - 1, (float) posY - 3, sr.getScaledWidth() - offSetValue.getPropertyValue(), (float) posY + 10 - spacingValue.getPropertyValue(), 1, new Color(0, 0, 0, bgOpacity.getPropertyValue()).getRGB());
                        }
                        switch (colorMode.getPropertyValue()){
                            case CustomFade:
                                bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                                bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).darker().darker().getRGB();
                                bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                                break;
                            case Gradient:
                                bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                bozoColor2 = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).darker().darker().getRGB();
                                bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                break;
                            case Rainbow:
                                bozoColor = getRainbow(3000, (int) (posY * 5));
                                bozoColorDarker = new Color(getRainbow(3000, (int) (posY * 5))).darker().darker().getRGB();
                                bozoColor2 = getRainbow(3000, (int) (posY * 5));
                                bozoColorX = getRainbow(3000, (int) (posY * 5));
                                break;
                            default:
//                            bozoColor = -1;
//                            bozoColorX = -1;
                                break;
                        }
                        if(lineBool.getPropertyValue())
                        switch (lineMode.getPropertyValue()) {
                            case Right:
                                Gui.drawRectWithWidth(sr.getScaledWidth() - offSetValue.getPropertyValue() - 1, posY - 3, 1, 13 - spacingValue.getPropertyValue(), bozoColor);
                                break;

                            case Left:
//                                BlurUtil.blur(2);
                                Gui.drawRectWithWidth((int) posX - 1.5, posY - 3, 2, 13 - spacingValue.getPropertyValue(), bozoColor);
                                break;
                        }
                    }
                    module.animation.setDirection(module.isModuleToggled() ? Direction.FORWARDS : Direction.BACKWARDS);
                    MFR.drawStringWithShadow(module.getModuleDisplayName(), posX + 1, posY - 1.25, bozoColor);
                    offset += module.animation.getOutput();



                    switch (colorMode.getPropertyValue()) {
                        case CustomFade:
                            bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                            bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                            bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()).darker().darker(), new Color(getColor2NotAsDark())).darker().darker().getRGB();
                            break;
                        case Gradient:
                            bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()).darker().darker(), new Color(getColor2Gradient())).darker().darker().getRGB();
                            bozoColor2 = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            break;
                        case Rainbow:
                            bozoColor = getRainbow(3000, (int) (posY * 5));
                            bozoColorDarker = new Color(getRainbow(3000, (int) (posY * 5))).darker().darker().getRGB();
                            bozoColor2 = getRainbow(3000, (int) (posY * 5));
                            bozoColorX = getRainbow(3000, (int) (posY * 5));
                            break;
                    }
//                    if(module.isModuleToggled()) {
                    moduleCounter.getAndIncrement();
                });
                Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();
                if (collection.isEmpty()) return;
                collection = collection.stream().sorted(Comparator.comparingInt(e -> ((PotionEffect) e).getEffectName().length())).collect(Collectors.toList());

                final int fontSize = 16, textureSize = 12;
                int y = sr.getScaledHeight() - 15; // Offset for watermark

                for (PotionEffect potioneffect : collection) {
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                    String potionName = I18n.format(potion.getName());

                    if (potioneffect.getAmplifier() == 1) {
                        potionName += " " + I18n.format("enchantment.level.2");
                    } else if (potioneffect.getAmplifier() == 2) {
                        potionName += " " + I18n.format("enchantment.level.3");
                    } else if (potioneffect.getAmplifier() == 3) {
                        potionName += " " + I18n.format("enchantment.level.4");
                    }
//            (potioneffect.getDuration()/20)*1000
                    potionName += " (" + (potioneffect.getDuration()/20)*1000 / (60 * 1000) % 60 + "m " + (potioneffect.getDuration()/20)*1000 / 1000 % 60 + "s)";

                    GL11.glPushMatrix();
                    GL11.glTranslated(sr.getScaledWidth() - 20, y, 0);
//                GL11.glScaled(0.8, 0.8, 0);
                    GL11.glTranslated(-(sr.getScaledWidth() - 20), -y, 0);

                    MFR.drawStringWithShadow(potionName, sr.getScaledWidth() - 5 - mc.fontRendererObj.getStringWidth(potionName), y + 3, -1);
                    GL11.glPopMatrix();

                    if (potion.hasStatusIcon()) {
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        int i1 = potion.getStatusIconIndex();
                        GL11.glPushMatrix();
                        GL11.glTranslated(sr.getScaledWidth() - 17, y, 0);
                        GL11.glScaled(0.666666667D, 0.666666667D, 1.0);
                        GL11.glTranslated(-(sr.getScaledWidth() - 17), -y, 0);
                        mc.ingameGUI.drawTexturedModalRect(sr.getScaledWidth() - 17, y, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                        GL11.glPopMatrix();
                    }

                    y -= textureSize;
                }
            } else {
                offset = 0;
                BozoWare.getInstance().getModuleManager().getModules(true).forEach(module -> {
                    if(colorMode.getPropertyValue().equals(colorModes.Category)){
                        switch (module.getModuleCategory()){
                            case VISUAL:
                                bozoColor = 0xFFFFFF99;
                                bozoColor2 = 0xFFFFFF00;
                                break;
                            case PLAYER:
                                bozoColor = 0xFFFF5050;
                                bozoColor2 = 0xFFFF5050;
                                break;
                            case WORLD:
                                bozoColor = 0xFF009030;
                                bozoColor2 = 0xFF009030;
                                break;
                            case COMBAT:
                                bozoColor = 0xFFFF0020;
                                bozoColor2 = 0xFFFF0020;
                                break;
                            case MOVEMENT:
                                bozoColor = 0xFF8040FF;
                                bozoColor2 = 0xFF8040FF;
                                break;
                            default:
                                bozoColor = 0xFF005080;
                                bozoColor2 = 0xFF005080;
                                break;
                        }
                    }
                    if(!lineBool.getPropertyValue())
                    posX = (float) ((sr.getScaledWidth() - (module.isModuleToggled() ? 3 + offSetValue.getPropertyValue() : 0)) - mc.fontRendererObj.getStringWidth(module.getModuleDisplayName()) * module.animation.getOutput());
                    else
                        posX = (float) ((sr.getScaledWidth() - (module.isModuleToggled() ? 5 + offSetValue.getPropertyValue() : 0)) - mc.fontRendererObj.getStringWidth(module.getModuleDisplayName()) * module.animation.getOutput());
                    posY = 4 + offSetValue.getPropertyValue() + (offset * (13 - spacingValue.getPropertyValue()));

                    if(module.isModuleToggled()){
                        if(bgBool.getPropertyValue()){
                            RenderUtil.drawRoundedRect((float) posX, (float) posY - 3, sr.getScaledWidth() - offSetValue.getPropertyValue() - 1, (float) posY + 10 - spacingValue.getPropertyValue(), 1, new Color(0, 0, 0, bgOpacity.getPropertyValue()).getRGB());
                        }
                        switch (colorMode.getPropertyValue()){
                            case CustomFade:
                                bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                                bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).darker().darker().getRGB();
                                bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                                break;
                            case Gradient:
                                bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                bozoColor2 = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).darker().darker().getRGB();
                                bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                                break;
                            case Rainbow:
                                bozoColor = getRainbow(3000, (int) (posY * 5));
                                bozoColorDarker = new Color(getRainbow(3000, (int) (posY * 5))).darker().darker().getRGB();
                                bozoColor2 = getRainbow(3000, (int) (posY * 5));
                                bozoColorX = getRainbow(3000, (int) (posY * 5));
                                break;
                            default:
//                            bozoColor = -1;
//                            bozoColorX = -1;
                                break;
                        }
                        if(lineBool.getPropertyValue())
                        switch (lineMode.getPropertyValue()) {
                            case Right:
                                Gui.drawRectWithWidth(sr.getScaledWidth() - offSetValue.getPropertyValue() - 1, posY - 3, 1, 13 - spacingValue.getPropertyValue(), bozoColor);
                                break;

                            case Left:
//                                BlurUtil.blur(2);
                                Gui.drawRectWithWidth((int) posX - 1.4, posY - 3, 2, 13 - spacingValue.getPropertyValue(), bozoColor);
                                break;
                        }
                    }
                    module.animation.setDirection(module.isModuleToggled() ? Direction.FORWARDS : Direction.BACKWARDS);
                    mc.fontRendererObj.drawStringWithShadow(module.getModuleDisplayName(), posX + 2, (float) (posY - 1.5), bozoColor);
                    offset += module.animation.getOutput();



                    switch (colorMode.getPropertyValue()) {
                        case CustomFade:
                            bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                            bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2NotAsDark())).getRGB();
                            bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()).darker().darker(), new Color(getColor2NotAsDark())).darker().darker().getRGB();
                            break;
                        case Gradient:
                            bozoColor = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            bozoColorDarker = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()).darker().darker(), new Color(getColor2Gradient())).darker().darker().getRGB();
                            bozoColor2 = ColorUtil.interpolateColorsDynamic(3, moduleCounter.get() * 15, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            bozoColorX = ColorUtil.interpolateColorsDynamic(3, sr.getScaledWidth() * Integer.MAX_VALUE, new Color(getColor1()), new Color(getColor2Gradient())).getRGB();
                            break;
                        case Rainbow:
                            bozoColor = getRainbow(3000, (int) (posY * 5));
                            bozoColorDarker = new Color(getRainbow(3000, (int) (posY * 5))).darker().darker().getRGB();
                            bozoColor2 = getRainbow(3000, (int) (posY * 5));
                            bozoColorX = getRainbow(3000, (int) (posY * 5));
                            break;
                    }
//                    if(module.isModuleToggled()) {
                    moduleCounter.getAndIncrement();
                });
                Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();
                if (collection.isEmpty()) return;
                collection = collection.stream().sorted(Comparator.comparingInt(e -> ((PotionEffect) e).getEffectName().length())).collect(Collectors.toList());

                final int fontSize = 16, textureSize = 12;
                int y = sr.getScaledHeight() - 15; // Offset for watermark

                for (PotionEffect potioneffect : collection) {
                    Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                    String potionName = I18n.format(potion.getName());

                    if (potioneffect.getAmplifier() == 1) {
                        potionName += " " + I18n.format("enchantment.level.2");
                    } else if (potioneffect.getAmplifier() == 2) {
                        potionName += " " + I18n.format("enchantment.level.3");
                    } else if (potioneffect.getAmplifier() == 3) {
                        potionName += " " + I18n.format("enchantment.level.4");
                    }
//            (potioneffect.getDuration()/20)*1000
                    potionName += " (" + (potioneffect.getDuration()/20)*1000 / (60 * 1000) % 60 + "m " + (potioneffect.getDuration()/20)*1000 / 1000 % 60 + "s)";

                    GL11.glPushMatrix();
                    GL11.glTranslated(sr.getScaledWidth() - 20, y, 0);
//                GL11.glScaled(0.8, 0.8, 0);
                    GL11.glTranslated(-(sr.getScaledWidth() - 20), -y, 0);

                    mc.fontRendererObj.drawStringWithShadow(potionName, sr.getScaledWidth() - 20 - mc.fontRendererObj.getStringWidth(potionName), y + 3, -1);
                    GL11.glPopMatrix();

                    if (potion.hasStatusIcon()) {
                        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        int i1 = potion.getStatusIconIndex();
                        GL11.glPushMatrix();
                        GL11.glTranslated(sr.getScaledWidth() - 17, y, 0);
                        GL11.glScaled(0.666666667D, 0.666666667D, 1.0);
                        GL11.glTranslated(-(sr.getScaledWidth() - 17), -y, 0);
                        mc.ingameGUI.drawTexturedModalRect(sr.getScaledWidth() - 20, y, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                        GL11.glPopMatrix();
                    }

                    y -= textureSize;
                }
            }
        });
        this.setModuleToggled(true);
    }



    public static HUD getInstance() {
        return (HUD) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(HUD.class);
    }
    public enum colorModes {
        CustomFade,
        Gradient,
        Rainbow,
        Category,
    }
    public enum watermarkModes {
        Bozoware,
        CSGO,
        Basic,
        idkwhat2call,
        Onetap,
    }
    public enum lineModes {
        Right,
        Left,
    }
    public static int wrapAngleToDirection(final float yaw, final int zones) {
        int angle = (int) (yaw + 360 / (2 * zones) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }
    public void drawPotionEffects(int color) {
        Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();
        if (collection.isEmpty()) return;
        collection = collection.stream().sorted(Comparator.comparingInt(e -> ((PotionEffect) e).getEffectName().length()).reversed()).collect(Collectors.toList());

        final int fontSize = 16, textureSize = 12;

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int y = sr.getScaledHeight() - 15; // Offset for watermark
        for (PotionEffect potioneffect : collection) {
            Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            String potionName = I18n.format(potion.getName());

            if (potioneffect.getAmplifier() == 1) {
                potionName += " " + I18n.format("enchantment.level.2");
            } else if (potioneffect.getAmplifier() == 2) {
                potionName += " " + I18n.format("enchantment.level.3");
            } else if (potioneffect.getAmplifier() == 3) {
                potionName += " " + I18n.format("enchantment.level.4");
            }
//            (potioneffect.getDuration()/20)*1000
            potionName += " (" + (potioneffect.getDuration()/20)*1000 / (60 * 1000) % 60 + "m " + (potioneffect.getDuration()/20)*1000 / 1000 % 60 + "s)";

            GL11.glPushMatrix();
            GL11.glTranslated(sr.getScaledWidth() - 20, y, 0);
//                GL11.glScaled(0.8, 0.8, 0);
            GL11.glTranslated(-(sr.getScaledWidth() - 20), -y, 0);
            mc.fontRendererObj.drawStringWithShadow(potionName, sr.getScaledWidth() - 20 - mc.fontRendererObj.getStringWidth(potionName), y + 3, -1);
            GL11.glPopMatrix();

            if (potion.hasStatusIcon()) {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                int i1 = potion.getStatusIconIndex();
                GL11.glPushMatrix();
                GL11.glTranslated(sr.getScaledWidth() - 17, y, 0);
                GL11.glScaled(0.666666667D, 0.666666667D, 1.0);
                GL11.glTranslated(-(sr.getScaledWidth() - 17), -y, 0);
                mc.ingameGUI.drawTexturedModalRect(sr.getScaledWidth() - 17, y, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                GL11.glPopMatrix();
            }

            y -= textureSize;
        }
    }
//    public enum bgModes {
//        Shadow,
//        Blur
//    }
//    public enum arrayListPos{
//        Top,
//        Bottom
//    }
}