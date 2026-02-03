package com.wfee.talectrified;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.wfee.talectrified.components.SolarPanelComponent;
import com.wfee.talectrified.systems.SolarPanelInitializerSystem;
import com.wfee.talectrified.systems.SolarPanelTickingSystem;

import javax.annotation.Nonnull;

public class Talectrified extends JavaPlugin {
    private static Talectrified instance;
    private ComponentType<ChunkStore, SolarPanelComponent> solarPanelComponentType;

    public Talectrified(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        super.setup();
        solarPanelComponentType = getChunkStoreRegistry().registerComponent(SolarPanelComponent.class, "Talectrified:SolarPanel", SolarPanelComponent.CODEC);
        getChunkStoreRegistry().registerSystem(new SolarPanelInitializerSystem());
        getChunkStoreRegistry().registerSystem(new SolarPanelTickingSystem());
    }

    public static Talectrified get() {
        return instance;
    }

    public ComponentType<ChunkStore, SolarPanelComponent> getSolarPanelComponentType() {
        return this.solarPanelComponentType;
    }
}
