package bozoware.impl.module.visual;
import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;

@ModuleData(moduleName = "Animations", moduleCategory = ModuleCategory.VISUAL)
public class Animations extends Module {

    public final EnumProperty<AnimationModes> blockMode = new EnumProperty<>("Mode", AnimationModes.Swing, this);
    public final ValueProperty<Float> Xpos = new ValueProperty<>("X", 0F, -1F, 1F, this);
    public final ValueProperty<Float> Ypos = new ValueProperty<>("Y", 0F, -1F, 1F, this);
    public final ValueProperty<Float> Zpos = new ValueProperty<>("Z", 0F, -1F, 1F, this);
    public final ValueProperty<Float> Size = new ValueProperty<>("Size", 1F, 0.1F, 2F, this);

    public Animations() {
        blockMode.onValueChange = () -> setModuleSuffix(blockMode.getPropertyValue().name());
    }

    public static Animations getInstance() {
        return (Animations) BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Animations.class);
    }

    public enum AnimationModes {
        Swing,
        Swong,
        Slide,
    }
}
