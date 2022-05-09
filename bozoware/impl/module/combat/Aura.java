package bozoware.impl.module.combat;

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
import bozoware.base.util.player.PlayerUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.module.player.Scaffold;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.MultiSelectEnumProperty;
import bozoware.impl.property.ValueProperty;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@ModuleData(moduleName = "Kill Aura", moduleCategory = ModuleCategory.COMBAT)
public class Aura extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    boolean blocking;
    private final EnumProperty<auraModes> auraMode = new EnumProperty<>("Aura Mode", auraModes.Single, this);
    private final EnumProperty<sortModes> sortMode = new EnumProperty<>("Sort Mode", sortModes.Health, this);
    private final ValueProperty<Integer> APS = new ValueProperty<>("APS", 15, 1, 25, this);
    public final ValueProperty<Double> Range = new ValueProperty<>("Attack Range", 4.2D, 1D, 6D, this);
//    public final ValueProperty<Double> TPRange = new ValueProperty<>("TP Range", 25D, 1D, 150D, this);
    public final ValueProperty<Double> ABRange = new ValueProperty<>("Block Range", 4.2D, 1D, 12D, this);
    private final BooleanProperty blockBool = new BooleanProperty("AutoBlock", true, this);
    private final EnumProperty<blockModes> blockMode = new EnumProperty<>("Block Mode", blockModes.NCP, this);
    private final MultiSelectEnumProperty<Targets> targeting = new MultiSelectEnumProperty<>("Targets", this, Targets.Players);
    private final BooleanProperty noSwing = new BooleanProperty("NoSwing", false, this);
