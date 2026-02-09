package com.wfee.talectrified.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.wfee.enertalic.components.EnergyNode;
import com.wfee.talectrified.components.SolarPanelComponent;
import com.wfee.talectrified.ui.SolarPanelUI;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

public class DisplayPanelInformationInteraction extends SimpleInteraction {
    public final static BuilderCodec<DisplayPanelInformationInteraction> CODEC = BuilderCodec
            .builder(DisplayPanelInformationInteraction.class, DisplayPanelInformationInteraction::new, SimpleInteraction.CODEC)
            .build();

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();
        PlayerRef playerRef = store.getComponent(owningEntity, PlayerRef.getComponentType());
        Player player = store.getComponent(owningEntity, Player.getComponentType());

        if (player == null || playerRef == null) return;

        World world = player.getWorld();
        if (world == null) return;

        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) return;

        Ref<ChunkStore> blockEntity = BlockModule.getBlockEntity(world,
                targetBlock.x, targetBlock.y, targetBlock.z);

        if (blockEntity == null) return;

        Store<ChunkStore> blockStore = blockEntity.getStore();
        SolarPanelComponent solarPanel = blockStore.getComponent(blockEntity, SolarPanelComponent.getComponentType());
        EnergyNode node = blockStore.getComponent(blockEntity, EnergyNode.getComponentType());
        player.getPageManager().openCustomPage(owningEntity, store, new SolarPanelUI(playerRef, node, solarPanel));
    }
}
