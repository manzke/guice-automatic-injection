package de.devsurf.injection.guice.install;

import java.util.LinkedList;
import java.util.List;

public enum BindingStage {
    INTERNAL,
    BOOT_BEFORE,
    BOOT,
    BOOT_POST,
    BINDING_BEFORE,
    BINDING,
    BINDING_POST,
    INSTALL_BEFORE,
    INSTALL,
    INSTALL_POST,
    BUILD_BEFORE,
    BUILD,
    BUILD_POST,
    IGNORE;

    public static final List<BindingStage> ORDERED = new LinkedList<BindingStage>();

    static {
        ORDERED.add(INTERNAL);
        ORDERED.add(BOOT_BEFORE);
        ORDERED.add(BOOT);
        ORDERED.add(BOOT_POST);
        ORDERED.add(BINDING_BEFORE);
        ORDERED.add(BINDING);
        ORDERED.add(BINDING_POST);
        ORDERED.add(INSTALL_BEFORE);
        ORDERED.add(INSTALL);
        ORDERED.add(INSTALL_POST);
        ORDERED.add(BUILD_BEFORE);
        ORDERED.add(BUILD);
        ORDERED.add(BUILD_POST);
    }
}
