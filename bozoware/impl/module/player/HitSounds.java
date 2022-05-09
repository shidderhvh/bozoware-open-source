package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.combat.Aura;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

@ModuleData(moduleName = "Hit Sounds", moduleCategory = ModuleCategory.PLAYER)
public class HitSounds extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public HitSounds(){
        onUpdatePositionEvent = (e -> {
            if (Aura.target != null && Aura.target.hurtTime > 9) {
                final double x = Aura.target.posX;
                final double y = Aura.target.posY;
                final double z = Aura.target.posZ;
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("assets/minecraft/BozoWare/Sounds/skeet.ogg"), (float) x, (float) y, (float) z));
            }
        });
    }

}
