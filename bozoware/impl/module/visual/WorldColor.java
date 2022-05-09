package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;

@ModuleData(moduleName = "WorldColor", moduleCategory = ModuleCategory.VISUAL)
public class WorldColor extends Module {


    public WorldColor(){

    }
    public static WorldColor getInstance() {
        return (WorldColor) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(WorldColor.class);
    }
}
