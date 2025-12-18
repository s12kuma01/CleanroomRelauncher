package com.cleanroommc.relauncher.gui;

import com.cleanroommc.javautils.JavaUtils;
import com.cleanroommc.javautils.api.JavaInstall;
import com.cleanroommc.javautils.spi.JavaLocator;
import com.cleanroommc.platformutils.Platform;
import com.cleanroommc.relauncher.CleanroomRelauncher;
import com.cleanroommc.relauncher.download.CleanroomRelease;
import net.minecraftforge.fml.cleanroomrelauncher.ExitVMBypass;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

public class RelauncherGUI extends JDialog {

    static {
        try {
            // Set Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Set Global Font
            Font font = new Font("Segoe UI", Font.PLAIN, 14);
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, font);
                }
            }
        } catch (Exception ignore) { }
    }

    private static void scaleComponent(Component component, float scale) {
        // scaling rect
        if (component instanceof JTextField ||
                component instanceof JButton ||
                component instanceof JComboBox) {
            Dimension size = component.getPreferredSize();
            component.setPreferredSize(new Dimension((int) (size.width * scale) + 10, (int) (size.height * scale)));
            component.setMaximumSize(new Dimension((int) (size.width * scale) + 10, (int) (size.height * scale)));
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            Icon icon = label.getIcon();
            if (icon instanceof ImageIcon) {
                ImageIcon imageIcon = (ImageIcon) icon;
                Image image = imageIcon.getImage();
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(
                            (int) (imageIcon.getIconWidth() * scale),
                            (int) (imageIcon.getIconHeight() * scale),
                            Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                }
            }
        }

        // scaling font
        if (component instanceof JLabel ||
                component instanceof JButton ||
                component instanceof JTextField ||
                component instanceof JComboBox) {
            Font font = component.getFont();
            if (font != null) {
                component.setFont(font.deriveFont(font.getSize() * scale));
            }
        }

        // scaling padding
        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            Insets margin = button.getMargin();
            if (margin != null) {
                button.setMargin(new Insets(
                        (int) (margin.top * scale),
                        (int) (margin.left * scale),
                        (int) (margin.bottom * scale),
                        (int) (margin.right * scale)
                ));
            }
        } else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            Insets margin = textField.getMargin();
            if (margin != null) {
                textField.setMargin(new Insets(
                        (int) (margin.top * scale),
                        (int) (margin.left * scale),
                        (int) (margin.bottom * scale),
                        (int) (margin.right * scale)
                ));
            }
        } else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            Insets margin = comboBox.getInsets();
            if (margin != null) {
                comboBox.setBorder(BorderFactory.createEmptyBorder(
                        (int) (margin.top * scale),
                        (int) (margin.left * scale),
                        (int) (margin.bottom * scale),
                        (int) (margin.right * scale)
                ));
            }
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            Insets margin = label.getInsets();
            if (margin != null) {
                label.setBorder(BorderFactory.createEmptyBorder(
                        (int) (margin.top * scale),
                        (int) (margin.left * scale),
                        (int) (margin.bottom * scale),
                        (int) (margin.right * scale)
                ));
            }
        } else if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Border existingBorder = panel.getBorder();

            Insets margin = existingBorder instanceof EmptyBorder ?
                    ((EmptyBorder) existingBorder).getBorderInsets()
                    : new Insets(0, 0, 0, 0);

            panel.setBorder(BorderFactory.createEmptyBorder(
                    (int) (margin.top * scale),
                    (int) (margin.left * scale),
                    (int) (margin.bottom * scale),
                    (int) (margin.right * scale)
            ));
        }

        component.revalidate();
        component.repaint();

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                scaleComponent(child, scale);
            }
        }
    }

    public static RelauncherGUI show(List<CleanroomRelease> eligibleReleases, Consumer<RelauncherGUI> consumer) {
        ImageIcon imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(RelauncherGUI.class.getResource("/cleanroom-relauncher.png")));
        return new RelauncherGUI(new SupportingFrame("Cleanroom Relaunch Configuration", imageIcon), eligibleReleases, consumer);
    }

    public CleanroomRelease selected;
    public String javaPath, javaArgs;
    public String maxMemory;
    public String gcType;
    public Set<String> jvmFlags;

    private JTextField argsField; // Reference to the args text field

    private JFrame frame;

    private RelauncherGUI(SupportingFrame frame, List<CleanroomRelease> eligibleReleases, Consumer<RelauncherGUI> consumer) {
        super(frame, frame.getTitle(), true);
        this.frame = frame;

        consumer.accept(this);

        this.setIconImage(frame.getIconImage());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RelauncherGUI.this.requestFocusInWindow();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selected = null;
                frame.dispose();

                CleanroomRelauncher.LOGGER.info("No Cleanroom releases were selected, instance is dismissed.");
                ExitVMBypass.exit(0);
            }
        });
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(true);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = env.getDefaultScreenDevice();
        Rectangle rect = screen.getDefaultConfiguration().getBounds();
        int width = rect.width / 3;
        int height = (int) (width * 1.5f); // Increased height for new options
        int x = (rect.width - width) / 2;
        int y = (rect.height - height) / 2;
        this.setLocation(x, y);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel cleanroomLogo = new JLabel(new ImageIcon(frame.getIconImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));

        mainPanel.add(initializeBasicOptions(eligibleReleases));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(initializeAdvancedOptions());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(initializeRelaunchPanel());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(cleanroomLogo, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        wrapper.add(contentPanel, gbc);

        this.add(wrapper, BorderLayout.NORTH);
        float scale = rect.width / 1463f;
        scaleComponent(this, scale);

        this.pack();
        this.setSize(width, height);
        this.setVisible(true);
        this.setAutoRequestFocus(true);
    }

    private JPanel initializeBasicOptions(List<CleanroomRelease> eligibleReleases) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Basic Options"));

        // Cleanroom Version
        JPanel crPanel = new JPanel(new BorderLayout(5, 5));
        crPanel.add(new JLabel("Cleanroom Loader:"), BorderLayout.NORTH);
        
        JComboBox<CleanroomRelease> releaseBox = new JComboBox<>(eligibleReleases.toArray(new CleanroomRelease[0]));
        releaseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CleanroomRelease) {
                    setText(((CleanroomRelease) value).tagName);
                }
                return this;
            }
        });

        int index = -1;
        if (selected != null) {
            for (int i = 0; i < eligibleReleases.size(); i++) {
                if (eligibleReleases.get(i).tagName.equals(selected.tagName)) {
                    index = i;
                    break;
                }
            }
        }
        if (index != -1) {
            releaseBox.setSelectedIndex(index);
        }
        releaseBox.addActionListener(e -> {
            CleanroomRelease r = (CleanroomRelease) releaseBox.getSelectedItem();
            if (r != null) {
                selected = r;
            }
        });
        crPanel.add(releaseBox, BorderLayout.CENTER);
        panel.add(crPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Java Path
        JPanel javaPanel = new JPanel(new BorderLayout(5, 5));
        javaPanel.add(new JLabel("Java Executable:"), BorderLayout.NORTH);

        JTextField javaField = new JTextField(javaPath);
        javaField.setColumns(30);
        addTextBoxEffect(javaField);
        listenToTextFieldUpdate(javaField, t -> javaPath = t.getText());
        
        javaPanel.add(javaField, BorderLayout.CENTER);
        
        // Buttons: Browse, Auto, Test
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                javaField.setText(chooser.getSelectedFile().getAbsolutePath());
                javaPath = javaField.getText();
            }
        });
        
        JButton autoBtn = new JButton("Auto-Detect");
        autoBtn.addActionListener(e -> {
            String javaHomePath = System.getProperty("java.home");
            if (javaHomePath != null) {
                 File javaBin = new File(javaHomePath, "bin/java" + (Platform.current().isWindows() ? ".exe" : ""));
                 if (javaBin.exists()) {
                     try {
                        String path = javaBin.getCanonicalPath();
                        javaField.setText(path);
                        javaPath = path;
                     } catch (IOException ex) {
                         ex.printStackTrace();
                     }
                 }
            }
        });

        JButton testBtn = new JButton("Test");
        testBtn.addActionListener(e -> {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        Process process = new ProcessBuilder(javaPath, "-version").start();
                        return process.waitFor() == 0;
                    } catch (Exception ex) {
                        return false;
                    }
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(frame, "Java is valid!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Java check failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error checking Java: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
        
        btnPanel.add(browseBtn);
        btnPanel.add(autoBtn);
        btnPanel.add(testBtn);
        
        javaPanel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(javaPanel);

        return panel;
    }

    private JPanel initializeAdvancedOptions() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Advanced Options"));

        // Memory Section
        JPanel memGroup = new JPanel(new BorderLayout(5, 5));
        
        // Quick Buttons Line
        JPanel quickMemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quickMemPanel.add(new JLabel("Memory:"));
        JButton btn4G = new JButton("4GB");
        JButton btn8G = new JButton("8GB");
        JButton btn16G = new JButton("16GB");
        
        quickMemPanel.add(btn4G);
        quickMemPanel.add(btn8G);
        quickMemPanel.add(btn16G);
        
        memGroup.add(quickMemPanel, BorderLayout.NORTH);

        // Slider and Field
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 0));
        JSlider memorySlider = new JSlider(512, 32768, 4096);
        
        // Configure Slider
        long totalRam = -1;
        try {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            totalRam = osBean.getTotalPhysicalMemorySize() / (1024 * 1024);
        } catch (Throwable t) {}
        if (totalRam > 0) {
            memorySlider.setMaximum((int) Math.min(totalRam, 65536));
        }

        int maxVal = memorySlider.getMaximum();
        int majorTick = 4096;
        if (maxVal > 32768) majorTick = 8192;
        
        memorySlider.setMajorTickSpacing(majorTick);
        memorySlider.setPaintTicks(true);
        memorySlider.setPaintLabels(true);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 0; i <= maxVal; i += majorTick) {
            if (i == 0) continue;
            labels.put(i, new JLabel(i / 1024 + "G"));
        }
        memorySlider.setLabelTable(labels);

        JTextField memoryField = new JTextField(maxMemory);
        addTextBoxEffect(memoryField);
        memoryField.setColumns(6);

        updateSliderFromText(memorySlider, maxMemory);

        // Logic
        Runnable syncMem = () -> {
            maxMemory = memoryField.getText();
            updateSliderFromText(memorySlider, maxMemory);
            updateArgumentField();
        };

        memorySlider.addChangeListener(e -> {
            if (memorySlider.getValueIsAdjusting()) return;
            int val = memorySlider.getValue();
            memoryField.setText(val + "M");
            maxMemory = memoryField.getText(); // Directly set
            updateArgumentField();
        });
        
        listenToTextFieldUpdate(memoryField, t -> syncMem.run());

        // Button Actions
        btn4G.addActionListener(e -> { memorySlider.setValue(4096); });
        btn8G.addActionListener(e -> { memorySlider.setValue(8192); });
        btn16G.addActionListener(e -> { memorySlider.setValue(16384); });

        sliderPanel.add(memorySlider, BorderLayout.CENTER);
        sliderPanel.add(memoryField, BorderLayout.EAST);
        memGroup.add(sliderPanel, BorderLayout.CENTER); // Add slider below buttons

        panel.add(memGroup);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Flags & Args Group
        JPanel flagsArgsGroup = new JPanel(new BorderLayout(5, 5));
        
        // Compact Headers Flag
        JCheckBox compactHeaders = new JCheckBox("UseCompactObjectHeaders");
        if (jvmFlags != null) compactHeaders.setSelected(jvmFlags.contains("UseCompactObjectHeaders"));
        compactHeaders.addActionListener(e -> {
             if (jvmFlags == null) jvmFlags = new HashSet<>();
             if (compactHeaders.isSelected()) jvmFlags.add("UseCompactObjectHeaders");
             else jvmFlags.remove("UseCompactObjectHeaders");
             updateArgumentField();
        });

        flagsArgsGroup.add(compactHeaders, BorderLayout.NORTH);

        // Args Field
        JPanel argsRow = new JPanel(new BorderLayout(5, 5));
        argsRow.add(new JLabel("JVM Args:"), BorderLayout.WEST);
        argsField = new JTextField(javaArgs);
        addTextBoxEffect(argsField);
        listenToTextFieldUpdate(argsField, t -> javaArgs = t.getText());
        argsRow.add(argsField, BorderLayout.CENTER);

        flagsArgsGroup.add(argsRow, BorderLayout.SOUTH);
        
        panel.add(flagsArgsGroup);

        return panel;
    }

    private void updateSliderFromText(JSlider slider, String text) {
        if (text == null) return;
        try {
            String num = text.toUpperCase().replace("M", "").replace("G", "");
            int val = Integer.parseInt(num);
            if (text.toUpperCase().endsWith("G")) {
                val *= 1024;
            }
            if (val >= slider.getMinimum() && val <= slider.getMaximum()) {
                slider.setValue(val);
            }
        } catch (NumberFormatException ignored) { }
    }

    private void updateArgumentField() {
        if (argsField == null) return;
        String currentArgs = argsField.getText();

        // Update Max Memory
        if (maxMemory != null && !maxMemory.isEmpty()) {
            String memArg = "-Xmx" + maxMemory;
            if (currentArgs.contains("-Xmx")) {
                currentArgs = currentArgs.replaceAll("-Xmx\\S+", memArg);
            } else {
                currentArgs = currentArgs.trim() + " " + memArg;
            }
        }

        // Update GC
        if (gcType != null && !gcType.isEmpty()) {
            String gcArg = "-XX:+Use" + gcType;
            if (currentArgs.matches(".*-XX:\\+Use.*GC.*")) {
                 currentArgs = currentArgs.replaceAll("-XX:\\+Use\\w+GC", gcArg);
            } else {
                currentArgs = currentArgs.trim() + " " + gcArg;
            }
        }

        // Update Flags
        // CompactObjectHeaders
        String compactHeaderArg = "-XX:+UseCompactObjectHeaders";
        boolean hasFlag = jvmFlags != null && jvmFlags.contains("UseCompactObjectHeaders");
        if (hasFlag) {
             if (!currentArgs.contains(compactHeaderArg)) {
                 currentArgs = currentArgs.trim() + " " + compactHeaderArg;
             }
        } else {
            if (currentArgs.contains(compactHeaderArg)) {
                currentArgs = currentArgs.replace(compactHeaderArg, "");
            }
        }

        currentArgs = currentArgs.replaceAll("\\s+", " ").trim();
        argsField.setText(currentArgs);
        javaArgs = currentArgs;
    }

    private Runnable testJavaAndReturn() {
        try {
            JavaInstall javaInstall = JavaUtils.parseInstall(javaPath);
            if (javaInstall.version().major() < 21) {
                CleanroomRelauncher.LOGGER.fatal("Java 21+ needed, user specified Java {} instead", javaInstall.version());
                return () -> JOptionPane.showMessageDialog(this, "Java 21 is the minimum version for Cleanroom. Currently, Java " + javaInstall.version().major() + " is selected.", "Old Java Version", JOptionPane.ERROR_MESSAGE);
            }
            CleanroomRelauncher.LOGGER.info("Java {} specified from {}", javaInstall.version().major(), javaPath);
        } catch (IOException e) {
            CleanroomRelauncher.LOGGER.fatal("Failed to execute Java for testing", e);
            return () -> JOptionPane.showMessageDialog(this, "Failed to test Java (more information in console): " + e.getMessage(), "Java Test Failed", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private JPanel initializeRelaunchPanel() {
        JPanel relaunchButtonPanel = new JPanel();

        JButton relaunchButton = new JButton("Relaunch with Cleanroom");
        relaunchButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        relaunchButton.addActionListener(e -> {
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a Cleanroom version in order to relaunch.", "Cleanroom Release Not Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (javaPath == null) {
                JOptionPane.showMessageDialog(this, "Please provide a valid Java Executable in order to relaunch.", "Java Executable Not Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Runnable test = this.testJavaAndReturn();
            if (test != null) {
                test.run();
                return;
            }
            frame.dispose();
        });
        relaunchButtonPanel.add(relaunchButton);

        return relaunchButtonPanel;
    }

    private void listenToTextFieldUpdate(JTextField text, Consumer<JTextField> textConsumer) {
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textConsumer.accept(text);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textConsumer.accept(text);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textConsumer.accept(text);
            }
        });
    }

    private void addTextBoxEffect(JTextField text) {
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                text.setBorder(BorderFactory.createLineBorder(new Color(142, 177, 204)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                text.setBorder(null);
            }
        });
    }
}
