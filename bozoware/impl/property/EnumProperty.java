package bozoware.impl.property;

import bozoware.base.module.Module;
import bozoware.base.property.Property;

import java.util.Arrays;

public class EnumProperty<T extends Enum<T>> extends Property<T> {

    private final T[] enumValues;

    public EnumProperty(String propertyLabel, T propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
        this.enumValues = propertyValue.getDeclaringClass().getEnumConstants();
    }

    public void increment() {
        T currentValue = getPropertyValue();
        for (T constant : getEnumValues()) {
            if (constant != currentValue) {
                continue;
            }
            T newValue;
            int ordinal = Arrays.asList(getEnumValues()).indexOf(constant);
            if (ordinal == getEnumValues().length - 1) {
                newValue = getEnumValues()[0];
            } else {
                newValue = getEnumValues()[ordinal + 1];
            }
            setPropertyValue(newValue);
            return;
        }
        onValueChange.run();
    }

    public void decrement() {
        T currentValue = getPropertyValue();
        for (T constant : getEnumValues()) {
            if (constant != currentValue) {
                continue;
            }
            T newValue;
            int ordinal = Arrays.asList(getEnumValues()).indexOf(constant);
            if (ordinal == 0) {
                newValue = getEnumValues()[getEnumValues().length - 1];
            } else {
                newValue = getEnumValues()[ordinal - 1];
            }
            setPropertyValue(newValue);
            return;
        }
        onValueChange.run();
    }

    @Override
    public void setPropertyValue(T propertyValue) {
        super.setPropertyValue(propertyValue);
        onValueChange.run();
    }

    @Override
    public T getPropertyValue() {
        return super.getPropertyValue();
    }

    public T[] getEnumValues() {
        return enumValues;
    }
}
