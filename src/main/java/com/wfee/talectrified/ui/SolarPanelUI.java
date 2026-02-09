package com.wfee.talectrified.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.wfee.enertalic.components.EnergyNode;
import com.wfee.enertalic.util.EnergyListener;
import com.wfee.enertalic.util.EnergyUpdateType;
import com.wfee.talectrified.components.SolarPanelComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class SolarPanelUI extends CustomUIPage {
    private final static HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final SolarPanelComponent solarPanel;
    private final EnergyNode node;

    public SolarPanelUI(@Nonnull PlayerRef playerRef, EnergyNode node, SolarPanelComponent solarPanel) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        this.node = node;
        this.solarPanel = solarPanel;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        Function<Long, String> energyText = currentEnergy -> String.format("Energy: %d / %d", currentEnergy, node.getMaxEnergy());
        node.onEnergyUpdated(new EnergyListener(false, currentEnergy -> updateText(energyText.apply(currentEnergy)), EnergyUpdateType.All));
        uiCommandBuilder.append("SolarPanelUI.ui");
        uiCommandBuilder.set("#EnergyDisplay.Text", energyText.apply(node.getCurrentEnergy()));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#EnergyConfigButton");
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, String rawData) {
        // TODO: show energy config
        sendUpdate();
    }

    public void updateText(String newText) {
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        uiCommandBuilder.set("#EnergyDisplay.TextSpans", Message.raw(newText));
        sendUpdate(uiCommandBuilder, false);
    }
}
