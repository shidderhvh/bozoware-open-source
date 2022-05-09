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
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleData(moduleName = "Velocity", moduleCategory = ModuleCategory.COMBAT)
public class Velocity extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final EnumProperty<Modes> mode = new EnumProperty<>("Mode", Modes.Cancel, this);
    private final ValueProperty<Double> stackSize = new ValueProperty<>("Stack Size", 3.0D, 2.0D, 6.0D, this);
    private final ValueProperty<Double> horizontalPercentageProperty = new ValueProperty<>("Horizontal", 0.0D, 0.0D, 100.0D, this);
    private final ValueProperty<Double> verticalPercentageProperty = new ValueProperty<>("Vertical", 0.0D, 0.0D, 100.0D, this);

    public double stack;

    public Velocity() {
//        if(mode.getPropertyValue().equals(Modes.Custom)) {
//            setModuleSuffix("0% 0%");
//        }
//        else {
//            setModuleSuffix(mode.getPropertyValue().toString());
//        }
        onPacketReceiveEvent = (e -> {
            switch (mode.getPropertyValue()){
                case Custom:
                    if (e.getPacket() instanceof S27PacketExplosion) {
                        S27PacketExplosion s27 = (S27PacketExplosion) e.getPacket();
                        s27.motionX = (float) (s27.motionX * (horizontalPercentageProperty.getPropertyValue() / 100D));
                        s27.motionY = (float) (s27.motionY * (verticalPercentageProperty.getPropertyValue() / 100D));
                        s27.motionZ = (float) (s27.motionZ * (horizontalPercentageProperty.getPropertyValue() / 100D));
                    }
                    if (e.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                        s12.motionX = (int) (s12.motionX * (horizontalPercentageProperty.getPropertyValue() / 100D));
                        s12.motionY = (int) (s12.motionY * (verticalPercentageProperty.getPropertyValue() / 100D));
                        s12.motionZ = (int) (s12.motionZ * (horizontalPercentageProperty.getPropertyValue() / 100D));
                    }
                    break;
                    
                case Cancel:
                    if (e.getPacket() instanceof S27PacketExplosion) {
                        e.setCancelled(true);
                    }
                    if (e.getPacket() instanceof S12PacketEntityVelocity) {
                        e.setCancelled(true);
                    }
                    break;
                case Stack:
                    if(e.getPacket() instanceof S12PacketEntityVelocity) {
                        if(stack < stackSize.getPropertyValue()) {
                            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();
                            s12.motionX = (int) (s12.motionX * (horizontalPercentageProperty.getPropertyValue() / 100D));
                            s12.motionY = (int) (s12.motionY * (verticalPercentageProperty.getPropertyValue() / 100D));
                            s12.motionZ = (int) (s12.motionZ * (horizontalPercentageProperty.getPropertyValue() / 100D));
                        }
                        if(stack >= stackSize.getPropertyValue()) {
                            e.setCancelled(false);
                            stack = 0;
                        }
                    }
                    if(e.getPacket() instanceof S27PacketExplosion) {
                        if (stack < stackSize.getPropertyValue()) {
                            S27PacketExplosion s27 = (S27PacketExplosion) e.getPacket();
                            s27.motionX = (float) (s27.motionX * (horizontalPercentageProperty.getPropertyValue() / 100D));
                            s27.motionY = (float) (s27.motionY * (verticalPercentageProperty.getPropertyValue() / 100D));
                            s27.motionZ = (float) (s27.motionZ * (horizontalPercentageProperty.getPropertyValue() / 100D));
                        }
                    }
                    break;
                case RedeskyPhase:
                    if(e.getPacket() instanceof S27PacketExplosion) {
                        e.setCancelled(true);
                    }
                    if(e.getPacket() instanceof S12PacketEntityVelocity) {
                        double x = mc.thePlayer.posX;
                        double y = mc.thePlayer.posY;
                        double z = mc.thePlayer.posZ;
                        if(mc.thePlayer.hurtTime >= 8){
                            mc.thePlayer.setPositionAndUpdate(x, y - 0.1F, z);
                        } else {
                            e.setCancelled(true);
                        }
                    }
                    break;
            }
        });
        onUpdatePositionEvent = (e -> {
            if(mode.getPropertyValue().equals(Modes.Stack)){
                System.out.println(stack);
                if(mc.thePlayer.hurtTime == 9) {
                    stack = stack + 1;
                }
            }
        });
//        if(mode.getPropertyValue().equals(Modes.Custom)) {
//            horizontalPercentageProperty.onValueChange = () -> setModuleSuffix(horizontalPercentageProperty.getPropertyValue() + "% " + verticalPercentageProperty.getPropertyValue() + "%");
//            verticalPercentageProperty.onValueChange = () -> setModuleSuffix(horizontalPercentageProperty.getPropertyValue() + "% " + verticalPercentageProperty.getPropertyValue() + "%");
//        }
    }

    public enum Modes {
        Cancel,
        Custom,
        Stack,
        RedeskyPhase
    }
}
