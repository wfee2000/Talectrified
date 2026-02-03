package com.wfee.talectrified.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.wfee.enertalic.components.EnergyNode;
import com.wfee.talectrified.components.SolarPanelComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolarPanelTickingSystem extends EntityTickingSystem<ChunkStore> {
    private final Query<ChunkStore> query;

    public SolarPanelTickingSystem() {
        this.query = Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        BlockSection blocks = archetypeChunk.getComponent(index, BlockSection.getComponentType());

        assert blocks != null;

        if (blocks.getTickingBlocksCountCopy() != 0) {
            ChunkSection section = archetypeChunk.getComponent(index, ChunkSection.getComponentType());

            assert section != null;

            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());

            assert blockComponentChunk != null;

            blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(), (_, commandBuffer1, localX, localY, localZ, _) -> {
                Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                if (blockRef == null) {
                    return BlockTickStrategy.IGNORED;
                }

                SolarPanelComponent solarPanel = commandBuffer1.getComponent(blockRef, SolarPanelComponent.getComponentType());
                EnergyNode energyNode = commandBuffer1.getComponent(blockRef, EnergyNode.getComponentType());

                if (solarPanel == null || energyNode == null) {
                    return BlockTickStrategy.IGNORED;
                }

                WorldChunk worldChunk = commandBuffer.getComponent(section.getChunkColumnReference(), WorldChunk.getComponentType());

                if (worldChunk == null) {
                    return BlockTickStrategy.IGNORED;
                }

                World world = worldChunk.getWorld();

                int globalX = localX + (worldChunk.getX() * 32);
                int globalZ = localZ + (worldChunk.getZ() * 32);

                world.execute(() -> computeBlock(dt, new Vector3i(globalX, localY, globalZ), solarPanel, energyNode, world));

                return BlockTickStrategy.CONTINUE;
            });
        }
    }

    private void computeBlock(float dt, Vector3i position, SolarPanelComponent solarPanel, EnergyNode energyNode, World world) {
        if (isPositionShaded(position, world)) {
            return;
        }

        double sunModificator = getSunlightModificator(world);

        long generated = Math.min(
                energyNode.getEnergyRemaining(),
                (long)(dt * solarPanel.getMaxGeneration() * sunModificator)
        );

        energyNode.addEnergy(generated);
    }

    private double getSunlightModificator(World world) {
        // TODO: get sun mod
        return 0.5;
    }

    private boolean isPositionShaded(Vector3i position, World world) {
        while (position.y < 320) {
            position.y++;

            BlockType blockType = world.getBlockType(position);

            if (blockType != null && blockType.getOpacity() != Opacity.Transparent) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public Query<ChunkStore> getQuery() {
        return this.query;
    }
}
