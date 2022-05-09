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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.List;


@ModuleData(moduleName = "Auto Armor", moduleCategory = ModuleCategory.PLAYER)
public class AutoArmor extends Module {

    public TimerUtil timer = new TimerUtil();
    private int[] chestplate;
    private int[] leggings;
    private List[] allArmors = new List[4];
    private int[] boots;
    private int[] helmet;
    private boolean dropping;
    private double delay;
    private int[] bestArmorSlot;
    private boolean best;
    private boolean equipping;
    double a;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Long> Speed = new ValueProperty<>("Speed", 100L, 0L, 200L, this);
    private final BooleanProperty openinv = new BooleanProperty("OpenInv", true, this);

    public AutoArmor() {
        onUpdatePositionEvent = (e -> {
            if(openinv.getPropertyValue())
                if (!(mc.currentScreen instanceof GuiInventory)) return;
            if (this.mc.currentScreen == null
                    || this.mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory
                    || this.mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) {
                if (this.timer.hasReached((long) delay))
                    getBestArmor();
                if (!this.dropping) {
                    if ((this.mc.currentScreen == null
                            || this.mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory
                            || this.mc.currentScreen instanceof net.minecraft.client.gui.GuiChat)
                            && this.timer.hasReached((long) delay))
                        getBestArmor();
                } else if (this.timer.hasReached((long) delay)) {
                    this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0,
                            (EntityPlayer) mc.thePlayer);
                    this.timer.reset();
                    this.dropping = false;
                }
            }
        });
    }

    private boolean checkDelay() {
        return !this.timer.hasReached(Speed.getPropertyValue());
    }

    public void drop(int slot) {
        if (this.timer.hasReached(((long) delay * 50L))
                && !this.dropping) {
            this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 0,
                    mc.thePlayer);
            this.dropping = true;
            this.timer.reset();
        }
    }

    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type))
                    continue;
                drop(4 + type);
            }
            for (int i = 9; i < 45; i++) {
                if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is2 = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is2, type) && getProtection(is2) > 0.0F) {
                        shiftClick(i);
                        this.timer.reset();
                        if (delay > 0L)
                            return;
                    }
                }
            }
        }
    }
    public void shiftClick(int slot) {
        this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1,
                (EntityPlayer) mc.thePlayer);
    }
    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType))
            return false;
        for (int i = 5; i < 45; i++) {
            Minecraft mc = Minecraft.getMinecraft();
            if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }
    public static float getProtection(ItemStack stack) {
        float prot = 0.0F;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += (float) (armor.damageReduceAmount + ((100 - armor.damageReduceAmount)
                    * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
            prot += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack)
                    / 100.0D);
            prot += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack)
                    / 100.0D);
            prot += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
            prot += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
            prot += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack)
                    / 100.0D);
        }
        return prot;
    }
}