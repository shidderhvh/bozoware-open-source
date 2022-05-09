package bozoware.impl.module.combat;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.property.ValueProperty;

@ModuleData(moduleName = "Reach", moduleCategory = ModuleCategory.COMBAT)
public class Reach extends Module {

    private final ValueProperty<Double> rangeValue = new ValueProperty("Range", 3.5, 3, 6, this);

    public Reach() {
        // stupid skid
    }
        private static Reach getInstance () {
            return (Reach) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Reach.class);
        }

        public static float getReachValue () {
            return getInstance().rangeValue.getPropertyValue().floatValue();
        }

}
