package bozoware.base.module;

import bozoware.base.BozoWare;
import bozoware.base.property.Property;
import bozoware.base.util.visual.Animate.Animation;
import bozoware.base.util.visual.Animate.impl.DecelerateAnimation;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class Module {

    private final String moduleName;
    private int moduleBind;
    private final ModuleCategory moduleCategory;
    protected static Minecraft<?> mc = Minecraft.getMinecraft();
    public final Animation animation = new DecelerateAnimation(250, 1);

    private boolean moduleToggled;

    private String moduleSuffix;

    public Module() {
        if (hasIdentifier()) {
            moduleName = getClass().getAnnotation(ModuleData.class).moduleName();
            moduleCategory = getClass().getAnnotation(ModuleData.class).moduleCategory();
        }
        else {
            moduleName = "Unidentified";
            moduleCategory = ModuleCategory.COMBAT;
        }
    }

    public Runnable onModuleEnabled = () -> {};

    public Runnable onModuleDisabled = () -> {};

    public String getModuleName() {
        return moduleName;
    }

    public int getModuleBind() {
        return moduleBind;
    }

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }

    public void setModuleBind(int moduleBind) {
        this.moduleBind = moduleBind;
    }

    public boolean isModuleToggled() {
        return moduleToggled;
    }

    public void setModuleSuffix(String moduleSuffix) {
        this.moduleSuffix = moduleSuffix;
    }

    public String getModuleDisplayName() {
        return getModuleName() + (moduleSuffix == null || HUD.getInstance().hideSuffixes.getPropertyValue() ? "" : " \2477" + moduleSuffix);
    }

    public void setModuleToggled(boolean moduleToggled) {
        this.moduleToggled = moduleToggled;
        if (moduleToggled) {
            BozoWare.getInstance().getEventManager().subscribe(this);
            onModuleEnabled.run();
        }
        else {
            BozoWare.getInstance().getEventManager().unsubscribe(this);
            onModuleDisabled.run();
        }
    }

    public void toggleModule() {
        this.moduleToggled = !this.moduleToggled;
        if (moduleToggled) {
            BozoWare.getInstance().getEventManager().subscribe(this);
            onModuleEnabled.run();
        }
        else {
            BozoWare.getInstance().getEventManager().unsubscribe(this);
            onModuleDisabled.run();
        }
    }

    public ArrayList<Property<?>> getModuleProperties() {
        return BozoWare.getInstance().getPropertyManager().getPropertiesByModule(this);
    }

    public JsonObject saveJson() {
        JsonObject moduleObject = new JsonObject();
        moduleObject.addProperty("toggled", moduleToggled);
        moduleObject.addProperty("bind", getModuleBind());
        if (!getModuleProperties().isEmpty()) {
            JsonObject propertiesObject = new JsonObject();
            getModuleProperties().forEach(property -> {
                if (property instanceof EnumProperty) {
                    EnumProperty<?> enumProperty = (EnumProperty<?>) property;
                    propertiesObject.add(enumProperty.getPropertyLabel(), new JsonPrimitive((enumProperty.getPropertyValue()).name()));
                }
                if (property instanceof BooleanProperty) {
                    BooleanProperty booleanProperty = (BooleanProperty) property;
                    propertiesObject.addProperty(booleanProperty.getPropertyLabel(), booleanProperty.getPropertyValue());
                }
                if (property instanceof ValueProperty) {
                    ValueProperty<?> valueProperty = (ValueProperty<?>) property;
                    propertiesObject.addProperty(valueProperty.getPropertyLabel(), valueProperty.getPropertyValue());
                }
                if (property instanceof ColorProperty) {
                    ColorProperty colorProperty = (ColorProperty) property;
                    propertiesObject.addProperty(colorProperty.getPropertyLabel(), colorProperty.getColorRGB());
                }
                if(property instanceof StringProperty){
                    StringProperty stringProperty = (StringProperty) property;
                    propertiesObject.addProperty(stringProperty.getPropertyLabel(), stringProperty.getPropertyValue());
                }
            });
            moduleObject.add("properties", propertiesObject);
        }
        return moduleObject;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void loadJson(JsonObject jsonObject) {
        if (jsonObject.has("toggled")) {
            setModuleToggled(jsonObject.get("toggled").getAsBoolean());
        }
        if (jsonObject.has("bind")) {
            setModuleBind(jsonObject.get("bind").getAsInt());
        }
        if (jsonObject.has("properties")) {
            JsonObject propertiesObject = jsonObject.getAsJsonObject("properties");
            getModuleProperties().forEach(property -> {
                if (propertiesObject.has(property.getPropertyLabel())) {
                    if (property instanceof EnumProperty) {
                        EnumProperty enumProperty = (EnumProperty) property;
                        for (int i = 0; i < enumProperty.getEnumValues().length; i++) {
                            if (enumProperty.getEnumValues()[i].name().equalsIgnoreCase(
                                    propertiesObject.getAsJsonPrimitive(property.getPropertyLabel()).getAsString())) {
                                enumProperty.setPropertyValue(enumProperty.getEnumValues()[i]);
                            }
                        }
                    }
                    if (property instanceof BooleanProperty) {
                        BooleanProperty booleanProperty = (BooleanProperty) property;
                        booleanProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsBoolean());
                    }
                    if(property instanceof StringProperty){
                        StringProperty stringProperty = (StringProperty) property;
                        stringProperty.setPropertyValue(((StringProperty) property).getPropertyValue());
                    }
                    if (property instanceof ValueProperty) {
                        ValueProperty<Number> valueProperty = (ValueProperty) property;
                        if (valueProperty.getPropertyValue() instanceof Integer) {
                            valueProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsInt());
                        }
                        if (valueProperty.getPropertyValue() instanceof Double) {
                            valueProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsDouble());
                        }
                        if (valueProperty.getPropertyValue() instanceof Float) {
                            valueProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsFloat());
                        }
                        if (valueProperty.getPropertyValue() instanceof Long) {
                            valueProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsLong());
                        }
                    }
                    if (property instanceof ColorProperty) {
                        ColorProperty colorProperty = (ColorProperty) property;
                        colorProperty.setPropertyValue(propertiesObject.get(property.getPropertyLabel()).getAsInt());
                    }
                }
            });
        }
    }

    private boolean hasIdentifier() {
        return getClass().isAnnotationPresent(ModuleData.class);
    }
}
