package com.cleanroommc.relauncher.gui.wizard;

import javax.swing.*;

/**
 * Represents a single step in the wizard.
 */
public interface WizardStep {

    /**
     * Gets the panel to display for this step.
     */
    JPanel getPanel();

    /**
     * Called when this step is entered.
     */
    default void onStepEnter() {}

    /**
     * Called when leaving this step.
     */
    default void onStepExit() {}

    /**
     * Validates the input for this step.
     * @return null if validation passes, otherwise an error message
     */
    default String validate() {
        return null;
    }

    /**
     * Gets the title of this step.
     */
    String getTitle();
}
