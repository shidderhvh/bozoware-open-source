package bozoware.impl.property;

import bozoware.base.module.Module;
import bozoware.base.property.Property;

public class ValueProperty<T extends Number> extends Property<T> {

    private final T minimumValue, maximumValue;

    public ValueProperty(String propertyLabel, T propertyValue, T minimumValue, T maximumValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public T getPropertyValue() {
        return super.getPropertyValue();
    }

    @Override
    public void setPropertyValue(T propertyValue) {
        super.setPropertyValue(propertyValue);
        onValueChange.run();
    }

    @Override
    public String getPropertyLabel() {
        return super.getPropertyLabel();
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    public T getMinimumValue() {
        return minimumValue;
    }
}
