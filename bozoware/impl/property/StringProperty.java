package bozoware.impl.property;

import bozoware.base.module.Module;
import bozoware.base.property.Property;

public class StringProperty extends Property<String> {

    public StringProperty(String propertyLabel, String propertyValue, Module parentModule) {
        super(propertyLabel, propertyValue, parentModule);
    }

    @Override
    public String getPropertyValue() {
        return super.getPropertyValue();
    }

    @Override
    public void setPropertyValue(String propertyValue) {
        super.setPropertyValue(propertyValue);
    }

}
