package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;
import com.sun.tracing.dtrace.ModuleName;

@ModuleData(moduleName = "Scoreboard", moduleCategory = ModuleCategory.VISUAL)
public class Scoreboard extends Module {

    public BooleanProperty hiddenBool = new BooleanProperty("Hidden", false, this);
    public ValueProperty<Integer> yPos = new ValueProperty<>("Y-Position", 500, 100, 1000, this);
    public static Scoreboard getInstance(){
        return (Scoreboard) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Scoreboard.class);
    }

}
