package bozoware.impl.property;

import bozoware.base.module.Module;
import bozoware.base.property.Property;

public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String propertyLabel, Boolean propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
    }

    @Override
    public Boolean getPropertyValue() {
        return super.getPropertyValue();
    }

    @Override
    public void setPropertyValue(Boolean propertyValue) {
        super.setPropertyValue(propertyValue);
        onValueChange.run();
    }
}
