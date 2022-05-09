package bozoware.base.util.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class BloomShader {

    private static ShaderProgram bloomShader = new ShaderProgram("bozoware/base/util/visual/fragment/bloom.frag");

    private static Framebuffer bloomBuffer = new Framebuffer(1, 1, false);

    private static float radius;

    public BloomShader(int radius){
        this.radius = radius;
    }

    public static void bloom() {
        // horizontal bloom
        bloomShader.init();
        setupUniforms(1, 0, 0);
        bloomBuffer.framebufferClear();
        bloomBuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
        bloomShader.renderCanvas();
        bloomBuffer.unbindFramebuffer();


        // vertical bloom
        bloomShader.init();
        setupUniforms(0, 1, 0);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, bloomBuffer.framebufferTexture);
        bloomShader.renderCanvas();
        bloomShader.uninit();
    }

    public static void setupUniforms(float x, float y, int textureID) {
        bloomShader.setUniformi("originalTexture", 0);
        bloomShader.setUniformi("checkedTexture", textureID);
        bloomShader.setUniformf("texelSize", (float) (1.0 / Minecraft.getMinecraft().displayWidth), (float) (1.0 / Minecraft.getMinecraft().displayHeight));
        bloomShader.setUniformf("direction", x, y);
        bloomShader.setUniformf("shadowAlpha", 120);
        bloomShader.setUniformf("radius", radius);
    }
}
