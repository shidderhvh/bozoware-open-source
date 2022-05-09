package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

@ModuleData(moduleName = "InvMove", moduleCategory = ModuleCategory.PLAYER)
public class Invmove extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Invmove() {
        onModuleDisabled = () -> {

        };
        onModuleEnabled = () -> {

        };
        onUpdatePositionEvent = (UpdatePositionEvent -> {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
                if (Keyboard.isKeyDown(17)) {
                    mc.gameSettings.keyBindForward.pressed = true;
                } else {
                    mc.gameSettings.keyBindForward.pressed = false;
                }

                if (Keyboard.isKeyDown(31)) {
                    mc.gameSettings.keyBindBack.pressed = true;
                } else {
                    mc.gameSettings.keyBindBack.pressed = false;
                }

                if (Keyboard.isKeyDown(32)) {
                    mc.gameSettings.keyBindRight.pressed = true;
                } else {
                    mc.gameSettings.keyBindRight.pressed = false;
                }

                if (Keyboard.isKeyDown(30)) {
                    mc.gameSettings.keyBindLeft.pressed = true;
                } else {
                    mc.gameSettings.keyBindLeft.pressed = false;
                }

                EntityPlayerSP thePlayer4;
                if (Keyboard.isKeyDown(203)) {
                    thePlayer4 = mc.thePlayer;
                    thePlayer4.rotationYaw -= 4.0F;
                }

                if (Keyboard.isKeyDown(205)) {
                    thePlayer4 = mc.thePlayer;
                    thePlayer4.rotationYaw += 4.0F;
                }

                if (Keyboard.isKeyDown(200)) {
                    thePlayer4 = mc.thePlayer;
                    thePlayer4.rotationPitch -= 4.0F;
                }

                if (Keyboard.isKeyDown(208)) {
                    thePlayer4 = mc.thePlayer;
                    thePlayer4.rotationPitch += 4.0F;
                }

                if (mc.thePlayer.rotationPitch >= 90.0F) {
                    mc.thePlayer.rotationPitch = 90.0F;
                }

                if (mc.thePlayer.rotationPitch <= -90.0F) {
                    mc.thePlayer.rotationPitch = -90.0F;
                }

                if (Keyboard.isKeyDown(57) && mc.thePlayer.onGround && !mc.thePlayer.isInWater()) {
                    mc.gameSettings.keyBindJump.pressed = true;
                } else {
                    mc.gameSettings.keyBindJump.pressed = false;
                }
                if(mc.thePlayer.isInWater() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())){
                    mc.gameSettings.keyBindJump.pressed = true;
                }
            }
        });
    }
}
