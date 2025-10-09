package com.cleanroommc.relauncher.gui.wizard;

import com.cleanroommc.relauncher.gui.DesignSystem;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A modern wizard-style dialog using FlatLaf.
 * Provides step-by-step navigation through multiple panels.
 */
public class WizardDialog extends JDialog {

    private final List<WizardStep> steps;
    private int currentStepIndex = 0;
    private boolean completed = false;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel stepIndicatorLabel;
    private JButton previousButton;
    private JButton nextButton;
    private JButton cancelButton;

    private Runnable onComplete;
    private Runnable onCancel;

    static {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {}
        }
    }

    public WizardDialog(Window owner, String title, ImageIcon icon) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.steps = new ArrayList<>();

        setIconImage(icon != null ? icon.getImage() : null);
        setSize(DesignSystem.WIZARD_WIDTH, DesignSystem.WIZARD_HEIGHT);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCancel();
            }
        });

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header with logo and step indicator
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(DesignSystem.SURFACE);
        add(contentPanel, BorderLayout.CENTER);

        // Footer with navigation buttons
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = DesignSystem.createPanel(
            DesignSystem.SPACING_LG,
            DesignSystem.SPACING_LG,
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_LG
        );
        header.setLayout(new BorderLayout());

        // Step indicator
        stepIndicatorLabel = DesignSystem.createCaption("Step 1 of 1");
        stepIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(stepIndicatorLabel, BorderLayout.CENTER);

        // Separator
        header.add(DesignSystem.createSeparator(), BorderLayout.SOUTH);

        return header;
    }

    private JPanel createFooterPanel() {
        JPanel footer = DesignSystem.createPanel(
            DesignSystem.SPACING_MD,
            DesignSystem.SPACING_LG,
            DesignSystem.SPACING_LG,
            DesignSystem.SPACING_LG
        );
        footer.setLayout(new BorderLayout());

        // Separator
        footer.add(DesignSystem.createSeparator(), BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, DesignSystem.SPACING_SM, DesignSystem.SPACING_MD));
        buttonPanel.setBackground(DesignSystem.SURFACE);

        cancelButton = DesignSystem.createSecondaryButton("キャンセル");
        cancelButton.addActionListener(e -> handleCancel());

        previousButton = DesignSystem.createSecondaryButton("< 戻る");
        previousButton.addActionListener(e -> previousStep());
        previousButton.setEnabled(false);

        nextButton = DesignSystem.createPrimaryButton("次へ >");
        nextButton.addActionListener(e -> nextStep());

        buttonPanel.add(cancelButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);

        footer.add(buttonPanel, BorderLayout.CENTER);

        return footer;
    }

    /**
     * Adds a step to the wizard.
     */
    public void addStep(WizardStep step) {
        steps.add(step);
        contentPanel.add(step.getPanel(), "step_" + steps.size());
        updateStepIndicator();
    }

    /**
     * Shows the wizard dialog.
     */
    public void showWizard() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("No steps added to wizard");
        }

        // Show first step
        cardLayout.show(contentPanel, "step_1");
        if (!steps.isEmpty()) {
            steps.get(0).onStepEnter();
        }

        updateNavigationButtons();
        setVisible(true);
    }

    private void previousStep() {
        if (currentStepIndex > 0) {
            steps.get(currentStepIndex).onStepExit();
            currentStepIndex--;
            cardLayout.show(contentPanel, "step_" + (currentStepIndex + 1));
            steps.get(currentStepIndex).onStepEnter();
            updateNavigationButtons();
            updateStepIndicator();
        }
    }

    private void nextStep() {
        WizardStep currentStep = steps.get(currentStepIndex);

        // Validate current step
        String validationError = currentStep.validate();
        if (validationError != null) {
            JOptionPane.showMessageDialog(
                this,
                validationError,
                "入力エラー",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if this is the last step
        if (currentStepIndex == steps.size() - 1) {
            handleComplete();
            return;
        }

        // Move to next step
        currentStep.onStepExit();
        currentStepIndex++;
        cardLayout.show(contentPanel, "step_" + (currentStepIndex + 1));
        steps.get(currentStepIndex).onStepEnter();
        updateNavigationButtons();
        updateStepIndicator();
    }

    private void updateNavigationButtons() {
        previousButton.setEnabled(currentStepIndex > 0);

        if (currentStepIndex == steps.size() - 1) {
            nextButton.setText("完了");
        } else {
            nextButton.setText("次へ >");
        }
    }

    private void updateStepIndicator() {
        if (!steps.isEmpty()) {
            stepIndicatorLabel.setText("ステップ " + (currentStepIndex + 1) + " / " + steps.size());
        }
    }

    private void handleComplete() {
        completed = true;
        if (onComplete != null) {
            onComplete.run();
        }
        dispose();
    }

    private void handleCancel() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "設定をキャンセルしますか？",
            "確認",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            completed = false;
            if (onCancel != null) {
                onCancel.run();
            }
            dispose();
        }
    }

    /**
     * Sets the callback to run when the wizard is completed.
     */
    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    /**
     * Sets the callback to run when the wizard is cancelled.
     */
    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    /**
     * Returns whether the wizard was completed successfully.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Gets the step at the specified index.
     */
    public WizardStep getStep(int index) {
        return steps.get(index);
    }

    /**
     * Gets all steps.
     */
    public List<WizardStep> getSteps() {
        return new ArrayList<>(steps);
    }
}
