package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.combat.Aura;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;

/**
 * Copyright Pablo Matias 2022
 * None of this code to be reused without my written permission
 * Intellectual Rights owned by Pablo Matias
 **/
@ModuleData(moduleName = "Auto Tool", moduleCategory = ModuleCategory.PLAYER)
public class AutoTool extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public AutoTool(){
        onUpdatePositionEvent = (e -> {
            if(e.isPre){
                if(Mouse.isButtonDown(0) && mc.objectMouseOver != null && Aura.target == null && !mc.thePlayer.isBlocking() && mc.currentScreen == null){
                    BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                    if (blockPos != null) {
                        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                        float strength = 1.0F;
                        int bestToolSlot = -1;

                        for(int i = 0; i < 9; ++i) {
                            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                            if (itemStack != null && itemStack.getStrVsBlock(block) > strength) {
                                strength = itemStack.getStrVsBlock(block);
                                bestToolSlot = i;
                            }
                        }

                        if (bestToolSlot != -1) {
                            mc.thePlayer.inventory.currentItem = bestToolSlot;
                        }
                    }
                }
            }
        });
    }

}
