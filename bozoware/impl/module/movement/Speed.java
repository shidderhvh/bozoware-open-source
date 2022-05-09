package bozoware.impl.module.movement;

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
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector2d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static bozoware.base.util.player.MovementUtil.getBaseMoveSpeed;
import static bozoware.base.util.player.MovementUtil.isMoving;

@ModuleData(moduleName = "Speed", moduleCategory = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    private final EnumProperty<SpeedModes> speedMode = new EnumProperty<>("Mode", SpeedModes.Watchdog, this);
    private final EnumProperty<watchdogSpeedModes> watchdogSpeedMode = new EnumProperty<>("Mode", watchdogSpeedModes.Bhop, this);
    private final ValueProperty<Double> Speed = new ValueProperty<>("Speed", 5D, 0D, 10D, this);
    private final ValueProperty<Double> funnyTicks = new ValueProperty<>("Funcraft Ticks", 5D, 5D, 20D, this);
    private final BooleanProperty groundStrafeBool = new BooleanProperty("Ground Strafe", false, this);
    public final BooleanProperty ffBool = new BooleanProperty("FastFall", false, this);
    private final ValueProperty<Float> fSpeed = new ValueProperty<>("Fall Speed", 0.42F, 0.01F, 1F, this);
    public final BooleanProperty timerBool = new BooleanProperty("Timer", false, this);
    private final ValueProperty<Double> timerMin = new ValueProperty<>("Timer Min", 0.75D, 0.1D, 5D, this);
    private final ValueProperty<Double> timerMax = new ValueProperty<>("Timer Max", 1.45D, 0.1D, 5D, this);
    private final BooleanProperty flagCheckBool = new BooleanProperty("Flag Check", false, this);

    private double bms;
    double difference;
    double motionY;
    boolean boosted, reset, lastReset;
    private boolean wasOnGround;
    private int stage, hop;
    private int hops;
    private double rounded;
    private double moveSpeed, lastDist, speed;
    public double startY;
    private double hDist;
    private boolean hDistSlowdown;
    private double[] values = new double[]{0.08D, 0.09316090325960147D, 1.35D, 2.149D, 0.66D};
    public TimerUtil timer = new TimerUtil();
    static int bruh = (int) System.currentTimeMillis();
    public boolean nigger = false;
    public boolean cooldown = false;
    public boolean falling;

    public Speed() {
//        watchdogSpeedMode.setHidden(true);
//        groundStrafeBool.setHidden(true);
        funnyTicks.setHidden(true);
        timerMax.setHidden(true);
        timerMin.setHidden(true);
        fSpeed.setHidden(true);
        timerBool.onValueChange = () -> {
            timerMax.setHidden(!timerBool.getPropertyValue());
            timerMin.setHidden(!timerBool.getPropertyValue());
        };
        ffBool.onValueChange = () -> {
            fSpeed.setHidden(!ffBool.getPropertyValue());
        };
        speedMode.onValueChange = () -> {
            if(speedMode.getPropertyValue().equals(SpeedModes.Funcraft2)){
                funnyTicks.setHidden(false);
            }
            else {
                funnyTicks.setHidden(true);
            }
            if(speedMode.getPropertyValue().equals(SpeedModes.Watchdog)){
                watchdogSpeedMode.setHidden(false);
            } else
                watchdogSpeedMode.setHidden(true);
        };
        setModuleBind(Keyboard.KEY_F);
        setModuleSuffix(speedMode.getPropertyValue().toString());
        onModuleDisabled = () -> {
            mc.timer.timerSpeed = 1;
            mc.gameSettings.keyBindJump.pressed = false;
            startY = 0;
            reset = true;
            speed = 0;
            moveSpeed = 0;
            lastDist = 0;
            stage = 0;
        };
        onModuleEnabled = () -> {
            bruh = (int) System.currentTimeMillis();
            this.moveSpeed = MovementUtil.getBaseMoveSpeed();
            this.lastDist = 0.0D;
            this.stage = 0;
            reset = true;
            speed = MovementUtil.getBaseMoveSpeed();
            hops = 1;
            moveSpeed = 0;
            lastDist = 0;
            stage = 0;
        };
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && flagCheckBool.getPropertyValue()) {
                if (mc.thePlayer != null && mc.theWorld != null) {
//                    NotificationManager.show(new Notification(NotificationType.INFO, "Disabled " + this.getModuleName(), "Disabled " + this.getModuleName() +  " due to a flag.", 1F));
                    this.toggleModule();
                }
            }
        });
        onPlayerMoveEvent = (e -> {
            Vector2d vector2d;
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            switch(speedMode.getPropertyValue()) {
                case Watchdog:
                    switch(watchdogSpeedMode.getPropertyValue()) {
                        case Smooth:
                            if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                                if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.4, 3)) {
                                    e.setMotionY(mc.thePlayer.motionY = 0.31);
                                } else if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.71, 3)) {
                                    e.setMotionY(mc.thePlayer.motionY = 0.04);
                                } else if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.75, 3)) {
                                    e.setMotionY(mc.thePlayer.motionY = -0.2);
                                } else if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.55, 3)) {
                                    e.setMotionY(mc.thePlayer.motionY = -0.14);
                                } else if (MathUtil.roundToPlace(mc.thePlayer.posY - (int) mc.thePlayer.posY, 3) == MathUtil.roundToPlace(0.41, 3)) {
                                    e.setMotionY(mc.thePlayer.motionY = -0.2);
                                }
                            }


                            switch (stage) {
                                case 1:

                                    mc.thePlayer.onGround = true;
                                    e.setMotionY(e.getMotionY() + 0.004 * Math.random());

                                    if (MovementUtil.isMoving()) {
                                        moveSpeed = 1.38 * MovementUtil.getBaseMoveSpeed() - 0.01;
                                    }
                                    break;
                                case 2:
                                    if (MovementUtil.isMoving()) {
                                        if (!mc.gameSettings.keyBindJump.isKeyDown())
                                            e.setMotionY(mc.thePlayer.motionY = 0.4);
                                        moveSpeed *= 1.5;
                                    }
                                    break;
                                case 3:
                                    moveSpeed *= 0.6;
                                    break;
                                default:
                                    List<AxisAlignedBB> collide = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0));
                                    if ((!collide.isEmpty() || mc.thePlayer.isCollidedVertically) && stage > 0) {
                                        stage = 0;
                                    }
                                    moveSpeed *= 0.96;
                                    break;
                            }
                                if (mc.thePlayer.isMoving())
                                    stage++;
                                MovementUtil.setSpeed(mc.gameSettings.keyBindForward.isKeyDown() ? getBaseMoveSpeedSlow() : 0.25);
                                break;
                            }
                    break;
                case Funcraft:
                    if (isMoving()) {
//                       bms = MovementUtil.getBaseMoveSpeed();
//                       if (Wrapper.getPlayer().onGround && isMoving() && !wasOnGround) {
//                           this.wasOnGround = true;
//                           motionY = 0.42F - 0.005F;
//                           e.setMotionY(Wrapper.getPlayer().motionY + motionY);
//                           Wrapper.getPlayer().motionY = motionY;
//                           moveSpeed *= 2.149;
//                       } else if (wasOnGround) {
//                           difference = 0.66 * (lastDist - bms);
//                           moveSpeed = lastDist - difference;
//                           wasOnGround = false;
//                       } else {
//                           moveSpeed = lastDist - lastDist / 159;
//                       }
//                       mc.timer.timerSpeed = 1.085F;
//                       MovementUtil.setSpeed(e, Math.max(moveSpeed, bms));
                        bms = MovementUtil.getBaseMoveSpeed();
                        if (mc.thePlayer.onGround) {
                            if (reset) {
                                e.setMotionY(mc.thePlayer.motionY = 0.39999998);
                                speed *= 2.1449999809265137;
                            } else {
                                speed = bms;
                            }
                        } else if (reset) {
                            speed = lastDist - 0.66 * (lastDist - bms);
                        } else {
                            speed = lastDist - lastDist / 159;
                        }
                        MovementUtil.setSpeed(e, speed);
                        reset = mc.thePlayer.onGround;
                    }
                    break;

                case Funcraft2:
                    if (!mc.thePlayer.isMoving()) {
                        moveSpeed = 0;
                        MovementUtil.setSpeed(e, 0);
                        return;
                    }
                    if (mc.thePlayer.onGround) {
                        for (int i = 0; i < funnyTicks.getPropertyValue(); i++) {
                            MovementUtil.hClip(0.15);
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                        }
                        e.setMotionY(mc.thePlayer.motionY = 0.3999);
                        moveSpeed = .6;
                        nigger = true;
                    } else {
                        if (nigger) {
                            moveSpeed = .8;
                            nigger = false;
                            MovementUtil.setSpeed(e, moveSpeed);
                            return;
                        }
                        moveSpeed = Math.max(moveSpeed - moveSpeed / 154, MovementUtil.getBaseMoveSpeed());
                    }
                    MovementUtil.setSpeed(e, moveSpeed);
                    break;

                case NCPClip:
                    if (!mc.thePlayer.isMoving()) {
                        moveSpeed = 0;
                        MovementUtil.setSpeed(e, 0);
                        return;
                    }
                    if (mc.thePlayer.onGround) {
                        for (int i = 0; i < 1; i++) {
                            MovementUtil.hClip(0.16);
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                        }
                        e.setMotionY(mc.thePlayer.motionY = 0.42);
                        moveSpeed = .4;
                        nigger = true;
                    } else {
                        if (nigger) {
                            moveSpeed = .4;
                            nigger = false;
                            MovementUtil.setSpeed(e, moveSpeed);
                            return;
                        }
                        moveSpeed = Math.max(moveSpeed - moveSpeed / 159, MovementUtil.getBaseMoveSpeed());
                    }
                    MovementUtil.setSpeed(e, moveSpeed);
                    break;
            }
        });
        onUpdatePositionEvent = (e -> {
            if (timerBool.getPropertyValue()) {
                double timerMinClamped = MathHelper.clamp_double(timerMin.getPropertyValue(), 0.1D, timerMax.getPropertyValue() - 0.1D);
                double bruh = ThreadLocalRandom.current().nextDouble(timerMinClamped, timerMax.getPropertyValue());
                mc.timer.timerSpeed = (float) bruh;
            }
            if(cooldown){
                timer.reset();
                if(this.timer.hasReached(500L))
                    cooldown = false;
                return;
            }
            lastDist = MovementUtil.lastDist();
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            if(mc.thePlayer.onGround){
                startY = mc.thePlayer.posY;
            }
            if (timerBool.getPropertyValue()) {
                double timerMinClamped = MathHelper.clamp_double(timerMin.getPropertyValue(), 0.1D, timerMax.getPropertyValue() - 0.1D);
                double bruh = ThreadLocalRandom.current().nextDouble(timerMinClamped, timerMax.getPropertyValue());
            }
            switch (speedMode.getPropertyValue()) {
                case LowHop:
                    if (e.isPre()) {
                        if(mc.thePlayer.isCollidedHorizontally){
//                            mc.gameSettings.keyBindJump.pressed = true;
                        }
                        if (mc.thePlayer.onGround && !mc.thePlayer.isCollidedHorizontally) {
//                            mc.gameSettings.keyBindJump.pressed = true;
//                            mc.timer.timerSpeed = 1.2F;
                            mc.thePlayer.onGround = false;
                            mc.thePlayer.motionY = 0.35F;
                        } else {
                            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                                    mc.gameSettings.keyBindJump.pressed = true;
//                                e.y -= 0.0005;
                                    speed = 1.17f;
                            } else {
                                mc.gameSettings.keyBindJump.pressed = false;
                                if (speed > 0.94) {
                                    speed -= 0.01;
                                }
                            }
                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.1);
//                            mc.timer.timerSpeed = 1.09F;
                        }
                    }
                    break;
                case Packet:
                    if ((mc.thePlayer.onGround) && (!mc.thePlayer.isSneaking()) &&
                            (!mc.gameSettings.keyBindSneak.pressed)) {
                        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.5D, 0.0D).expand(-0.04D, 0.0D, -0.04D)).isEmpty()) {
                            mc.gameSettings.keyBindJump.pressed = true;
                        } else {
                            if(!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()))
                                mc.gameSettings.keyBindJump.pressed = false;
                        }
                    }
                    double[] xZCalculations = {
                            -Math.sin(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed(),
                            Math.cos(Math.toRadians(e.getYaw())) * MovementUtil.getBaseMoveSpeed()
                    };
                    if (e.isPre() && mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                        Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(
                                e.getX() - xZCalculations[0],
                                e.getY(),
                                e.getZ() - xZCalculations[1],
                                e.isOnGround()));
                    }
                    if (mc.thePlayer.onGround && mc.thePlayer.isMoving())
                        if(mc.thePlayer.ticksExisted % 2 == 0) {
                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.1);
                        } else {
                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
                        }
                    break;
                case Motion:
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
//                    if (mc.thePlayer.ticksExisted % 2 == 0) {
//                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
//                    }
                    if (Wrapper.getPlayer().onGround && Wrapper.getPlayer().isMoving()) {
                        Wrapper.getPlayer().motionY = 0.42F;
                    }
                    MovementUtil.setMoveSpeed(Speed.getPropertyValue());
                    break;
                case Viper:
                    int groundY = (int) mc.thePlayer.posY;
                    boolean canJump = false;
                if(mc.thePlayer.onGround) {
                    canJump = true;
                    groundY = (int) mc.thePlayer.posY;
                }
                if(mc.thePlayer.isMoving())
                if(canJump && groundY <= mc.thePlayer.posY) {
                    mc.thePlayer.motionY = 0.5f;
                    e.setOnGround(true);
                } else {
                    canJump = false;
                }
                if(!canJump && !mc.thePlayer.onGround){
                    mc.thePlayer.motionY = -0.11f;
                    e.setOnGround(true);
                }
                if(mc.thePlayer.isCollidedVertically){
                    canJump = false;
                }
