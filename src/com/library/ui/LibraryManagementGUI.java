package com.library.ui;

import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.service.LibraryManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Rafay Asim, Eesha Siddiqui
 */
public class LibraryManagementGUI extends JFrame {
    // Storefront Color Palette
    private static final Color CLR_BLACK = new Color(11, 11, 12);
    private static final Color CLR_DARK_CARD = new Color(24, 26, 31);
    private static final Color CLR_DARK_TILE = new Color(34, 37, 44);
    private static final Color CLR_LIME = new Color(184, 242, 0);
    private static final Color CLR_LIME_HOVER = new Color(168, 224, 0);
    private static final Color CLR_WHITE = Color.WHITE;
    private static final Color CLR_BG_SUBTLE = new Color(248, 249, 250);
    private static final Color CLR_BORDER = new Color(229, 231, 235);
    private static final Color CLR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color CLR_DANGER_BG = new Color(58, 29, 36);
    private static final Color CLR_DANGER_FG = new Color(255, 138, 155);

    private final LibraryManager manager;
    private BookStatus activeStatusFilter = null; // null = View All

    private JTextField txtId;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JComboBox<BookStatus> cmbStatus;
    private JTextField txtSearch;

    private JButton tabAll;
    private JButton tabAvailable;
    private JButton tabIssued;
    private JButton tabReserved;

    private MetricTile tileAvailable;
    private MetricTile tileIssued;
    private MetricTile tileReserved;

    private CardLayout catalogCardLayout;
    private JPanel catalogCardPanel;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JLabel lblTicker;
    private JLabel lblFooterStatus;

    /**
     * Constructs and initializes the Library Book Management System interface.
     *
     * @param manager business logic service managing the library catalog
     */
    public LibraryManagementGUI(LibraryManager manager) {
        this.manager = manager;
        initWindow();
        refreshView(false);
    }

    /**
     * Configures the main frame and assembles header, hero studio, catalog, and footer sections.
     */
    private void initWindow() {
        setTitle("LIBRA — Library Book Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 740);
        setMinimumSize(new Dimension(940, 660));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CLR_WHITE);
        setContentPane(root);

        // North Stack: Black Brand Bar + White Filter/Search Sub-Header
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(buildTopBrandBar(), BorderLayout.NORTH);
        northWrapper.add(buildSubHeaderBar(), BorderLayout.SOUTH);
        root.add(northWrapper, BorderLayout.NORTH);

        // Center Workspace: Editorial Hero Studio + Catalog Table
        JPanel workspace = new JPanel(new BorderLayout(0, 16));
        workspace.setBackground(CLR_WHITE);
        workspace.setBorder(new EmptyBorder(16, 24, 12, 24));
        workspace.add(buildHeroStudio(), BorderLayout.NORTH);
        workspace.add(buildCatalogSection(), BorderLayout.CENTER);
        root.add(workspace, BorderLayout.CENTER);

        // Bottom Status Bar
        root.add(buildFooterBar(), BorderLayout.SOUTH);
    }

