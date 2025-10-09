package com.cleanroommc.relauncher.gui.wizard.steps;

import com.cleanroommc.relauncher.download.CleanroomRelease;
import com.cleanroommc.relauncher.gui.DesignSystem;
import com.cleanroommc.relauncher.gui.wizard.WizardStep;

import javax.swing.*;
import java.awt.*;

/**
 * Wizard step for confirming selections before relaunching.
 */
public class ConfirmationStep implements WizardStep {

    private final JPanel panel;
    private JLabel cleanroomVersionLabel;
    private JLabel javaPathLabel;
    private JLabel javaArgsLabel;

    private CleanroomRelease cleanroomRelease;
    private String javaPath;
    private String javaArgs;

    public ConfirmationStep() {
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
        JLabel titleLabel = DesignSystem.createHeading("Confirm Configuration");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        // Description
        JLabel descLabel = DesignSystem.createBody(
            "Cleanroom will relaunch with the following configuration. Please review."
        );
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));

        // Configuration summary card
        JPanel summaryCard = createSummaryCard();
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(summaryCard);

        // Warning panel
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));
        JPanel warningPanel = createWarningPanel();
        warningPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(warningPanel);

        // Push content to top
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private JPanel createSummaryCard() {
        JPanel card = DesignSystem.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DesignSystem.PRIMARY, 2),
            BorderFactory.createEmptyBorder(
                DesignSystem.SPACING_LG,
                DesignSystem.SPACING_LG,
                DesignSystem.SPACING_LG,
                DesignSystem.SPACING_LG
            )
        ));

        // Cleanroom version
        JPanel versionPanel = createInfoRow("Cleanroom Version:");
        cleanroomVersionLabel = DesignSystem.createBody("");
        cleanroomVersionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        versionPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_XS));
        versionPanel.add(cleanroomVersionLabel);
        card.add(versionPanel);

        card.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));
        card.add(DesignSystem.createSeparator());
        card.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));

        // Java path
        JPanel javaPanel = createInfoRow("Java Executable:");
        javaPathLabel = DesignSystem.createBodySmall("");
        javaPathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        javaPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_XS));
        javaPanel.add(javaPathLabel);
        card.add(javaPanel);

        card.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));
        card.add(DesignSystem.createSeparator());
        card.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_MD));

        // Java args
        JPanel argsPanel = createInfoRow("Java Arguments:");
        javaArgsLabel = DesignSystem.createBodySmall("");
        javaArgsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        argsPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_XS));
        argsPanel.add(javaArgsLabel);
        card.add(argsPanel);

        return card;
    }

    private JPanel createInfoRow(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DesignSystem.SURFACE);

        JLabel label = DesignSystem.createSubheading(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        return panel;
    }

    private JPanel createWarningPanel() {
        JPanel panel = DesignSystem.createPanel(
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD
        );
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 243, 224)); // Light orange background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DesignSystem.WARNING, 1),
            BorderFactory.createEmptyBorder(
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD
            )
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel titleLabel = DesignSystem.createSubheading("⚠ Warning");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(DesignSystem.WARNING);
        panel.add(titleLabel);
        panel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        JLabel warningLabel = DesignSystem.createBody(
            "Clicking Finish will exit the current Minecraft process and relaunch with Cleanroom."
        );
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        warningLabel.setForeground(DesignSystem.TEXT_PRIMARY);
        panel.add(warningLabel);

        return panel;
    }

    /**
     * Updates the confirmation display with current selections.
     */
    public void updateConfirmation(CleanroomRelease release, String javaPath, String javaArgs) {
        this.cleanroomRelease = release;
        this.javaPath = javaPath;
        this.javaArgs = javaArgs;

        if (release != null) {
            cleanroomVersionLabel.setText("<html><b>" + release.name + "</b></html>");
        }

        if (javaPath != null && !javaPath.isEmpty()) {
            javaPathLabel.setText("<html><code>" + javaPath + "</code></html>");
        }

        if (javaArgs != null && !javaArgs.isEmpty()) {
            javaArgsLabel.setText("<html><code>" + javaArgs + "</code></html>");
        } else {
            javaArgsLabel.setText("(None)");
        }
    }

    @Override
    public void onStepEnter() {
        // Update display when entering this step
        // Data will be set via updateConfirmation() method
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Confirmation";
    }

    @Override
    public String validate() {
        // No validation needed for confirmation step
        return null;
    }
}
