package com.cleanroommc.relauncher.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Design system for the Cleanroom Relauncher GUI.
 * Provides consistent styling tokens for colors, spacing, fonts, and component styles.
 */
public class DesignSystem {

    // Colors - Cleanroom Brand
    public static final Color PRIMARY = new Color(76, 175, 80);        // Cleanroom Green
    public static final Color PRIMARY_DARK = new Color(56, 142, 60);
    public static final Color PRIMARY_LIGHT = new Color(129, 199, 132);

    public static final Color SECONDARY = new Color(66, 66, 66);       // Dark Gray
    public static final Color SECONDARY_LIGHT = new Color(97, 97, 97);

    public static final Color BACKGROUND = new Color(250, 250, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color ERROR = new Color(244, 67, 54);
    public static final Color WARNING = new Color(255, 152, 0);
    public static final Color SUCCESS = new Color(76, 175, 80);
    public static final Color INFO = new Color(33, 150, 243);

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color TEXT_DISABLED = new Color(189, 189, 189);
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    // Spacing (in pixels)
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    public static final int SPACING_XXL = 48;

    // Border Radius
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 11);

    // Dimensions
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 40;
    public static final int WIZARD_WIDTH = 600;
    public static final int WIZARD_HEIGHT = 500;

    // Animation durations (in milliseconds)
    public static final int ANIMATION_FAST = 150;
    public static final int ANIMATION_NORMAL = 250;
    public static final int ANIMATION_SLOW = 350;

    /**
     * Creates a standard panel with consistent padding.
     */
    public static JPanel createPanel(int padding) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        panel.setBackground(SURFACE);
        return panel;
    }

    /**
     * Creates a panel with custom padding for each side.
     */
    public static JPanel createPanel(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        panel.setBackground(SURFACE);
        return panel;
    }

    /**
     * Creates a styled label with the specified font.
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Creates a title label.
     */
    public static JLabel createTitle(String text) {
        return createLabel(text, FONT_TITLE, TEXT_PRIMARY);
    }

    /**
     * Creates a heading label.
     */
    public static JLabel createHeading(String text) {
        return createLabel(text, FONT_HEADING, TEXT_PRIMARY);
    }

    /**
     * Creates a subheading label.
     */
    public static JLabel createSubheading(String text) {
        return createLabel(text, FONT_SUBHEADING, TEXT_PRIMARY);
    }

    /**
     * Creates a body text label.
     */
    public static JLabel createBody(String text) {
        return createLabel(text, FONT_BODY, TEXT_SECONDARY);
    }

    /**
     * Creates a caption label (small text).
     */
    public static JLabel createCaption(String text) {
        return createLabel(text, FONT_CAPTION, TEXT_SECONDARY);
    }

    /**
     * Creates a small body text label.
     */
    public static JLabel createBodySmall(String text) {
        return createLabel(text, FONT_BODY_SMALL, TEXT_SECONDARY);
    }

    /**
     * Creates a styled primary button.
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(PRIMARY);
        button.setForeground(TEXT_ON_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + SPACING_LG * 2, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY);
            }
        });

        return button;
    }

    /**
     * Creates a styled secondary button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(SURFACE);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_LIGHT, 1),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_LG, SPACING_SM, SPACING_LG)
        ));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SURFACE);
            }
        });

        return button;
    }

    /**
     * Creates a styled text field.
     */
    public static JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(FONT_BODY);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, INPUT_HEIGHT));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
        ));

        // Focus effect
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(SPACING_SM - 1, SPACING_MD - 1, SPACING_SM - 1, SPACING_MD - 1)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                    BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
                ));
            }
        });

        return textField;
    }

    /**
     * Creates a styled combo box.
     */
    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(FONT_BODY);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, INPUT_HEIGHT));
        comboBox.setBackground(SURFACE);
        return comboBox;
    }

    /**
     * Creates a vertical spacer with the specified height.
     */
    public static Component createVerticalSpace(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    /**
     * Creates a horizontal spacer with the specified width.
     */
    public static Component createHorizontalSpace(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }

    /**
     * Creates a separator line.
     */
    public static JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(224, 224, 224));
        return separator;
    }

    /**
     * Creates a card-style panel with a border and shadow effect.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        ));
        return card;
    }
}
