package com.cleanroommc.relauncher.gui.wizard.steps;

import com.cleanroommc.relauncher.download.CleanroomRelease;
import com.cleanroommc.relauncher.gui.DesignSystem;
import com.cleanroommc.relauncher.gui.wizard.WizardStep;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Wizard step for selecting Cleanroom version.
 */
public class CleanroomVersionStep implements WizardStep {

    private final List<CleanroomRelease> releases;
    private final JPanel panel;
    private JComboBox<CleanroomRelease> releaseComboBox;
    private JLabel descriptionLabel;
    private JLabel latestBadge;

    private CleanroomRelease selectedRelease;

    public CleanroomVersionStep(List<CleanroomRelease> releases, CleanroomRelease initialSelection) {
        this.releases = releases;
        this.selectedRelease = initialSelection;
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
        JLabel titleLabel = DesignSystem.createHeading("Select Cleanroom Version");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        // Description
        JLabel descLabel = DesignSystem.createBody(
            "Choose the version of Cleanroom Loader to relaunch with."
        );
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));

        // Version selection card
        JPanel versionCard = createVersionSelectionCard();
        versionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(versionCard);

        // Info section
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));
        JPanel infoPanel = createInfoPanel();
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(infoPanel);

        // Push content to top
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private JPanel createVersionSelectionCard() {
        JPanel card = DesignSystem.createCard();
        card.setLayout(new BorderLayout(DesignSystem.SPACING_MD, DesignSystem.SPACING_MD));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Left side - label and combo box
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(DesignSystem.SURFACE);

        JLabel label = DesignSystem.createSubheading("Version:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(label);
        leftPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        // Create combo box
        releaseComboBox = DesignSystem.createComboBox();
        DefaultComboBoxModel<CleanroomRelease> model = new DefaultComboBoxModel<>();
        for (CleanroomRelease release : releases) {
            model.addElement(release);
        }
        releaseComboBox.setModel(model);
        releaseComboBox.setRenderer(new CleanroomReleaseRenderer());
        releaseComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignSystem.INPUT_HEIGHT));
        releaseComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (selectedRelease != null) {
            releaseComboBox.setSelectedItem(selectedRelease);
        } else if (!releases.isEmpty()) {
            releaseComboBox.setSelectedIndex(0);
            selectedRelease = releases.get(0);
        }

        releaseComboBox.addActionListener(e -> {
            selectedRelease = (CleanroomRelease) releaseComboBox.getSelectedItem();
            updateDescriptionLabel();
        });

        leftPanel.add(releaseComboBox);
        card.add(leftPanel, BorderLayout.CENTER);

        // Right side - latest badge
        if (!releases.isEmpty() && selectedRelease == releases.get(0)) {
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            rightPanel.setBackground(DesignSystem.SURFACE);

            latestBadge = new JLabel("Latest");
            latestBadge.setFont(DesignSystem.FONT_CAPTION);
            latestBadge.setForeground(DesignSystem.TEXT_ON_PRIMARY);
            latestBadge.setBackground(DesignSystem.SUCCESS);
            latestBadge.setOpaque(true);
            latestBadge.setBorder(BorderFactory.createEmptyBorder(
                DesignSystem.SPACING_XS,
                DesignSystem.SPACING_SM,
                DesignSystem.SPACING_XS,
                DesignSystem.SPACING_SM
            ));
            latestBadge.setVisible(selectedRelease == releases.get(0));

            rightPanel.add(latestBadge, BorderLayout.NORTH);
            card.add(rightPanel, BorderLayout.EAST);
        }

        return card;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = DesignSystem.createPanel(
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD
        );
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(232, 244, 253)); // Light blue background
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DesignSystem.INFO, 1),
            BorderFactory.createEmptyBorder(
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD
            )
        ));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel infoTitle = DesignSystem.createSubheading("ℹ Version Information");
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoTitle.setForeground(DesignSystem.INFO);
        infoPanel.add(infoTitle);
        infoPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        descriptionLabel = DesignSystem.createBody("");
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionLabel.setForeground(DesignSystem.TEXT_PRIMARY);
        infoPanel.add(descriptionLabel);

        updateDescriptionLabel();

        return infoPanel;
    }

    private void updateDescriptionLabel() {
        if (selectedRelease != null) {
            boolean isLatest = !releases.isEmpty() && selectedRelease == releases.get(0);
            String desc = String.format(
                "<html>Selected: <b>%s</b>%s</html>",
                selectedRelease.name,
                isLatest ? " <span style='color: green;'>(Latest)</span>" : ""
            );
            descriptionLabel.setText(desc);

            if (latestBadge != null) {
                latestBadge.setVisible(isLatest);
            }
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Select Cleanroom Version";
    }

    @Override
    public String validate() {
        if (selectedRelease == null) {
            return "Please select a Cleanroom version.";
        }
        return null;
    }

    public CleanroomRelease getSelectedRelease() {
        return selectedRelease;
    }

    /**
     * Custom renderer for CleanroomRelease combo box.
     */
    private static class CleanroomReleaseRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof CleanroomRelease) {
                CleanroomRelease release = (CleanroomRelease) value;
                setText(release.name + (index == 0 ? " (Latest)" : ""));
            }
            return this;
        }
    }
}
