package bozoware.base.property;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;

public class Property<T> {

    private final String propertyLabel;
    private T propertyValue;
    private final Module parentModule;
    private boolean hidden;

    public Property(String propertyLabel, T propertyValue, Module parentModule) {
        this.propertyLabel = propertyLabel;
        this.propertyValue = propertyValue;
        this.parentModule = parentModule;
        BozoWare.getInstance().getPropertyManager().addProperty(this);
    }

    public T getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(T propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public Module getParentModule() {
        return parentModule;
    }

    public Class<?> getType() {
        return propertyValue.getClass();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Runnable onValueChange = () -> {};
}
