package com.cleanroommc.relauncher.gui;

import com.cleanroommc.relauncher.download.CleanroomRelease;
import com.cleanroommc.relauncher.gui.wizard.WizardDialog;
import com.cleanroommc.relauncher.gui.wizard.steps.CleanroomVersionStep;
import com.cleanroommc.relauncher.gui.wizard.steps.ConfirmationStep;
import com.cleanroommc.relauncher.gui.wizard.steps.JavaArgumentsStep;
import com.cleanroommc.relauncher.gui.wizard.steps.JavaSelectionStep;
import net.minecraftforge.fml.cleanroomrelauncher.ExitVMBypass;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modern wizard-based GUI for Cleanroom Relauncher configuration.
 * Replaces the old RelauncherGUI with a more user-friendly, step-by-step interface.
 */
public class ModernRelauncherGUI {

    public CleanroomRelease selected;
    public String javaPath;
    public String javaArgs;

    private ModernRelauncherGUI() {}

    /**
     * Shows the modern wizard GUI and returns the configured settings.
     *
     * @param eligibleReleases List of available Cleanroom releases
     * @param initialSelected Initially selected release (can be null)
     * @param initialJavaPath Initially set Java path (can be null)
     * @param initialJavaArgs Initially set Java arguments (can be null)
     * @return ModernRelauncherGUI instance with user selections, or null if cancelled
     */
    public static ModernRelauncherGUI show(
        List<CleanroomRelease> eligibleReleases,
        CleanroomRelease initialSelected,
        String initialJavaPath,
        String initialJavaArgs
    ) {
        ModernRelauncherGUI result = new ModernRelauncherGUI();

        // Create icon
        ImageIcon icon = new ImageIcon(
            Toolkit.getDefaultToolkit().getImage(
                ModernRelauncherGUI.class.getResource("/cleanroom-relauncher.png")
            )
        );

        // Create wizard dialog
        WizardDialog wizard = new WizardDialog(
            null,
            "Cleanroom Relaunch Configuration",
            icon
        );

        // Create wizard steps
        CleanroomVersionStep versionStep = new CleanroomVersionStep(eligibleReleases, initialSelected);
        JavaSelectionStep javaStep = new JavaSelectionStep(initialJavaPath);
        JavaArgumentsStep argsStep = new JavaArgumentsStep(initialJavaArgs);
        ConfirmationStep confirmStep = new ConfirmationStep();

        // Add steps to wizard
        wizard.addStep(versionStep);
        wizard.addStep(javaStep);
        wizard.addStep(argsStep);

        // Add confirmation step with dynamic update
        wizard.addStep(new com.cleanroommc.relauncher.gui.wizard.WizardStep() {
            @Override
            public JPanel getPanel() {
                return confirmStep.getPanel();
            }

            @Override
            public String getTitle() {
                return confirmStep.getTitle();
            }

            @Override
            public void onStepEnter() {
                // Update confirmation with latest values when entering this step
                confirmStep.updateConfirmation(
                    versionStep.getSelectedRelease(),
                    javaStep.getJavaPath(),
                    argsStep.getJavaArgs()
                );
            }

            @Override
            public String validate() {
                return confirmStep.validate();
            }
        });

        // Set completion callback
        wizard.setOnComplete(() -> {
            result.selected = versionStep.getSelectedRelease();
            result.javaPath = javaStep.getJavaPath();
            result.javaArgs = argsStep.getJavaArgs();
        });

        // Set cancellation callback
        wizard.setOnCancel(() -> {
            ExitVMBypass.exit(0);
        });

        // Show the wizard
        wizard.showWizard();

        // Return null if not completed
        if (!wizard.isCompleted()) {
            return null;
        }

        return result;
    }
}
