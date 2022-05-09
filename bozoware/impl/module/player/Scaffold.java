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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@ModuleData(moduleName = "Scaffol2d", moduleCategory = ModuleCategory.PLAYER)
public class Scaffold extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;
    private final ValueProperty<Long> placeDelayProperty = new ValueProperty<>("Place Delay", 50L, 0L, 500L, this);
    private final BooleanProperty swingProperty = new BooleanProperty("Swing", true, this);
    private final BooleanProperty keepYProperty = new BooleanProperty("Keep-Y", false, this);
    private final BooleanProperty customSpeedBool = new BooleanProperty("Custom Speed", true, this);
    private final ValueProperty<Double> customSpeedValue = new ValueProperty<>("Custom Speed Value", 0.22D, 0.1D, 0.5D, this);
    private final BooleanProperty timerBoostProperty = new BooleanProperty("Timer Boost", true, this);
    private final ValueProperty<Float> timerSpeedProperty = new ValueProperty<>("Timer Speed", 1.0F, 1.0F, 2.2F, this);

    public final BooleanProperty safeWalkProperty = new BooleanProperty("Safe Walk", true, this);
    private final BooleanProperty towerProperty = new BooleanProperty("Tower", false, this);

    public final BooleanProperty autoJump = new BooleanProperty("Auto Jump", false, this);
    public final BooleanProperty sprintProperty = new BooleanProperty("Sprint", false, this);
    public final BooleanProperty downwardsProperty = new BooleanProperty("Downwards", true, this);
    private float cachedTimer;
    private int facing = 2;
    public double ogY;
    public double Y;
    private double height;

    private int currentHeldItem;

    public final TimerUtil timer = new TimerUtil();
    private final TimerUtil itemTimer = new TimerUtil();

    private float[] rotations;

    private float yaw = 0F, pitch = 0F;

    public boolean placing, downwards;

    private int stage = 0;

    private final List<Packet<?>> packets = new ArrayList<>();

    public static Scaffold getInstance() {
        return (Scaffold) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Scaffold.class);
    }
    private static List<Block> invalidBlocks = Arrays.asList(
            Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser,
            Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand,
            Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever,
            Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
            Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2,
            Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars
    );

    public Scaffold() {

        onModuleDisabled = () -> {
            if (timerBoostProperty.getPropertyValue()) mc.timer.timerSpeed = cachedTimer;
            mc.thePlayer.inventory.currentItem = currentHeldItem;
            if(autoJump.getPropertyValue()) mc.gameSettings.keyBindJump.pressed = false;
        };
        onModuleEnabled = () -> {
            if (keepYProperty.getPropertyValue())
                ogY = mc.thePlayer.posY;
            packets.clear();
            placing = false;
            currentHeldItem = mc.thePlayer.inventory.currentItem;
            cachedTimer = mc.timer.timerSpeed;
            if (timerBoostProperty.getPropertyValue()) mc.timer.timerSpeed = timerSpeedProperty.getPropertyValue();

        };
        onUpdatePositionEvent = (e -> {
            if(e.isPre) {
                if (customSpeedBool.getPropertyValue()) {
                    MovementUtil.setSpeed(customSpeedValue.getPropertyValue());
                }
            }
            if(!sprintProperty.getPropertyValue())
                mc.thePlayer.setSprinting(false);

            if(keepYProperty.getPropertyValue()) {
                if ((!mc.thePlayer.isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) || (mc.thePlayer.isCollidedVertically || mc.thePlayer.onGround)) {
                    Y = MathHelper.floor_double(mc.thePlayer.posY);
                }
            } else {
                Y = MathHelper.floor_double(mc.thePlayer.posY);
            }
            BlockPos underPos = new BlockPos(mc.thePlayer.posX, Y - 1.0, mc.thePlayer.posZ);
            BlockData blockData = find(new Vec3(0, mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown() ? -1 : 0, 0));



            if (e.isPre()) {
                // if (event.getPosY() % 1 == 0) event.setOnGround(true);
                if (pitch > 90) pitch = 90;
            } else {
                if (getBlockSlot() == -1)
                    if (itemTimer.hasReached(150)) {
                        getBlocksFromInventory();
                        itemTimer.reset();
                    }
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown() && downwardsProperty.getPropertyValue()) {
                mc.thePlayer.setSneaking(false);
                downwards = true;
            } else
                downwards = false;

            if(getBlockSlot() != -1){
                e.setPitch(75F);
                mc.thePlayer.rotationPitchHead = 75F;
                float yaw = MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw + 180);
                mc.thePlayer.renderYawOffset = yaw;
                mc.thePlayer.rotationYawHead = yaw;
                e.setYaw((float) (yaw + Math.random()));
                if (autoJump.getPropertyValue() && mc.thePlayer.isMoving() && getBlockSlot() != -1) {
                    mc.gameSettings.keyBindJump.pressed = true;
                }
//            if(mc.thePlayer.isAirBorne && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
//                mc.gameSettings.keyBindJump.pressed = false;
//            }

                if (autoJump.getPropertyValue() && !mc.thePlayer.isMoving() && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
                    mc.gameSettings.keyBindJump.pressed = false;
                }
            }

            if (mc.theWorld.getBlockState(underPos).getBlock().getMaterial().isReplaceable() && blockData != null) {
                placing = true;
                if (getBlockSlot() != -1) {
                    if (e.isPre()) {
                        if (timer.hasReached(placeDelayProperty.getPropertyValue())) {
                            stage = 0;
                            if (swingProperty.getPropertyValue())
                                mc.thePlayer.swingItem();
                            else
                                mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
                        }
                    } else {
                        mc.thePlayer.inventory.currentItem = getBlockSlot();
                        double hitvecx = (blockData.position.getX() + height) + (blockData.face.getFrontOffsetX() / facing);
                        double hitvecy = (blockData.position.getY() + height) + (blockData.face.getFrontOffsetY() / facing);
                        double hitvecz = (blockData.position.getZ() + height) + (blockData.face.getFrontOffsetZ() / facing);
                        Vec3 vec = new Vec3(hitvecx, hitvecy, hitvecz);
                        if (timer.hasReached(placeDelayProperty.getPropertyValue())) {
                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockData.position, blockData.face, vec);
                            if (sprintProperty.getPropertyValue() && !mc.thePlayer.isSprinting())
                                mc.thePlayer.setSprinting(true);
                                if (mc.thePlayer.isMoving() && sprintProperty.getPropertyValue() && !autoJump.getPropertyValue()) {
                                    if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
                                        double[] xZCalculations = {
                                                -Math.sin(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed(),
                                                Math.cos(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed()
                                        };
                                        if (mc.thePlayer.onGround && mc.thePlayer.isMoving() && mc.thePlayer.ticksExisted % 4 == 0) {
                                            Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                                    e.getX() - xZCalculations[0],
                                                    e.getY(),
                                                    e.getZ() - xZCalculations[1],
                                                    mc.thePlayer.onGround
                                            ));
//                                            Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), mc.thePlayer.onGround));
                                        }

//                                    Wrapper.sendPacketDirect(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
//                                        mc.thePlayer.setSprinting(false);
                                    }
                                }

                                timer.reset();
                            }
                    }
                }
            }

            if (e.isPre && towerProperty.getPropertyValue() && Keyboard.isKeyDown(57) && !mc.thePlayer.isPotionActive(Potion.jump)) {
                mc.thePlayer.cameraPitch = 0F;
                if (MovementUtil.isOnGround(0.15) && mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionX *= 0.8;
                    mc.thePlayer.motionZ *= 0.8;
                    mc.thePlayer.motionY = 0.41999976;
                }
            }

