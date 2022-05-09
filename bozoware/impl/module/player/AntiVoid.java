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
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.movement.Flight;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

@ModuleData(moduleName = "Anti Void", moduleCategory = ModuleCategory.WORLD)
public class AntiVoid extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    public static int flags;
    TimerUtil timer = new TimerUtil();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();
    public double[] lastGroundPos = new double[3];



    private final ValueProperty<Integer> distance = new ValueProperty("Distance", 8, 1, 16, this);
    private final EnumProperty<damode> antivoid = new EnumProperty<>("Mode", damode.Hypixel, this);

    BlockPos lastPos;
    public boolean damaged;

    public AntiVoid(){
        distance.setHidden(true);
        onPacketSendEvent = (e -> {
            switch(this.antivoid.getPropertyValue()){
                case Hypixel:
                    if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100)
                        packets.clear();
                    if(mc.thePlayer.ticksExisted < 150)
                        return;
                    if (e.getPacket() instanceof C03PacketPlayer && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled()) {
                        C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());
                        if (isInVoid() && this.mc.thePlayer.fallDistance > distance.getPropertyValue()) {
                            if(timer.hasReached(450L) && mc.thePlayer.motionY < 0) {
//                                mc.thePlayer.addChatMessage(new ChatComponentText("hi"));
                                e.setCancelled(true);
                                packets.add(packet);

                                if (timer.hasReached(800L) && !isBlockUnder()) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1], lastGroundPos[2], true));
                                }
                            }
                        } else {
                            this.timer.reset();
                            lastGroundPos[0] = mc.thePlayer.posX;
                            lastGroundPos[1] = mc.thePlayer.posY;
                            lastGroundPos[2] = mc.thePlayer.posZ;

                            if (!packets.isEmpty()) {
                                for (C03PacketPlayer p : packets)
                                    mc.getNetHandler().getNetworkManager().sendPacket(p);
                                packets.clear();
                            }
                            timer.reset();
                        }
                    }
                    break;
            }
        });
        onPacketReceiveEvent = (e -> {
            if(e.getPacket() instanceof S08PacketPlayerPosLook && !isBlockUnder()){
                AntiVoid.flags = flags + 1;
            }
            if(e.getPacket() instanceof S08PacketPlayerPosLook){
                packets.clear();
            }
        });
        onUpdatePositionEvent = (e -> {
            if (mc.thePlayer.onGround) {
                lastPos = mc.thePlayer.getPosition();
            }
            switch (antivoid.getPropertyValue()) {
                case Motion:
                    if (this.mc.thePlayer.fallDistance > distance.getPropertyValue() && !isBlockUnder()) {
                        if (!this.timer.hasReached(1000L)) {
                            mc.thePlayer.motionX = 0.2;
                            mc.thePlayer.motionZ = 0.2;
                        }
                    }
                    break;
                case Stop:
                        if (this.mc.thePlayer.fallDistance > distance.getPropertyValue() && !isBlockUnder()) {
                            if(!this.timer.hasReached(1000L)){
                                mc.thePlayer.motionY = 0;
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                            }
                        } else {
                            this.timer.reset();
                        }
                    break;
                case Jump:
                    if(AntiVoid.flags < 2){
                        if (this.mc.thePlayer.fallDistance > distance.getPropertyValue() && !isBlockUnder()) {
                            mc.thePlayer.jump();
                        }
                    }
                    break;
                case Position:
                    if(AntiVoid.flags < 8) {
                        if(!Jesus.isInLiquid() && !Jesus.isOnLiquid()){
                            if (this.mc.thePlayer.fallDistance > distance.getPropertyValue() && !isBlockUnder()) {
                                if (mc.thePlayer.ticksExisted % 2 == 0) {
                                    e.setX(e.getX() + Math.max(MovementUtil.getMoveSpeed(), 0.2 + Math.random() / 100));
                                    e.setZ(e.getZ() + Math.max(MovementUtil.getMoveSpeed(), Math.random() / 100));
                                } else {
                                    e.setX(e.getX() - Math.max(MovementUtil.getMoveSpeed(), 0.2 + Math.random() / 100));
                                    e.setZ(e.getZ() - Math.max(MovementUtil.getMoveSpeed(), Math.random() / 100));
                                }
                                break;
                            }
                        }
                    }
                break;
                case Hypixel:

            }
            if(mc.thePlayer.onGround){
                flags = 0;
            }
        });
        antivoid.onValueChange = () -> {
            setModuleSuffix(antivoid.getPropertyValue().name());
            if(antivoid.getPropertyValue().equals(damode.Hypixel)){
                distance.setHidden(true);
            } else {
                distance.setHidden(false);
            }
        };


    }

    public static boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }
    public static boolean isInVoid() {
        for (int i = 0; i <= 128; i++) {
            if (MovementUtil.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }
    private enum damode{
        Hypixel,
        Jump,
        Stop,
        Motion,
        Position
    }
    }

