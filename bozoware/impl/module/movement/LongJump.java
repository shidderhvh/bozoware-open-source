package bozoware.impl.module.movement;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.player.PlayerUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;


@ModuleData(moduleName = "LongJump", moduleCategory = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.Watchdog, this);
    private final ValueProperty<Integer> Speed = new ValueProperty<>("Speed", 2, 1, 10, this);
    private final BooleanProperty glideBool = new BooleanProperty("Hover", false, this);
    private final ValueProperty<Double> glideAmount = new ValueProperty<>("Hover Amount", 5D, 5D, 10D, this);

    public TimerUtil timer = new TimerUtil();
    double speed, startY, moveSpeed;
    double lastDist;
    private int offGroundTicks;
    private int tick, stage;
    boolean boosted, lostBoost;
    boolean bowd, prevGround;
    private static final float[] YMotions = { /* Initial Jump */0.4f, 0.4f, 0.38f, 0.365f, 0.33f, 0.26f, 0.25f, 0.23f, 0.21f, 0.12f, 0.1f,/* Glide */ -0.01f, -0.05f, -0.09f, -0.12f, -0.15f, -0.18f, -0.21f, -0.24f, -0.27f, -0.3f};

    public LongJump() {
        glideAmount.setHidden(true);
        setModuleSuffix(Mode.getPropertyValue().toString());
        glideBool.onValueChange = () -> {
            glideAmount.setHidden(!glideBool.getPropertyValue());
            if(!glideBool.getPropertyValue()){
                glideAmount.setHidden(true);
            }
        };
        onModuleDisabled = () -> {

        };
        onModuleEnabled = () -> {
            offGroundTicks = 0;
            tick = 0;
            stage = 0;
            speed = 0;
            moveSpeed = 0.0D;
            lostBoost = false;
            boosted = false;
            timer.reset();
            if(mc.thePlayer.onGround){
                startY = mc.thePlayer.posY;
            }
            switch(Mode.getPropertyValue()){
                case Vanilla:

                    break;
                case Funcraft:

                    break;
                case NCP:
                    int oldSlot = mc.thePlayer.inventory.currentItem;
                    ItemStack block = mc.thePlayer.getCurrentEquippedItem();
                    int slot = mc.thePlayer.inventory.currentItem;
                    for (short g = 0; g < 9; g++) {
                        if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0 && (block == null || (block.getItem() instanceof ItemBow))) {
                            slot = g;
                            block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
                        }
                    }
                    if(slot != -1) {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(block));
                    } else {
                        this.toggleModule();
                        return;
                    }
                    break;
                case SpartanLong:
                    speed = 8;
                    break;
                case Verus:
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.2F;
                    }
                    break;
            }
        };
        onRender2DEvent = (ev -> {
            ScaledResolution sr = ev.getScaledResolution();
        });
        onPlayerMoveEvent = (e -> {
            switch(Mode.getPropertyValue()) {
                case Funcraft:
                    if(mc.thePlayer.isMoving() && !lostBoost) {
                        switch(stage) {
                            case 2:
                                ++stage;
                                double motionY = 0.4D;
                                e.setMotionY(mc.thePlayer.motionY = motionY);
                                moveSpeed *= 2.149D;
                                break;
                            case 3:
                                ++stage;
                                double difference = 0.763D * (lastDist - MovementUtil.getBaseMoveSpeed());
                                moveSpeed = lastDist - difference;
                                break;
                            default:
                                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, mc.thePlayer.motionY, 0.0D)).size() > 0 || mc.thePlayer.isCollidedVertically) {
                                    stage = 1;
                                }
                                moveSpeed = lastDist - lastDist / 159D;
                                break;
                        }
                        moveSpeed = 5 * MovementUtil.getBaseMoveSpeed() - 0.01D;
                        moveSpeed = Math.max(moveSpeed, MovementUtil.getBaseMoveSpeed());
                        MovementUtil.setSpeed(e, moveSpeed);
                        stage++;
                    } else if(mc.thePlayer.isMoving() && lostBoost) {
                        MovementUtil.setSpeed(e, MovementUtil.getBaseMoveSpeed());
                    }
                    break;
                case NCP:

                    break;
            }
        });
        onUpdatePositionEvent = (e -> {
            if(!mc.thePlayer.onGround){
                offGroundTicks++;
            } else {
                offGroundTicks = 0;
            }
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            lastDist = MovementUtil.lastDist();
            switch(Mode.getPropertyValue()) {
                case Watchdog:
                    if (MovementUtil.isMoving()) {
                        if (mc.thePlayer.onGround) {
                            lastDist = MovementUtil.lastDist();
                            if (Wrapper.getBlock(new BlockPos(x, y - 0.01, z)).getMaterial() == Material.ice) {
                                speed *= 0.1;
                            }
                            speed = Math.min(0.25 * speed, speed * 0.35);
                            mc.thePlayer.motionY = 0.42F;
                        } else {
                            speed = lastDist - lastDist / 159.0D;
                        }
                        speed = Math.max(speed, MovementUtil.getBaseMoveSpeed());
                        MovementUtil.setSpeed(speed);
                        if (mc.thePlayer.fallDistance > 0.0F && !glideBool.getPropertyValue()) {
                            this.toggleModule();
                        }
                    }
                    break;
            }
        });
        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }
    public static EntityLivingBase getClosestEntity(float range) {
        EntityLivingBase closestEntity = null;
        float mindistance = range;
        for (Object o : mc.theWorld.loadedEntityList) {
            if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (!Aura.getInstance().isValid(en)) {
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(en) < mindistance) {
                    mindistance = mc.thePlayer.getDistanceToEntity(en);
                    closestEntity = en;
                }
            }
        }
        return closestEntity;
    }
    public static boolean isNotItem(Object o) {
        if (!(o instanceof EntityLivingBase)) {
            return false;
        }
        return true;
    }
    private enum Modes{
        Watchdog,
        NCP,
        Funcraft,
        SpartanLong,
        Verus,
        Vanilla
    }
    public static Block getBlockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ)).getBlock();
    }
}