//                    if(mc.thePlayer.onGround){
//                        mc.gameSettings.keyBindJump.pressed = true;
//                    }
                    final double height = this.mc.thePlayer.getEntityBoundingBox().minY - this.mc.thePlayer.posY;
                    final boolean canStep = !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled();

                    if((canStep && height >= 0.625 && mc.thePlayer.isCollidedHorizontally) || mc.gameSettings.keyBindJump.pressed)
                        mc.thePlayer.motionY = 0.32;
//                    if(!mc.thePlayer.onGround && !mc.thePlayer.isCollidedHorizontally)
//                        mc.thePlayer.motionY = -0.3;
                    MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.125);
                    break;
                case NCP:
                    motionY = 0.25F - 0.005F;
                    MovementUtil.Dynamic(MovementUtil.getBaseMoveSpeed(), (float) motionY, 159, 0.66, false, false, 0, 0);
                    if (mc.thePlayer.isAirBorne && ffBool.getPropertyValue() && mc.thePlayer.posY == startY + 0.42F) {
                        mc.thePlayer.motionY = -fSpeed.getPropertyValue();
                    }
                    break;
                case SpartanHop:
                    if(isMoving()){
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.motionY = 0.42F;
                            moveSpeed = 1.3;
                        } else if (mc.thePlayer.isAirBorne){
                            if(mc.thePlayer.posY > startY && mc.thePlayer.fallDistance > 0){
                                moveSpeed = 1.1;
                            }
                            MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * moveSpeed - Math.random() / 450);
                        }
                    }
                    break;
                case SpartanGround:
                    if (mc.thePlayer.onGround) {
                        if (isMoving()) {
                            mc.thePlayer.motionY = 0.42F;
                            speed = 1.28f;
                        }
                    }
                    if (mc.thePlayer.isAirBorne) {
                        if (isMoving()) {
                            mc.thePlayer.motionY = -0.42F;
                            mc.gameSettings.keyBindJump.pressed = false;
                            if (speed > 0.94) {
                                speed -= 0.01;
                            }
                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * speed - Math.random() / 450);
                        }
                    }
                    break;
                case Watchdog:
//                    e.setYaw(MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw));
                    switch(watchdogSpeedMode.getPropertyValue()) {
                        case Bhop:
                            if (groundStrafeBool.getPropertyValue()) {
                            if (!reset) {
//                            e.setY(e.y + 0.0001);
//                            e.setOnGround(false);
//                                mc.thePlayer.motionY = 0.26F;
                                if (!Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()) && !Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
                                    MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * 1.125);
                            }
                        }
                        if (!groundStrafeBool.getPropertyValue()) {
                            if (mc.thePlayer.onGround) {
                                if (mc.thePlayer.isMoving()) {
//                                mc.thePlayer.onGround = true;
                                }
                                falling = false;
                                if (isMoving()) {
                                    mc.gameSettings.keyBindJump.pressed = true;
//                                e.y -= 0.0005;
                                    if((!Keyboard.isKeyDown(30) && !Keyboard.isKeyDown(31) && !Keyboard.isKeyDown(32) && Keyboard.isKeyDown(17)) || Keyboard.isKeyDown(30) && Keyboard.isKeyDown(32) && Keyboard.isKeyDown(17) && !Keyboard.isKeyDown(31))
                                    speed = 1.15f;
                                    else
                                        speed = 1.05f;
                                }
                            } else {
                                mc.gameSettings.keyBindJump.pressed = false;
                                if (speed > 0.94) {
                                    speed -= 0.01;
                                }
                                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * speed - Math.random() / 500);
                            }
                        }
                        if (groundStrafeBool.getPropertyValue()) {
                            if (isMoving()) {
                                if (!mc.thePlayer.onGround && Keyboard.isKeyDown(30) || Keyboard.isKeyDown(32)) {
                                    mc.gameSettings.keyBindJump.pressed = true;
                                    return;
                                }
                                if (mc.thePlayer.onGround) {
                                    mc.gameSettings.keyBindJump.pressed = true;
//                                mc.thePlayer.motionY = 0.26F;
                                    if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//                                    MovementUtil.setMoveSpeed(0.19);
                                    } else {
//                                    MovementUtil.setMoveSpeed(0.24);
                                    }
//                                mc.thePlayer.motionY = (0.26 - (0.26 / 1.4));
                                    reset = false;
                                } else {
                                    mc.gameSettings.keyBindJump.pressed = false;
                                    if (!reset) {
//                                mc.thePlayer.motionY = 0.26F;
//                                    e.setY(e.y + 0.0001);
//                                    e.setOnGround(false);
                                        if (!Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()) && !Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
                                            MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * 1.125);
                                    }
                                    MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * 0.9);
                                    reset = true;
                                }
                            } else {
                                mc.gameSettings.keyBindJump.pressed = false;
                            }
                        }
                        break;
                    }
                    break;
                case VerusLowHop:
                    if(isMoving() && !mc.thePlayer.onGround && MovementUtil.isOnGround(1F)){
                        mc.thePlayer.motionY = -0.07840000152587834;
                        MovementUtil.setSpeed(0.3);
                    }
                    if(isMoving() && mc.thePlayer.onGround){
                        mc.thePlayer.jump();
                        MovementUtil.setMoveSpeed(0.5);
                    }
                    break;
                case Verus:
                    MovementUtil.setMoveSpeed(0.36);
                    if(Wrapper.getPlayer().onGround) {
                        Wrapper.getPlayer().jump();
                        MovementUtil.setMoveSpeed(0.5);
                    }
                    break;
                case VerusAir:
                    if(isMoving()) {
                        if(mc.thePlayer.onGround){
                            mc.thePlayer.motionY = 0.42F;
                        }
                        else {
                            if (MovementUtil.isOnGround(3F)) {
                                mc.thePlayer.motionY = -0.07840000152587834;
                            }
                            if(mc.thePlayer.isCollidedHorizontally){
                                mc.thePlayer.motionY = 0.42F;
                            }
                            if(mc.thePlayer.fallDistance >= 2 && e.getY() < mc.thePlayer.posY) {
                                mc.thePlayer.onGround = true;
                            }
                        }
                        MovementUtil.setMoveSpeed(mc.thePlayer.ticksExisted % 4 == 0 ? 0.36 : 0.3);
                    }
                    break;
                    case YPort:
                    e.setY(e.getY() + (lastReset ? 0.42F : 0.001));
