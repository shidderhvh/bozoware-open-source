package bozoware.impl.module.movement;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.player.PlayerUtils;
import bozoware.impl.event.block.EventAABB;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.module.player.Scaffold;
import bozoware.impl.module.world.PingSpoofer;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BlockData;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.toRadians;

@ModuleData(moduleName = "Flight", moduleCategory = ModuleCategory.MOVEMENT)
public class Flight extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;
    @EventListener
    EventConsumer<EventAABB> onEventAABB;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    private final EnumProperty<FlightModes> flightModeProperty = new EnumProperty<>("Mode", FlightModes.Motion, this);
    private final EnumProperty<VerusModes> VerusModeProperty = new EnumProperty<>("Verus Mode", VerusModes.Basic, this);
    private final ValueProperty<Double> Speed = new ValueProperty<>("Speed", 1D, 0D, 30D, this);
    private final EnumProperty<damageModes> damageMode = new EnumProperty<>("Damage Mode", damageModes.Basic, this);
    public final BooleanProperty ColOrMotion = new BooleanProperty("Verus Collision", true, this);
    public final BooleanProperty verusntBool = new BooleanProperty("Damage Timer", false, this);
    public final BooleanProperty damageBool = new BooleanProperty("Damage", false, this);
    public final BooleanProperty boostBool = new BooleanProperty("Boost", false, this);
    public final BooleanProperty timerBool = new BooleanProperty("Timer", false, this);
    private final ValueProperty<Double> timerMin = new ValueProperty<>("Timer Min", 0.75D, 0.1D, 5D, this);
    private final ValueProperty<Double> timerMax = new ValueProperty<>("Timer Max", 1.45D, 0.1D, 5D, this);
    private final BooleanProperty viewBobBool = new BooleanProperty("View-Bobbing", true, this);
    private final BooleanProperty flagCheckBool = new BooleanProperty("Flag Check", false, this);
    private final BooleanProperty stopOnDisable = new BooleanProperty("Stop on Disable", false, this);
    LinkedList<Packet> packets = new LinkedList<>();


    public double moveSpeed;
    public TimerUtil timer = new TimerUtil();
    TimerUtil blinkTimer = new TimerUtil();
    double lastDist = MovementUtil.lastDist();
    double speed, startY;
    static int stage, stage2, airTicks;
    public int ticks;
    static int state;
    static boolean hasClipped;
    TimerUtil viperStopWatch = new TimerUtil();
    boolean received, damaged, lostBoost, canSend;
    private static int y;
    boolean Jumped = false;
    boolean Clipped = false;
    private int lastX, lastY, lastZ;
    double Y;

    public Flight() {
        VerusModeProperty.setHidden(true);
        damageMode.setHidden(true);
        ColOrMotion.setHidden(true);
        verusntBool.setHidden(true);
        timerMax.setHidden(true);
        boostBool.setHidden(true);
        timerMin.setHidden(true);
        damageBool.setHidden(true);

        timerBool.onValueChange = () -> {
            timerMax.setHidden(!timerBool.getPropertyValue());
            timerMin.setHidden(!timerBool.getPropertyValue());
        };
        if(stopOnDisable.getPropertyValue()){
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
        VerusModeProperty.onValueChange = () -> {
            if(VerusModeProperty.getPropertyValue().equals(VerusModes.Damage) || VerusModeProperty.getPropertyValue().equals(VerusModes.New)) {
                damageMode.setHidden(false);
                ColOrMotion.setHidden(false);
                verusntBool.setHidden(false);
            }
            else {
                damageMode.setHidden(true);
                ColOrMotion.setHidden(true);
                verusntBool.setHidden(true);
            }
        };
        setModuleBind(Keyboard.KEY_X);
        setModuleSuffix(flightModeProperty.getPropertyValue().toString());
        onModuleDisabled = () -> {
            this.timer.reset();
            timer.reset();
            MovementUtil.setMoveSpeed(0);
            mc.gameSettings.keyBindJump.pressed = false;
            ticks = 0;
            mc.timer.timerSpeed = 1;
            Jumped = false;
            Clipped = false;
        };
        onModuleEnabled = () -> {
//            hasClipped = true;
            this.blinkTimer.reset();
            lastX = (int) mc.thePlayer.posX;
            lastY = (int) mc.thePlayer.posY;
            lastZ = (int) mc.thePlayer.posZ;

            Jumped = false;
            Clipped = false;
            state = 0;
            ticks = 0;
            canSend = true;
            viperStopWatch.reset();
            timer.reset();
            lastDist = 0;
            stage = 0;
            stage2 = 0;
            lostBoost = false;
            airTicks = 18;
            if (timerBool.getPropertyValue()) {
                double timerMinClamped = MathHelper.clamp_double(timerMin.getPropertyValue(), 0.1D, timerMax.getPropertyValue() - 0.1D);
                double bruh = ThreadLocalRandom.current().nextDouble(timerMinClamped, timerMax.getPropertyValue());
                mc.timer.timerSpeed = (float) bruh;
            }
            switch (flightModeProperty.getPropertyValue()) {
//                case WatchdogNew:
//                    if(!mc.thePlayer.onGround){
//                        this.toggleModule();
//                    } else {
//                        mc.thePlayer.motionY = 0.075f;
//                        damage();
//                    }
//                    break;
                case Watchdog:
                    if(mc.thePlayer.onGround)
                    mc.thePlayer.jump();

//                    else
//                        this.toggleModule();
                    break;
                case LoyisaNCP:
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8.0E-15D, mc.thePlayer.posZ);
                    break;
            }
            if (VerusModeProperty.getPropertyValue().equals(VerusModes.Damage)) {
                switch (damageMode.getPropertyValue()) {
                    case Basic:
                        PlayerUtils.damageVerusBasic();
                        break;
                    case Advanced:
                        PlayerUtils.damageVerusAdvanced();
                        break;
                }
            }
            speed = 0;
            moveSpeed = 0;
        };
        onPacketSendEvent = (e -> {
//            if(flightModeProperty.getPropertyValue().equals(FlightModes.Watchdog)){
//
//            }
//            if(e.getPacket() instanceof C0FPacketConfirmTransaction && flightModeProperty.getPropertyValue().equals(FlightModes.Watchdog)){
//                e.setCancelled(true);
//                Wrapper.sendPacketDelayed(e.getPacket(), 100L);
//            }
//            if(e.getPacket() instanceof C00PacketKeepAlive && flightModeProperty.getPropertyValue().equals(FlightModes.Watchdog)){
//                e.setCancelled(true);
//                Wrapper.sendPacketDelayed(e.getPacket(), 500L);
//            }
//            if(e.getPacket() instanceof C03PacketPlayer && flightModeProperty.getPropertyValue().equals(FlightModes.Viper) && mc.thePlayer.ticksExisted % 20 == 0){
//                e.setCancelled(true);
//                Wrapper.sendPacketDelayed(e.getPacket(), 500L);
//                e.setPacket(new C0CPacketInput());
//            }
        });
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && flagCheckBool.getPropertyValue()) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    BozoWare.getInstance().chat("Disabled Flight because you flagged/got teleported!");
//                    lostBoost = true;
                    this.toggleModule();
                }
            }
            if (e.getPacket() instanceof S08PacketPlayerPosLook && stage == 1) {
                final S08PacketPlayerPosLook packetPlayerPosLook = (S08PacketPlayerPosLook) e.getPacket();
                y = (int) packetPlayerPosLook.getY();
                System.out.println(y);
                mc.thePlayer.motionY = 0.05;
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), false));
                stage = 2;
                e.setCancelled(true);
            }
        });
        onUpdatePositionEvent = (e -> {
            VerusModeProperty.setHidden(!flightModeProperty.getPropertyValue().equals(FlightModes.Verus));
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            lastDist = MovementUtil.lastDist();
            if(viewBobBool.getPropertyValue()){
                mc.thePlayer.cameraYaw = (float) .1;
            }
            if (e.isPre()) {
                switch (flightModeProperty.getPropertyValue()) {
                    case Motion:
                        Wrapper.getPlayer().motionY = 0;
                        if (mc.thePlayer.movementInput.jump) {
                            Wrapper.getPlayer().motionY = Speed.getPropertyValue() / 2;
                        }
                        if (mc.thePlayer.movementInput.sneak) {
                            Wrapper.getPlayer().motionY = -Speed.getPropertyValue() / 2;
                        }
                        MovementUtil.setMoveSpeed(Speed.getPropertyValue());
                        break;
                    case Watchdog:
                        if(!Jumped){
                            if(!BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled())
                                BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).toggleModule();
                            Jumped = true;
                        }
                        if(!Clipped){
                            mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                        }
//                            MovementUtil.setSpeed(0.0001);
                        if(!Clipped && mc.thePlayer.onGround){
                             mc.thePlayer.setPosition(e.getX(), e.getY() - 0.525f, e.getZ());
                            Clipped = true;
                            BozoWare.getInstance().chat("clipped");
                        }
                        if(Clipped){
                            MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * 0.8);
                            if(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled())
                                BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).toggleModule();
                            mc.thePlayer.motionY = 0;
                        }
                        break;
//                        if (!e.isPre()) {
//                            break;
//                        }
//                        ++this.ticks;
//                        ++state;
//                        if (state == 2 || this.ticks >= 11) {
//                            mc.thePlayer.motionY = 0.0;
//                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.585);
//                        }
//                        if (state != 0) {
//                            break;
//                        }
//                        if (this.ticks == 1) {
//                            mc.thePlayer.motionY = 0.39547834756;
//                        }
//                        if (this.ticks == 12) {
//                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.39547834756, mc.thePlayer.posZ);
//                        }
//                        if (this.ticks == 13) {
//                            mc.thePlayer.motionY = -0.4057689;
//                        }
//                        if (this.ticks == 14) {
//                            mc.thePlayer.motionY = -0.4057689;
//                            state = 1;
//                            break;
//                        }
                    case Viper:
                        mc.thePlayer.motionY = 0;
                        MovementUtil.setSpeed(Speed.getPropertyValue() * 1.1);
//                        if(this.timer.hasReached(100L) && canSend){
//                            Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
//                            Wrapper.sendPacketDirect(new C03PacketPlayer(true));
//                            BozoWare.getInstance().chat("Sent");
//                            this.timer.reset();
//                        }
//                        if(viperStopWatch.hasReached(5000L)){
//                            canSend = false;
//                            MovementUtil.setSpeed(0.36);
//                        }
                        e.setOnGround(true);
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.motionY = Speed.getPropertyValue() * 2;
                        }
                        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.thePlayer.motionY = -(Speed.getPropertyValue() * 2);
                        }
                        break;
                    case WatchdogNew:
                        mc.thePlayer.motionY = 0;
                        if (mc.thePlayer.ticksExisted % 4 == 0) {
                            float yaw = mc.thePlayer.rotationYaw;
                            double dist = 7.9;
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x + (-Math.sin(toRadians(yaw)) * dist), y - 1.75, z + (Math.cos(toRadians(yaw)) * dist), mc.thePlayer.onGround));
                            this.toggleModule();
                        }
                        break;

                    case LoyisaNCP:
                        mc.thePlayer.motionY = 0.0D;
                        MovementUtil.setSpeed(0.262D);
                        break;
                    case AntiPlate:
                        if (mc.thePlayer.isMoving()) {
                            if (mc.thePlayer.onGround) {
                                moveSpeed = 0.29D;
                                MovementUtil.setSpeed(moveSpeed);
                                mc.thePlayer.jump();
                                airTicks = 9;
                            } else if(Wrapper.getBlock(new BlockPos(x, y - 1.3, z)).isFullBlock()){
                                mc.timer.timerSpeed = 1.085F;
                                moveSpeed = 0.262D;
                                MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
                                --airTicks;
                                if (airTicks < 0) {
                                    if (airTicks <= 0) {
                                        airTicks = 0;
                                    }
                                    if(!(mc.thePlayer.hurtTime > 0)){
                                        MovementUtil.setSpeed(moveSpeed);
                                    }
                                    else {
                                        MovementUtil.setSpeed(moveSpeed * 1.5);
                                    }
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8.0E-15D, mc.thePlayer.posZ);
                                    mc.thePlayer.motionY = 0.0D;
                                }
                            }
                        }
                        break;
                    case Collision:
                        mc.thePlayer.onGround = true;
                        break;
                    case Funcraft:
                        mc.thePlayer.jumpMovementFactor = 0;
                        if(!mc.thePlayer.isMoving() || mc.thePlayer.isCollidedHorizontally) {
                            lostBoost = true;
                        }
                        if(stage > 0 || lostBoost) {
                            e.setOnGround(true);
                            mc.thePlayer.motionY = 0;
                            if(MovementUtil.isOnGround(0.01))
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.24, mc.thePlayer.posZ);
                            if(!MovementUtil.isOnGround(3.33315597345063e-11)) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.33315597345063e-11, mc.thePlayer.posZ);
                            }
                        }
