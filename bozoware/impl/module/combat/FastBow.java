package bozoware.impl.module.combat;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import bozoware.visual.screens.dropdown.GuiDropDown;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;

@ModuleData(moduleName = "FastBow", moduleCategory = ModuleCategory.COMBAT)
public class FastBow extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.Basic, this);
    private final ValueProperty<Integer> Packets = new ValueProperty<>("Packets", 25, 10, 100, this);

    public FastBow() {
        onModuleEnabled = () -> {
        };
        onUpdatePositionEvent = (e -> {
            switch (Mode.getPropertyValue()) {
                case Basic:
                    if(mc.thePlayer.getCurrentEquippedItem() == null) return;
                    if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow)) return;
                    if(!(mc.currentScreen instanceof GuiInventory) || !(mc.currentScreen instanceof GuiDropDown)) {
                        if (Mouse.isButtonDown(1)) {
                            if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && mc.thePlayer.getCurrentEquippedItem().getItem() != null) {
                                mc.rightClickDelayTimer = 0;
                                for (int i = 0; i < (Packets.getPropertyValue() * 10); i++) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                                }
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                mc.thePlayer.stopUsingItem();
                            }
                            else {
                                return;
                            }
                        }
                    }
                    break;
                case SPEED:
                    if(mc.thePlayer.getCurrentEquippedItem() == null) return;
                    if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow)) return;
                    if (e.isPre()) {
                        if (mc.thePlayer.onGround && mc.gameSettings.keyBindUseItem.pressed) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                            for (int i = 0; i < 20; ++i) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-9, mc.thePlayer.posZ, true));
                            }
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        }
                    }
                    break;
            }
        });
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof S18PacketEntityTeleport) {
                final S18PacketEntityTeleport packet = (S18PacketEntityTeleport) e.getPacket();
                if (this.mc.thePlayer != null) {
                    packet.setYaw(((byte) this.mc.thePlayer.rotationYaw));
                }
                packet.setPitch(((byte) this.mc.thePlayer.rotationPitch));
            }
        });
    }
    private enum Modes {
        Basic,
        SPEED
    }
}
