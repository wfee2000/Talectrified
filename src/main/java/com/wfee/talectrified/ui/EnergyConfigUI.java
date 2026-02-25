package com.wfee.talectrified.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.wfee.enertalic.components.EnergyObject;
import com.wfee.enertalic.data.EnergyConfig;
import com.wfee.enertalic.data.EnergySideConfig;
import com.wfee.enertalic.util.Direction;
import com.wfee.enertalic.util.ReactiveListener;
import com.wfee.enertalic.util.UpdateType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.awt.*;

public class EnergyConfigUI extends InteractiveCustomUIPage<EnergyConfigUI.Data> {
    private final EnergyObject object;
    private final ReactiveListener<EnergySideConfig> listener;

    public EnergyConfigUI(@Nonnull PlayerRef playerRef, EnergyObject object) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, Data.CODEC);
        this.object = object;
        this.listener = new ReactiveListener<>(
                false,
                this::updateEnergyConfig,
                UpdateType.All
        );
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        object.getEnergySideConfig().observe(listener);
        uiCommandBuilder.append("EnergyConfigUI.ui");

        for (Direction direction : Direction.values()) {
            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    String.format("#%s", direction.name()),
                    EventData.of("Direction", direction.name()));
        }
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl Data data) {
        object.getEnergySideConfig().modify(sides -> {
            int index = (sides.getDirection(data.direction).ordinal() + 1) % EnergyConfig.values().length;
            sides.setDirection(data.direction, EnergyConfig.values()[index]);
            return sides;
        });
    }

    public void updateEnergyConfig(EnergySideConfig energySideConfig) {
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();

        for (Direction direction : Direction.values()) {
            uiCommandBuilder.set(String.format("#%s.Background", direction.name()), getColorFor(energySideConfig.getDirection(direction)));
        }

        sendUpdate(uiCommandBuilder);
    }

    private String getColorFor(EnergyConfig config) {
        int result = (switch (config) {
            case IN -> Color.GREEN;
            case OFF -> Color.RED;
            case INOUT -> Color.MAGENTA;
            case OUT -> Color.BLUE;
        }).getRGB();

        return String.format("#%06x", result & 0xffffff);
    }

    @Override
    public void onDismiss(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store) {
        object.getEnergySideConfig().removeListener(listener);
    }

    public static class Data {
        public static final KeyedCodec<String> DIRECTION = new KeyedCodec<>("Direction", Codec.STRING);
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(
                        DIRECTION,
                        (object, value) -> object.direction = Direction.valueOf(value),
                        object -> object.direction.name())
                .add()
                .build();

        private Direction direction;
    }
}
