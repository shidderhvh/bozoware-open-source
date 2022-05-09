package bozoware.impl.module.player;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.MathUtil;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.visual.BloomUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import bozoware.visual.font.MinecraftFontRenderer;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@ModuleData(moduleName = "Scaffold", moduleCategory = ModuleCategory.PLAYER)
public class BlockFly extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final ValueProperty<Double> expandAmount = new ValueProperty<>("Expand Amount", 3D, 0D, 12D, this);
    private final EnumProperty<RotModes> RotMode = new EnumProperty<>("Rotation Mode", RotModes.Watchdog, this);
    private BooleanProperty keepY = new BooleanProperty("Keep-Y", true, this);
    private BooleanProperty autoJump = new BooleanProperty("Auto Jump", true, this);
    private BooleanProperty hideJumps = new BooleanProperty("Hide Jumps", true, this);
    private BooleanProperty customSpeedBool = new BooleanProperty("Custom Speed", false, this);
    private ValueProperty<Double> customSpeed = new ValueProperty<>("Custom Speed Value", 0.3D, 0.1D, 0.5D, this);
    private final BooleanProperty RPBool = new BooleanProperty("Random Pitch", false, this);
    private final BooleanProperty towerBool = new BooleanProperty("Tower", false, this);
    private final EnumProperty<TowerModes> towerMode = new EnumProperty<>("Tower Mode", TowerModes.NCP, this);
    private final BooleanProperty switchBool = new BooleanProperty("Switch To Block", true, this);
    public final BooleanProperty downWardsBool = new BooleanProperty("Downwards", true, this);
    public final BooleanProperty swBool = new BooleanProperty("SafeWalk", true, this);
    private final BooleanProperty noSwing = new BooleanProperty("NoSwing", true, this);
    private final BooleanProperty CD0 = new BooleanProperty("ClickDelay0", true, this);
    private final BooleanProperty normalizeVec = new BooleanProperty("Normalize Vec", true, this);
    private final BooleanProperty slowedBool = new BooleanProperty("Slow Movement", true, this);
    private final BooleanProperty noSprintBool = new BooleanProperty("No Sprint", true, this);
    public final BooleanProperty timerBool = new BooleanProperty("Timer", true, this);
    private final ValueProperty<Double> timerMin = new ValueProperty<>("Timer Min", 0.75D, 0.1D, 5D, this);
    private final ValueProperty<Double> timerMax = new ValueProperty<>("Timer Max", 1.45D, 0.1D, 5D, this);
    private double xPosi;
    private double xPosition;


    static BlockPos blockBef, NCP, blockUnder;
    EnumFacing facing = null;
    boolean placing = false;
    private static double Y;
    BlockData blockdata;
    BlockData data;
    private static double yOnEnable;
    private final Vector3d vec = new Vector3d();
    public TimerUtil timer = new TimerUtil();
    private final List<Block> invalid;
    double YPos;
    private int slot, lastSlot, blockCount;
    ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

    public static BlockFly getInstance() {
        return (BlockFly) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class);
    }

    public BlockFly() {
        customSpeed.setHidden(true);
        hideJumps.setHidden(false);
        autoJump.onValueChange = () ->{
            if(autoJump.getPropertyValue()){
                hideJumps.setHidden(false);
            } else {
                hideJumps.setHidden(true);
            }
        };
        customSpeedBool.onValueChange = () ->{
            if(customSpeedBool.getPropertyValue()){
                customSpeed.setHidden(false);
            } else {
                customSpeed.setHidden(true);
            }
        };

        invalid = Arrays.asList(Blocks.beacon, Blocks.nether_wart, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.snow_layer, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.crafting_table, Blocks.furnace, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,  Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.flower_pot, Blocks.red_flower, Blocks.yellow_flower, Blocks.waterlily, Blocks.double_plant);
//        setModuleSuffix(Mode.getPropertyValue().toString());
        setModuleBind(Keyboard.KEY_M);
//        timerMax.setHidden(true);
//        timerMin.setHidden(true);
        timerBool.onValueChange = () -> {
            timerMax.setHidden(!timerBool.getPropertyValue());
            timerMin.setHidden(!timerBool.getPropertyValue());
        };
        onModuleDisabled = () -> {
            mc.gameSettings.keyBindJump.pressed = false;
            mc.timer.timerSpeed = 1;
            if (switchBool.getPropertyValue()) {
                mc.thePlayer.inventory.currentItem = lastSlot;
            } else {
                Wrapper.sendPacketDirect(new C09PacketHeldItemChange(lastSlot));
            }
            blockUnder = null;
            blockBef = null;
        };
        onModuleEnabled = () -> {
            xPosi = sr.getScaledWidth() /2;
            xPosition = sr.getScaledWidth() /2;
            yOnEnable = mc.thePlayer.posY;
            Y = MathHelper.floor_double(mc.thePlayer.posY);
            if (switchBool.getPropertyValue() && mc.thePlayer.inventory.getCurrentItem() != null) {
                lastSlot = mc.thePlayer.inventory.currentItem;
            }
            blockUnder = null;
            blockBef = null;
        };
        onUpdatePositionEvent = (e -> {
            if(mc.thePlayer.onGround){
                yOnEnable = mc.thePlayer.posY;
            }
            if(hideJumps.getPropertyValue() && autoJump.getPropertyValue()) {
                if (!(yOnEnable > mc.thePlayer.posY) && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                    mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                }
            }
            if (autoJump.getPropertyValue() && mc.thePlayer.isMoving() && getBlockSlot() != -1 && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                mc.gameSettings.keyBindJump.pressed = true;
            }
//            if(mc.thePlayer.isAirBorne && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
//                mc.gameSettings.keyBindJump.pressed = false;
//            }

            if (autoJump.getPropertyValue() && !mc.thePlayer.isMoving() && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
                mc.gameSettings.keyBindJump.pressed = false;
            }
            if(customSpeedBool.getPropertyValue() && getBlockSlot() != -1){
                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * (1 + customSpeed.getPropertyValue()) / 1.5);
            }
            blockCount = getBlockCount();

            if (e.isPre()) {
                if (switchBool.getPropertyValue()) {
                    if (getBlockSlot() != -1) {
//                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(getBlockSlot()));
                        mc.thePlayer.inventory.currentItem = getBlockSlot();
                    } else {
//                        mc.thePlayer.inventory.currentItem = lastSlot;
//                        mc.thePlayer.setSprinting(true);
                        return;
                    }
                }
                if (slowedBool.getPropertyValue()) {
                    if(mc.thePlayer.isPotionActive(Potion.moveSpeed))
                        MovementUtil.setMoveSpeed(0.125);
                        else
                    MovementUtil.setMoveSpeed(0.11);
                }
                if (noSprintBool.getPropertyValue()) {
                    mc.thePlayer.setSprinting(false);
                } else
                if (Wrapper.getPlayer().isMovingForward() && (Wrapper.getPlayer().getFoodStats().getFoodLevel() > 6 || Wrapper.getPlayer().capabilities.isCreativeMode) && !mc.thePlayer.isCollidedHorizontally)
                    mc.thePlayer.setSprinting(true);
                if (timerBool.getPropertyValue()) {
                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                        mc.timer.timerSpeed = 1;
                    }
                    if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {

                        double timerMinClamped = MathHelper.clamp_double(timerMin.getPropertyValue(), 0.1D, timerMax.getPropertyValue() - 0.1D);
                        double bruh = ThreadLocalRandom.current().nextDouble(timerMinClamped, timerMax.getPropertyValue());
                        mc.timer.timerSpeed = (float) bruh;
                    }
                }
                if (CD0.getPropertyValue()) {
                    mc.rightClickDelayTimer = 0;
                } else {
                    mc.rightClickDelayTimer = 6;
                }
//                if (mc.thePlayer.getCurrentEquippedItem().getItem() == null || mc.thePlayer.getCurrentEquippedItem() == null) return;
                double x = mc.thePlayer.posX;
                double z = mc.thePlayer.posZ;
                double y = mc.thePlayer.posY;
                blockUnder = null;
                blockBef = null;
                if(keepY.getPropertyValue()) {
                    if ((!mc.thePlayer.isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) || (mc.thePlayer.isCollidedVertically || mc.thePlayer.onGround)) {
                        Y = MathHelper.floor_double(mc.thePlayer.posY);
                    }
                } else {
                    Y = MathHelper.floor_double(mc.thePlayer.posY);
                }
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.thePlayer.isCollidedHorizontally && downWardsBool.getPropertyValue()) {
                    mc.thePlayer.setSneaking(false);
                    mc.gameSettings.keyBindSneak.pressed = false;
                    Y = MathHelper.floor_double(mc.thePlayer.posY) - 1;
                }
                        for (double n2 = expandAmount.getPropertyValue() + 0.0001, n3 = 0.0; n3 <= n2; n3 += n2 / (Math.floor(n2))) {
                            if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()))
                            n2 = 0.0001;
                                NCP = new BlockPos(mc.thePlayer.posX - MathHelper.sin(MathUtil.clampRotation()) * n3, Y - 1.0, mc.thePlayer.posZ + MathHelper.cos(MathUtil.clampRotation()) * n3);
                            if (Wrapper.getBlock(NCP).getMaterial() == Material.air) {
                                blockUnder = new BlockPos(mc.thePlayer.posX - MathHelper.sin(MathUtil.clampRotation()) * n3, Y - 1.0, mc.thePlayer.posZ + MathHelper.cos(MathUtil.clampRotation()) * n3);
                                for (EnumFacing facing : EnumFacing.values()) {
                                    BlockPos offset = blockUnder.offset(facing);
                                    if (Wrapper.getBlock(offset).getMaterial() != Material.air) {
                                        this.facing = facing;
                                        blockBef = offset;
                                        break;
                                    }
                                }
                                final IBlockState blockState = mc.theWorld.getBlockState(blockUnder);
                                if (blockState != null && blockState.getBlock() == Blocks.air) {
                                    NCP = blockUnder;
                                    break;
                                }
                            }
                        }
                switch (RotMode.getPropertyValue()) {
                    case Basic:
                        if (blockBef != null) {
                            float[] rots = Wrapper.getFacePos(Wrapper.getVec3(blockBef));
                            float yaw = mc.thePlayer.rotationYaw;
                            double dist = 0;
                            final BlockPos underPos = new BlockPos(x + (-Math.sin(Math.toRadians(yaw)) * dist), y, z + (Math.cos(Math.toRadians(yaw)) * dist));
                            data = this.getBlockData(underPos);
                        } else {
                            if(e.getYaw() != mc.thePlayer.prevRotationYaw + 180)
                            e.setYaw(mc.thePlayer.prevRotationYaw + 180);
                            mc.thePlayer.rotationYawHead = mc.thePlayer.prevRotationYaw + 180;
                            mc.thePlayer.renderYawOffset = mc.thePlayer.prevRotationYaw + 180;
                            if (!RPBool.getPropertyValue()) {
                                e.setPitch(75);
                                mc.thePlayer.rotationPitchHead = 75;
                            } else {
                                float rpitch = ThreadLocalRandom.current().nextInt(75, 90);
                                e.setPitch(rpitch);
                                mc.thePlayer.rotationPitchHead = rpitch;
                            }
                        }
                        break;
                    case Watchdog:
                        if (!RPBool.getPropertyValue()) {
                            e.setPitch(75);
                            mc.thePlayer.rotationPitchHead = 75;
                        } else {
                            float rpitch = ThreadLocalRandom.current().nextInt(75, 90);
                            e.setPitch(rpitch);
                            mc.thePlayer.rotationPitchHead = rpitch;
                        }
                        if (blockBef != null) {
                            float[] rots = Wrapper.getFacePos(Wrapper.getVec3(blockBef));
                            e.setPitch(rots[1]);
                            mc.thePlayer.rotationPitchHead = rots[1];
                            e.setYaw(rots[0]);
                            mc.thePlayer.rotationYawHead = rots[0];
                            mc.thePlayer.renderYawOffset = rots[0];
                        } else {
                            e.setYaw(mc.thePlayer.rotationYaw + 180);
                            mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 180;
                            mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 180;
                        }
                        break;
                    case LookDir:
                        e.setPitch(75F);
                        mc.thePlayer.rotationPitchHead = 75F;
                        float yaw = MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw + 180);
                        mc.thePlayer.renderYawOffset = yaw;
                        mc.thePlayer.rotationYawHead = yaw;
                        e.setYaw((float) (yaw + Math.random()));
                        break;
                    case Behind:
                        if (!RPBool.getPropertyValue()) {
                            e.setPitch(75);
                            mc.thePlayer.rotationPitchHead = 75;
                        } else {
                            float rpitch = ThreadLocalRandom.current().nextInt(75, 90);
                            e.setPitch(rpitch);
                            mc.thePlayer.rotationPitchHead = rpitch;
                        }
                        e.setYaw(mc.thePlayer.rotationYaw + 180);
                        mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 180;
                        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 180;
                        break;
                }
            }
            if (!e.isPre()) {
                placing = false;
                if (blockUnder == null) return;
                if (blockBef == null) return;
                placing = true;
                BlockPos pos2 = new BlockPos(blockBef.getX(), blockBef.getY(), blockBef.getZ());
                MovingObjectPosition pos = mc.theWorld.rayTraceBlocks(Wrapper.getVec3(blockUnder).addVector(0.5, 0.5, 0.5),
                        Wrapper.getVec3(blockBef).addVector(0.5, 0.5, 0.5));
                if (pos == null) {
                    return;
                }
                Vec3 hitVec = Wrapper.getVec3(blockUnder);
                    if (blockBef != null) {
                        if (normalizeVec.getPropertyValue() ? mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), pos2, pos.sideHit, hitVec.normalize()) : mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), pos2, pos.sideHit, hitVec)) {
                            if(mc.thePlayer.isMoving()) {
                                if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net") && !noSprintBool.getPropertyValue()) {
                                    double[] xZCalculations = {
                                            -Math.sin(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed(),
                                            Math.cos(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed()
                                    };
                                    if(mc.thePlayer.onGround && mc.thePlayer.isMoving())
                                    Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                            e.getX() + xZCalculations[0],
                                            e.getY(),
                                            e.getZ() + xZCalculations[1],
                                            mc.thePlayer.onGround
                                    ));

//                                    Wrapper.sendPacketDirect(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
//                                        mc.thePlayer.setSprinting(false);
                                }
                            }
                            if (noSwing.getPropertyValue()) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            } else {
                                mc.thePlayer.swingItem();
                            }
                            if(mc.thePlayer.isMoving() && mc.timer.timerSpeed == 0.5f && !timerBool.getPropertyValue()){
                                mc.timer.timerSpeed = 1;
                            }
                            if (towerBool.getPropertyValue() && Keyboard.isKeyDown(57) && !mc.thePlayer.isPotionActive(Potion.jump)) {
                                switch (towerMode.getPropertyValue()) {
                                    case NCP:
                                        if(!e.isPre) {
                                            if (getBlockSlot() != -1 && mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isMoving()) {
                                                mc.thePlayer.motionZ = 0;
                                                mc.thePlayer.motionX = 0;
                                                if (mc.thePlayer.onGround) {
                                                    mc.thePlayer.jump();
                                                    if (timer.hasReached(1500)) {
                                                        mc.thePlayer.motionY = -0.28;
                                                        timer.reset();
                                                    }
                                                }
                                                mc.thePlayer.motionY = .41955;
                                            }
                                        }
                                        mc.timer.timerSpeed = 0.5f;
                                        break;
                                    case Watchdog:
                                        mc.thePlayer.cameraPitch = 0F;
                                        if (MovementUtil.isOnGround(0.002) && mc.gameSettings.keyBindJump.isKeyDown()) {
                                                mc.thePlayer.motionX *= 0.5;
                                                mc.thePlayer.motionZ *= 0.5;
                                                mc.thePlayer.motionY = 0.41999976;
                                        }
                                        break;
                                    case Watchdog2:
                                        if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isMoving() && !mc.thePlayer.isPotionActive(Potion.jump)) {
                                            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                                            mc.thePlayer.motionY = -0.28f;
//                                            mc.timer.timerSpeed = 1.5f;
                                        } else {
                                        }
                                        break;
                                    case Slow:
                                        mc.thePlayer.motionY += -1;
                                        mc.thePlayer.motionY += 0.42;
                                        break;
                                }
                            }
                        }
                    }
            }
        });
        onRender2DEvent = (e -> {
            MinecraftFontRenderer LFR = BozoWare.getInstance().getFontManager().largeFontRenderer;
            MinecraftFontRenderer LFR2 = BozoWare.getInstance().getFontManager().largeFontRenderer2;
            ScaledResolution SR = e.getScaledResolution();
            xPosi = SR.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(blockCount + " blocks")/2;
            xPosition = RenderUtil.animate(xPosi, this.xPosition, 0.05D);
            if(blockCount != 1) {
                mc.fontRendererObj.drawStringWithShadow("" + blockCount + ChatFormatting.WHITE + " blocks", (float) xPosition, SR.getScaledHeight() / 2F + 5, HUD.getInstance().bozoColor);
                BloomUtil.bloom(() -> mc.fontRendererObj.drawStringWithShadow("" + blockCount, (float) xPosition, (float) (SR.getScaledHeight() / 2F + 4.75), HUD.getInstance().bozoColor));
            }else {
                mc.fontRendererObj.drawStringWithShadow("" + blockCount + ChatFormatting.WHITE + " block", (float) xPosition, SR.getScaledHeight() / 2F + 5, HUD.getInstance().bozoColor);
                BloomUtil.bloom(() -> mc.fontRendererObj.drawStringWithShadow("" + blockCount, (float) xPosition, (float) (SR.getScaledHeight() / 2F + 4.75), HUD.getInstance().bozoColor));
                //            LFR.drawStringWithShadow("Blocks " + blockCount, SR.getScaledWidth() / 2F - 20F, SR.getScaledHeight() / 2F + 25F, -1);
            }
        });
