package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;


@ModuleData(moduleName = "FastUse", moduleCategory = ModuleCategory.PLAYER)
public class FastUse extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.Vanilla, this);

    public FastUse() {
        setModuleSuffix(Mode.getPropertyValue().toString());
        onPacketReceiveEvent = (xd -> {

        });
        onUpdatePositionEvent = (e -> {
            switch(Mode.getPropertyValue().toString()){
                case "Vanilla":
                    if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood || mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAppleGold) {
                        if (Mouse.isButtonDown(1)) {
                            for (int i = 0; i < 40; i++) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                            }
                        }
                    }
                    break;
                case "BlocksMC": //flags after some use
                    if (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && mc.thePlayer.onGround) {
                        for(int i = 5; i > 0; i--){
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.ticksExisted % 2 == 0));
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.ticksExisted % 2 == 0));
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.ticksExisted % 2 == 0));
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.ticksExisted % 2 == 0));
                        }
                    }
                    break;
                case "NCP":
                    if (mc.thePlayer.isUsingItem() && !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) && !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow) && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && mc.thePlayer.getItemInUseDuration() >= 16) {
                        for (int i = 0; i < 40; ++i) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                        }
                        mc.thePlayer.stopUsingItem();
                    }
                    break;
                case "HvH":
                    if (mc.gameSettings.keyBindUseItem.pressed || mc.thePlayer.isUsingItem() && !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) && !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow) && mc.thePlayer.getItemInUseDuration() >= 0) {
                        if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood || mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAppleGold) {
                            mc.rightClickDelayTimer = -0;
                            for (int i = 0; i < 40; i++) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                            }
                            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            mc.thePlayer.stopUsingItem();
                        }
                    }
                    break;
            }
        });
        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }

    private enum Modes{
        Vanilla,
        BlocksMC,
        NCP,
        HvH
    }
}
