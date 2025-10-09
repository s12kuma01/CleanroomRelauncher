package com.cleanroommc.relauncher.gui.wizard.steps;

import com.cleanroommc.relauncher.gui.DesignSystem;
import com.cleanroommc.relauncher.gui.wizard.WizardStep;

import javax.swing.*;
import java.awt.*;

/**
 * Wizard step for entering Java arguments.
 */
public class JavaArgumentsStep implements WizardStep {

    private final JPanel panel;
    private JTextArea javaArgsArea;
    private String javaArgs;

    public JavaArgumentsStep(String initialArgs) {
        this.javaArgs = initialArgs;
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
        JLabel titleLabel = DesignSystem.createHeading("Java Arguments (Optional)");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        // Description
        JLabel descLabel = DesignSystem.createBody(
            "Specify additional Java arguments. This can usually be left empty."
        );
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descLabel);
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));

        // Arguments card
        JPanel argsCard = createArgumentsCard();
        argsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(argsCard);

        // Examples panel
        mainPanel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_LG));
        JPanel examplesPanel = createExamplesPanel();
        examplesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(examplesPanel);

        // Push content to top
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private JPanel createArgumentsCard() {
        JPanel card = DesignSystem.createCard();
        card.setLayout(new BorderLayout(DesignSystem.SPACING_SM, DesignSystem.SPACING_SM));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Label
        JLabel label = DesignSystem.createSubheading("Java Arguments:");
        card.add(label, BorderLayout.NORTH);

        // Text area with scroll pane
        javaArgsArea = new JTextArea(5, 40);
        javaArgsArea.setFont(DesignSystem.FONT_BODY);
        javaArgsArea.setLineWrap(true);
        javaArgsArea.setWrapStyleWord(true);
        javaArgsArea.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACING_SM,
            DesignSystem.SPACING_SM,
            DesignSystem.SPACING_SM,
            DesignSystem.SPACING_SM
        ));

        if (javaArgs != null) {
            javaArgsArea.setText(javaArgs);
        }

        javaArgsArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateArgs(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateArgs(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateArgs(); }
            private void updateArgs() {
                javaArgs = javaArgsArea.getText();
            }
        });

        JScrollPane scrollPane = new JScrollPane(javaArgsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createExamplesPanel() {
        JPanel panel = DesignSystem.createPanel(
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_MD
        );
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 248, 225)); // Light yellow background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DesignSystem.WARNING, 1),
            BorderFactory.createEmptyBorder(
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD,
                DesignSystem.SPACING_MD
            )
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = DesignSystem.createSubheading("💡 Example Arguments");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setForeground(DesignSystem.WARNING);
        panel.add(titleLabel);
        panel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_SM));

        String[] examples = {
            "-XX:+UseG1GC - Use G1 garbage collector",
            "-XX:MaxGCPauseMillis=200 - Limit GC pause time",
            "-Dfile.encoding=UTF-8 - Specify file encoding"
        };

        for (String example : examples) {
            JLabel exampleLabel = DesignSystem.createBodySmall("• " + example);
            exampleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            exampleLabel.setForeground(DesignSystem.TEXT_PRIMARY);
            panel.add(exampleLabel);
            panel.add(DesignSystem.createVerticalSpace(DesignSystem.SPACING_XS));
        }

        return panel;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Java Arguments";
    }

    @Override
    public String validate() {
        // Java arguments are optional, so no validation needed
        return null;
    }

    public String getJavaArgs() {
        return javaArgs;
    }
}
