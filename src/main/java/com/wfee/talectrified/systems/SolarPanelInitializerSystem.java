package com.wfee.talectrified.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.wfee.talectrified.components.SolarPanelComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolarPanelInitializerSystem extends RefSystem<ChunkStore> {
    private final Query<ChunkStore> query;

    public SolarPanelInitializerSystem() {
        this.query = Query.and(BlockModule.BlockStateInfo.getComponentType(), SolarPanelComponent.getComponentType());
    }

    @Override
    public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason addReason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        setTicking(ref, commandBuffer, true);
    }

    @Override
    public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason removeReason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        setTicking(ref, commandBuffer, false);
    }

    private void setTicking(@Nonnull Ref<ChunkStore> ref, @Nonnull CommandBuffer<ChunkStore> commandBuffer, boolean ticking) {
        BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        if (info == null) return;

        SolarPanelComponent generator = commandBuffer.getComponent(ref, SolarPanelComponent.getComponentType());

        if (generator != null) {
            int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(info.getIndex());

            WorldChunk worldChunk = commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());

            if (worldChunk != null) {
                worldChunk.setTicking(x, y, z, ticking);
            }
        }
    }

    @Nullable
    @Override
    public Query<ChunkStore> getQuery() {
        return this.query;
    }
}
