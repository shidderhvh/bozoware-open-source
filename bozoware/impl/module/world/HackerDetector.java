package bozoware.impl.module.world;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.player.MovementUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.combat.AntiBot;
import bozoware.impl.module.player.AntiVoid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;

@ModuleData(moduleName = "Hacker Detect (WIP)", moduleCategory = ModuleCategory.WORLD)
public class HackerDetector extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> event;

    public static List<Integer> flaggedPlayers = new ArrayList<Integer>();

    TimerUtil flyStopWatch = new TimerUtil();
    boolean possibleFlying = false;

    public HackerDetector(){
        onModuleEnabled = () -> {
            possibleFlying = false;
            flyStopWatch.reset();
        };
        event = (e -> {
            for(Entity en : mc.theWorld.loadedEntityList){
                if(en instanceof EntityOtherPlayerMP && !flaggedPlayers.contains(en.getEntityId()) && !AntiBot.botList.contains(en.getEntityId())){
                    if(en.motionY == 0){
                        for (int i = 0; i <= 128; i++) {
                            if (mc.theWorld.getCollidingBoundingBoxes(en, en.getEntityBoundingBox().offset(0.0D, -i, 0.0D)).isEmpty()) {
                                possibleFlying = true;
                            } else {
                                possibleFlying = false;
                            }
                            if(flyStopWatch.hasReached(2500L) && possibleFlying && mc.thePlayer.isMoving()){
                                if(!flaggedPlayers.contains(en.getEntityId()))
                                    BozoWare.getInstance().chat(en.getName() + " might be using fly");
                                flaggedPlayers.add(en.getEntityId());
                            }
                        }
                    } else {
                        flyStopWatch.reset();
                    }
                }
                if(en.rotationPitch > 90 || en.rotationPitch < -90){
                    flaggedPlayers.add(en.getEntityId());
                    BozoWare.getInstance().chat(en.getName() + " might be using scaffold (bad rotations L)");
                }
            }
        });
    }
    public static double getBaseMoveSpeed(EntityOtherPlayerMP en) {
        double baseSpeed = 0.2875;
        if (en != null)
            if (en.isPotionActive(Potion.moveSpeed)) {
                final int amplifier = en.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        return baseSpeed;
    }
    public static double getMoveSpeed(EntityOtherPlayerMP en) {
        return Math.sqrt(en.motionX * en.motionX + en.motionZ * en.motionZ);
    }
}
