package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleData(moduleName = "KillSays", moduleCategory = ModuleCategory.PLAYER)
public class KillSays extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private int messageIndex;
    private final String[] MESSAGES = new String[]{"%s should've used BozoWare", "%s, bozoware > ur shiddy paste", "%s ask me what dn is. I dare you", "%s COPE!", "sorry nn, this bypass value is exclusive", "SHIDDER HVH > YOU | Quit.#1706 UID 0000 ON KCATS JAJAJA"};


    public KillSays(){
        onPacketReceiveEvent = (event -> {
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat)event.getPacket();
                String text = packet.getChatComponent().getUnformattedText();

                if (text.contains("by " + Minecraft.getMinecraft().thePlayer.getName()) && !text.contains(":")) {
                    if (this.messageIndex >= MESSAGES.length) {
                        this.messageIndex = 0;
                    }

                    Minecraft.getMinecraft().thePlayer.sendChatMessage(String.format(MESSAGES[this.messageIndex], text.split(" ")[0]));
                    ++this.messageIndex;
                }
            }

        });
    }

}
