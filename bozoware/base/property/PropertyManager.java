package bozoware.base.property;

import bozoware.base.module.Module;

import java.util.ArrayList;

public class PropertyManager {

    private ArrayList<Property<?>> properties;

    public ArrayList<Property<?>> getProperties() {
        return properties;
    }

    public ArrayList<Property<?>> getPropertiesByModule(Module module) {
        ArrayList<Property<?>> properties = new ArrayList<>(getProperties());
        properties.removeIf(property -> !property.getParentModule().equals(module));
        return properties;
    }

    public void addProperty(Property<?> property) {
        properties.add(property);
    }

    public PropertyManager() {
        Runnable onStartTask = () -> properties = new ArrayList<>();
        onStartTask.run();
    }

}
