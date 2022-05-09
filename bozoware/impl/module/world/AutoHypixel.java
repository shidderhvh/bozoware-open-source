package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import bozoware.impl.property.BooleanProperty;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

@ModuleData(moduleName = "Auto Hypixel", moduleCategory = ModuleCategory.WORLD)
public class AutoHypixel extends Module {
    private final BooleanProperty antiLimbo = new BooleanProperty("Limbo Quit", true, this);
    private final BooleanProperty autoLeave = new BooleanProperty("Auto Leave on Ban", true, this);

    TimerUtil timer = new TimerUtil();

    public static boolean BOZO = true;
    public int time = (int) System.currentTimeMillis();

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onLoadWorldEvent;

    public AutoHypixel(){
        onLoadWorldEvent = (e -> {

        });

//        onModuleEnabled = () -> {
//            if(apiKey.getPropertyValue()) {
//                mc.thePlayer.sendChatMessage("/api new");
//            }
//        };

        onPacketReceiveEvent = (e -> {
            if(e.getPacket() != null)
            if(e.getPacket() instanceof S02PacketChat) {
                S02PacketChat s02 = (S02PacketChat) e.getPacket();
                if (s02.getChatComponent().getUnformattedText() != null) {
                    if (antiLimbo.getPropertyValue()) {
                            if (s02.getChatComponent().getUnformattedText().contains("You were spawned in Limbo.") || s02.getChatComponent().getUnformattedText().contains("/limbo for more information.")) {
                                    mc.thePlayer.sendChatMessage("/play solo_insane");
//                                NotificationManager.show(new Notification(NotificationType.INFO, "Auto Hypixel", "Automatically sent you to a new game", 1F));

                            }
                        }
                    if (autoLeave.getPropertyValue()) {
                        if (s02.getChatComponent().getUnformattedText().contains("A player has been removed")) {
                                mc.thePlayer.sendChatMessage("/play solo_insane");
//                            NotificationManager.show(new Notification(NotificationType.INFO, "Auto Hypixel", "Automatically sent you to a new game", 1F));
                            }
                        }
                    }
            }
            if(e.getPacket() != null)
            if(e.getPacket() instanceof S45PacketTitle) {
                S45PacketTitle s45 = (S45PacketTitle) e.getPacket();
                if (!(s45.getMessage() == null)) {
                    String message = s45.getMessage().getUnformattedText();
                    if (message.contains("YOU DIED!") || message.contains("GAME END") || message.contains("VICTORY!") || message.contains("You are now a spectator!")) {
//                        NotificationManager.show(new Notification(NotificationType.INFO, "Auto Hypixel", "Automatically sent you to a new game", 1F));
                        mc.thePlayer.sendChatMessage("/play solo_insane");
                    }
                }
            }
        });

        onRender2DEvent = (e -> {
            if(BOZO == false){
                if(this.timer.hasReached(3000L)) {
//                    BOZO = true;
//                    timer.reset();
                } else {
//                    Gui.drawRect(sr.getScaledWidth() + 400, sr.getScaledHeight() + 225, sr.getScaledWidth() + 600, sr.getScaledHeight() + 400, 0x40000000);
//                    mc.fontRendererObj.drawStringWithShadow("Sent to new game!", sr.getScaledWidth() + 425, sr.getScaledHeight() + 240, -1);
                }
            }
        });
    }

}
