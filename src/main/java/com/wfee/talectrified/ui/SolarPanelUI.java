package com.wfee.talectrified.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.wfee.enertalic.components.EnergyNode;
import com.wfee.enertalic.util.ReactiveListener;
import com.wfee.enertalic.util.UpdateType;
import com.wfee.talectrified.components.SolarPanelComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class SolarPanelUI extends CustomUIPage {
    private final static HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final Player player;
    private final SolarPanelComponent solarPanel;
    private final EnergyNode node;
    private final ReactiveListener<Long> storedListener;
    private final ReactiveListener<Long> generatedListener;

    public SolarPanelUI(Player player, @Nonnull PlayerRef playerRef, EnergyNode node, SolarPanelComponent solarPanel) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        this.player = player;
        this.node = node;
        this.solarPanel = solarPanel;
        this.storedListener = new ReactiveListener<>(
                false,
                this::updateStoredText,
                UpdateType.All
        );

        this.generatedListener = new ReactiveListener<>(
                false,
                this::updateGeneratedText,
                UpdateType.All
        );
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        node.getCurrentEnergy().observe(storedListener);
        solarPanel.getLastGeneration().observe(generatedListener);
        uiCommandBuilder.append("SolarPanelUI.ui");
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#EnergyConfigButton");
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, String rawData) {
        player.getPageManager().openCustomPage(ref, store, new EnergyConfigUI(playerRef, node));
    }

    public void updateStoredText(long currentEnergy) {
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        uiCommandBuilder.set("#EnergyStored.TextSpans", Message.raw(String.format("Energy: %d / %d", currentEnergy, node.getMaxEnergy())));
        sendUpdate(uiCommandBuilder);
    }

    public void updateGeneratedText(long currentGeneration) {
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        uiCommandBuilder.set("#EnergyGenerated.TextSpans", Message.raw(String.format("Generating: %d HE/s", currentGeneration)));
        sendUpdate(uiCommandBuilder);
    }

    @Override
    public void onDismiss(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store) {
        node.getCurrentEnergy().removeListener(storedListener);
        solarPanel.getLastGeneration().removeListener(generatedListener);
    }
}
