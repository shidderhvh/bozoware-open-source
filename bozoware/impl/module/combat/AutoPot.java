package bozoware.impl.module.combat;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

@ModuleData(moduleName = "Auto Potion", moduleCategory = ModuleCategory.COMBAT)
public class AutoPot extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    TimerUtil timer = new TimerUtil();
    public static int counter;


    public AutoPot() {
        onUpdatePositionEvent = (event -> {
            int pcount = getPotionCount();
            setModuleSuffix(String.valueOf(pcount));
            if(!mc.thePlayer.onGround)
                return;
            if (event.isPre()) {
                if(mc.thePlayer.ticksExisted < 200)
                    return;
                for (int i = 0; i < 9; i++) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                        continue;
                    if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSkull) {
                        if (mc.thePlayer.getHealth() < 5) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i));
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
                }
            }
        for(int i = 0; i < 9; i++){
            if(mc.thePlayer.inventory.getStackInSlot(i) == null)
                continue;
            if(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSkull){
                if(mc.thePlayer.getHealth() < 5){
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
            }
        }
        final boolean speed = true;
        final boolean regen = true;


        int spoofSlot = getBestSpoofSlot();
        int pots[] = {6,-1,-1};
        if(regen)
            pots[1] = 10;
        if(speed)
            pots[2] = 1;

        for(int i = 0; i < pots.length; i ++){
            if(pots[i] == -1)
                continue;
            if(pots[i] == 6 || pots[i] == 10){
                if(timer.hasReached(900) && !mc.thePlayer.isPotionActive(pots[i])){
                    if(mc.thePlayer.getHealth() < 15){
                        getBestPot(spoofSlot, pots[i]);
                    }
                }
            }else
            if(timer.hasReached(1000) && !mc.thePlayer.isPotionActive(pots[i])){
                getBestPot(spoofSlot, pots[i]);
            }
        }
        });
    }

    public void swap(int slot1, int hotbarSlot) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }
    float[] getRotations(){
        double movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 26.0D;
        double movedPosY = mc.thePlayer.boundingBox.minY - 3.6D;
        double movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 26.0D;
        return getRotationFromPosition(movedPosX, movedPosZ, movedPosY);
    }
    int getBestSpoofSlot(){
        int spoofSlot = 5;
        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }else if(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                spoofSlot = i - 36;
                break;
            }
        }
        return spoofSlot;
    }
    void getBestPot(int hotbarSlot, int potID){
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() &&(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
                    ItemPotion pot = (ItemPotion)is.getItem();
                    if(pot.getEffects(is).isEmpty())
                        return;
                    PotionEffect effect = (PotionEffect) pot.getEffects(is).get(0);
                    int potionID = effect.getPotionID();
                    if(potionID == potID)
                        if(ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)){
                                if (36 + hotbarSlot != i)
                                    swap(i, hotbarSlot);
                                timer.reset();
                            boolean canpot = true;
                            int oldSlot = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(hotbarSlot));
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(getRotations()[0], getRotations()[1], mc.thePlayer.onGround));
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));

                            break;
                        }
                }
            }
        }
    }
    boolean isBestPot(ItemPotion potion, ItemStack stack){
        if(potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
            return false;
        PotionEffect effect = (PotionEffect) potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier();
        int duration = effect.getDuration();
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
                    ItemPotion pot = (ItemPotion)is.getItem();
                    if (pot.getEffects(is) != null) {
                        for (Object o : pot.getEffects(is)) {
                            PotionEffect effects = (PotionEffect) o;
                            int id = effects.getPotionID();
                            int ampl = effects.getAmplifier();
                            int dur = effects.getDuration();
                            if (id == potionID && ItemPotion.isSplash(is.getItemDamage())){
                                if(ampl > amplifier){
                                    return false;
                                }else if (ampl == amplifier && dur > duration){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    private int getPotionCount() {
        int potioncount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
                if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemPotion && !isBadPotion(stack) && ItemPotion.isSplash(stack.getItemDamage()))
                    potioncount += stack.stackSize;
        }
        return potioncount;
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

    static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }
}