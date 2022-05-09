package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;


@ModuleData(moduleName = "Chest Stealer", moduleCategory = ModuleCategory.PLAYER)
public class ChestStealer extends Module {

    public TimerUtil timer = new TimerUtil();
    double a;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Double> Speed = new ValueProperty<>("Speed", 100D, 50D, 200D, this);
    private final BooleanProperty autoClose = new BooleanProperty("Auto Close", true, this);
    private final BooleanProperty titleCheck = new BooleanProperty("Title Check", true, this);

    public ChestStealer() {
//        setModuleSuffix(Speed.getPropertyValue().toString());
        onModuleDisabled = () -> {

        };
        onModuleEnabled = () -> {
            a = Speed.getPropertyValue();
        };
        onUpdatePositionEvent = (e -> {
            if(!(mc.currentScreen instanceof GuiChest))
                return;
            if (mc.thePlayer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
                GuiChest chest1 = (GuiChest) mc.currentScreen;
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); ++i) {
                    if(titleCheck.getPropertyValue())
                    if (chest.getLowerChestInventory().getStackInSlot(i) != null && chest1.lowerChestInventory.getDisplayName().getUnformattedText().contains("Chest") || chest1.lowerChestInventory.getDisplayName().getUnformattedText().contains("Il y a 3 étoiles dans ce coffre")) {
                        if (this.timer.hasReached((long) a) && !isTrash(chest.getLowerChestInventory().getStackInSlot(i))) {
                            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                            this.timer.reset();
                        }
                    }
                }
                if(autoClose.getPropertyValue()){
                    if (isChestEmpty(chest1) || isInventoryFull()){
                        mc.thePlayer.closeScreen();
                    }
                }
            }
        });
//        Speed.onValueChange = () -> setModuleSuffix(Speed.getPropertyValue().toString());
    }

    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                    return false;
        }
        return true;
    }

    private boolean isChestEmpty(final GuiChest chest) {
        for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
            ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
            if (stack != null)
                if (!isTrash(stack))
                    return false;
        }
        return true;
    }

    public static boolean isTrash(ItemStack item) {
        return ((item.getItem().getUnlocalizedName().contains("tnt")) || item.getDisplayName().contains("frog") ||
                (item.getItem().getUnlocalizedName().contains("stick"))||
                (item.getItem().getUnlocalizedName().contains("string")) || (item.getItem().getUnlocalizedName().contains("flint")) ||
                (item.getItem().getUnlocalizedName().contains("feather")) || (item.getItem().getUnlocalizedName().contains("bucket")) ||
                (item.getItem().getUnlocalizedName().contains("snow")) || (item.getItem().getUnlocalizedName().contains("enchant")) ||
                (item.getItem().getUnlocalizedName().contains("exp")) || (item.getItem().getUnlocalizedName().contains("shears")) ||
                (item.getItem().getUnlocalizedName().contains("arrow")) || (item.getItem().getUnlocalizedName().contains("anvil")) ||
                (item.getItem().getUnlocalizedName().contains("torch")) || (item.getItem().getUnlocalizedName().contains("seeds")) ||
                (item.getItem().getUnlocalizedName().contains("leather")) || (item.getItem().getUnlocalizedName().contains("boat")) ||
                (item.getItem().getUnlocalizedName().contains("fishing")) || (item.getItem().getUnlocalizedName().contains("wheat")) ||
                (item.getItem().getUnlocalizedName().contains("flower")) || (item.getItem().getUnlocalizedName().contains("record")) ||
                (item.getItem().getUnlocalizedName().contains("note")) || (item.getItem().getUnlocalizedName().contains("sugar")) ||
                (item.getItem().getUnlocalizedName().contains("wire")) || (item.getItem().getUnlocalizedName().contains("trip")) ||
                (item.getItem().getUnlocalizedName().contains("slime")) || (item.getItem().getUnlocalizedName().contains("web")) ||
                ((item.getItem() instanceof ItemGlassBottle)) || (item.getItem().getUnlocalizedName().contains("piston")) ||
                (item.getItem().getUnlocalizedName().contains("potion") && (isBadPotion(item))) ||
                (item.getItem() instanceof ItemEgg || (item.getItem().getUnlocalizedName().contains("bow")) && !item.getDisplayName().contains("Kit")) ||
                (item.getItem().getUnlocalizedName().contains("Raw")));
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
