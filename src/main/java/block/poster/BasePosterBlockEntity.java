package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class BasePosterBlockEntity extends BlockEntity {
    private static final Logger LOGGER = Logger.getLogger("BasePosterBlockEntity");

    // 只保留已读玩家记录功能，其他数据运行时直接从PosterManager获取
    protected Set<UUID> readByPlayers = new HashSet<>();

    public BasePosterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // 检查玩家是否已读
    public boolean hasBeenReadBy(UUID playerUUID) {
        return readByPlayers.contains(playerUUID);
    }

    // 标记玩家已读
    public void markAsRead(UUID playerUUID) {
        readByPlayers.add(playerUUID);
        setChanged();
        LOGGER.info("玩家 " + playerUUID + " 已阅读海报");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        // 只保存已读玩家信息
        ListTag readByList = new ListTag();
        for (UUID uuid : readByPlayers) {
            readByList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put("ReadBy", readByList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        // 只读取已读玩家信息
        readByPlayers.clear();
        if (tag.contains("ReadBy")) {
            ListTag readByList = tag.getList("ReadBy", 8);
            for (int i = 0; i < readByList.size(); i++) {
                try {
                    readByPlayers.add(UUID.fromString(readByList.getString(i)));
                } catch (IllegalArgumentException e) {
                    // 记录错误但继续处理
                    LOGGER.warning("Invalid UUID in poster data: " + readByList.getString(i));
                }
            }
        }
    }
}