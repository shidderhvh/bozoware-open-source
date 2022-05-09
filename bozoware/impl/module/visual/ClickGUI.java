package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.property.EnumProperty;

@ModuleData(moduleName = "ClickGUI", moduleCategory = ModuleCategory.VISUAL)
public class ClickGUI extends Module {

    public final EnumProperty<clickGuiModes> clickGuiMode = new EnumProperty<>("Mode", clickGuiModes.Dropdown, this);
    public final EnumProperty<loliModes> loliMode = new EnumProperty<>("Woman", loliModes.Uzaki, this);

    public ClickGUI() {
        this.setModuleToggled(false);
        loliMode.setHidden(false);
        clickGuiMode.onValueChange = () -> {
            if(clickGuiMode.getPropertyValue().equals(clickGuiModes.Dropdown))
                loliMode.setHidden(false);
            else
                loliMode.setHidden(true);
        };
        onModuleEnabled = () -> {
            this.setModuleToggled(false);
        };
    }

    public static ClickGUI getInstance() {
        return (ClickGUI) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(ClickGUI.class);
    }


    public enum clickGuiModes{
        Dropdown,
        Onetap
    }
    public enum loliModes {
        Uzaki,
        ZeroTwo,
        Rias,
        None,
        Kanna,
    }
}
