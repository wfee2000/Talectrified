package com.wfee.talectrified;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class Talectrified extends JavaPlugin {
    private static Talectrified instance;
    public Talectrified(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static Talectrified get() {
        return instance;
    }
}
