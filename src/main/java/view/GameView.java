package view;

import interfaces.GameControllerInterface;
import model.ItemModel;
import model.NPCModel;
import model.TaskModel;
import util.ResourcePathUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.List;

public class GameView extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel sidePanel;
    private JTextArea txtDescription;
    private JPanel backpackPanel;
    private JPanel worldObjectsPanel;
    private JPanel gameAssetsPanel;
    private GameControllerInterface controller;

    public GameView() {
        setTitle("NPC Interaction Demo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    public void setController(GameControllerInterface controller) {
        this.controller = controller;
    }

    private void initComponents() {
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(250, 600));

        backpackPanel = createItemPanel();
        worldObjectsPanel = createItemPanel();
        gameAssetsPanel = createItemPanel();

        sidePanel.add(new JScrollPane(backpackPanel));
        sidePanel.add(new JScrollPane(worldObjectsPanel));
        sidePanel.add(new JScrollPane(gameAssetsPanel));

        pane.add(sidePanel, BorderLayout.EAST);

        tabbedPane = new JTabbedPane();
        pane.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createItemPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        return panel;
    }


    public void addNpcTab(NPCModel npc) {
        JPanel npcPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // NPC信息标签
        JLabel lblNpcInfo = new JLabel(npc.getDescription());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        npcPanel.add(lblNpcInfo, gbc);

        // 创建任务列表
        DefaultListModel<String> taskModel = new DefaultListModel<>();
        npc.getTasks().forEach(task -> taskModel.addElement(task.getDescription()));
        JList<String> taskList = new JList<>(taskModel);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        // 设置任务列表的滚动面板首选尺寸和最大尺寸
        taskScrollPane.setPreferredSize(new Dimension(50, 60));
        taskScrollPane.setMaximumSize(new Dimension(200, 60));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        npcPanel.add(taskScrollPane, gbc);

        // 创建对话区域
        JTextArea txtHistory = new JTextArea(5, 20);
        txtHistory.setEditable(false);
        JScrollPane historyScrollPane = new JScrollPane(txtHistory);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        npcPanel.add(historyScrollPane, gbc);

        // 输入框和发送按钮
        JTextField txtInput = new JTextField();
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(e -> {
            String inputText = txtInput.getText();
            if (!inputText.trim().isEmpty()) {
                txtHistory.append("You: " + inputText + "\n");
                txtInput.setText(""); // 清空输入框

                // 处理与 NPC 的交互
                //String response = controller.interactWithNPC(npc, inputText);
                //txtHistory.append(npc.getNPCName() + ": "+ response + "\n");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        npcPanel.add(txtInput, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        npcPanel.add(btnSend, gbc);

        // 将NPC面板添加到tabbedPane
        tabbedPane.addTab(npc.getNPCName(), npcPanel);
    }
    public void updateDescription(String description) {
        txtDescription.setText(description);
    }

    public void loadItemsIntoPanel(List<ItemModel> backpackItems, List<ItemModel> worldObjects, List<ItemModel> gameAssets) {
        loadItems(backpackPanel, backpackItems);
        loadItems(worldObjectsPanel, worldObjects);
        loadItems(gameAssetsPanel, gameAssets);
    }

    private void loadItems(JPanel panel, List<ItemModel> items) {
        panel.removeAll();
        for (ItemModel item : items) {
            String imagePath = "image/" + item.getImagePath();
            System.out.println("Loading image from: " + imagePath);

            try (InputStream is = ResourcePathUtil.getResourceAsStream(imagePath)) {
                if (is != null) {
                    ImageIcon icon = new ImageIcon(ImageIO.read(is));
                    if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                        System.out.println("Failed to load image: " + imagePath);
                        continue;
                    }
                    ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                    JButton button = new JButton(scaledIcon);
                    button.setPreferredSize(new Dimension(50, 50));
                    button.setToolTipText(item.getName());
                    panel.add(button);
                } else {
                    System.out.println("InputStream is null for image: " + imagePath);
                }
            } catch (Exception e) {
                System.out.println("Error loading image: " + imagePath);
                e.printStackTrace();
            }
        }
        panel.revalidate();
        panel.repaint();
    }
}