package bozoware.impl.module.combat;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.player.RotationUtils;
import bozoware.base.util.visual.GLUtil;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.module.movement.Flight;
import bozoware.impl.module.movement.Speed;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

@ModuleData(moduleName = "Target Strafe", moduleCategory = ModuleCategory.COMBAT)
public class TargetStrafe extends Module {

    @EventListener
    EventConsumer<PlayerMoveEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventRender3D> onEventRender3D;

    private static int direction = -1;
    public final ValueProperty<Double> radius = new ValueProperty<>("Radius", 2.0D, 0.1D, 4D, this);
    public final BooleanProperty render = new BooleanProperty("Render Circle", true, this);
    public final ColorProperty renderColor = new ColorProperty("Circle Color", new Color(0xffff0000), this);
    public final BooleanProperty space = new BooleanProperty("On Space Held", true, this);
    public final BooleanProperty onSpeed = new BooleanProperty("Speed", true, this);
    private Aura aura;

    public TargetStrafe(){
        renderColor.setHidden(false);
        render.onValueChange = () -> {
            if(render.getPropertyValue()){
                renderColor.setHidden(false);
            } else {
                renderColor.setHidden(true);
            }
        };
        onUpdatePositionEvent = (e -> {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.switchDirection();
            }

            if (mc.gameSettings.keyBindLeft.isPressed()) {
                direction = 1;
            }

            if (mc.gameSettings.keyBindRight.isPressed()) {
                direction = -1;
            }
            doStrafeAtSpeed(e, MovementUtil.getMoveSpeed());
        });
        onEventRender3D = (e -> {
            if (this.canStrafe() && this.render.getPropertyValue()) {
                this.drawCircle(Aura.target, e.partialTicks, this.radius.getPropertyValue(), renderColor.getPropertyValue());
            }
        });
    }
    private void drawCircle(Entity entity, float partialTicks, double rad, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GLUtil.startSmooth();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().viewerPosZ;
        float r = 0.003921569F * (float)Color.WHITE.getRed();
        float g = 0.003921569F * (float)Color.WHITE.getGreen();
        float b = 0.003921569F * (float)Color.WHITE.getBlue();

        for(int i = 0; i <= 90; ++i) {
            GlStateManager.color(Objects.requireNonNull(color).getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            GL11.glVertex3d(x + rad * Math.cos((double)i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin((double)i * 6.283185307179586D / 45.0D));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GLUtil.endSmooth();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    private void switchDirection() {
        if (direction == 1) {
            direction = -1;
        } else {
            direction = 1;
        }

    }

    public final boolean doStrafeAtSpeed(PlayerMoveEvent event, final double moveSpeed) {
        final boolean strafe = canStrafe();
        if (strafe) {
            float[] rotations = RotationUtils.getNeededRotations(Aura.target);
            if(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled()){
                mc.gameSettings.keyBindJump.pressed = false;
            }
            if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(Aura.target) <= radius.getPropertyValue()) {
                MovementUtil.setSpeed(event, moveSpeed, rotations[0], direction, 0);
            } else {
                MovementUtil.setSpeed(event, moveSpeed, rotations[0], direction, 1);
            }
        }
        return strafe;
    }

    public static void setSpeed(PlayerMoveEvent updatePositionEvent, double moveSpeed) {
        setSpeed(updatePositionEvent, moveSpeed, mc.thePlayer.rotationYaw, (double)mc.thePlayer.movementInput.moveStrafe, (double)mc.thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(PlayerMoveEvent updatePositionEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0.0D) {
            if (pseudoStrafe > 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
            } else if (pseudoStrafe < 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0.0D) {
                forward = 1.0D;
            } else if (pseudoForward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        updatePositionEvent.setMotionX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        updatePositionEvent.setMotionZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }
    public boolean canStrafe(){
        if(space.getPropertyValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) != true)
            return false;
        if(onSpeed.getPropertyValue() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Speed.class).isModuleToggled())
            return false;
        return (BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class).isModuleToggled() && aura.target != null);
    }

}
