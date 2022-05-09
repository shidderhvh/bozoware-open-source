package bozoware.base.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData {

    String moduleName();
    ModuleCategory moduleCategory();

}
