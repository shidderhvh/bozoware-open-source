package bozoware.impl.module.visual;

import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;

import java.util.ArrayList;
import java.util.List;

@ModuleData(moduleName = "Xray", moduleCategory = ModuleCategory.VISUAL)
public class XRay extends Module {

    public List<Integer> blocks = new ArrayList<>();

    public XRay(){
        blocks.add(14);
        blocks.add(15);
        blocks.add(16);
        blocks.add(21);
        blocks.add(56);
        blocks.add(73);
        blocks.add(129);
        blocks.add(133);
        blocks.add(57);
        blocks.add(41);
        blocks.add(42);
        blocks.add(173);
        blocks.add(152);

        onModuleEnabled = () -> {
            if(mc.theWorld != null){
                mc.renderGlobal.loadRenderers();
            }
        };
        onModuleDisabled = () -> {
            if(mc.theWorld != null){
                mc.renderGlobal.loadRenderers();
            }
        };
    }
    public List<Integer> getBlocks() {
        return blocks;
    }
}