    /**
     * Builds the top black brand bar with logo mark and live catalog ticker.
     *
     * @return configured header {@link JPanel}
     */
    private JPanel buildTopBrandBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CLR_BLACK);
        bar.setPreferredSize(new Dimension(0, 44));
        bar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("LIBRA");
        logo.setForeground(CLR_WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 20));

        lblTicker = new JLabel("‹   Catalog Ready  •  Zero Duplicate IDs Enforced   ›", SwingConstants.CENTER);
        lblTicker.setForeground(new Color(229, 231, 235));
        lblTicker.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel rightTag = new JLabel("SWING  |  CLO-5");
        rightTag.setForeground(CLR_LIME);
        rightTag.setFont(new Font("SansSerif", Font.BOLD, 11));

        bar.add(logo, BorderLayout.WEST);
        bar.add(lblTicker, BorderLayout.CENTER);
        bar.add(rightTag, BorderLayout.EAST);
        return bar;
    }

    /**
     * Builds the white category navigation bar with status filter tabs and live search pill.
     *
     * @return configured sub-header {@link JPanel}
     */
    private JPanel buildSubHeaderBar() {
        JPanel subBar = new JPanel(new BorderLayout(16, 0));
        subBar.setBackground(CLR_WHITE);
        subBar.setPreferredSize(new Dimension(0, 54));
        subBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER),
                new EmptyBorder(0, 24, 0, 24)
        ));

        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 12));
        tabsPanel.setBackground(CLR_WHITE);

        tabAll = createNavTab("View All", true);
        tabAvailable = createNavTab("Available", false);
        tabIssued = createNavTab("Issued", false);
        tabReserved = createNavTab("Reserved", false);

        tabAll.addActionListener(e -> {
            activeStatusFilter = null;
            updateActiveTabStyles();
            refreshView(true);
        });
        tabAvailable.addActionListener(e -> {
            activeStatusFilter = BookStatus.AVAILABLE;
            updateActiveTabStyles();
            refreshView(false);
        });
        tabIssued.addActionListener(e -> {
            activeStatusFilter = BookStatus.ISSUED;
            updateActiveTabStyles();
            refreshView(false);
        });
        tabReserved.addActionListener(e -> {
            activeStatusFilter = BookStatus.RESERVED;
            updateActiveTabStyles();
            refreshView(false);
        });

        tabsPanel.add(tabAll);
        tabsPanel.add(tabAvailable);
        tabsPanel.add(tabIssued);
        tabsPanel.add(tabReserved);

        // Right Search Pill
        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 9));
        searchWrap.setBackground(CLR_WHITE);

        RoundedPanel searchBox = new RoundedPanel(28, CLR_BG_SUBTLE, CLR_BORDER);
        searchBox.setLayout(new BorderLayout(6, 0));
        searchBox.setPreferredSize(new Dimension(260, 34));
        searchBox.setBorder(new EmptyBorder(0, 12, 0, 12));

        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setFont(new Font("SansSerif", Font.BOLD, 15));
        searchIcon.setForeground(CLR_TEXT_MUTED);

        txtSearch = new JTextField();
        txtSearch.setBorder(null);
        txtSearch.setBackground(CLR_BG_SUBTLE);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setToolTipText("Filter catalog by ID, title, or author");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshView(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshView(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshView(false);
            }
        });

        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);

        PillButton btnClear = new PillButton("Clear Form", CLR_BG_SUBTLE, CLR_BLACK, CLR_BORDER);
        btnClear.setPreferredSize(new Dimension(102, 34));
        btnClear.addActionListener(e -> clearForm());

        searchWrap.add(searchBox);
        searchWrap.add(btnClear);

        subBar.add(tabsPanel, BorderLayout.WEST);
        subBar.add(searchWrap, BorderLayout.EAST);
        return subBar;
    }

    /**
     * @return configured hero {@link JPanel}
     */
    private JPanel buildHeroStudio() {
        RoundedPanel hero = new RoundedPanel(18, CLR_DARK_CARD, CLR_DARK_CARD);
        hero.setLayout(new BorderLayout(24, 0));
        hero.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Left: Editorial Heading + Input Grid + Action Buttons
        JPanel leftEditor = new JPanel(new BorderLayout(0, 14));
        leftEditor.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel lblManage = new JLabel("MANAGE");
        lblManage.setForeground(CLR_WHITE);
        lblManage.setFont(new Font("SansSerif", Font.BOLD, 22));

        RoundedPanel badgePanel = new RoundedPanel(8, CLR_LIME, CLR_LIME);
        badgePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));
        JLabel lblCatalogBadge = new JLabel("CATALOG");
        lblCatalogBadge.setForeground(CLR_BLACK);
        lblCatalogBadge.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 19));
        badgePanel.add(lblCatalogBadge);

        JLabel lblSub = new JLabel("   Add, update, or change circulation status");
        lblSub.setForeground(new Color(156, 163, 175));
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        titleRow.add(lblManage);
        titleRow.add(badgePanel);
        titleRow.add(lblSub);

        // 2x2 Form Fields
        JPanel fieldsGrid = new JPanel(new GridLayout(2, 2, 14, 10));
        fieldsGrid.setOpaque(false);

        txtId = styleInputField();
        txtTitle = styleInputField();
        txtAuthor = styleInputField();
        cmbStatus = new JComboBox<>(BookStatus.values());
        cmbStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        cmbStatus.setBackground(CLR_WHITE);
        cmbStatus.setForeground(CLR_BLACK);

        fieldsGrid.add(wrapField("BOOK ID", txtId));
        fieldsGrid.add(wrapField("BOOK TITLE", txtTitle));
        fieldsGrid.add(wrapField("AUTHOR NAME", txtAuthor));
        fieldsGrid.add(wrapField("CIRCULATION STATUS", cmbStatus));

        // Action Pill Buttons
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsRow.setOpaque(false);

        PillButton btnAdd = new PillButton("+ Add Book", CLR_LIME, CLR_BLACK, CLR_LIME);
        btnAdd.setHoverBackground(CLR_LIME_HOVER);
        btnAdd.setPreferredSize(new Dimension(126, 36));

        PillButton btnUpdate = new PillButton("Update Details", CLR_WHITE, CLR_BLACK, CLR_WHITE);
        btnUpdate.setPreferredSize(new Dimension(132, 36));

        PillButton btnSetStatus = new PillButton("Set Status", CLR_DARK_TILE, CLR_WHITE, new Color(75, 85, 99));
        btnSetStatus.setPreferredSize(new Dimension(114, 36));

        PillButton btnDelete = new PillButton("Delete Book", CLR_DANGER_BG, CLR_DANGER_FG, new Color(127, 29, 29));
        btnDelete.setPreferredSize(new Dimension(120, 36));

        btnAdd.addActionListener(e -> handleAddBook());
        btnUpdate.addActionListener(e -> handleUpdateBook());
        btnSetStatus.addActionListener(e -> handleSetStatus());
        btnDelete.addActionListener(e -> handleDeleteBook());

        actionsRow.add(btnAdd);
        actionsRow.add(btnUpdate);
        actionsRow.add(btnSetStatus);
        actionsRow.add(btnDelete);

        leftEditor.add(titleRow, BorderLayout.NORTH);
        leftEditor.add(fieldsGrid, BorderLayout.CENTER);
        leftEditor.add(actionsRow, BorderLayout.SOUTH);

        // Right: 3 Storefront-Style Metric Tiles
        JPanel tilesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        tilesPanel.setOpaque(false);
        tilesPanel.setPreferredSize(new Dimension(340, 0));

        tileAvailable = new MetricTile("Available", new Color(6, 78, 59));
        tileIssued = new MetricTile("Issued", new Color(120, 53, 15));
        tileReserved = new MetricTile("Reserved", new Color(30, 58, 138));

        tileAvailable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeStatusFilter = BookStatus.AVAILABLE;
                updateActiveTabStyles();
                refreshView(false);
            }
        });
        tileIssued.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeStatusFilter = BookStatus.ISSUED;
                updateActiveTabStyles();
                refreshView(false);
            }
        });
        tileReserved.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                activeStatusFilter = BookStatus.RESERVED;
                updateActiveTabStyles();
                refreshView(false);
            }
        });

        tilesPanel.add(tileAvailable);
        tilesPanel.add(tileIssued);
        tilesPanel.add(tileReserved);

        hero.add(leftEditor, BorderLayout.CENTER);
        hero.add(tilesPanel, BorderLayout.EAST);
        return hero;
    }

    /**
     * @return configured catalog {@link JPanel}
     */
    private JPanel buildCatalogSection() {
        catalogCardLayout = new CardLayout();
        catalogCardPanel = new JPanel(catalogCardLayout);
        catalogCardPanel.setBackground(CLR_WHITE);

        // Card 1: Empty Library State
        RoundedPanel emptyPanel = new RoundedPanel(14, CLR_BG_SUBTLE, CLR_BORDER);
        emptyPanel.setLayout(new GridBagLayout());
        JPanel emptyContent = new JPanel();
        emptyContent.setOpaque(false);
        emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));

        JLabel emptyHeading = new JLabel("No Books in the Library Catalog");
        emptyHeading.setFont(new Font("SansSerif", Font.BOLD, 16));
        emptyHeading.setForeground(CLR_BLACK);
        emptyHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySub = new JLabel("Enter a Book ID, Title, and Author above and click '+ Add Book' to populate the catalog.");
        emptySub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        emptySub.setForeground(CLR_TEXT_MUTED);
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyContent.add(emptyHeading);
        emptyContent.add(Box.createVerticalStrut(6));
        emptyContent.add(emptySub);
        emptyPanel.add(emptyContent);

        // Card 2: Styled Catalog Table
        String[] columns = {"BOOK ID", "TITLE", "AUTHOR", "STATUS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(38);
        bookTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookTable.setForeground(CLR_BLACK);
        bookTable.setBackground(CLR_WHITE);
        bookTable.setGridColor(CLR_BORDER);
        bookTable.setShowVerticalLines(false);
        bookTable.setSelectionBackground(new Color(243, 244, 246));
        bookTable.setSelectionForeground(CLR_BLACK);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = bookTable.getTableHeader();
        header.setBackground(CLR_BG_SUBTLE);
        header.setForeground(CLR_TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        bookTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(340);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(240);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        bookTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                int row = bookTable.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtTitle.setText(tableModel.getValueAt(row, 1).toString());
                txtAuthor.setText(tableModel.getValueAt(row, 2).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(row, 3));
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CLR_BORDER, 1));
        scrollPane.getViewport().setBackground(CLR_WHITE);

        catalogCardPanel.add(emptyPanel, "EMPTY");
        catalogCardPanel.add(scrollPane, "TABLE");
        return catalogCardPanel;
    }

    /**
     * @return configured footer {@link JPanel}
     */
    private JPanel buildFooterBar() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(CLR_BG_SUBTLE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, CLR_BORDER),
                new EmptyBorder(7, 24, 7, 24)
        ));

        lblFooterStatus = new JLabel("The library is currently empty.");
        lblFooterStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFooterStatus.setForeground(CLR_TEXT_MUTED);

        JLabel tipLabel = new JLabel("Tip: Click any row in the catalog to load its details for editing or status changes.");
        tipLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tipLabel.setForeground(CLR_TEXT_MUTED);

        footer.add(lblFooterStatus, BorderLayout.WEST);
        footer.add(tipLabel, BorderLayout.EAST);
        return footer;
    }

    /**
     * Handles adding a new book with validation and error dialogs.
     */
    private void handleAddBook() {
        try {
            manager.addBook(txtId.getText(), txtTitle.getText(), txtAuthor.getText());
            BookStatus chosen = (BookStatus) cmbStatus.getSelectedItem();
            if (chosen != null && chosen != BookStatus.AVAILABLE) {
                manager.setBookStatus(txtId.getText(), chosen);
            }
            activeStatusFilter = null;
            updateActiveTabStyles();
            refreshView(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Book added successfully to the catalog.",
                    "Book Added", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles updating title, author, and status of an existing book.
     */
    private void handleUpdateBook() {
        try {
            manager.updateBook(
                    txtId.getText(),
                    txtTitle.getText(),
                    txtAuthor.getText(),
                    (BookStatus) cmbStatus.getSelectedItem()
            );
            refreshView(false);
            JOptionPane.showMessageDialog(this, "Book details updated successfully.",
                    "Book Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles updating only the status (Available, Issued, or Reserved) of an existing book.
     */
    private void handleSetStatus() {
        try {
            BookStatus chosen = (BookStatus) cmbStatus.getSelectedItem();
            manager.setBookStatus(txtId.getText(), chosen);
            refreshView(false);
            JOptionPane.showMessageDialog(this, "Book status updated to " + chosen + ".",
                    "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Status Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles deleting a book from the catalog.
     */
    private void handleDeleteBook() {
        try {
            manager.deleteBook(txtId.getText());
            refreshView(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Book removed from the library.",
                    "Book Deleted", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the table, metric tiles, and empty-library state.
     *
     * @param notifyIfEmpty whether to display an informational dialog when the library is empty
     */
    private void refreshView(boolean notifyIfEmpty) {
        List<Book> allBooks = manager.getAllBooks();

        int availableCount = 0;
        int issuedCount = 0;
        int reservedCount = 0;
        for (Book b : allBooks) {
            if (b.getStatus() == BookStatus.AVAILABLE) availableCount++;
            else if (b.getStatus() == BookStatus.ISSUED) issuedCount++;
            else if (b.getStatus() == BookStatus.RESERVED) reservedCount++;
        }

        tileAvailable.setCount(availableCount);
        tileIssued.setCount(issuedCount);
        tileReserved.setCount(reservedCount);
        lblTicker.setText("‹   Total Catalog: " + allBooks.size() + " Books  •  "
                + availableCount + " Available  •  " + issuedCount + " Issued   ›");

        if (manager.isEmpty()) {
            tableModel.setRowCount(0);
            catalogCardLayout.show(catalogCardPanel, "EMPTY");
            String emptyMsg = "The library is currently empty. No books available to display.";
            lblFooterStatus.setText(emptyMsg);
            if (notifyIfEmpty) {
                JOptionPane.showMessageDialog(this, emptyMsg,
                        "Empty Library", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        String query = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        for (Book book : allBooks) {
            boolean matchesStatus = (activeStatusFilter == null || book.getStatus() == activeStatusFilter);
            boolean matchesQuery = query.isEmpty()
                    || book.getId().toLowerCase().contains(query)
                    || book.getTitle().toLowerCase().contains(query)
                    || book.getAuthor().toLowerCase().contains(query);

            if (matchesStatus && matchesQuery) {
                tableModel.addRow(new Object[]{
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getStatus()
                });
            }
        }

        catalogCardLayout.show(catalogCardPanel, "TABLE");
        lblFooterStatus.setText("Displaying " + tableModel.getRowCount() + " of " + allBooks.size() + " books.");
    }

    /**
     * Resets input fields and table selection.
     */
    private void clearForm() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        cmbStatus.setSelectedIndex(0);
        bookTable.clearSelection();
    }

    /**
     * @param text   tab label
     * @param active initial active state
     * @return styled {@link JButton}
     */
    private JButton createNavTab(String text, boolean active) {
        JButton tab = new JButton(text);
        tab.setFocusPainted(false);
        tab.setContentAreaFilled(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
        tab.setForeground(active ? CLR_BLACK : CLR_TEXT_MUTED);
        tab.setBorder(active
                ? BorderFactory.createMatteBorder(0, 0, 2, 0, CLR_BLACK)
                : new EmptyBorder(0, 0, 2, 0));
        return tab;
    }

    private void updateActiveTabStyles() {
        styleTabState(tabAll, activeStatusFilter == null);
        styleTabState(tabAvailable, activeStatusFilter == BookStatus.AVAILABLE);
        styleTabState(tabIssued, activeStatusFilter == BookStatus.ISSUED);
        styleTabState(tabReserved, activeStatusFilter == BookStatus.RESERVED);
    }

    /**
     * Applies active or inactive visual styling to a navigation tab.
     *
     * @param tab    button to style
     * @param active true if currently selected
     */
    private void styleTabState(JButton tab, boolean active) {
        tab.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 13));
        tab.setForeground(active ? CLR_BLACK : CLR_TEXT_MUTED);
        tab.setBorder(active
                ? BorderFactory.createMatteBorder(0, 0, 2, 0, CLR_BLACK)
                : new EmptyBorder(0, 0, 2, 0));
    }

    /**
     * Creates a crisp white text input field with padding.
     *
     * @return styled {@link JTextField}
     */
    private JTextField styleInputField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(CLR_WHITE);
        field.setForeground(CLR_BLACK);
        field.setCaretColor(CLR_BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    /**
     * Wraps a form control with an uppercase micro-label above it.
     *
     * @param labelText uppercase field caption
     * @param component input component
     * @return labeled wrapper {@link JPanel}
     */
    private JPanel wrapField(String labelText, JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(209, 213, 219));
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Custom rounded container panel for cards, badges, and search boxes.
     */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;
        private final Color borderColor;

        /**
         * Constructs a rounded panel with custom fill and border colors.
         *
         * @param radius      corner arc diameter
         * @param bgColor     interior fill color
         * @param borderColor outline stroke color
         */
        RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Custom pill-shaped button painted via Graphics2D so colors render consistently across OS themes.
     */
    private static class PillButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private final Color borderCol;

        /**
         * Constructs a pill-shaped button.
         *
         * @param text      button label
         * @param bg        background color
         * @param fg        text color
         * @param borderCol border outline color
         */
        PillButton(String text, Color bg, Color fg, Color borderCol) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = bg.darker();
            this.borderCol = borderCol;
            setForeground(fg);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        /**
         * Sets a custom hover fill color.
         *
         * @param hoverBg color when mouse hovers over the button
         */
        void setHoverBackground(Color hoverBg) {
            this.hoverBg = hoverBg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? hoverBg : normalBg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            g2.setColor(borderCol);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Storefront-inspired vertical metric tile displaying live counts per book status.
     */
    private static class MetricTile extends RoundedPanel {
        private final JLabel lblCount;

        /**
         * Constructs a metric tile with a bottom caption banner.
         *
         * @param label     status category label
         * @param bannerCol bottom banner accent color
         */
        MetricTile(String label, Color bannerCol) {
            super(12, CLR_DARK_TILE, new Color(55, 65, 81));
            setLayout(new BorderLayout());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            lblCount = new JLabel("0", SwingConstants.CENTER);
            lblCount.setForeground(CLR_WHITE);
            lblCount.setFont(new Font("SansSerif", Font.BOLD, 28));

            JPanel bottomBanner = new JPanel(new BorderLayout());
            bottomBanner.setBackground(bannerCol);
            bottomBanner.setPreferredSize(new Dimension(0, 30));

            JLabel lblName = new JLabel(label, SwingConstants.CENTER);
            lblName.setForeground(CLR_WHITE);
            lblName.setFont(new Font("SansSerif", Font.BOLD, 11));
            bottomBanner.add(lblName, BorderLayout.CENTER);

            add(lblCount, BorderLayout.CENTER);
            add(bottomBanner, BorderLayout.SOUTH);
        }

        /**
         * Updates the numeric counter shown in the center of the tile.
         *
         * @param count updated book count
         */
        void setCount(int count) {
            lblCount.setText(String.valueOf(count));
        }
    }

    /**
     * Renders {@link BookStatus} values inside the catalog table as color-coded pill badges.
     */
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(isSelected ? table.getSelectionBackground() : CLR_WHITE);

            BookStatus status = (BookStatus) value;
            Color pillBg = new Color(220, 252, 231);
            Color pillFg = new Color(22, 101, 52);

            if (status == BookStatus.ISSUED) {
                pillBg = new Color(254, 243, 199);
                pillFg = new Color(146, 64, 14);
            } else if (status == BookStatus.RESERVED) {
                pillBg = new Color(219, 234, 254);
                pillFg = new Color(30, 64, 175);
            }

            RoundedPanel pill = new RoundedPanel(18, pillBg, pillBg);
            pill.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 2));
            JLabel text = new JLabel(status.toString());
            text.setFont(new Font("SansSerif", Font.BOLD, 11));
            text.setForeground(pillFg);
            pill.add(text);

            cell.add(pill);
            return cell;
        }
    }
}