package block.poster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import registry.BlockRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class BasePosterBlockEntity extends BlockEntity {
    private static final Logger LOGGER = Logger.getLogger("BasePosterBlockEntity");

    protected String title = "";
    protected List<String> content = new ArrayList<>();
    protected String imagePath = "";
    protected String expertType = "学术专家";
    protected String conceptKey = "";
    protected String associatedQuestionId = "";

    // 记录哪些玩家已经阅读过此海报
    protected Set<UUID> readByPlayers = new HashSet<>();

    public BasePosterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // 设置海报数据
    public void setPosterData(String title, List<String> content, String imagePath,
                              String expertType, String conceptKey, String questionId) {
        this.title = title;
        this.content = new ArrayList<>(content);
        this.imagePath = imagePath;
        this.expertType = expertType;
        this.conceptKey = conceptKey;
        this.associatedQuestionId = questionId;
        setChanged();

        // 添加调试输出
        LOGGER.info("设置海报数据: 标题='" + title +
                "', 内容行数=" + content.size() +
                ", 图像='" + imagePath +
                "', 专家='" + expertType +
                "', 概念='" + conceptKey +
                "', 问题ID='" + questionId + "'");
    }

    // 检查玩家是否已读
    public boolean hasBeenReadBy(UUID playerUUID) {
        return readByPlayers.contains(playerUUID);
    }

    // 标记玩家已读
    public void markAsRead(UUID playerUUID) {
        readByPlayers.add(playerUUID);
        setChanged();
    }

    // Getters
    public String getTitle() { return title; }
    public List<String> getContent() { return content; }
    public String getImagePath() { return imagePath; }
    public String getExpertType() { return expertType; }
    public String getConceptKey() { return conceptKey; }
    public String getAssociatedQuestionId() { return associatedQuestionId; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        // 保存基本数据
        tag.putString("Title", title);
        tag.putString("ImagePath", imagePath);
        tag.putString("ExpertType", expertType);
        tag.putString("ConceptKey", conceptKey);
        tag.putString("QuestionId", associatedQuestionId);

        // 保存内容数组
        ListTag contentList = new ListTag();
        for (String line : content) {
            contentList.add(StringTag.valueOf(line));
        }
        tag.put("Content", contentList);

        // 保存已读玩家
        ListTag readByList = new ListTag();
        for (UUID uuid : readByPlayers) {
            readByList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put("ReadBy", readByList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        // 读取基本数据
        title = tag.getString("Title");
        imagePath = tag.getString("ImagePath");
        expertType = tag.getString("ExpertType");
        conceptKey = tag.getString("ConceptKey");
        associatedQuestionId = tag.getString("QuestionId");

        // 读取内容数组
        content.clear();
        ListTag contentList = tag.getList("Content", 8); // 8是字符串标签的类型ID
        for (int i = 0; i < contentList.size(); i++) {
            content.add(contentList.getString(i));
        }

        // 读取已读玩家
        readByPlayers.clear();
        ListTag readByList = tag.getList("ReadBy", 8);
        for (int i = 0; i < readByList.size(); i++) {
            try {
                readByPlayers.add(UUID.fromString(readByList.getString(i)));
            } catch (IllegalArgumentException e) {
                // 记录错误但继续处理
                LOGGER.warning("Invalid UUID in poster data: " + readByList.getString(i));
            }
        }

        LOGGER.info("加载海报数据: 标题='" + title + "', 内容行数=" + content.size());
    }
}