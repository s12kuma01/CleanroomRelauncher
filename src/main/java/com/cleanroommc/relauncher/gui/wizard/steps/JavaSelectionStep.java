package com.cleanroommc.relauncher.gui.wizard.steps;

import com.cleanroommc.javautils.JavaUtils;
import com.cleanroommc.javautils.api.JavaInstall;
import com.cleanroommc.javautils.spi.JavaLocator;
import com.cleanroommc.platformutils.Platform;
import com.cleanroommc.relauncher.gui.DesignSystem;
import com.cleanroommc.relauncher.gui.wizard.WizardStep;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Wizard step for selecting Java installation.
 */
public class JavaSelectionStep implements WizardStep {

    private final JPanel panel;
    private JTextField javaPathField;
    private JComboBox<JavaInstall> javaInstallComboBox;
    private JButton autoDetectButton;
    private JButton testButton;
    private JButton browseButton;
    private JLabel statusLabel;
    private JPanel comboBoxPanel;

    private String javaPath;
    private List<JavaInstall> detectedJavaInstalls = Collections.emptyList();

    public JavaSelectionStep(String initialJavaPath) {
        this.javaPath = initialJavaPath;
        this.panel = createPanel();
    }

    private JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DesignSystem.SURFACE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACING_XL,
            DesignSystem.SPACING_XL,
            DesignSystem.SPACING_XL,
            DesignSystem.SPACING_XL
        ));

        // Title
        JLabel titleLabel = DesignSystem.createHeading("Java実行ファイルを選択");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        // Description
        JLabel descLabel = DesignSystem.createBody(
            "Cleanroomの実行に使用するJava 21以上の実行ファイルを選択してください。"
        );
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));

        // Path input card
        JPanel pathCard = createPathInputCard();
        pathCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(pathCard);

        // Detected Java installations (initially hidden)
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));
        comboBoxPanel = createDetectedJavaPanel();
        comboBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBoxPanel.setVisible(false);
        mainPanel.add(comboBoxPanel);

        // Action buttons
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));
        JPanel actionPanel = createActionButtonsPanel();
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(actionPanel);

        // Status label
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));
        statusLabel = DesignSystem.createBody("");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);

        // Push content to top
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private JPanel createPathInputCard() {
        JPanel card = DesignSystem.createCard();
        card.setLayout(new BorderLayout(DesignSystem.SPACING_SM, DesignSystem.SPACING_SM));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Label
        JLabel label = DesignSystem.createSubheading("Javaパス:");
        card.add(label, BorderLayout.NORTH);

        // Path field and browse button
        JPanel inputPanel = new JPanel(new BorderLayout(DesignSystem.SPACING_SM, 0));
        inputPanel.setBackground(DesignSystem.SURFACE);

        javaPathField = DesignSystem.createTextField("");
        if (javaPath != null) {
            javaPathField.setText(javaPath);
        }
        javaPathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePath(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePath(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePath(); }
            private void updatePath() {
                javaPath = javaPathField.getText();
                clearStatus();
            }
        });

        inputPanel.add(javaPathField, BorderLayout.CENTER);

        browseButton = DesignSystem.createSecondaryButton("参照...");
        browseButton.addActionListener(e -> browseForJava());
        inputPanel.add(browseButton, BorderLayout.EAST);

        card.add(inputPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetectedJavaPanel() {
        JPanel panel = DesignSystem.createCard();
        panel.setLayout(new BorderLayout(DesignSystem.SPACING_SM, DesignSystem.SPACING_SM));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBackground(new Color(232, 244, 253));

        JLabel label = DesignSystem.createSubheading("検出されたJava:");
        panel.add(label, BorderLayout.NORTH);

        javaInstallComboBox = DesignSystem.createComboBox();
        javaInstallComboBox.setRenderer(new JavaInstallRenderer());
        javaInstallComboBox.addActionListener(e -> {
            JavaInstall selected = (JavaInstall) javaInstallComboBox.getSelectedItem();
            if (selected != null) {
                javaPath = selected.executable(true).getAbsolutePath();
                javaPathField.setText(javaPath);
                setStatus("Java " + selected.version().major() + " が選択されました", DesignSystem.SUCCESS);
            }
        });

        panel.add(javaInstallComboBox, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, DesignSystem.SPACING_SM, 0));
        panel.setBackground(DesignSystem.SURFACE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignSystem.BUTTON_HEIGHT));

        autoDetectButton = DesignSystem.createPrimaryButton("自動検出");
        autoDetectButton.addActionListener(e -> autoDetectJava());

        testButton = DesignSystem.createSecondaryButton("テスト");
        testButton.addActionListener(e -> testJava());

        panel.add(autoDetectButton);
        panel.add(testButton);

        return panel;
    }

    private void browseForJava() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Java実行ファイルを選択");

        if (javaPath != null && !javaPath.isEmpty()) {
            File currentFile = new File(javaPath);
            if (currentFile.getParentFile() != null && currentFile.getParentFile().exists()) {
                fileChooser.setCurrentDirectory(currentFile.getParentFile());
            }
        }

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) return true;
                if (file.isFile()) {
                    return !Platform.current().isWindows() || file.getName().endsWith(".exe");
                }
                return false;
            }

            @Override
            public String getDescription() {
                return Platform.current().isWindows() ? "Java実行ファイル (*.exe)" : "Java実行ファイル";
            }
        };

        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
            javaPath = fileChooser.getSelectedFile().getAbsolutePath();
            javaPathField.setText(javaPath);
            testJava();
        }
    }

    private void autoDetectJava() {
        String originalText = autoDetectButton.getText();
        autoDetectButton.setText("検出中");
        autoDetectButton.setEnabled(false);
        setStatus("Java 21以上を検索中...", DesignSystem.INFO);

        AtomicInteger dotCount = new AtomicInteger(0);
        String[] dots = { ".", "..", "..." };
        Timer timer = new Timer(400, e -> {
            autoDetectButton.setText("検出中" + dots[dotCount.get()]);
            dotCount.set((dotCount.get() + 1) % dots.length);
        });
        timer.start();

        SwingWorker<List<JavaInstall>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<JavaInstall> doInBackground() {
                return JavaLocator.locators().parallelStream()
                    .map(JavaLocator::all)
                    .flatMap(Collection::stream)
                    .filter(install -> install.version().major() >= 21)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            }

            @Override
            protected void done() {
                timer.stop();
                autoDetectButton.setText(originalText);
                autoDetectButton.setEnabled(true);

                try {
                    detectedJavaInstalls = get();

                    if (detectedJavaInstalls.isEmpty()) {
                        setStatus("Java 21以上が見つかりませんでした", DesignSystem.WARNING);
                        comboBoxPanel.setVisible(false);
                    } else {
                        setStatus(detectedJavaInstalls.size() + "個のJava 21以上のインストールが見つかりました", DesignSystem.SUCCESS);

                        // Populate combo box
                        DefaultComboBoxModel<JavaInstall> model = new DefaultComboBoxModel<>();
                        for (JavaInstall install : detectedJavaInstalls) {
                            model.addElement(install);
                        }
                        javaInstallComboBox.setModel(model);
                        comboBoxPanel.setVisible(true);

                        // Select first one
                        if (!detectedJavaInstalls.isEmpty()) {
                            javaInstallComboBox.setSelectedIndex(0);
                        }
                    }

                    panel.revalidate();
                    panel.repaint();
                } catch (Exception e) {
                    setStatus("検出中にエラーが発生しました: " + e.getMessage(), DesignSystem.ERROR);
                }
            }
        };

        worker.execute();
    }

    private void testJava() {
        if (javaPath == null || javaPath.trim().isEmpty()) {
            setStatus("Javaパスを入力してください", DesignSystem.WARNING);
            return;
        }

        File javaFile = new File(javaPath);
        if (!javaFile.exists()) {
            setStatus("指定されたJava実行ファイルが存在しません", DesignSystem.ERROR);
            return;
        }

        try {
            JavaInstall install = JavaUtils.parseInstall(javaPath);
            int majorVersion = install.version().major();

            if (majorVersion < 21) {
                setStatus("Java 21以上が必要です（現在: Java " + majorVersion + "）", DesignSystem.ERROR);
            } else {
                setStatus("✓ Java " + majorVersion + " (" + install.vendor() + ") - 正常に動作します", DesignSystem.SUCCESS);
            }
        } catch (IOException e) {
            setStatus("Javaのテストに失敗しました: " + e.getMessage(), DesignSystem.ERROR);
        }
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Java選択";
    }

    @Override
    public String validate() {
        if (javaPath == null || javaPath.trim().isEmpty()) {
            return "Javaパスを入力してください。";
        }

        File javaFile = new File(javaPath);
        if (!javaFile.exists()) {
            return "指定されたJava実行ファイルが存在しません。";
        }

        try {
            JavaInstall install = JavaUtils.parseInstall(javaPath);
            if (install.version().major() < 21) {
                return "Java 21以上が必要です。現在: Java " + install.version().major();
            }
        } catch (IOException e) {
            return "Javaのバージョン確認に失敗しました: " + e.getMessage();
        }

        return null;
    }

    public String getJavaPath() {
        return javaPath;
    }

    /**
     * Custom renderer for JavaInstall combo box.
     */
    private static class JavaInstallRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof JavaInstall) {
                JavaInstall install = (JavaInstall) value;
                setText(String.format("Java %d - %s", install.version().major(), install.vendor()));
            }
            return this;
        }
    }
}
