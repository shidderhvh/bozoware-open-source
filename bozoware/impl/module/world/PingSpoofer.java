package bozoware.impl.module.world;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.module.movement.Flight;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.module.player.InvManager;
import bozoware.impl.module.player.Scaffold;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@ModuleData(moduleName = "Ping Spoof", moduleCategory = ModuleCategory.WORLD)
public class PingSpoofer extends Module {

    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private final EnumProperty<pingSpoofMode> mode = new EnumProperty<>("Mode", pingSpoofMode.Watchdog, this);
    public final ValueProperty<Integer> delay = new ValueProperty<>("Delay", 500, 100, 5000, this);
    public static final TimerUtil spikeStopwatch = new TimerUtil();
    public static int stage = 0;
    public final ValueProperty<Double> spikeInterval = new ValueProperty<Double>("Spike Interval", 10000D, 5000D, 20000D, this);
    public final ValueProperty<Double> spikeAmount = new ValueProperty<Double>("Spike Delay", 200D, 150D, 500D, this);
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onLoadWorldEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public PingSpoofer(){
        delay.setHidden(true);
        mode.onValueChange = () -> {
            if (mode.getPropertyValue().equals(pingSpoofMode.Normal)) {
                delay.setHidden(false);
                spikeInterval.setHidden(true);
                spikeAmount.setHidden(true);
            } else {
                delay.setHidden(true);
                spikeInterval.setHidden(false);
                spikeAmount.setHidden(false);
            }
            setModuleSuffix(mode.getPropertyValue() + "");
        };
        onUpdatePositionEvent = (e -> {
            if (!e.isPre())
                return;
            if (stage == 0) {
                if (spikeStopwatch.hasReached(1000)) {
                    if (!packetQueue.isEmpty())
                        mc.getNetHandler().addToSendQueue(packetQueue.remove());
                    spikeStopwatch.reset();
                }
            }
            if (stage > 0 && shouldReset()) {
                stage = 0;
            }
            if (stage == 1 && spikeStopwatch.hasReached(spikeInterval.getPropertyValue().intValue()) && !BlockFly.getInstance().isModuleToggled() && !Scaffold.getInstance().isModuleToggled()) {
                spikeStopwatch.reset();
                stage = 2;
            }
        });
        onLoadWorldEvent = (e -> {
            packetQueue.clear();
//            stage = 0;
            spikeStopwatch.reset();
        });
        onModuleEnabled = () -> setModuleSuffix(mode.getPropertyValue() + "");;
        delay.onValueChange = () -> {
            setModuleSuffix(mode.getPropertyValue() + "");
        };
        onPacketReceiveEvent = (e -> {
            switch (mode.getPropertyValue()){
                case Watchdog:
                    if (e.getPacket() instanceof S08PacketPlayerPosLook && !Flight.getInstance().isModuleToggled() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Test.class).isModuleToggled() && mc.thePlayer.ticksExisted > 5) {
                        final S08PacketPlayerPosLook packetIn = (S08PacketPlayerPosLook) e.getPacket();
                        final EntityPlayer entityplayer = mc.thePlayer;
                        double d0 = packetIn.getX();
                        double d1 = packetIn.getY();
                        double d2 = packetIn.getZ();

                        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
                            d0 += entityplayer.posX;
                        } else {
                            entityplayer.motionX = 0.0D;
                        }

                        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
                            d1 += entityplayer.posY;
                        } else {
                            entityplayer.motionY = 0.0D;
                        }

                        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
                            d2 += entityplayer.posZ;
                        } else {
                            entityplayer.motionZ = 0.0D;
                        }
                        entityplayer.setPosition(d0, d1, d2);
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, false));

                        if (!mc.getNetHandler().doneLoadingTerrain) {
                            entityplayer.prevPosX = entityplayer.posX;
                            entityplayer.prevPosY = entityplayer.posY;
                            entityplayer.prevPosZ = entityplayer.posZ;
                            mc.getNetHandler().doneLoadingTerrain = true;
                            mc.displayGuiScreen(null);
                        }
                        e.setCancelled(true);
                        if (stage == 1) {
                            spikeStopwatch.reset();
                            stage = 2;
//                            BozoWare.getInstance().chat("Spiking... Lagback...");
                        }
                    }
                    else if (e.getPacket() instanceof S32PacketConfirmTransaction && stage == 0) {
                        final S32PacketConfirmTransaction packetConfirmTransaction = (S32PacketConfirmTransaction) e.getPacket();
                        if (packetConfirmTransaction.getWindowId() == 0 && packetConfirmTransaction.getActionNumber() < 0) {
                            stage = 1;
                        }
                    }
                    break;
            }
        });
        onPacketSendEvent = (e -> {
            switch (mode.getPropertyValue()) {
                case Normal:
                    if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                        e.setCancelled(true);
                        packetQueue.add(e.getPacket());
                    }
                    if (e.getPacket() instanceof C01PacketPing) {
                        C01PacketPing packet = (C01PacketPing) e.getPacket();
                        packet.setClientTime(delay.getPropertyValue());
                        e.setPacket(packet);
//                    BozoWare.getInstance().chat("Sent");

                    }
                    if (e.getPacket() instanceof C00PacketKeepAlive) {
                        e.setCancelled(true);
                        packetQueue.add(e.getPacket());
                    }
                    if(spikeStopwatch.hasReached(delay.getPropertyValue()))
                        if(!packetQueue.isEmpty()) {
                            Wrapper.sendPacketDirect(packetQueue.poll());
//                        BozoWare.getInstance().chat("SENT");
                        }
                    break;
                case Watchdog:
                    switch (stage) {
                        case 0:
                            packetQueue.add(e.getPacket());
                            e.setCancelled(true);
                            break;
                        case 1:
                            while (!packetQueue.isEmpty())
                                Wrapper.sendPacketDirect(packetQueue.remove());
                            // idk yet
                            break;
                        case 2:
                            if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof C0FPacketConfirmTransaction) {
                                packetQueue.add(e.getPacket());
                                e.setCancelled(true);
                            }
                            if (spikeStopwatch.hasReached(spikeAmount.getPropertyValue().intValue())) {
                                while (!packetQueue.isEmpty())
                                    Wrapper.sendPacketDirect(packetQueue.remove());
                                spikeStopwatch.reset();
//                                BozoWare.getInstance().chat("Flushed");
                                stage = 1;
                            }
                            break;
                    }
                    break;
            }
        });
    }
    private enum pingSpoofMode{
        Normal,
        Watchdog
    }
    private boolean shouldReset() {
        return mc.thePlayer.ticksExisted < 5;
    }
}