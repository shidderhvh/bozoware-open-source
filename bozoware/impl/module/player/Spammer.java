package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.command.SpamCommand;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.network.play.client.C01PacketChatMessage;

@ModuleData(moduleName = "Spammer", moduleCategory = ModuleCategory.PLAYER)
public class Spammer extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> event;

    private final ValueProperty<Long> spamDelay = new ValueProperty<>("Spamm Delay", 3000L, 1L, 10000L, this);
    private final BooleanProperty randomCharacters = new BooleanProperty("Random Chars", true, this);

    static TimerUtil spamTimer = new TimerUtil();

    public static String spamMessage = SpamCommand.spamMessage;

    public Spammer(){
        onModuleEnabled = () -> spamTimer.reset();
        event = (e -> {
           if(e.isPre){
               spamMessage = SpamCommand.spamMessage;
                if(spamTimer.hasReached((long) spamDelay.getPropertyValue())){
                    String bozo = "";
                    String numba[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

                    for (int i = 0; i < 13; i++){
                        bozo += numba[(int) Math.floor(Math.random() * numba.length)];;
                    }
                    mc.thePlayer.sendChatMessage(randomCharacters.getPropertyValue() ? "[" + bozo + "] " + spamMessage : spamMessage + (randomCharacters.getPropertyValue() ? " [" + bozo + "] " : ""));
                    spamTimer.reset();

                }
//               spamTimer.reset();
           }
        });
    }
}
