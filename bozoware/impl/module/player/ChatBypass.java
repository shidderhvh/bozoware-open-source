package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.module.world.AntiNovoline;
import bozoware.impl.property.EnumProperty;
import net.minecraft.network.play.client.C01PacketChatMessage;

@ModuleData(moduleName = "Chat Bypass", moduleCategory = ModuleCategory.PLAYER)
public class ChatBypass extends Module {

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    private final EnumProperty<bypassModes> bypassMode = new EnumProperty<>("Mode", bypassModes.Invis, this);


    public ChatBypass() {
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        onPacketSendEvent = (e -> {
            if(e.getPacket() instanceof C01PacketChatMessage){
                C01PacketChatMessage c01 = (C01PacketChatMessage) e.getPacket();
                String firstLetter = c01.getMessage().charAt(0) + "";
                if(firstLetter.equals("/"))
                    return;
                if(firstLetter.equals("."))
                    return;
                e.setCancelled(true);
                char addChar;
                final char[] array = c01.getMessage().toCharArray();
                stringBuilder.delete(0, stringBuilder.toString().length());
                switch (bypassMode.getPropertyValue()) {
                    case Invis:
                    for (int i = 0; i < c01.getMessage().length(); i++) {
                        addChar = array[i];
                        stringBuilder.append(addChar).append("\u200B");
                    }
                    break;
                    case Dots:
                        for (int i = 0; i < c01.getMessage().length(); i++) {
                            addChar = array[i];
                            stringBuilder.append(addChar).append(".");
                        }
                }
                Wrapper.sendPacketDirect(new C01PacketChatMessage(stringBuilder.toString()));
            }
        });
    }
    public enum bypassModes {
        Invis,
        Dots
    }
}