//                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.33315597345063e-11, mc.thePlayer.posZ);
                        break;
                    case Funcraft2:
                        e.setOnGround(true);
                        mc.thePlayer.jumpMovementFactor = 0;
                        if(!mc.thePlayer.isMoving())
                            moveSpeed = 0.25;
                        if(moveSpeed > 0.24) {
                            moveSpeed -= moveSpeed / 159;
                        }
                        if(e.isPre()) {
                            mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), mc.thePlayer.posX, mc.thePlayer.posY + 0.2D, mc.thePlayer.posZ, -mc.thePlayer.motionX, -0.5D, -mc.thePlayer.motionZ);
                            if(mc.thePlayer.isMoving())
                                MovementUtil.setMoveSpeed(moveSpeed);
                            mc.thePlayer.motionY = 0;
                            mc.thePlayer.setPosition(x, y - 3.33315597345063e-11, z);
                        }
                        break;
                }
                if(flightModeProperty.getPropertyValue().equals(FlightModes.Verus)) {
                    switch (VerusModeProperty.getPropertyValue()) {
                        case Basic:
//                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                            break;
                        case Advanced:
                            mc.thePlayer.motionY = 0;
                            timer.reset();
                            if(timer.hasReached(12)) {
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8, mc.thePlayer.posZ, true));
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8, mc.thePlayer.posZ, true));
                                timer.reset();
                            }
                            break;
                        case Glide:
                            mc.thePlayer.onGround = true;
                            mc.thePlayer.motionY = -0.07840000152587834;
                            if(mc.thePlayer.isCollidedHorizontally){
                                mc.thePlayer.motionY = 0.3F;
                            }
                            if(mc.thePlayer.isAirBorne && !mc.thePlayer.isCollidedVertically) {
                                MovementUtil.setMoveSpeed(0.26);
                            }
                            break;
                        case Damage:
                            damaged = false;
                            lostBoost = false;
                            mc.thePlayer.onGround = true;
                            if(!ColOrMotion.getPropertyValue()){
                                mc.thePlayer.motionY = 0;
                            }
                            else {
                                if(mc.thePlayer.ticksExisted % 10 == 0){
                                    mc.thePlayer.jump();
                                }
                                mc.gameSettings.keyBindJump.pressed = false;
                            }
                            if(received){
                                lostBoost = true;
                            }
                            if (mc.thePlayer.hurtTime > 0) {
                                received = true;
                                mc.timer.timerSpeed = 1F;
                                damaged = true;
                                if (mc.thePlayer.isCollidedHorizontally && ticks <= 99) {
                                    ticks = 999;
                                    BozoWare.getInstance().chat("Disabled Boost For Safety.");
                                }
                                ++ticks;
                                if (!received) {
                                    MovementUtil.setMoveSpeed(0);
                                } else {
                                    if (ticks <= 150) {
                                        MovementUtil.setMoveSpeed(Speed.getPropertyValue());
                                    }
                                }
                            } else if (verusntBool.getPropertyValue() && lostBoost) {
                                mc.timer.timerSpeed = 0.35F;
                            }
                            break;
                        case New:
                            mc.thePlayer.setSprinting(true);
                            MovementUtil.setMoveSpeed(mc.thePlayer.ticksExisted % 4 == 0 ? 0.5 : 0.36);
                            break;
                        case New2:
                            if(mc.thePlayer.inventory.getCurrentItem() == null) {
                                if(mc.gameSettings.keyBindJump.isKeyDown() && timer.hasReached(100)) {
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.6, mc.thePlayer.posZ);
                                    timer.reset();
                                }

                                if(mc.thePlayer.isSneaking() && timer.hasReached(100)) {
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ);
                                    timer.reset();
                                }

                                final BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1, mc.thePlayer.posZ);
                                final Vec3 vec = new Vec3(blockPos).addVector(0.4F, 0.4F, 0.4F).add(new Vec3(EnumFacing.UP.getDirectionVec()));
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), blockPos, EnumFacing.UP, new Vec3(vec.xCoord * 0.4F, vec.yCoord * 0.4F, vec.zCoord * 0.4F));
                                MovementUtil.setSpeed(0.27F);

                                mc.timer.timerSpeed = (float) (1 + Speed.getPropertyValue());
                            }else{
                                mc.timer.timerSpeed = 1;
                                BozoWare.getInstance().chat("§8[§c§lMineplex-§a§lFly§8] §aSelect an empty slot to fly.");
                                this.toggleModule();
                            }
                            break;
                        case Floatish:
                            if(mc.thePlayer.posY == startY){
                                mc.thePlayer.motionY = 0.42F;
                            }
                            if(mc.thePlayer.posY < startY){
                                mc.thePlayer.motionY = 0.42F;
                                mc.thePlayer.posY = startY;
                            }
                            if(mc.thePlayer.posY >= startY + 0.42F && mc.thePlayer.isAirBorne){
//                                mc.thePlayer.motionY = -0.01;
                                mc.thePlayer.posY = startY + 0.42;
                                MovementUtil.setMoveSpeed(MovementUtil.getBaseMoveSpeed() * Integer.MAX_VALUE);
                            }
                            MovementUtil.setMoveSpeed(0.36);
                            break;
                    }
                }
            }
        });
        onPlayerMoveEvent = (e -> {
            switch(flightModeProperty.getPropertyValue()){
                case Funcraft:
                    if (mc.thePlayer.isMoving() && !lostBoost) {
                        switch (stage){
                            case 0:
                                moveSpeed = 0;
                                break;
                            case 1:
                                e.setMotionY(mc.thePlayer.motionY + 0.3999);
                                mc.thePlayer.motionY = 0.3999;
                                moveSpeed *= 2.149;
                                break;
                            case 2:
                                moveSpeed = 1.6;
                                break;
                            default:
                                moveSpeed = lastDist - lastDist / 159;
                                break;
                        }
                        moveSpeed = Math.max(moveSpeed, MovementUtil.getBaseMoveSpeed());
                        MovementUtil.setSpeed(e, moveSpeed);
                        stage++;
                    } else if(mc.thePlayer.isMoving() && lostBoost) {
                        MovementUtil.setSpeed(e, MovementUtil.getBaseMoveSpeed());
                    }
                    break;
                case Watchdog:
//                    if(timer.hasReached(2500L)) {
//                        if(!Wrapper.getPlayer().isPotionActive(Potion.moveSpeed))
//                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
//                        else
//                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
//                    } else {
//                        if(Wrapper.getPlayer().isPotionActive(Potion.moveSpeed))
//                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.9);
//                        else
//                            MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed());
//                    }
                    break;
            }
        });
        onEventAABB = (e -> {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            if (flightModeProperty.getPropertyValue().equals(FlightModes.Collision)) {
                AxisAlignedBB blockBB = null;
                if (!mc.thePlayer.movementInput.sneak) {
                        blockBB = AxisAlignedBB.fromBounds(-5000, -2, -5000, 5000, 2, 5000).offset(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
                }
                else if (!Wrapper.getBlock(new BlockPos(x, y - 1, z)).isFullCube() && !Wrapper.getBlock(new BlockPos(x, y - 1, z)).isFullBlock() && !Wrapper.getBlock(new BlockPos(x, y - 1, z)).isCollidable()){
                        blockBB = AxisAlignedBB.fromBounds(-5000, y - 1, -5000, 5000, 2, 5000).offset(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
                }
                    e.setBoundingBox(blockBB);
            }
            if(ColOrMotion.getPropertyValue() && VerusModeProperty.getPropertyValue().equals(VerusModes.Damage) || VerusModeProperty.getPropertyValue().equals(VerusModes.New)){
                AxisAlignedBB blockBB = AxisAlignedBB.fromBounds(-5000, -2, -5000, 5000, 2, 5000).offset(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
                    e.setBoundingBox(blockBB);
            }
            if(VerusModeProperty.getPropertyValue().equals(VerusModes.New2) && mc.thePlayer.inventory.getCurrentItem() == null && e.getPos().getY() < mc.thePlayer.posY){
                e.setBoundingBox(AxisAlignedBB.fromBounds(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), e.getPos().getX() + 1, mc.thePlayer.posY, e.getPos().getZ() + 1));
            }
        });
        if(flightModeProperty.getPropertyValue() != FlightModes.Verus) {
            flightModeProperty.onValueChange = () -> {
                setModuleSuffix(flightModeProperty.getPropertyValue().name());
                if(flightModeProperty.getPropertyValue().equals(FlightModes.Watchdog)) {
                    boostBool.setHidden(false);
                    damageBool.setHidden(false);
                }
                else {
                    boostBool.setHidden(true);
                    damageBool.setHidden(true);
                }
                };
        }
        else {
            VerusModeProperty.onValueChange = () -> setModuleSuffix(VerusModeProperty.getPropertyValue().name());
        }
    }

    public static Flight getInstance() {
        return (Flight) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class);
    }

    private enum FlightModes {
        Motion,
        Watchdog,
        WatchdogNew,
        NCP,
        LoyisaNCP,
        AntiPlate,
        Collision,
        Verus,
        Funcraft,
        Viper,
        Funcraft2
    }
    private enum VerusModes {
        Basic,
        Advanced,
        Glide,
        Floatish,
        Damage,
        New,
        Intave,
        New2
    }
    private enum damageModes {
        Basic,
        Advanced
    }

    public static void damage() {
        double offset = 0.060100000351667404D;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        EntityPlayerSP player = mc.thePlayer;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        for(int i = 0; (double)i < (double)getMaxFallDist() / 0.05510000046342611D + 1.0D; ++i) {
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404D, z, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4D, z, false));
            netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.004999999888241291D + 6.01000003516674E-8D, z, false));
        }

        netHandler.addToSendQueue(new C03PacketPlayer(true));
    }
    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        int f = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        return (float)(mc.thePlayer.getMaxFallHeight() + f);
    }
    public Scaffold.BlockData find(Vec3 offset3) {

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);
            if (mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                continue;
            return new Scaffold.BlockData(offset, invert[facing.ordinal()]);
        }
        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(2, 0, 0), new BlockPos(-2, 0, 0)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
            if (!(mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir)) continue;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);
                if (mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.01f), getPositionByFace(offset, invert[facing.ordinal()])))
                    continue;
                return new Scaffold.BlockData(offset2, invert[facing.ordinal()]);
            }
        }
        return null;
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
    public Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3 offset = new Vec3((double) facing.getDirectionVec().getX() / 2.0, (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3 point = new Vec3((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5);
        return point.add(offset);
    }
}