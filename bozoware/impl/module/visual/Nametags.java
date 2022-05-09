package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.PlayerUtils;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.RenderNametagEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.module.combat.AntiBot;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@ModuleData(moduleName = "Nametags", moduleCategory = ModuleCategory.VISUAL)
public class Nametags extends Module {

    @EventListener
    EventConsumer<RenderNametagEvent> onRenderNameTagEvent;

    @EventListener
    EventConsumer<Render2DEvent> onRender3D;

    public Nametags() {
        onRenderNameTagEvent = (e -> {
            e.setCancelled(true);
        });
        onRender3D = (event -> {
            MinecraftFontRenderer fr = BozoWare.getInstance().getFontManager().McFontRenderer;

            for (EntityPlayer entity : mc.theWorld.playerEntities) {

                if (entity.isInvisible() || entity == mc.thePlayer)
                    continue;
                if(AntiBot.botList.contains(entity.getEntityId()))
                    return;

                GL11.glPushMatrix();


                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                //float distance = mc.thePlayer.getDistanceToEntity(entity);


                GL11.glTranslated(x, y + entity.getEyeHeight() + 1.7, z);
                GL11.glNormal3f(0, 1, 0);
                if (mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                    GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
                } else {
                    GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0, 1, 0);
                    GlStateManager.rotate(mc.thePlayer.rotationPitch, 1, 0, 0);
                }
                float distance = mc.thePlayer.getDistanceToEntity(entity),
                        scaleConst_1 = 0.02672f, scaleConst_2 = 0.10f;
                double maxDist = 7.0;


                float scaleFactor = (float) (distance <= maxDist ? maxDist * scaleConst_2 : (double) (distance * scaleConst_2));
                scaleConst_1 *= scaleFactor;

                float scaleBet = (float) (3 * 10E-3);
                scaleConst_1 = Math.min(scaleBet, scaleConst_1);


                GL11.glScalef(-scaleConst_1, -scaleConst_1, .2f);

                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GL11.glDisable(GL11.GL_DEPTH_TEST);



                String colorCode = entity.getHealth() > 15 ? "\247a" : entity.getHealth() > 10 ? "\247e" : entity.getHealth() > 7 ? "\2476" : "\247c";
                int colorrectCode = entity.getHealth() > 15 ? 0xff4DF75B : entity.getHealth() > 10 ? 0xffF1F74D : entity.getHealth() > 7 ? 0xffF7854D : 0xffF7524D;
                String thing = entity.getName() + " " + colorCode + (int) entity.getHealth();
                float namewidth = (float) fr.getStringWidth(thing);


//                Gui.drawRect(-namewidth / 2 - 2, 42, namewidth / 2 + 2, 40, 0x90080808);


//                    Gui.drawRect(-namewidth / 2 - 15, 42, namewidth / 2 + 15 - (1 - (entity.getHealth() / entity.getMaxHealth())) * (namewidth + 4), 40, colorrectCode);

//                    Gui.drawRect(-namewidth / 2 - 15, 20, namewidth / 2 + 15, 40, 0x90202020);


//                Gui.drawRect(-20 - (namewidth / 2), 43, namewidth / 2 + 15, 26, 0x40000000);
//                BlurUtil.blurArea(-20 - (namewidth / 2), 43, namewidth / 2 + 15, 26);
                RenderUtil.drawBoxOutline(-20 - (namewidth / 2), 43, namewidth / 2 + 15, 26, 30, 1);
                fr.drawCenteredStringWithShadow(entity.getName(), -20, 30, -1);
                fr.drawCenteredStringWithShadow(colorCode + (int) entity.getHealth(), namewidth / 2, 30, -1);


                GlStateManager.disableBlend();
                GlStateManager.depthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);


                double movingArmor = 1.2;

                if (namewidth <= 65) {
                    movingArmor = 2;
                }
                if (namewidth <= 85) {
                    movingArmor = 1.2;
                }

                if (namewidth <= 100) {
                    movingArmor = 1.1;
                }

                    for (int index = 0; index < 5; index++) {

                        if (entity.getEquipmentInSlot(index) == null)
                            continue;


//                        renderItem(entity.getEquipmentInSlot(index), (int) (index * 19 / movingArmor) - 30, -10);


                    }

                GL11.glPopMatrix();

            }
        });

        }

    private boolean isValidEntity(Entity entity) {
        return entity != null && entity != mc.thePlayer;
    }
    public int getNametagColor(EntityLivingBase entity) {
        int color = 0xFFFFFFFF;
        if (entity instanceof EntityPlayer && PlayerUtils.isOnSameTeam(entity)) {
            color = 0xFF4DB3FF;
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = 0xFFFFE600;
        } else if (entity.isSneaking()) {
            color = 0xFFFF0000;
        }
        return color;
    }
    public void drawNametags(EntityLivingBase entity, double x, double y, double z) {
        String entityName = entity.getDisplayName().getFormattedText();

        if (getNametagColor(entity) != 0xFFFFFFFF)
            entityName = StringUtils.stripControlCodes(entityName);
        if (entity.isDead)
            return;
//        if ((entity instanceof EntityPlayer) && ((EntityPlayer) entity).capabilities.isFlying)
//            entityName = "\247a[F] \247r" + entityName;

//        if ((entity instanceof EntityPlayer) && ((EntityPlayer) entity).capabilities.isCreativeMode)
//            entityName = "\247a[C] \247r" + entityName;

//        if (entity.getDistanceToEntity(mc.thePlayer) >= 64) {
//            entityName = "\2472* \247r" + entityName;
//        }

        double health = entity.getHealth() / 2;
        double maxHealth = entity.getMaxHealth() / 2;
        double percentage = 100 * (health / maxHealth);
        String healthColor;
        healthColor = "2";
        DecimalFormat df = new DecimalFormat();
        String healthDisplay = df.format(Math.floor((health + (double) 0.5F / 2) / 0.5F) * 0.5F);
        String maxHealthDisplay = df.format(Math.floor((entity.getMaxHealth() + (double) 0.5F / 2) / 0.5F) * 0.5F);
        entityName = String.format("%s \247%s%s", entityName, healthColor, healthDisplay);
        float distance = mc.thePlayer.getDistanceToEntity(entity);
        float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 0.95f;
        float var14 = 0.016666668F * var13;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(x + 0.0F, y + entity.height + 0.5F, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        if (mc.gameSettings.thirdPersonView == 2) {
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
        } else {
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.scale(-var14, -var14, var14);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int var17 = 0;
        if (entity.isSneaking()) {
            var17 += 4;
        }

        var17 -= distance / 5;
        if (var17 < -8) {
            var17 = -8;
        }

        GlStateManager.enableAlpha();
        worldRenderer.startDrawingQuads();
        int var18 = mc.fontRendererObj.getStringWidth(entityName) / 2;
        worldRenderer.color(0.0F, 0.0F, 0.0F, 0.25f);
        worldRenderer.pos(-var18 - 2, -2 + var17, 0.0D);
        worldRenderer.pos(-var18 - 2, 9 + var17, 0.0D);
        worldRenderer.pos(var18 + 2, 9 + var17, 0.0D);
        worldRenderer.pos(var18 + 2, -2 + var17, 0.0D);
        tessellator.draw();
        GlStateManager.disableAlpha();

        BozoWare.getInstance().getFontManager().mediumFontRenderer.drawNoBSString(entityName, -var18, var17, getNametagColor(entity), true);
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            List<ItemStack> items = new ArrayList<>();
            if (player.getCurrentEquippedItem() != null) {
                items.add(player.getCurrentEquippedItem());
            }

            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];
                if (stack != null) {
                    items.add(stack);
                }
            }

            int offset = var18 - (items.size() - 1) * 9 - 9;
            int xPos = 0;
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                RenderHelper.enableStandardItemLighting();
                mc.getRenderItem().zLevel = -100.0F;
                mc.getRenderItem().renderItemAboveHead(stack, -var18 + offset + xPos, var17 - 20);
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, -var18 + offset + xPos, var17 - 20);
                mc.getRenderItem().zLevel = 0.0F;
                RenderHelper.disableStandardItemLighting();
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.scale(0.50F, 0.50F, 0.50F);
                if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
                    BozoWare.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow("god", (-var18 + offset + xPos) * 2, (var17 - 20) * 2,
                            0xFFFF0000);
                } else {
                    NBTTagList enchants = stack.getEnchantmentTagList();

                    if (enchants != null) {
                        int encY = 0;
                        Enchantment[] important = new Enchantment[]{Enchantment.protection,
                                Enchantment.unbreaking, Enchantment.sharpness, Enchantment.fireAspect,
                                Enchantment.efficiency, Enchantment.featherFalling, Enchantment.power,
                                Enchantment.flame, Enchantment.punch, Enchantment.fortune, Enchantment.infinity,
                                Enchantment.thorns};
                        if (enchants.tagCount() >= 6) {
                            BozoWare.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow("god", (-var18 + offset + xPos) * 2,
                                    (var17 - 20) * 2, 0xFFFF0000);
                        } else {
                            for (int index = 0; index < enchants.tagCount(); ++index) {
                                short id = enchants.getCompoundTagAt(index).getShort("id");
                                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                                Enchantment enc = Enchantment.getEnchantmentById(id);
                                if (enc != null) {
                                    for (Enchantment importantEnchantment : important) {
                                        if (enc == importantEnchantment) {
                                            String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                                            if (level > 99)
                                                encName = encName + "99+";
                                            else
                                                encName = encName + level;
                                            BozoWare.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(encName, (-var18 + offset + xPos) * 2, (var17 - 20 + encY) * 2, 0xFFAAAAAA);
                                            encY += 5;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                xPos += 18;
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
    public static void renderItem(ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = -100.0f;
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        GlStateManager.enableDepth();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y + 8);
        //mc.getRenderItem().renderItemOverlayIntoGUINameTags(mc.fontRendererObj, stack, x - 1, y + 10, null);
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        //NameTags.renderEnchantText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }
}
