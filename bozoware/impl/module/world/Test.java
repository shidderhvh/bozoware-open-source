package bozoware.impl.module.world;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.player.RotationUtils;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.movement.LongJump;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.module.player.Scaffold;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.text.DecimalFormat;

import static bozoware.impl.module.player.Scaffold.isValid;

@ModuleData(moduleName = "Test", moduleCategory = ModuleCategory.WORLD)
public class Test extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private State state;
    private Vec3 startPos;
    private double distXZ;
    private int bestBlockStack;
    private int ogSlot;
    private DecimalFormat format = new DecimalFormat("0.0");

    public Test() {
        onModuleEnabled = () -> {
            ogSlot = mc.thePlayer.inventory.currentItem;
            this.bestBlockStack = -1;
            this.state = State.PRE_INIT_JUMP;
            if (mc.thePlayer != null) {
                startPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
        };
        onModuleDisabled = () -> {
            mc.thePlayer.inventory.currentItem = ogSlot;
        };
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && this.state == State.CLIPPED) {
                this.state = State.FLYING;
            }
        });
        onPlayerMoveEvent = (e -> {
            if (this.state != State.FLYING) {
                e.setMotionX(0);
                e.setMotionZ(0);
            }
        });
        onRender2DEvent = (e -> {
            mc.fontRendererObj.drawStringWithShadow("Dist Traveled " + format.format(distXZ) + " Blocks", 418, 440, 0xFFFFFFFF);
        });
        onUpdatePositionEvent = (e -> {

            final double dffX = getDifference(startPos.xCoord, mc.thePlayer.posX), dffZ = getDifference(startPos.zCoord, mc.thePlayer.posZ);
            distXZ = Math.sqrt(dffX * dffX + dffZ * dffZ);
            if (e.isPre()) {
                this.bestBlockStack = getBlockSlot();
                if(bestBlockStack != -1)
                    mc.thePlayer.inventory.currentItem = bestBlockStack;
                if (this.bestBlockStack == -1 && this.state == State.PRE_INIT_JUMP) {
                    this.toggleModule();
                    return;
                }

                mc.thePlayer.rotationPitchHead = 90.0F;
                e.setPitch(90.0F);

                switch (this.state) {
                    case PRE_INIT_JUMP:
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.4f;
                        }
                        break;
                    case PLACED:
                        if (this.mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42f, mc.thePlayer.posZ);
                            this.state = State.JUMPED;
                        }
                        break;
                    case JUMPED:
                        if (this.mc.thePlayer.onGround) {
                            e.y -= 0.425;
                            this.state = State.CLIPPED;
                        }
                        break;
                    case FLYING:
                        this.mc.thePlayer.motionY = 0;
                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.8);
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//                            setModuleToggled(false);
                        } else if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//                            setModuleToggled(false);
                        }
                        break;
                }
            } else {
                switch (this.state) {
                    case PRE_INIT_JUMP:
                        this.place();
                        break;
                }
            }
        });
    }
    private double getDifference(double base, double yaw) {
        final double bigger;
        if (base >= yaw) {
            bigger = base - yaw;
        } else {
            bigger = yaw - base;
        }
        return bigger;
    }
    private boolean place() {
        // Cast ray directly down
        final MovingObjectPosition rayTrace = RotationUtils.rayTraceBlocks(mc, 0.0F, 90.0F);
        // If ray trace did not collide into block that can be placed on
        if (rayTrace == null || rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || rayTrace.hitVec == null)
            return false;

        final ItemStack heldItem = this.mc.thePlayer.inventoryContainer.getSlot(bestBlockStack).getStack();
        final BlockPos blockPos = rayTrace.getBlockPos();
        if (this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), blockPos, rayTrace.sideHit, rayTrace.hitVec)) {
            BozoWare.getInstance().chat("NOT GOOD");
            Wrapper.sendPacketDirect(new C0APacketAnimation());
            this.state = State.PLACED;
            return true;
        }
        return false;
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

    private enum State {
        PRE_INIT_JUMP, PLACED, JUMPED, CLIPPED, FLYING;
    }
}