package bozoware.impl.module.world;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.notifications.Notification;
import bozoware.base.notifications.NotificationManager;
import bozoware.base.notifications.NotificationType;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.MathUtil;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.player.MovementUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.module.movement.Flight;
import bozoware.impl.module.movement.LongJump;
import bozoware.impl.module.movement.Speed;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.module.player.InvManager;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@ModuleData(moduleName = "Disabler", moduleCategory = ModuleCategory.WORLD)
public class Disabler extends Module {

    LinkedList<Packet> packetQueue = new LinkedList<>();
    final TimerUtil timer = new TimerUtil();

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onWorldLoadEvent;

    private final EnumProperty<disablerModes> mode = new EnumProperty<>("Mode", disablerModes.Watchdog, this);
    private final BooleanProperty pingSpoofBool = new BooleanProperty("Ping Spoof", true, this);
    private final ValueProperty<Long> pingSpoof = new ValueProperty<>("Ping Spoof Delay", 350L, 50L, 30000L, this);
    private final BooleanProperty strafeFixBool = new BooleanProperty("Strafe Fix", true, this);
    private int bypassValue = 0;
    private double posX;
    private double posY;
    private double posZ;
    private long lastTransaction = 0L;
    int CanceledPackets;
    int count;
    boolean didTPVerus;
    private final ConcurrentHashMap<Packet<?>, Long> packets = new ConcurrentHashMap<>();
    private Deque<Packet<?>> transactionQueue = new ArrayDeque<>();
    private int lagbacks = 0;
    private boolean spike = false;
    private TimerUtil timer1 = new TimerUtil();
    private TimerUtil timer2 = new TimerUtil();
    private boolean cancel;
    private TimerUtil spikeTimer = new TimerUtil();
    private TimerUtil hypixelTimer = new TimerUtil();
    private TimerUtil packetTimer = new TimerUtil();

