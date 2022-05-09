package bozoware.impl.property;

import bozoware.base.module.Module;
import bozoware.base.property.Property;

import java.awt.*;

public class ColorProperty extends Property<Color> {

    private Color color;

    public ColorProperty(String propertyLabel, Color propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
    }

    public Color getPropertyValue() {
        return super.getPropertyValue();
    }

    public int getColorRGB() {
        return super.getPropertyValue().getRGB();
    }

    public void setPropertyValue(Color color) {
        super.setPropertyValue(color);
    }

    public void setPropertyValue(int color) {super.setPropertyValue(new Color(color));}
}
