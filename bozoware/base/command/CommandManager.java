package bozoware.base.command;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.util.Wrapper;
import bozoware.impl.command.*;
import bozoware.impl.event.player.SendChatMessageEvent;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;

public class CommandManager {

    private final String commandPrefix = ".";

    @EventListener
    EventConsumer<SendChatMessageEvent> onSendChatMessageEvent;

    private final ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        this.onSendChatMessageEvent = (chatEvent -> {
            if (chatEvent.getMessage().startsWith(commandPrefix)) {
                chatEvent.setCancelled(true);
                String afterPrefix = chatEvent.getMessage().substring(1);
                String[] afterPrefixSplit = afterPrefix.split(" ");
                for (Command command : commands) {
                    for (String alias : command.getAliases()) {
                        if (afterPrefixSplit[0].equalsIgnoreCase(alias)) {
                            try {
                                command.execute(afterPrefixSplit);
                            }
                            catch (CommandArgumentException e) {
                                Wrapper.getPlayer().addChatMessage(new ChatComponentText(e.getMessage()));
                            }
                            break;
                        }
                    }
                }
            }
        });
        registerCommands();
        BozoWare.getInstance().getEventManager().subscribe(this);
    }

    private void registerCommands() {
        commands.add(new VClipCommand());
        commands.add(new ToggleCommand());
        commands.add(new CopyNameCommand());
        commands.add(new BindCommand());
        commands.add(new BozoCommand());
        commands.add(new HelpCommand());
        commands.add(new TPCommand());
        commands.add(new ConfigCommand());
        commands.add(new SusCommand());
        commands.add(new BanCommand());
        commands.add(new HideCommand());
        commands.add(new SpamCommand());
        commands.add(new WatermarkCommand());
    }
}