//        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }
    private BlockData getBlockData(final BlockPos pos) {
        if (this.isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(0, -1, 0).add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(0, -1, 0).add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        final BlockPos pos2 = pos.add(0, -1, 0).add(1, 0, 0);
        final BlockPos pos3 = pos.add(0, -1, 0).add(0, 0, 1);
        final BlockPos pos4 = pos.add(0, -1, 0).add(-1, 0, 0);
        final BlockPos pos5 = pos.add(0, -1, 0).add(0, 0, -1);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        if (this.isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP, (BlockData)null);
        }
        if (this.isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST, (BlockData)null);
        }
        if (this.isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST, (BlockData)null);
        }
        if (this.isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH, (BlockData)null);
        }
        if (this.isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH, (BlockData)null);
        }
        return null;
    }
    private boolean isPosSolid(final BlockPos pos) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isFullCube()|| block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }
    private class BlockData
    {
        public BlockPos position;
        public EnumFacing face;

        private BlockData(final BlockPos position, final EnumFacing face, BlockData blockData) {
            this.position = position;
            this.face = face;
        }
    }
    private enum Modes {
        NCP,
        Expand,
        ReallyBad
    }
    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBlock && isValidBlock(((ItemBlock) stack.getItem())))
                blockCount += stack.stackSize;
        }
        return blockCount;
    }
    public Vec3 getVec3(final BlockPos pos, final EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += face.getFrontOffsetX() / 2.0;
        z += face.getFrontOffsetZ() / 2.0;
        y += face.getFrontOffsetY() / 2.0;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += getRandomInRange(0.25, -0.25);
            z += getRandomInRange(0.25, -0.25);
        }
        else {
            y += getRandomInRange(0.25, -0.25);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += getRandomInRange(0.25, -0.25);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += getRandomInRange(0.25, -0.25);
        }
        return new Vec3(x, y, z);
    }
    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }
    private boolean isValidBlock(ItemBlock Block) {
        Block block = Block.getBlock();
        if(Block.getBlock() instanceof BlockFlower)
            return false;
        if(Block.getBlock() instanceof BlockDoublePlant)
            return false;

        return !this.invalid.contains(block);
    }
    private int getBlockSlot() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && isValidBlock((ItemBlock) itemStack.getItem())) {
                return i - 36;
            }
        }
        return -1;
    }
    private enum TowerModes{
        NCP,
        Vanilla,
        Slow,
        Watchdog,
        Watchdog2
    }
    private enum RotModes{
        Watchdog,
        LookDir,
        Basic,
        Behind
    }
}