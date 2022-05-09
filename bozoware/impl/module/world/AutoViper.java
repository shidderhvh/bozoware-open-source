package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.HashMap;

/**
 * Copyright Pablo Matias 2022
 * None of this code to be reused without my written permission
 * Intellectual Rights owned by Pablo Matias
 **/
@ModuleData(moduleName = "Auto Viper", moduleCategory = ModuleCategory.WORLD)
public class AutoViper extends Module {
  private String scrambledMessage = null;
  private String unscrambledMessage = null;
  private final HashMap<String, String> decoder = new HashMap<>();
  @EventListener
  EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
  @EventListener
  EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;


  public AutoViper() {
    onUpdatePositionEvent = (e -> {
      if (unscrambledMessage != null && scrambledMessage != null) {
        mc.thePlayer.sendChatMessage(unscrambledMessage);
      } else if (unscrambledMessage != null) {
        mc.thePlayer.sendChatMessage(unscrambledMessage);
      }
    });
    onPacketReceiveEvent = (event -> {
      if (event.getPacket() instanceof S02PacketChat) {
        S02PacketChat S02 = (S02PacketChat) event.getPacket();
        String message = S02.getChatComponent().getUnformattedText();
        if (message.contains("[Chat Games] Hover for the word to unscramble")) {
          scrambledMessage = S02.getChatComponent()
                  .getChatStyle()
                  .getChatHoverEvent()
                  .getValue()
                  .getUnformattedTextForChat();
        } else if (message.contains("[Chat Games] Hover for the word to type")) {
          String word = S02.getChatComponent()
                  .getChatStyle()
                  .getChatHoverEvent()
                  .getValue()
                  .getUnformattedTextForChat();
          unscrambledMessage = word;
          unscramble();
        } else if (message.contains("unscrambled") && message.contains("[Chat Games]")) {
          String[] line = message.split(" ");
          if (scrambledMessage != null && unscrambledMessage == null) {
            decoder.put(scrambledMessage, line[line.length - 1]);
            // add chat message here if you want to debug what gets added
            scrambledMessage = null;
          }
        }
      }
    });
  }

  private void unscramble() {
    if (scrambledMessage == null) return;
    String scrambled = scrambledMessage;
    if (decoder.containsKey(scrambled)) {
      unscrambledMessage = decoder.get(scrambled);
    }
  }
}
