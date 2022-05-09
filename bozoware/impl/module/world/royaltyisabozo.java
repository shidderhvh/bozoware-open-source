package bozoware.impl.module.world;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static bozoware.impl.module.visual.HUD.wrapAngleToDirection;

@ModuleData(moduleName = "Skyblock Macros", moduleCategory = ModuleCategory.WORLD)
public class royaltyisabozo extends Module {

    private BooleanProperty nuke = new BooleanProperty("Nuker", true, this);
    private BooleanProperty placer = new BooleanProperty("Placer", false, this);
    private BooleanProperty autoMove = new BooleanProperty("Auto Move", true, this);
    public ValueProperty<Integer> bpsValue = new ValueProperty<>("Ticks", 8, 1, 40, this);
    private ValueProperty<Integer> radiusValue = new ValueProperty<Integer>("Nuker Radius", 8, 1, 20, this);
//    private BooleanProperty inviter = new BooleanProperty("Advertise", true, this);
//    private StringProperty message = new StringProperty("Advertise Message", "BozoWare > You! gg . 5m5KWcz3qv", this);

    boolean isThreadRunning;
    private final ArrayList<BlockPos> blocks = new ArrayList<>();
    private final ArrayList<BlockPos> brokenBlocks = new ArrayList<>();
    public static String prevDirection;
    public static String[] directions;
    public static String direction;
    BlockPos front;
    BlockPos left;
    BlockPos right;
    BlockPos behind;
    public Block currentBlock;
    TimerUtil timer = new TimerUtil();
    static int bruh = (int) System.currentTimeMillis();

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;



