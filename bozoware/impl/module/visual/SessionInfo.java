package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.visual.BlurUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

import java.util.Objects;

@ModuleData(moduleName = "SessionInfo", moduleCategory = ModuleCategory.VISUAL)
public class SessionInfo extends Module {

    public static int wins = 0;
    public static int bruh = (int) System.currentTimeMillis();
    public static int kills = 0;
    public static String name;

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onLoadWorldEvent;

    public SessionInfo() {
        onModuleEnabled = () -> {
            name = mc.thePlayer.getName();
        };

        onLoadWorldEvent = (e -> {

        });

        onPacketReceiveEvent = (rece -> {
            if(rece.getPacket() != null) {
                if (rece.getPacket() instanceof S45PacketTitle) {
                    S45PacketTitle s45 = (S45PacketTitle) rece.getPacket();
                    if (!(s45.getMessage() == null)) {
                        String message = s45.getMessage().getUnformattedText();
                        if (message.contains("VICTORY!")) {
                            wins = wins + 1;
                        }
                    }
                }

                if (rece.getPacket() instanceof S02PacketChat) {
                    S02PacketChat s02 = (S02PacketChat) rece.getPacket();
                    if (s02.getChatComponent().getUnformattedText() != null) {
                        if (((S02PacketChat) rece.getPacket()).getChatComponent().getUnformattedText().contains("by " + mc.thePlayer.getName()) && !(((S02PacketChat) rece.getPacket()).getChatComponent().getUnformattedText().contains(":"))) {
                            kills = kills + 1;
                        }
                    }
                }
            }
        });
        onRender2DEvent = (e -> {
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            if(!(Objects.equals(mc.thePlayer.getName(), name))){
                bruh = (int) System.currentTimeMillis();
                wins = 0;
                kills = 0;
                name = mc.thePlayer.getName();
            }
//            Gui.drawRect(2, 33, 98, 75, 0xff1f1f1f);

            RenderUtil.drawSmoothRoundedRect(2, 33, 100, 78, 15, 0x70171717);

            RenderUtil.drawRoundedOutline(2, 33, 100, 78, 0xff000000, 3, 2);
            RenderUtil.drawRoundedOutline(2, 33, 100, 78, 0xff3c3c3c, 2, 2);
            RenderUtil.drawRoundedOutline(2, 33, 100, 78, 0xff282828, 0.8F, 2);
            BlurUtil.blurArea(2, 33, 98, 45);

//            RenderUtil.glHorizontalGradientQuad(3.75, 34, 50, 2, 0xff37b1da, 0xffc862b5);
//            RenderUtil.glHorizontalGradientQuad(48, 34, 50.75, 2, 0xffc862b5, 0xffcce236);

            BozoWare.getInstance().getFontManager().smallCSGORenderer.drawStringWithShadow("Session Info", 30, 39, -1);

            int diff = (int) ((int) System.currentTimeMillis() - bruh);
            BozoWare.getInstance().getFontManager().smallCSGORenderer.drawStringWithShadow("Session Time: " + diff / (60 * 60 * 1000) % 24 + "h " + diff / (60 * 1000) % 60 + "m " + diff / 1000 % 60 + "s", 7, 49, -1);
            BozoWare.getInstance().getFontManager().smallCSGORenderer.drawStringWithShadow("Wins: " + wins, 7, 59, -1);
            BozoWare.getInstance().getFontManager().smallCSGORenderer.drawStringWithShadow("Kills: " + kills, 7, 69, -1);
//            BlurUtil.blur(2);
//            BlurUtil.blur(2);
        });
    }
}