    public Disabler() {
        setModuleSuffix(mode.getPropertyValue().toString());
        pingSpoof.setHidden(false);
        pingSpoofBool.setHidden(false);
        onModuleDisabled = () -> {

        };
        onModuleEnabled = () -> {
            timer.reset();
            setModuleSuffix(mode.getPropertyValue().toString());
            CanceledPackets = 0;
            count = 0;
        };
        onWorldLoadEvent = (e -> {
            CanceledPackets = 0;
            transactionQueue.clear();
            packets.clear();
            packetQueue.clear();
            spikeTimer.reset();
            hypixelTimer.reset();
            packetTimer.reset();
            lastTransaction = 0L;
            lagbacks = 0;
            spike = false;
        });
        onPacketReceiveEvent = (e -> {
            if(e.getPacket() != null && mc.thePlayer != null)
            switch (mode.getPropertyValue()) {
                case Watchdog:
//                    if(mc.thePlayer.ticksExisted < 150 && mc.thePlayer.ticksExisted > 10){
//                        mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
//                        mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
//                    }
//                    if (e.getPacket() instanceof S08PacketPlayerPosLook) {
//                        if (mc.thePlayer.ticksExisted < 150 && mc.thePlayer.ticksExisted > 10) {
//                            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();
//                            s08.setY(s08.getY() - 9999);
//                        }
//                    }
                    break;
                case WatchdogTimer:
                    if (e.getPacket() instanceof S07PacketRespawn) {
                        this.lagbacks = 0;
                        this.packetQueue.clear();
                        this.spike = false;
                        this.spikeTimer.reset();
                    }

                    if (e.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted >= 4 && mc.thePlayer.isCollidedVertically) {
                        S08PacketPlayerPosLook S08 = (S08PacketPlayerPosLook) e.getPacket();
                        if (this.hypixelTimer.hasReached(2000L)) {
                            if (mc.thePlayer.getDistance(S08.getX(), S08.getY(), S08.getZ()) < 10.0D) {
                                ++this.lagbacks;
                                this.lastTransaction = 6L;
                                e.setCancelled(true);
                            }
                        }
                    }
                    break;
                case BlocksMC:
                    if (e.getPacket() instanceof S08PacketPlayerPosLook && this.didTPVerus) {
                        final S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)e.getPacket();
                        this.didTPVerus = false;
                        e.setCancelled(true);
                        Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(s08.getX(), s08.getY(), s08.getZ(), s08.getYaw(), s08.getPitch(), true));
                    }
                    break;
            }
        });
        onPacketSendEvent = (e -> {
            if (strafeFixBool.getPropertyValue()) {
                if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled() && BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Speed.class).isModuleToggled()) {
                    e.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket()).getPositionX(), ((C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket()).getPositionY(), ((C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket()).getPositionZ(), MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw), ((C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket()).getPitch(), ((C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket()).isOnGround()));
                }
            }
            if(e.getPacket() != null && mc.thePlayer != null)
            switch(mode.getPropertyValue()) {
                case WatchdogTimer:
                    if (e.getPacket() instanceof C03PacketPlayer) {
                        if (this.lastTransaction > 0L) {
                            e.setCancelled(true);
                            --this.lastTransaction;
                            return;
                        }
                    }

                    if (e.getPacket() instanceof C03PacketPlayer) {
                        C03PacketPlayer C03 = (C03PacketPlayer) e.getPacket();
                        if (!C03.isMoving() && !C03.getRotating() && mc.thePlayer.motionY == 0) {
                            e.setCancelled(true);
                        } else {
                            this.packetQueue.push(e.getPacket());
                        }
                        e.setCancelled(true);
                    }

                    if (e.getPacket() instanceof C00PacketKeepAlive) {
                        this.packetQueue.push(e.getPacket());
                        e.setCancelled(true);
                    }

                    if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                        C0FPacketConfirmTransaction C0F = (C0FPacketConfirmTransaction) e.getPacket();
                        this.packetQueue.push(e.getPacket());
                        ++this.lastTransaction;
                        Wrapper.sendPacketDirect(new C0CPacketInput());
                        e.setCancelled(true);
                    }
                    break;
                case Watchdog:
                    if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook && mc.thePlayer.ticksExisted < 100) {
                        C03PacketPlayer.C06PacketPlayerPosLook c06 = (C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket();
                        c06.setY(0);
                    }
                    if (pingSpoofBool.getPropertyValue() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled() && Aura.target == null && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled()) {
                        if (mc.isSingleplayer())
                            return;
                        final Packet<?> p = e.getPacket();

                        if (p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive) {
                            packets.put(p, (long) (System.currentTimeMillis() + pingSpoof.getPropertyValue()));
                            e.setCancelled(true);
                        }
                    }
                    break;
                case Viper:
                    if(timer.hasReached(5250) || mc.thePlayer.ticksExisted < 20){
                        if(e.getPacket() instanceof C0FPacketConfirmTransaction){
                            e.setCancelled(true);
                            Wrapper.sendPacketDelayed(e.getPacket(), pingSpoof.getPropertyValue());
                        }
                        if(e.getPacket() instanceof C00PacketKeepAlive) {
                            e.setCancelled(true);
                            Wrapper.sendPacketDelayed(e.getPacket(), pingSpoof.getPropertyValue());
                        }
                        if(timer.hasReached(6250 + pingSpoof.getPropertyValue())) {
                            timer.reset();
                        }
                    }
                    break;
                case BlocksMC:
                    if (e.getPacket() instanceof C03PacketPlayer) {
//                            C03PacketPlayer c03x = (C03PacketPlayer) e.getPacket();
//                            if(!c03x.isMoving() && !c03x.getRotating())
//                                return;
                        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
                        final C03PacketPlayer c03 = (C03PacketPlayer)e.getPacket();
                        if (mc.thePlayer.ticksExisted % 20 == 0) {
                            c03.y = RandomUtils.nextDouble(0.0, 1000.0);
                            c03.setOnGround(false);
                            this.didTPVerus = true;
//                                BozoWare.getInstance().chat("poop");
                        }
                    }
                    if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                        e.setCancelled(true);
                        this.packetQueue.add(e.getPacket());
                    }
                    if (e.getPacket() instanceof C00PacketKeepAlive) {
                        ((C00PacketKeepAlive)e.getPacket()).setKey(-2);
                    }
                    break;
                case NoPayload:
                    if (e.getPacket() instanceof C17PacketCustomPayload) {
                        e.setCancelled(true);
                    }
                    break;
            }
        });
        onUpdatePositionEvent = (e -> {
            setModuleSuffix(mode.getPropertyValue().name());
            if (strafeFixBool.getPropertyValue()) {
                if (!BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled() && BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Speed.class).isModuleToggled() || BozoWare.getInstance().getModuleManager().getModuleByClass.apply(LongJump.class).isModuleToggled()) {
                    e.setYaw(MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw));
                    mc.thePlayer.rotationYawHead = MovementUtil.getDirectionStrafeFix(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, mc.thePlayer.rotationYaw);
                }
            }
            if(mc.thePlayer != null)
            switch(mode.getPropertyValue()){

                case WatchdogTimer:
                    if (!e.isPre()) {
                        if (Minecraft.getMinecraft().theWorld == null) {
                            this.packetQueue.clear();
                        }
                        if (this.packetTimer.hasReached(mc.thePlayer.ticksExisted < 70 ? 1250L : pingSpoof.getPropertyValue())) {
                            Iterator<Packet> iterator = packetQueue.descendingIterator();
                            while(iterator.hasNext()) {
                                Packet<?> packet = iterator.next();
                                //System.out.println("Send Packet "+packet.toString());
                                Wrapper.sendPacketDirect(packet);
                            }
                            System.out.println("Cleared Queue");
                            packetQueue.clear();
                            this.spike = false;
                        }

                        if (mc.thePlayer.ticksExisted == 40) {
                            BozoWare.getInstance().chat("Disabled Watchdog!");
                            e.setX(e.getX() + 1.0D);
                            e.setZ(e.getZ() + 1.0D);
                        }

                        if (this.spikeTimer.hasReached(10000L)) {
                            this.spike = true;
                            this.packetTimer.reset();
                        }
                    } else {
                        Iterator<Packet> iterator = packetQueue.descendingIterator();
                        while(iterator.hasNext()) {
                            Packet<?> packet = iterator.next();
                            Wrapper.sendPacketDirect(packet);
                        }
                        packetQueue.clear();
                        this.packetTimer.reset();
                    }
                    break;
                case Viper:
//                    if(!packetQueue.isEmpty()){
//                        if(mc.thePlayer.ticksExisted % 30 == 0){
//                            Wrapper.sendPacketDirect(this.packetQueue.remove(0));
//                            if (this.packetQueue.size() > 4) {
//                                Wrapper.sendPacketDirect(this.packetQueue.poll());
//                                e.setOnGround(true);
//                                e.setY(e.getY() - 0.22f);
//                            }
//                        }
//                        if (mc.thePlayer.ticksExisted % 250 == 0) {
//                            this.packetQueue.clear();
//                        }
//                    }
                case BlocksMC:
                    if (!this.packetQueue.isEmpty()) {
                        if (mc.thePlayer.ticksExisted % 25 == 0) {
                            Wrapper.sendPacketDirect(this.packetQueue.remove(0));
                            if (this.packetQueue.size() > 4) {
                                Wrapper.sendPacketDirect(this.packetQueue.poll());
                            }
                        }
                        if (mc.thePlayer.ticksExisted % 250 == 0) {
                            this.packetQueue.clear();
                        }
                    }
                    break;
                case Spectator:
                    Wrapper.sendPacketDirect((new C18PacketSpectate((UUID.randomUUID()))));
                    break;
                case NullPlace:
                        Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0, 0, 0));
                    break;
                case TransactionSpam:
                    if(mc.thePlayer.ticksExisted % 5 == 0)
                    Wrapper.sendPacketDirect(new C0FPacketConfirmTransaction(0, (short) MathUtil.getRandomInRange(-32767, 32767), true));
                    break;
                case Watchdog:
