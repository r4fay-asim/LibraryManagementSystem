package com.library;

import com.library.service.LibraryManager;
import com.library.ui.LibraryManagementGUI;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Library Book Management System application.
 *
 * @author Rafay Asim, Eesha Siddiqui
 */
public class Main {

    /**
     * Launches the Swing GUI on the Event Dispatch Thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fallback to default Swing LookAndFeel
            }
            LibraryManager manager = new LibraryManager();
            new LibraryManagementGUI(manager).setVisible(true);
        });
    }
}