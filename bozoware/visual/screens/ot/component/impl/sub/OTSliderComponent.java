package bozoware.visual.screens.ot.component.impl.sub;

import bozoware.base.util.misc.MathUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.screens.ot.component.OTComponent;
import bozoware.visual.screens.ot.component.impl.ModuleButtonComponent;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OTSliderComponent extends OTComponent {

    private final Supplier<Number> valueSupplier;
    private final Supplier<Number> maximumSupplier;
    private final Supplier<Number> minimumSupplier;
    private final Consumer<Number> consumer;
    private final String sliderLabel;
    private boolean active;

    private double sliderValueLabelAnimation, sliderValueLabelTarget;

    public OTSliderComponent(OTComponent parentComponent, Supplier<Number> supplier,
                             Supplier<Number> maximumSupplier,
                             Supplier<Number> minimumSupplier,
                             Consumer<Number> consumer, String sliderLabel, double xPosition, double yPosition, double width, double height) {
        super(parentComponent, xPosition, yPosition, width, height);
        this.valueSupplier = supplier;
        this.maximumSupplier = maximumSupplier;
        this.minimumSupplier = minimumSupplier;
        this.consumer = consumer;
        this.sliderLabel = sliderLabel;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition();

        RenderUtil.drawSmoothRoundedRect((float) x - 0.5f, (float) y + 10.5f, (float) (x + 195.5f), (float) (y + 18.5f), 4, 0xff353535);
        RenderUtil.drawSmoothRoundedRect((float) x, (float) y + 11, (float) (x + 195), (float) (y + 18), 4, 0xff252525);

        float sliderRectPercentage = (195.5f *
                ((valueSupplier.get().floatValue() - minimumSupplier.get().floatValue())
                        / (maximumSupplier.get().floatValue() - minimumSupplier.get().floatValue())));
        float indicatorPercentage = (190.5f *
                ((valueSupplier.get().floatValue() - minimumSupplier.get().floatValue())
                        / (maximumSupplier.get().floatValue() - minimumSupplier.get().floatValue())));

        if (sliderRectPercentage >= 1)
        RenderUtil.drawSmoothRoundedRect((float) (x - 0.5f), (float) (y + 10.5f), (float)
                (x + (sliderRectPercentage)), (float) (y + 18.5f), 4, isHovering(mouseX, mouseY) ? Color.ORANGE.brighter().getRGB() : Color.ORANGE.getRGB());

        //Slider indicator
        RenderUtil.drawSmoothRoundedRect((float) (x + (indicatorPercentage)),
                (float) y + 8, (float) (x + (indicatorPercentage) + 6), (float) (y + 21), 5, -1);

        RenderUtil.setColor(0xff909090);
        getDefaultFontRenderer().drawString(sliderLabel, x, y, 0xff909090);

        if (active && isHovering(mouseX, mouseY)) {
            final double mousePercent = (mouseX - (x - 0.5f)) / 195.5f;
            final double newValue = MathUtil.linearInterpolate(minimumSupplier.get().doubleValue(), maximumSupplier.get().doubleValue(), mousePercent);
            if (valueSupplier.get() instanceof Double) {
                consumer.accept(MathUtil.roundToPlace(newValue, 2));
            }
            if (valueSupplier.get() instanceof Integer) {
                consumer.accept((int) MathUtil.roundToPlace(newValue, 2));
            }
            if (valueSupplier.get() instanceof Float) {
                consumer.accept((float) MathUtil.roundToPlace(newValue, 2));
            }
            if(valueSupplier.get() instanceof Long){
                consumer.accept((long) MathUtil.roundToPlace(newValue, 2));
            }
        }

        sliderValueLabelAnimation = RenderUtil.animate(sliderValueLabelTarget, sliderValueLabelAnimation, 0.03);
        sliderValueLabelAnimation = Math.max(21, Math.min(sliderValueLabelAnimation, 127));

        getDefaultFontRenderer().drawString(String.valueOf(MathUtil.roundToPlace(valueSupplier.get().doubleValue(), 2)), x + getDefaultFontRenderer().getStringWidth(sliderLabel) + 3, y, new Color(
                (int) sliderValueLabelAnimation,
                (int) sliderValueLabelAnimation,
                (int) sliderValueLabelAnimation).getRGB());

        if (!active)
            sliderValueLabelTarget = 21;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY) && mouseButton == 0) {
            active = true;
            sliderValueLabelTarget = 127;
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        active = false;
    }

    @Override
    public void onKeyTyped(int typedKey) {

    }

    @Override
    public double getHeight() {
        return 26;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition() + ((ModuleButtonComponent) getParentComponent()).scrollValue;
        return mouseX >= x - 0.5f && mouseX <= x + 195.5f && mouseY >= y + 10.5f && mouseY <= y + 18.5f;
    }
}
