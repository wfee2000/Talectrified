package com.wfee.talectrified.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.wfee.talectrified.Talectrified;

import javax.annotation.Nullable;

public class SolarPanelComponent implements Component<ChunkStore> {
    public static final KeyedCodec<Long> MAX_GENERATION = new KeyedCodec<>("MaxGeneration", BuilderCodec.LONG);


    public static final BuilderCodec<SolarPanelComponent> CODEC = BuilderCodec
            .builder(SolarPanelComponent.class, SolarPanelComponent::new)
                .append(
                        MAX_GENERATION,
                        (object, value) -> object.maxGeneration = value,
                        object -> object.maxGeneration)
                .add()
            .build();


    private long maxGeneration = 0;
    private long lastGeneration = 0;

    public SolarPanelComponent() {}

    public SolarPanelComponent(SolarPanelComponent solarPanelComponent) {
       this.maxGeneration = solarPanelComponent.maxGeneration;
       this.lastGeneration = 0;
    }

    public static ComponentType<ChunkStore, SolarPanelComponent> getComponentType() {
        return Talectrified.get().getSolarPanelComponentType();
    }

    public long  getMaxGeneration() {
        return maxGeneration;
    }

    @Nullable
    @Override
    public Component<ChunkStore> clone() {
        return new SolarPanelComponent(this);
    }

    public long getLastGeneration() {
        return lastGeneration;
    }

    public void setLastGeneration(long lastGeneration) {
        this.lastGeneration = lastGeneration;
    }
}