//                    if (timer1.hasReached(6000L)) {
//                        cancel = true;
//                        timer2.reset();
//                        timer1.reset();
//                    }
//                    if(mc.thePlayer.ticksExisted < 20){
//                        e.setOnGround(true);
//                        mc.timer.timerSpeed = 0.1f;
//                    }
//                    if(mc.thePlayer.ticksExisted == 20 || mc.thePlayer.ticksExiste
//                    d == 21){
//                        mc.timer.timerSpeed = 1;
//                    }
//                    if(mc.thePlayer.ticksExisted == 100 || mc.thePlayer.ticksExisted == 101){
//                        Wrapper.sendPacketDirect(packetQueue.poll());
//                    }
                    if (mc.isSingleplayer())
                        return;
                if(pingSpoofBool.getPropertyValue()) {
                    for (final Iterator<Map.Entry<Packet<?>, Long>> iterator = packets.entrySet().iterator(); iterator.hasNext(); ) {
                        final Map.Entry<Packet<?>, Long> entry = iterator.next();

                        if (entry.getValue() < System.currentTimeMillis()) {
                            Wrapper.sendPacketDirect(entry.getKey());
                            iterator.remove();
                        }
                    }
                }
                    break;
            }
            mode.onValueChange = () -> {
                if(mode.getPropertyValue().equals(disablerModes.Watchdog)){
                    pingSpoof.setHidden(false);
                    pingSpoofBool.setHidden(false);
                    setModuleSuffix(mode.getPropertyValue().name);
                } else {
                    pingSpoof.setHidden(true);
                    pingSpoofBool.setHidden(true);
                    setModuleSuffix(mode.getPropertyValue().name);
                }
            };
        });
    }
    private enum disablerModes {
        Watchdog("Watchdog"),
        WatchdogTimer("Watchdog Timer"),
        NoPayload("No Payload"),
        Spectator("Spectator"),
        TransactionSpam("Transaction Spam"),
        BlocksMC("BlocksMC"),
        Viper("Viper"),
        NullPlace("Null Place");

        private final String name;

        disablerModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }
    private boolean checkAction(final short action) {
        return action > 0 && action < 100;
    }
}