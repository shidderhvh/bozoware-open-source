package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeybindingComponent extends Component {

    private final Supplier<Integer> keyBindSupplier;
    private final Consumer<Integer> keyBindConsumer;

    public boolean active;

    private final ModuleButtonComponent parent;

    private int offset;

    public KeybindingComponent(Supplier<Integer> keyBindSupplier, Consumer<Integer> keyBindConsumer, ModuleButtonComponent parent, int offset) {
        this.keyBindSupplier = keyBindSupplier;
        this.keyBindConsumer = keyBindConsumer;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {

        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, 0xff151515);

        getFontRenderer().drawStringWithShadow("Key Bind", x + 3, y + 4, -1);
        final String displayString = "[" + (active ? "..." :
                Keyboard.getKeyName(keyBindSupplier.get())) + "]";

        getFontRenderer().drawStringWithShadow(displayString, x + 113 - getFontRenderer().getStringWidth(displayString), y + 4, 0xff909090);

        super.onDrawScreen(mouseX, mouseY);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY)) {
            active = !active;
        }
        else active = false;
    }

    @Override
    public void onKeyTyped(int typedKey) {
        if (active) {
            typedKey = Keyboard.getEventKey();
            keyBindConsumer.accept(typedKey == Keyboard.KEY_ESCAPE ? 0 : typedKey);
            active = false;
        }
    }

    public void onGuiClosed() {
        active = false;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}