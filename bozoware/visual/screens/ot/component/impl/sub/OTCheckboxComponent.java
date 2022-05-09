package bozoware.visual.screens.ot.component.impl.sub;

import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.screens.ot.component.OTComponent;
import bozoware.visual.screens.ot.component.impl.ModuleButtonComponent;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OTCheckboxComponent extends OTComponent {

    private final Supplier<Boolean> supplier;
    private final Consumer<Boolean> consumer;
    private final String checkboxLabel;

    public OTCheckboxComponent(OTComponent parentComponent, String checkboxLabel, Supplier<Boolean> supplier, Consumer<Boolean> consumer, double xPosition, double yPosition, double width, double height) {
        super(parentComponent, xPosition, yPosition, width, height);
        this.supplier = supplier;
        this.consumer = consumer;
        this.checkboxLabel = checkboxLabel;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition();

        RenderUtil.drawSmoothRoundedRect((float) x, (float) y, (float) (x + 10), (float) (y + 10), 3, isHovering(mouseX, mouseY) ? 0xff404040 : 0xff303030);
        RenderUtil.setColor(0xff909090);
        getDefaultFontRenderer().drawString(checkboxLabel, x + 15, y + 2.2, 0xff909090);

        if (supplier.get()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(2.0F);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBlendFunc(770, 771);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            RenderUtil.setColor(-1);
            GL11.glVertex2d(x + 1, y + 5);
            GL11.glVertex2d(x + 3, y + 9);
            GL11.glVertex2d(x + 8, y + 2);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY) && mouseButton == 0) {
            consumer.accept(!supplier.get());
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    private boolean isHovering(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition() + ((ModuleButtonComponent) getParentComponent()).scrollValue;
        return mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 10;
    }

    @Override
    public double getHeight() {
        return 16;
    }

    @Override
    public void onKeyTyped(int typedKey) {

    }
}
