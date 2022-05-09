package bozoware.base.api;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {

    public static boolean running = true;
    private static long created = 0;

    public static void start() {
        created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {

            @Override
            public void apply(DiscordUser user) {
                System.out.println("Welcome " + user.username + "#" + user.discriminator);
                update("Loading Bozoware", "");
            }

        }).build();

        DiscordRPC.discordInitialize("913142366003687485", handlers, true);

        new Thread("DiscordRPC Callback") {

            @Override
            public void run() {
                while(running){
                    DiscordRPC.discordRunCallbacks();
                }
            }

        }.start();

    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public static void update(String firstLine, String secondLine) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
        {
            b.setBigImage("bozoheart512x", "Hello Bozo!");
            b.setSmallImage("bozoblack", "by Kobley, Posk, and Shidder");
            b.setDetails(firstLine);
            b.setStartTimestamps(created);

        }

        DiscordRPC.discordUpdatePresence(b.build());
    }


    public void onEnable() {
        running = true;
        DiscordRPC.discordRunCallbacks();
    }


    public void onDisable() {
        running = false;
        DiscordRPC.discordShutdown();
    }

}
