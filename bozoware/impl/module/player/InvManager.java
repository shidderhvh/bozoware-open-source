package bozoware.impl.module.player;

import bozoware.base.BozoWare;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bozoware.impl.module.player.AutoArmor.getProtection;
import static bozoware.impl.module.player.AutoArmor.isBestArmor;


@ModuleData(moduleName = "InvManager", moduleCategory = ModuleCategory.PLAYER)
public class InvManager extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Double> Speed = new ValueProperty<>("Speed", 100D, 50D, 200D, this);
    private final BooleanProperty openinvBool = new BooleanProperty("OpenInv", true, this);
    private final BooleanProperty organize = new BooleanProperty("Organize", true, this);
    private final BooleanProperty throwFood = new BooleanProperty("Throw Food", true, this);
    private final ValueProperty<Integer> swordSlot = new ValueProperty<Integer>("Sword Slot", 1, 1, 9, this);
    private final ValueProperty<Integer> gapSlot = new ValueProperty<Integer>("Gapple Slot", 1, 1, 9, this);
    private final BooleanProperty throwTools = new BooleanProperty("Throw Tools", true, this);
//    private final BooleanProperty throwSword = new BooleanProperty("Clean Worse Weapons", true, this);

    public Minecraft mc = Minecraft.getMinecraft();
    boolean hasSet = false;
    private double delay;
    private TimerUtil updateTimer = new TimerUtil();
    public static boolean dropping = false;
    public int weaponSlot = 1;
    static float bestSwordDamage;
    static boolean cleaning, sorting;
    int realSlot;
    int bestSword;
    public TimerUtil timer = new TimerUtil();
    static double a;

    public static InvManager getInstance() {
        return (InvManager) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(InvManager.class);
    }

    public InvManager() {

//        setModuleSuffix(Speed.getPropertyValue().toString());
        onModuleDisabled = () -> {

        };
        onModuleEnabled = () -> {

        };
        swordSlot.onValueChange = () -> {
            realSlot = swordSlot.getPropertyValue() + 1;
        };
        Speed.onValueChange = () -> {
            a = Speed.getPropertyValue() * 2;
        };
        onUpdatePositionEvent = (e -> {
            if (updateTimer.hasReached((long) delay)) {
                hasSet = true;
            }
            if (!hasSet) {
                return;
            }
            boolean openinv = openinvBool.getPropertyValue();
            if (openinv && !(mc.currentScreen instanceof GuiInventory)) {
                return;
            }
            weaponSlot = swordSlot.getPropertyValue();
            if (weaponSlot == 0 || weaponSlot > 9) {
                weaponSlot = 69;
            }
            weaponSlot--;
            if (e.isPre()) {
                if (mc.thePlayer != null && !dropping
                        && (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiInventory)
                        && this.timer.hasReached((long) delay)) {
                    for (int i = 9; i < 45; ++i) {
                        if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                            if (this.isBad(is, i) && !(is.getItem() instanceof ItemArmor)
                                    && is != mc.thePlayer.getCurrentEquippedItem()) {
                                this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i,
                                        0, 0, mc.thePlayer);
                                this.dropping = true;
                                this.timer.reset();

                                break;
                            } else {
                                if (weaponSlot < 10 && is.getItem() instanceof ItemSword && isBestWeapon(is)
                                        && 45 - i - 9 != weaponSlot && !dropping) {
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, weaponSlot,
                                            2, mc.thePlayer);
                                    timer.reset();
                                }
                                if(is.getItem() instanceof ItemArmor) {
                                    float prot = getProtection(is);
                                    String strType = "";
                                    for (int type = 0; type < 5; type++) {
                                        if (type == 1) {
                                            strType = "helmet";
                                        } else if (type == 2) {
                                            strType = "chestplate";
                                        } else if (type == 3) {
                                            strType = "leggings";
                                        } else if (type == 4) {
                                            strType = "boots";
                                        }
                                        if (!is.getUnlocalizedName().contains(strType))
                                            this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i,
                                                    0, 0, mc.thePlayer);
                                        this.dropping = true;
                                        this.timer.reset();                                        for (int x = 5; x < 45; x++) {
                                            Minecraft mc = Minecraft.getMinecraft();
                                            if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                                                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                                                    this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i,
                                                            0, 0, mc.thePlayer);
                                                this.dropping = true;
                                                this.timer.reset();                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (dropping && this.timer.hasReached((long) (delay / 2))) {
                    this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0,
                            mc.thePlayer);
                    timer.reset();
                    dropping = false;
                }
            }
        });
        onModuleEnabled = () -> {
            this.dropping = false;
        };
    }

    private ItemStack bestSword () {
        ItemStack best = null;
        float swordDamage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    final float swordD = this.getItemDamage(is);
                    if (swordD > swordDamage) {
                        swordDamage = swordD;
                        best = is;
                    }
                }
            }
        }
        return best;
    }


    private boolean isBad ( final ItemStack stack, int slot){
        if (stack.getDisplayName().toLowerCase().contains("(right click)")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("\u00A7k||")) {
            return false;
        }
        if ((slot == weaponSlot && isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack()))) {
            return false;
        }
        if (stack.getItem() instanceof ItemSword && !isBestWeapon(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemPickaxe && !isBestPickaxe(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemAxe && !isBestAxe(stack)) {
            return true;
        }
        if (stack.getItem().getUnlocalizedName().contains("shovel") && !isBestShovel(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemSword && weaponSlot > 10) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion) {
            if (isBadPotion(stack)) {
                return true;
            }
        }
        if (stack.getItem() instanceof ItemFood && throwFood.getPropertyValue()
                && !(stack.getItem() instanceof ItemAppleGold)) {
            return true;
        }
        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemArmor) {
            return true;
        }
        if (((stack.getItem().getUnlocalizedName().contains("tnt"))
                || (stack.getItem().getUnlocalizedName().contains("stick"))
                || (stack.getItem().getUnlocalizedName().contains("egg"))
                || (stack.getItem().getUnlocalizedName().contains("string"))
                || (stack.getItem().getUnlocalizedName().contains("cake"))
                || (stack.getItem().getUnlocalizedName().contains("mushroom"))
                || (stack.getItem().getUnlocalizedName().contains("flint"))
                || (stack.getItem().getUnlocalizedName().contains("compass"))
                || (stack.getItem().getUnlocalizedName().contains("dyePowder"))
                || (stack.getItem().getUnlocalizedName().contains("feather"))
                || (stack.getItem().getUnlocalizedName().contains("bucket"))
                || (stack.getItem().getUnlocalizedName().contains("chest")
                && !stack.getDisplayName().toLowerCase().contains("collect"))
                || (stack.getItem().getUnlocalizedName().contains("snow"))
                || (stack.getItem().getUnlocalizedName().contains("fish"))
                || (stack.getItem().getUnlocalizedName().contains("enchant"))
                || (stack.getItem().getUnlocalizedName().contains("exp"))
                || (stack.getItem().getUnlocalizedName().contains("shears"))
                || (stack.getItem().getUnlocalizedName().contains("anvil"))
                || (stack.getItem().getUnlocalizedName().contains("torch"))
                || (stack.getItem().getUnlocalizedName().contains("seeds"))
                || (stack.getItem().getUnlocalizedName().contains("leather"))
                || (stack.getItem().getUnlocalizedName().contains("reeds"))
                || (stack.getItem().getUnlocalizedName().contains("skull"))
                || (stack.getItem().getUnlocalizedName().contains("record"))
                || (stack.getItem().getUnlocalizedName().contains("snowball"))
                || (stack.getItem() instanceof ItemGlassBottle)
                || (stack.getItem().getUnlocalizedName().contains("piston")))) {
            return true;
        }
        if (isDuplicate(stack, slot)) {
            return true;
        }
        return false;
    }

    private List<ItemStack> getBest () {
        final List<ItemStack> best = new ArrayList<ItemStack>();
        for (int i = 0; i < 4; ++i) {
            ItemStack armorStack = null;
            for (final ItemStack itemStack : mc.thePlayer.inventory.armorInventory) {
                if (itemStack != null) {
                    if (itemStack.getItem() instanceof ItemArmor) {
                        final ItemArmor stackArmor = (ItemArmor) itemStack.getItem();
                        if (stackArmor.armorType == i) {
                            armorStack = itemStack;
                        }
                    }
                }
            }
            final double reduction = (armorStack == null) ? -1.0 : this.getArmorStrength(armorStack);
            ItemStack slotStack = this.findBestArmor(i);
            if (slotStack != null && this.getArmorStrength(slotStack) <= reduction) {
                slotStack = armorStack;
            }
            if (slotStack != null) {
                best.add(slotStack);
            }
        }
        return best;
    }

    public boolean isDuplicate (ItemStack stack,int slot){
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is != stack && slot != i && is.getUnlocalizedName().equalsIgnoreCase(stack.getUnlocalizedName())
                        && !(is.getItem() instanceof ItemPotion) && !(is.getItem() instanceof ItemBlock)) {
                    if (is.getItem() instanceof ItemSword) {
                        if (this.getDamage(is) != this.getDamage(stack)) {
                        } else {
                            return true;
                        }
                    } else if (is.getItem() instanceof ItemTool) {
                        if (this.getToolEffect(is) != this.getToolEffect(stack)) {
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int getBlockCount () {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }

    private ItemStack findBestArmor ( final int itemSlot){
        ItemStack i = null;
        double maxReduction = 0.0;
        for (int slot = 0; slot < 36; ++slot) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            if (itemStack != null) {
                final double reduction = this.getArmorStrength(itemStack);
                if (reduction != -1.0) {
                    final ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
                    if (itemArmor.armorType == itemSlot) {
                        if (reduction >= maxReduction) {
                            maxReduction = reduction;
                            i = itemStack;
                        }
                    }
                }
            }
        }
        return i;
    }

    private double getArmorStrength ( final ItemStack itemStack){
        if (!(itemStack.getItem() instanceof ItemArmor)) {
            return -1.0;
        }
        float damageReduction = (float) ((ItemArmor) itemStack.getItem()).damageReduceAmount;
        final Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if (enchantments.containsKey(Enchantment.protection.effectId)) {
            final int level = (int) enchantments.get(Enchantment.protection.effectId);
            damageReduction += Enchantment.protection.calcModifierDamage(level, DamageSource.generic);
        }
        return damageReduction;
    }

    private boolean isBadPotion ( final ItemStack stack){
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId()
                            || effect.getPotionID() == Potion.moveSlowdown.getId()
                            || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private float getItemDamage ( final ItemStack itemStack){
        float damage = ((ItemSword) itemStack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f;
        return damage;
    }

    private boolean isBestPickaxe (ItemStack stack){
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestShovel (ItemStack stack){
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestAxe (ItemStack stack){
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isBestWeapon (ItemStack stack){
        float damage = getDamage(stack);
        for (int i = 0; i < 36; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        return true;
    }

    private float getDamage (ItemStack stack){
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            damage += tool.getDamage();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getAttackDamage();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
                + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }

    private float getToolEffect (ItemStack stack){
        Item item = stack.getItem();
        if (!(item instanceof ItemTool))
            return 0;
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool) item;
        float value = 1;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100d;
        return value;
    }
}