//            if (e.isPre()) {
//                if (mc.gameSettings.keyBindJump.isKeyDown() && (towerProperty.getPropertyValue()) && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, 2, 0)).getBlock() instanceof BlockAir) {
//                    mc.thePlayer.cameraPitch = 0F;
//
//                    final double[] jumpY = {
//                            .41999998688698,
//                            .7531999805212
//                    };
//
//                    final double divideY = e.getY() % 1;
//                    final double roundY = MathHelper.floor_double(mc.thePlayer.posY);
//
//                    if (divideY > .419 && divideY < .753) {
//                        e.setY(roundY + jumpY[0]);
//                    } else if (divideY > .753) {
//                        e.setY(roundY + jumpY[1]);
//                    } else {
//                        e.setY(roundY);
//                        e.setOnGround(true);
//                    }
//
//                    if (!MovementUtil.isMoving()) {
//                        final double randomPosition = RandomUtils.nextDouble(.06, .0625);
//                        // event.setPosX(event.getPosX() + (mc.thePlayer.ticksExisted % 2 == 0 ? randomPosition : -randomPosition));
//                        // event.setPosZ(event.getPosZ() + (mc.thePlayer.ticksExisted % 2 != 0 ? randomPosition : -randomPosition));
//                    }
//                    if(MovementUtil.isMoving()){
//                        if (MovementUtil.isOnGround(0.15) && mc.gameSettings.keyBindJump.isKeyDown()) {
//                            mc.timer.timerSpeed = timerSpeedProperty.getPropertyValue();
//                            mc.thePlayer.motionX *= 0.8;
//                            mc.thePlayer.motionZ *= 0.8;
//                            mc.thePlayer.motionY = 0.41999976;
//                        }
//                    }
//
//                }
//            }
        });
        onRender2DEvent = (e -> {
            ScaledResolution SR = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            if(getBlockCount() != 1)
            mc.fontRendererObj.drawStringWithShadow("" + getBlockCount() + ChatFormatting.WHITE + " blocks", (float) SR.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(getBlockCount() + " blocks")/2, SR.getScaledHeight() / 2F + 5, HUD.getInstance().bozoColor);
            else
                mc.fontRendererObj.drawStringWithShadow("" + getBlockCount() + ChatFormatting.WHITE + " block", (float) SR.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(getBlockCount() + " block")/2, SR.getScaledHeight() / 2F + 5, HUD.getInstance().bozoColor);
        });
    }
    private static float nextPitch() {
        return 73F + RandomUtils.nextFloat(0.0F, 2.0F);
    }

    private BlockData getBlockData(final BlockPos pos) {
        if (this.isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(-1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(-1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(1, 0, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(1, 0, 0).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, 1).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, 1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, 1).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, -1, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(1, 0, 0))) {
            return new BlockData(pos.add(0, 0, -1).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, 0, -1).add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1).add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(-1, 0, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(1, 0, 0))) {
            return new BlockData(pos.add(0, -1, 0).add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, 0, 1))) {
            return new BlockData(pos.add(0, -1, 0).add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos.add(0, -1, 0).add(0, 0, -1))) {
            return new BlockData(pos.add(0, -1, 0).add(0, 0, -1), EnumFacing.SOUTH);
        }
        final BlockPos pos2 = pos.add(0, -1, 0).add(1, 0, 0);
        final BlockPos pos3 = pos.add(0, -1, 0).add(0, 0, 1);
        final BlockPos pos4 = pos.add(0, -1, 0).add(-1, 0, 0);
        final BlockPos pos5 = pos.add(0, -1, 0).add(0, 0, -1);
        if (this.isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (this.isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }
    private boolean isPosSolid(final BlockPos pos) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isFullCube()|| block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }
    public static class BlockData
    {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(final BlockPos position, final EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock && isValidBlock(((ItemBlock) stack.getItem())))
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
        return !this.invalidBlocks.contains(Block.getBlock());
    }
    private enum TowerModes{
        NCP,
        Watchdog,
        Watchdog2
    }
    private enum RotModes{
        Watchdog,
        LookDir,
        Basic,
        Behind
    }
    public static boolean contains(Block block) {
        return invalidBlocks.contains(block);
    }

    private void getBlocksFromInventory() {
        if (mc.currentScreen instanceof GuiChest)
            return;
        for (int index = 9; index < 36; index++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null)
                continue;
            if (isValid(stack.getItem())) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    public static int getBlockSlot() {
        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                continue;
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (isValid(stack.getItem()))
                return i - 36;
        }
        return -1;
    }

    public static boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        } else {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !invalidBlocks.contains(block);
        }
    }

    public BlockData find(Vec3 offset3) {

        double x = mc.thePlayer.posX;
        double y = keepYProperty.getPropertyValue() ? Y : mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);
            if (mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                continue;
            return new BlockData(offset, invert[facing.ordinal()]);
        }
        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(2, 0, 0), new BlockPos(-2, 0, 0)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
            if (!(mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir)) continue;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);
                if (mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.01f), getPositionByFace(offset, invert[facing.ordinal()])))
                    continue;
                return new BlockData(offset2, invert[facing.ordinal()]);
            }
        }
        return null;
    }

    private float[] getBlockRotations(int x, int y, int z, EnumFacing facing) {
        Entity temp = new EntitySnowball(mc.theWorld);
        temp.posX = (x + 0.5);
        temp.posY = (y + (height = 0.5));
        temp.posZ = (z + 0.5);
        return mc.thePlayer.canEntityBeSeen(temp) ? getAngles(temp) : getRotationToBlock(new BlockPos(x, y, z), facing);
    }

    private float[] getAngles(Entity e) {
        return new float[]{getYawChangeToEntity(e) + mc.thePlayer.rotationYaw, getPitchChangeToEntity(e) + mc.thePlayer.rotationPitch};
    }

    private float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity;
        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if ((deltaZ < 0) && (deltaX < 0)) {
            yawToEntity = 90 + v;
        } else {
            if ((deltaZ < 0) && (deltaX > 0.0D)) {
                yawToEntity = -90 + v;
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }
        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
    }

    private float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6D + entity.getEyeHeight() - 0.4 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity);
    }

    public float[] getRotationToBlock(BlockPos pos, EnumFacing face) {
        double random = 0.5;
        int ranface = 3;
        double xDiff = pos.getX() + (height = random) - mc.thePlayer.posX + face.getDirectionVec().getX() / 3;
        double zDiff = pos.getZ() + (height = random) - mc.thePlayer.posZ + face.getDirectionVec().getZ() / 3;
        double yDiff = pos.getY() - mc.thePlayer.posY - 1;
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) -Math.toDegrees(Math.atan2(xDiff, zDiff));
        float pitch = (float) -Math.toDegrees(Math.atan(yDiff / distance));

        return new float[]{Math.abs(yaw - mc.thePlayer.rotationYaw) < .1 ? mc.thePlayer.rotationYaw : yaw, Math.abs(pitch - mc.thePlayer.rotationPitch) < .1 ? mc.thePlayer.rotationPitch : pitch};
    }

    public Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3 offset = new Vec3((double) facing.getDirectionVec().getX() / 2.0, (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3 point = new Vec3((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5);
        return point.add(offset);
    }

    private boolean rayTrace(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        int steps = 10;
        double x = difference.xCoord / (double) steps;
        double y = difference.yCoord / (double) steps;
        double z = difference.zCoord / (double) steps;
        Vec3 point = origin;
        for (int i = 0; i < steps; ++i) {
            BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
            IBlockState blockState = mc.theWorld.getBlockState(blockPosition);
            if (blockState.getBlock() instanceof BlockLiquid || blockState.getBlock() instanceof BlockAir) continue;
            AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, blockPosition, blockState);
            if (boundingBox == null) {
                boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
            if (!boundingBox.offset(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).isVecInside(point))
                continue;
            return true;
        }
        return false;
    }

}