//                    mc.thePlayer.posY = e.getY() + (lastReset ? 0.42F : 0.001);
                    if(mc.thePlayer.onGround) {
                        lastReset = mc.thePlayer.ticksExisted % 2 == 0;
                        MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * 1.125 - Math.random() / 150);
                    }
                    BozoWare.getInstance().chat(String.valueOf(lastReset));
                    break;
                case Dev:
                    if(isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.26F;
                            MovementUtil.setMoveSpeed(0.19);
                            mc.thePlayer.motionY = (0.26-(0.26 / 1.4));
                            reset = false;
                        } else {
                            if (!reset) {
                                mc.thePlayer.motionY = 0.26F;
                                MovementUtil.setMoveSpeed(0.35);
                            }
                            reset = true;
                        }
                    }
                    break;
            }
        });
        watchdogSpeedMode.onValueChange = () -> {
            if(watchdogSpeedMode.getPropertyValue().equals(watchdogSpeedModes.Smooth)){
                groundStrafeBool.setHidden(true);
            } else
                groundStrafeBool.setHidden(false);
        };
        speedMode.onValueChange = () -> {
            if(speedMode.getPropertyValue().equals(SpeedModes.Watchdog)){
                watchdogSpeedMode.setHidden(false);
            } else {
                watchdogSpeedMode.setHidden(true);
            }
            setModuleSuffix(speedMode.getPropertyValue().name());
        };
    }
    private enum SpeedModes {
//        Bhop,
        NCP,
        NCPClip,
        Watchdog,
        Watchdog2,
        Motion,
        LowHop,
        SpartanHop,
        SpartanGround,
        Packet,
        Viper,
        YPort,
        Funcraft,
        Funcraft2,
        Verus,
        VerusAir,
        VerusLowHop,
        Dev
    }
    private enum watchdogSpeedModes {
        Smooth,
        Bhop
    }
        public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static double getBaseMoveSpeedSlow() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.11 * (amplifier + 1);
        }
        return baseSpeed;
    }
}