    public royaltyisabozo() {
        onModuleEnabled = () -> {
//            this.timer.reset();
            if (!isThreadRunning && autoMove.getPropertyValue()) {
                runThread();
                isThreadRunning = true;
            }
        bruh = (int) System.currentTimeMillis();
        };
        onModuleDisabled = () -> {
            mc.gameSettings.keyBindForward.pressed = false;
            isThreadRunning = false;

        };
        onUpdatePositionEvent = (e -> {
            if (!isThreadRunning && autoMove.getPropertyValue()) {
                runThread();
                isThreadRunning = true;
            }
//            e.setPitch(0);
//                Minecraft Minecraft = net.minecraft.client.Minecraft.getMinecraft();
//                if (Minecraft.theWorld != null && !e.isPre) {
//                    int radius = radiusValue.getPropertyValue();
//
//                    for (int y = radius; y >= -radius; --y) {
//                        for (int x = -radius; x <= radius; ++x) {
//                            for (int z = -radius; z <= radius; ++z) {
//                                BlockPos pos = new BlockPos(Minecraft.thePlayer.posX - 0.5D + (double) x, Minecraft.thePlayer.posY - 0.5D + (double) y, Minecraft.thePlayer.posZ - 0.5D + (double) z);
//                                Block block = Minecraft.theWorld.getBlockState(pos).getBlock();
//                                if (this.getFacingDirection(pos) != null && (block instanceof BlockNetherWart) || (block instanceof BlockCrops) || (block instanceof BlockReed)) {
////                                if(((BlockNetherWart) block).AGE.equals(3)) {
//                                    if(nuke.getPropertyValue()) {
//                                        if(mc.thePlayer.ticksExisted % bpsValue.getPropertyValue() == 0)
//                                        this.eraseBlock(pos, this.getFacingDirection(pos));
//                                    }
//                                    if(placer.getPropertyValue())
//                                        this.placeBlock(pos, this.getFacingDirection(pos));
//
////                                } else
////                                    return;
//                                }
//                            }
//                        }
//                }
//            }
            if(autoMove.getPropertyValue()) {
                directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                direction = directions[wrapAngleToDirection(mc.thePlayer.rotationYaw, directions.length)];
                directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                prevDirection = directions[wrapAngleToDirection(mc.thePlayer.rotationYaw - 180, directions.length)];
//                    if(mc.thePlayer.rotationYaw % 90 != 0){
//                        if(direction == "N")
//                            mc.thePlayer.rotationYaw = 180;
//                        if(direction == "S")
//                            mc.thePlayer.rotationYaw = 0;
//                        if(direction == "W")
                    if (direction == "N") {
                        if(mc.thePlayer.rotationYaw % 90 != 0){
                            mc.thePlayer.rotationYaw = 180;
                        }
                        front = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 0.75);
                        behind = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1);
                        left = new BlockPos(mc.thePlayer.posX - 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                        right = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }
                    if (direction == "S") {
                        if(mc.thePlayer.rotationYaw % 90 != 0){
                            mc.thePlayer.rotationYaw = 0;
                        }
                        front = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 0.75);
                        behind = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1);
                        left = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                        right = new BlockPos(mc.thePlayer.posX - 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }
                    if (direction == "W") {
                        if(mc.thePlayer.rotationYaw % 90 != 0){
                            mc.thePlayer.rotationYaw = 90;
                        }
                        front = new BlockPos(mc.thePlayer.posX - 0.75, mc.thePlayer.posY, mc.thePlayer.posZ);
                        behind = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                        left = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1);
                        right = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1);
                    }
                    if (direction == "E") {
                        if(mc.thePlayer.rotationYaw % 90 != 0){
                            mc.thePlayer.rotationYaw = -90;
                        }
                        front = new BlockPos(mc.thePlayer.posX + 0.75, mc.thePlayer.posY, mc.thePlayer.posZ);
                        behind = new BlockPos(mc.thePlayer.posX - 1, mc.thePlayer.posY, mc.thePlayer.posZ);
                        left = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ - 1);
                        right = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ + 1);
                    }
                    if (Wrapper.getBlock(front).getMaterial() == Material.air) {
                        mc.gameSettings.keyBindForward.pressed = true;
                        this.timer.reset();
                    } else {
                        mc.gameSettings.keyBindForward.pressed = true;
                        if (prevDirection == "S" && direction == "N") {
                            if (Wrapper.getBlock(left).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 90;
//                                BozoWare.getInstance().chat("turned left");
                            }
                            if (Wrapper.getBlock(right).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = -90;
//                                BozoWare.getInstance().chat("turned right");
                            }
                            if(Wrapper.getBlock(right).getMaterial() != Material.air && Wrapper.getBlock(left).getMaterial() != Material.air && Wrapper.getBlock(behind).getMaterial() == Material.air && Wrapper.getBlock(front).isFullBlock()){
                                mc.thePlayer.rotationYaw = 0;
                            }
                        }
                        if(direction == "W"){
                            if (Wrapper.getBlock(left).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 0;
//                                BozoWare.getInstance().chat("turned left");
                            }
                            if (Wrapper.getBlock(right).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 180;
//                                BozoWare.getInstance().chat("turned r");
                            }
                            if(Wrapper.getBlock(right).getMaterial() != Material.air && Wrapper.getBlock(left).getMaterial() != Material.air && Wrapper.getBlock(behind).getMaterial() == Material.air && Wrapper.getBlock(front).isFullBlock()){
                                mc.thePlayer.rotationYaw = -90;
                            }
                        }
                        if(direction == "E"){
                            if (Wrapper.getBlock(left).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 180;
//                                BozoWare.getInstance().chat("turned left");
                            }
                            if (Wrapper.getBlock(right).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 0;
//                                BozoWare.getInstance().chat("turned r");
                            }
                            if(Wrapper.getBlock(right).getMaterial() != Material.air && Wrapper.getBlock(left).getMaterial() != Material.air && Wrapper.getBlock(behind).getMaterial() == Material.air && Wrapper.getBlock(front).isFullBlock()){
                                mc.thePlayer.rotationYaw = 90;
                            }
                        }
                        if(direction == "S"){
                            if (Wrapper.getBlock(left).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = -90;
//                                BozoWare.getInstance().chat("turned left");
                            }
                            if (Wrapper.getBlock(right).getMaterial() == Material.air) {
                                mc.thePlayer.rotationYaw = 90;
//                                BozoWare.getInstance().chat("turned r");
                            }
                            if(Wrapper.getBlock(right).getMaterial() != Material.air && Wrapper.getBlock(left).getMaterial() != Material.air && Wrapper.getBlock(behind).getMaterial() == Material.air && Wrapper.getBlock(front).isFullBlock()){
                                mc.thePlayer.rotationYaw = 180;
                            }
                        }
                    }
            }
        });
        onRender2DEvent = (e -> {
//            Gui.drawRect(1, 30, 110 + 4 + 13, 70, 0x40000000);
//            Gui.drawRect(5, 35, 110 + 13, 65, 0x60000000);
//            RenderUtil.glHorizontalGradientQuad(5, 35, 110 + 8, 2, HUD.getInstance().getColor2Gradient(), HUD.getInstance().bozoColor);
//            mc.fontRendererObj.drawStringWithShadow("Farm Stats", 37, 27, HUD.getInstance().bozoColor);
//            bruh = (int) System.currentTimeMillis();
            int diff = (int) ((int) System.currentTimeMillis() - bruh);
//            prevDirection = directions[wrapAngleToDirection(mc.thePlayer.rotationYaw - 180, directions.length)];
            mc.fontRendererObj.drawStringWithShadow("Session Time: " + diff / (60 * 60 * 1000) % 24 + "h " + diff / (60 * 1000) % 60 + "m " + diff / 1000 % 60 + "s", 8, 40, -1);
            mc.fontRendererObj.drawStringWithShadow("Estimated Profit: " + getAverageWartPrice() + " coins", 8, 50, -1);
//            mc.fontRendererObj.drawStringWithShadow("Blocks Broken: " + blocksBroken, 8, 60, -1);
    }
        );
    }
    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.UP;
        } else if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.DOWN;
        } else if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.EAST;
        } else if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.WEST;
        } else if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.NORTH;
        }

        return direction;
    }
    public void eraseBlock(BlockPos pos, EnumFacing facing) {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
        mc.thePlayer.swingItem();
    }
    public void placeBlock(BlockPos pos, EnumFacing facing) {
//        if(getNetherwartStack() != null)
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(pos, 0, getNetherwartStack(), 0, 0, 0));
//        else
//            this.toggleModule();
    }
    private ItemStack getNetherwartStack() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockNetherWart) {
                return itemStack;
            }
        }
        return null;
    }
    private int getAverageWartPrice() {
        int wart = 0;
        int enchantedWart = 0;
        int mutantWart = 0;

        for (int i = 44; i >= 9; i--) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if(!slot.getHasStack()) continue;
            ItemStack item = slot.getStack();
            if (item.getItem() == null) {
                continue;
            }
            if(item.getItem() == Item.getItemById(372)) {
                if(item.isItemEnchanted()) {
                    enchantedWart += item.stackSize;
                } else {
                    wart += item.stackSize;
                }
            }
            if(item.getDisplayName().contains("Mutant Nether Wart")) {
                mutantWart += item.stackSize;
            }
        }
        return (int) (wart * 3.6) + (enchantedWart * 316) + (mutantWart * 50000);
    }
    public static royaltyisabozo getInstance(){
        return (royaltyisabozo) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(royaltyisabozo.class);
    }
    private int getBlockAge(IBlockState blockState) {
        for (Map.Entry<IProperty, Comparable> entry : blockState.getProperties().entrySet()) {
            if (entry.getKey().getName().equals("age")) {
                return (int) entry.getValue();
            }
        }
        return -1;
    }
    public void runThread() {
        new Thread() {

            final TimerUtil breakTimer = new TimerUtil();

            @Override
            public void run() {
                while (this.isAlive()) {
                    double pauseTime = (1D / bpsValue.getPropertyValue()) * 1000;
                    updateBlocks();
                    if (breakTimer.hasReached((long) pauseTime) && blocks.size() >= 1 &&
                            mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemHoe) {
                        breakBlock();
                        breakTimer.reset();
                    }
                }
            }
        }.start();
    }
    public void breakBlock() {
        BlockPos blockPos;
        blockPos = getAndRemove();
        if (blockPos == null) return;
        IBlockState blockState = mc.theWorld.getBlockState(blockPos);
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                blockPos,
                EnumFacing.DOWN));
        brokenBlocks.add(blockPos);
            Wrapper.sendPacketDirect(new C0APacketAnimation());
    }

    private BlockPos getAndRemove() {
        try {
            return blocks.remove(getNextIndex());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private int getNextIndex() {
        if (blocks.size() > 0) {
            return new Random().nextInt(blocks.size());
        }
        return 0;
    }

    private BlockPos getPos() {
        try {
            return blocks.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    public void updateBlocks() {
        blocks.clear();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        BlockPos blockPos1 = playerPos.add(-4, -3, -4);
        BlockPos blockPos2 = playerPos.add(4, 3, 4);
        BlockPos.getAllInBox(blockPos1, blockPos2).forEach((blockPos) -> {
            if (mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockNetherWart && !brokenBlocks.contains(blockPos)) {
                if (getBlockAge(mc.theWorld.getBlockState(blockPos)) == getMaxBlockAge(mc.theWorld.getBlockState(blockPos))) {
                    if (mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= 5.8) {
                        blocks.add(blockPos);
                    }
                }
            }
        });
    }
    private int getMaxBlockAge(IBlockState blockState) {
        for (Map.Entry<IProperty, Comparable> entry : blockState.getProperties().entrySet()) {
            if (entry.getKey().getName().equals("age")) {
                int maxValue = Integer.MAX_VALUE;
                ArrayList<Integer> values = new ArrayList<>();
                entry.getKey().getAllowedValues().forEach(value -> {
                    values.add(Integer.parseInt(value.toString()));
                });
                return values.get(values.size() - 1);
            }
        }
        return Integer.MAX_VALUE;
    }
}
