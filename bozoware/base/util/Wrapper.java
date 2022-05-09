package bozoware.base.util;

import bozoware.base.security.utils.SecurityUtils;
import bozoware.impl.UI.BozoAuthMenu;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.util.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bozoware.base.BozoWare.BozoUserName;

public class Wrapper {

    public static Minecraft<?> getMinecraft() {
        return Minecraft.getMinecraft();
    }
    public static EntityPlayerSP getPlayer() {
        return getMinecraft().thePlayer;
    }
    public static WorldClient getWorld() {
        return getMinecraft().theWorld;
    }

    public static boolean isSinglePlayer;

    public static void sendMessageToPlayer(String message) {
        getPlayer().addChatMessage(new ChatComponentText(message));
    }
    public static void sendMessageAsPlayer(String message) {
        getPlayer().sendChatMessage(message);
    }
    public static void sendPacketDirect(Packet<?> packet) {
        getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
    public static Block getBlock(BlockPos pos) {
        return getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    public static Vec3 getVec3(BlockPos blockPos) {
        return new Vec3((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
    }
    public static boolean isServerSinglePlayer() {
        return (isSinglePlayer);
    }

    public static String getCurrentServerIP() {
        return isServerSinglePlayer() ? "Singleplayer" : (Wrapper.getMinecraft().getCurrentServerData()).serverIP;
    }
    public static float[] getEntityRotations(final EntityPlayer player, final Entity target) {
        final double posX = target.posX - player.posX;
        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight() + 0.1);
        final double posZ = target.posZ - player.posZ;
        final float yaw = (float)(Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f;
        final double posMulti = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        final float pitch = (float)(-(Math.atan2(posY, posMulti) * 180.0 / Math.PI));
        return new float[] { yaw, pitch };
    }
    public static float[] getFacePos(Vec3 vec) {
        double diffX = vec.xCoord + 0.5D - Minecraft.getMinecraft().thePlayer.posX;
        double diffY = vec.yCoord + 0.5D - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double diffZ = vec.zCoord + 0.5D - Minecraft.getMinecraft().thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0D / Math.PI));
        return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }
        public static void sendPacketDelayed(final Packet packet, final long delay) {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(delay);
                            Wrapper.sendPacketDirect(packet);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            catch (Exception ignored) {}
        }
}