//    private final BooleanProperty shitGCDBool = new BooleanProperty("Shit GCD", false, this);
//    private final BooleanProperty KSBool = new BooleanProperty("Keep Sprint", true, this);
    private final BooleanProperty flagCheckBool = new BooleanProperty("Flag Check", false, this);
    private final BooleanProperty rotations = new BooleanProperty("Rotations", true, this);
    //    private final BooleanProperty hitCheck = new BooleanProperty("Hit Check", true, this);
    private final ValueProperty<Double> smoothFactor = new ValueProperty<>("Smoothing Speed", 180D, 1D, 180D, this);
    private final BooleanProperty particleBool = new BooleanProperty("Particle", false, this);
    private final EnumProperty<particleModes> particleMode = new EnumProperty<>("Particle Mode", particleModes.Crit, this);

    public TimerUtil timer = new TimerUtil();
    public List<EntityLivingBase> targetList = new ArrayList<>();
    public static EntityLivingBase target = null;
    public static boolean isBlocking;
    private double yawAnimation, yaw, pitch;

    public static Aura getInstance() {
        return (Aura) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class);
    }

    public Aura() {
        particleMode.setHidden(true);
        blockMode.setHidden(!blockBool.getPropertyValue());
        particleBool.onValueChange = () -> {
            particleMode.setHidden(!particleBool.getPropertyValue());
            if (!particleBool.getPropertyValue()) {
                particleMode.setHidden(true);
            }
        };
//        auraMode.onValueChange = () -> {
//            switch(auraMode.getPropertyValue()){
//                case Single:
//                    Range.setHidden(false);
//                    TPRange.setHidden(true);
//                    break;
//                case Infinite:
//                    Range.setHidden(true);
//                    TPRange.setHidden(false);
//                    break;
//            }
//        };
        setModuleBind(Keyboard.KEY_N);
        setModuleSuffix(sortMode.getPropertyValue().toString());
        onModuleDisabled = () -> {
            targetList.clear();
            target = null;
        };
        onModuleEnabled = () -> {
            targetList.clear();
        };
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && flagCheckBool.getPropertyValue()) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    BozoWare.getInstance().chat("Disabled Aura because you flagged/got teleported!");
                    this.toggleModule();
                }
            }
        });
        onUpdatePositionEvent = (event -> {
            if(blockMode.getPropertyValue().equals(blockModes.SetUse) && Aura.target == null && !Mouse.isButtonDown(1) && mc.gameSettings.keyBindUseItem.pressed)
                mc.gameSettings.keyBindUseItem.pressed = false;
            if (BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled() || BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Scaffold.class).isModuleToggled()) {
                Aura.target = null;
                return;
            }
//            if (!KSBool.getPropertyValue()) mc.thePlayer.setSprinting(false);

            target = getTarget();
            if (rotations.getPropertyValue() && target != null) {
                float[] f = Wrapper.getEntityRotations(mc.thePlayer, target);
                double smoothSpeed = smoothFactor.getPropertyValue() / 100D;
                yawAnimation = RenderUtil.animate(f[0], yawAnimation, smoothSpeed);
//                double InsaneGCD = ThreadLocalRandom.current().nextDouble(ThreadLocalRandom.current().nextDouble(ThreadLocalRandom.current().nextDouble(-5 - sensitivity, ThreadLocalRandom.current().nextDouble(-5 - sensitivity, -1 - sensitivity)), -1 - sensitivity), ThreadLocalRandom.current().nextDouble(ThreadLocalRandom.current().nextDouble(1 + sensitivity, 5 + sensitivity), ThreadLocalRandom.current().nextDouble(1 + sensitivity, 5 + sensitivity)));
                mc.thePlayer.rotationYawHead = (float) (yawAnimation);
                mc.thePlayer.renderYawOffset = (float) (yawAnimation);
                mc.thePlayer.rotationPitchHead = f[1];
                event.setYaw((float) (yawAnimation));
                event.setPitch(f[1]);
            }
            switch (auraMode.getPropertyValue()) {
                case Switch:
                case Single:
                    if (target != null && (Math.round(event.getYaw()) == Math.round(Wrapper.getEntityRotations(mc.thePlayer, target)[0])) && target.getDistanceToEntity(mc.thePlayer) <= Range.getPropertyValue()) {
                        if (this.timer.hasReached(1000L / APS.getPropertyValue())) {
                            if (noSwing.getPropertyValue()) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            } else {
                                mc.thePlayer.swingItem();
                            }
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                            doParticle();
                            this.timer.reset();
                        }
                    }
                    break;
            }
            if (canBlock()) {
                switch (blockMode.getPropertyValue()) {
                    case Interact:
                        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
                        break;
                    case C08:
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && !event.isPre()) {
                            isBlocking = true;
                            if (timer.hasReached(140)) {
                                if (blocking) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    blocking = false;
                                }
                                timer.reset();
                            }
                            if (!timer.hasReached(40)) {
                                if (!blocking) {
                                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    blocking = true;
                                }
                            }
                        }
                        break;
                    case SetUse:
                        mc.gameSettings.keyBindUseItem.pressed = true;
                        MovementUtil.setSpeed(MovementUtil.getMoveSpeed());
                        isBlocking = true;
                        break;
                    case NCP:
                        if (!event.isPre()) {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                            isBlocking = true;
                        }
                        break;
                    case Fake:
                        isBlocking = true;
                        break;
                }
            } else {
                isBlocking = false;
            }
            setModuleSuffix(auraMode.getPropertyValue().name());
            auraMode.onValueChange = () -> {
                setModuleSuffix(auraMode.getPropertyValue().name());
            };
        });
    }

    public boolean canBlock() {
        final ItemStack heldItem = mc.thePlayer.getHeldItem();
        return target != null && blockBool.getPropertyValue() && target.getDistanceToEntity(mc.thePlayer) <= ABRange.getPropertyValue() && heldItem != null && heldItem.getItem() instanceof ItemSword;
    }

    public void doParticle() {
        if (particleBool.getPropertyValue() && target != null) {
            switch (particleMode.getPropertyValue()) {
                case Crit:
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                    break;
                case Enchant:
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                    break;
                case Heart:
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.HEART);
                    break;
                case Explosion:
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.EXPLOSION_LARGE);
                    break;
                case Blood:
                    for (int i = 0; i < 8; i++) {
                        World targetWorld = target.getEntityWorld();
                        double x, y, z;
                        x = target.posX;
                        y = target.posY;
                        z = target.posZ;

                        targetWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, x + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), y + ThreadLocalRandom.current().nextDouble(-1, 1), z + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), 23, 23, 23, 152);
                    }
                    break;
                default:
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                    break;
            }
        }
    }

    public EntityLivingBase getTarget() {
        EntityLivingBase currentTarget = null;
        double currentDistance = 0;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != null) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                    if (!isValid(entityLivingBase)) continue;
                    EntityLivingBase target = (EntityLivingBase) entity;
                    if (currentTarget != null && !auraMode.getPropertyValue().equals(auraModes.Single)) {
                            switch (sortMode.getPropertyValue()) {
                            case Health: {
                                if (target.getHealth() < currentTarget.getHealth()) currentTarget = target;
                                break;
                            }
                                case HurtTime: {
                                    if(target.hurtTime < currentTarget.getHealth()) currentTarget = target;
                                    break;
                                }
                            case Distance: {
                                    if (target.getDistanceToEntity(mc.thePlayer) < currentDistance) {
                                    currentTarget = target;
                                    currentDistance = currentTarget.getDistanceToEntity(mc.thePlayer);
                                }
                                break;
                            }
                            default:
                                return target;
                        }
                    } else {
                        currentTarget = target;
                        switch (sortMode.getPropertyValue()) {
                            case Health: {
                                if (target.getHealth() < currentTarget.getHealth()) currentTarget = target;
                                break;
                            }
                            case Distance: {
                                if (target.getDistanceToEntity(mc.thePlayer) < currentDistance) {
                                    currentTarget = target;
                                    currentDistance = currentTarget.getDistanceToEntity(mc.thePlayer);
                                }
                                break;
                            }
                            default:
                                return target;
                        }
                    }
                }
            }
        }
        return currentTarget;
    }

    public boolean isValid(EntityLivingBase targetIn) {
        if (AntiBot.botList.contains(targetIn.getEntityId())) {
            return false;
        }
        switch (auraMode.getPropertyValue()) {
            case Switch:
            case Single:
                if (targetIn instanceof EntityPlayer && targeting.isSelected(Targets.Players) && !targetIn.getDisplayName().getFormattedText().contains("NPC") && !PlayerUtils.isOnSameTeam(targetIn) && !targetIn.isDead && ((EntityPlayer) targetIn).getHealth() > 0 && targetIn != mc.thePlayer && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                if (targetIn instanceof EntityPlayer && targeting.isSelected(Targets.Teams) && !targetIn.getDisplayName().getFormattedText().contains("NPC") && PlayerUtils.isOnSameTeam(targetIn) && !targetIn.isDead && ((EntityPlayer) targetIn).getHealth() > 0 && targetIn != mc.thePlayer && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                if (targetIn instanceof EntityPlayer && targeting.isSelected(Targets.NPCS) && targetIn.getDisplayName().getFormattedText().contains("NPC") && !targetIn.isDead && ((EntityPlayer) targetIn).getHealth() > 0 && targetIn != mc.thePlayer && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                if (targetIn instanceof EntityVillager && targeting.isSelected(Targets.Villagers) && !targetIn.isDead && ((EntityVillager) targetIn).getHealth() > 0 && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                if (targetIn instanceof EntityMob && targeting.isSelected(Targets.Mobs) && !targetIn.isDead && ((EntityMob) targetIn).getHealth() > 0 && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                if (targetIn instanceof EntityAnimal && targeting.isSelected(Targets.Passives) && !targetIn.isDead && ((EntityAnimal) targetIn).getHealth() > 0 && (mc.thePlayer.getDistanceToEntity(targetIn) <= (ABRange.getPropertyValue() > Range.getPropertyValue() ? ABRange.getPropertyValue() : Range.getPropertyValue()))) {
                    return true;
                }
                break;
        }
        return false;
    }

    private enum auraModes {
        Single,
        Switch
//        Infinite
    }

    private enum Targets {
        Players,
        NPCS,
        Teams,
        Passives,
        Mobs,
        Villagers,
    }

    private enum sortModes {
        Health,
        HurtTime,
        Distance
    }

    private enum blockModes {
        Interact,
        C08,
        SetUse,
        NCP,
        Fake
    }

    private enum particleModes {
        Crit,
        Enchant,
        Heart,
        Explosion,
        Blood
    }
}
