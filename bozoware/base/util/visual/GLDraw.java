package bozoware.base.util.visual;

import static org.lwjgl.opengl.GL11.*;

public class GLDraw {

    public static void glFilledQuad(double x, double y, double width, double height, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_QUADS);
        RenderUtil.setColor(color);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void glFilledEllipse(double x, double y, float radius, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_POINT_SMOOTH);
        glPointSize(radius);
        glBegin(GL_POINTS);
        RenderUtil.setColor(color);
        {
            glVertex2d(x, y);
        }
        glEnd();
        glDisable(GL_POINT_SMOOTH);
        glEnable(GL_TEXTURE_2D);
    }
}
