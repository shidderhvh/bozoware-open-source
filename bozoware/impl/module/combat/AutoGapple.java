package bozoware.impl.module.combat;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

@ModuleData(moduleName = "Auto Gapple", moduleCategory = ModuleCategory.COMBAT)
public class AutoGapple extends Module {


    private final ValueProperty<Integer> health = new ValueProperty<>("HP", 5, 1, 20, this);
    private final BooleanProperty eat_while_attacking = new BooleanProperty("Eat while Attacking", true, this);

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public boolean eating;
    public int slotBefore;

    public AutoGapple(){
        onUpdatePositionEvent = (e -> {
            int gcount = getGappleCount();
            setModuleSuffix(String.valueOf(gcount));
            if(!eating){
                slotBefore = mc.thePlayer.inventory.currentItem;
            }
            int HP = (int) mc.thePlayer.getHealth();
            if(eating && mc.thePlayer.isPotionActive(Potion.regeneration)){
                mc.thePlayer.inventory.currentItem = slotBefore;
                mc.gameSettings.keyBindUseItem.pressed = false;
                eating = false;
//                BozoWare.getInstance().chat("Auto Gapple automatically ate a gapple (This was made by shidder and not skidded btw)");
            }
            if(HP < health.getPropertyValue() && !mc.thePlayer.isPotionActive(Potion.regeneration)){
                if(eat_while_attacking.getPropertyValue()) {
                    if (!(getGappleSlot() == -1)) {
                        eating = true;
                        mc.thePlayer.inventory.currentItem = getGappleSlot();
                        mc.gameSettings.keyBindUseItem.pressed = true;
                    }
                } else {
                    if(Aura.getInstance().getTarget() == null){
                        if (!(getGappleSlot() == -1)) {
                            mc.thePlayer.inventory.currentItem = getGappleSlot();
                            mc.gameSettings.keyBindUseItem.pressed = true;
                            eating = true;
                        }
                    }
                }
            }
        });

    }
    private int getGappleCount() {
        int gappleCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemAppleGold)
                gappleCount += stack.stackSize;
        }
        return gappleCount;
    }
    private int getGappleSlot() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && itemStack.getItem() instanceof ItemAppleGold) {
                return i - 36;
            }
        }
        return -1;
    }
}
