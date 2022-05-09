package bozoware.impl.module.movement;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@ModuleData(moduleName = "IceSpeed", moduleCategory = ModuleCategory.MOVEMENT)
public class IceSpeed extends Module {

    @EventListener
    EventConsumer<PlayerMoveEvent> playerMoveEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.NCP, this);

    public IceSpeed() {
        setModuleSuffix(Mode.getPropertyValue().toString());
        setModuleBind(0);
        onModuleEnabled = () -> {

        };
        onModuleDisabled = () -> {
            Blocks.ice.slipperiness = 0.98F;
            Blocks.packed_ice.slipperiness = 0.98F;
        };
        playerMoveEvent = (e -> {
            switch(Mode.getPropertyValue()){
                case Vanilla:
                    if(Wrapper.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ)).getMaterial() == Material.ice || Wrapper.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ)).getMaterial() == Material.packedIce) {
                        mc.thePlayer.motionY = -1;
                        mc.thePlayer.jump();
                    }
                    break;
                case Spartan:
                    final Material material = Wrapper.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getMaterial();

                    if(material == Material.ice || material == Material.packedIce) {
                        final Block upBlock = Wrapper.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2D, mc.thePlayer.posZ));

                        if(!(upBlock instanceof BlockAir)) {
                            mc.thePlayer.motionX *= 1.342D;
                            mc.thePlayer.motionZ *= 1.342D;
                        }else{
                            mc.thePlayer.motionX *= 1.18D;
                            mc.thePlayer.motionZ *= 1.18D;
                        }

                        Blocks.ice.slipperiness = 0.6F;
                        Blocks.packed_ice.slipperiness = 0.6F;
                    }
                    break;
                case NCP:
                    Blocks.ice.slipperiness = 0.39F;
                    Blocks.packed_ice.slipperiness = 0.39F;
                    break;
            }
        });
    }
    private enum Modes{
        NCP,
        Spartan,
        Vanilla
    }
}
