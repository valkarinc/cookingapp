package portfolio;

import portfolio.input.Constants;
import portfolio.input.PdfUploader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Client {

    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JLabel statusLabel;
    private JButton newPageButton;
    private JButton saveButton;
    private JButton settingsButton;
    private JButton uploadPdfButton;
    private JButton viewPdfButton;

    public Client() {
        initializeWindow();
        setupComponents();
        createDefaultPages();
    }

    private void initializeWindow() {

        mainFrame = new JFrame(Constants.APP_NAME + " v" + Constants.APP_VERSION);
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.setLayout(new BorderLayout());
    }

    private void setupComponents() {

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode(Constants.PRIMARY_COLOR));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel authorLabel = new JLabel("by " + Constants.AUTHOR);
        authorLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 12));
        authorLabel.setForeground(Color.LIGHT_GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(authorLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        newPageButton = createHeaderButton("+ New Page", Constants.SECONDARY_COLOR);
        uploadPdfButton = createHeaderButton("Upload PDF", "#9B59B6");
        viewPdfButton = createHeaderButton("View PDF", "#3498DB");
        saveButton = createHeaderButton("Save", "#27AE60");
        settingsButton = createHeaderButton("Settings", "#95A5A6");

        buttonPanel.add(newPageButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(uploadPdfButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(viewPdfButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(settingsButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.DEFAULT_FONT_SIZE));
        tabbedPane.setBackground(Color.decode(Constants.BACKGROUND_COLOR));

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.decode(Constants.PRIMARY_COLOR));
        footerPanel.setPreferredSize(new Dimension(0, 30));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        statusLabel = new JLabel("Ready - " + Constants.getLastSavedTime());
        statusLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        JLabel pagesLabel = new JLabel("Pages: " + Constants.getTotalPages() + "/" + Constants.MAX_PAGES);
        pagesLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        pagesLabel.setForeground(Color.LIGHT_GRAY);

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(pagesLabel, BorderLayout.EAST);

        mainFrame.add(headerPanel, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(footerPanel, BorderLayout.SOUTH);

        setupButtonActions();
    }

    private JButton createHeaderButton(String text, String colorHex) {
        JButton button = new JButton(text);
        button.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 11));
        button.setBackground(Color.decode(colorHex));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(85, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupButtonActions() {
        newPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Constants.canAddPage()) {
                    Constants.registerNewPageClick();
                    createNewPage();
                    updateStatus("New page created");
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Maximum pages (" + Constants.MAX_PAGES + ") reached!",
                            "Cannot Add Page", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        uploadPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatus("Uploading PDF...");
                if (PdfUploader.uploadPdf(mainFrame)) {
                    updateStatus("PDF uploaded: " + Constants.getLastUploadedPdf());

                    createPdfPage();
                } else {
                    updateStatus("PDF upload cancelled or failed");
                }
            }
        });

        viewPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Constants.getLastUploadedPdf().isEmpty()) {
                    openPdfInSystem();
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "No PDF uploaded yet!\nClick 'Upload PDF' first.",
                            "No PDF Available", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Constants.markSaved();
                updateStatus("Portfolio saved successfully");
                updateFooter();
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsDialog();
            }
        });
    }

    private void createDefaultPages() {

        if (Constants.hasNoPages()) {
            Constants.setTotalPages(Constants.DEFAULT_START_PAGES);

            for (int i = 1; i <= Constants.getTotalPages(); i++) {
                addPageTab(i, Constants.getPageName(i));
            }
            updateFooter();
        }
    }

    private void createNewPage() {
        Constants.confirmPageCreation();
        int pageNum = Constants.getTotalPages();
        addPageTab(pageNum, "Page " + pageNum);
        updateFooter();
    }

    private void createPdfPage() {
        if (Constants.canAddPage()) {
            Constants.confirmPageCreation();
            int pageNum = Constants.getTotalPages();
            String pdfName = Constants.getLastUploadedPdf();
            String pageName = "PDF: " + pdfName.substring(0, Math.min(pdfName.length(), 15));

            addPdfTab(pageNum, pageName, pdfName);
            updateFooter();
            updateStatus("PDF page created: " + pageName);
        }
    }

    private void addPageTab(int pageNumber, String pageName) {
        JPanel pagePanel = createPageContent(pageNumber, pageName);
        tabbedPane.addTab(pageName, pagePanel);

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private void addPdfTab(int pageNumber, String pageName, String pdfFileName) {
        JPanel pdfPanel = createPdfPageContent(pageNumber, pageName, pdfFileName);
        tabbedPane.addTab(pageName, pdfPanel);

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private JPanel createPageContent(int pageNumber, String pageName) {
        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.setBackground(Color.WHITE);
        pagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);

        JLabel pageTitle = new JLabel(pageName);
        pageTitle.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 16));
        pageTitle.setForeground(Color.decode(Constants.PRIMARY_COLOR));

        JLabel pageInfo = new JLabel("Page " + pageNumber + " of " + Constants.getTotalPages());
        pageInfo.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        pageInfo.setForeground(Color.GRAY);

        pageHeader.add(pageTitle, BorderLayout.WEST);
        pageHeader.add(pageInfo, BorderLayout.EAST);

        JPanel contentArea = new JPanel();
        contentArea.setBackground(Color.decode(Constants.BACKGROUND_COLOR));
        contentArea.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel contentLabel = new JLabel("<html><center>Click to add content...<br><br>" +
                "This page can contain:<br>" +
                "• Recipes<br>" +
                "• Photos<br>" +
                "• Cooking notes<br>" +
                "• Techniques<br>" +
                "• PDFs</center></html>");
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 12));
        contentLabel.setForeground(Color.GRAY);

        contentArea.add(contentLabel);

        pagePanel.add(pageHeader, BorderLayout.NORTH);
        pagePanel.add(Box.createVerticalStrut(15), BorderLayout.WEST);
        pagePanel.add(contentArea, BorderLayout.CENTER);

        return pagePanel;
    }

    private JPanel createPdfPageContent(int pageNumber, String pageName, String pdfFileName) {
        JPanel pdfPanel = new JPanel(new BorderLayout());
        pdfPanel.setBackground(Color.WHITE);
        pdfPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);

        JLabel pageTitle = new JLabel(pageName);
        pageTitle.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 16));
        pageTitle.setForeground(Color.decode(Constants.PRIMARY_COLOR));

        JLabel pageInfo = new JLabel("Page " + pageNumber + " of " + Constants.getTotalPages());
        pageInfo.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        pageInfo.setForeground(Color.GRAY);

        pageHeader.add(pageTitle, BorderLayout.WEST);
        pageHeader.add(pageInfo, BorderLayout.EAST);

        JPanel pdfInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pdfInfoPanel.setOpaque(false);

        JLabel fileLabel = new JLabel("📄 " + pdfFileName);
        fileLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        fileLabel.setForeground(Color.decode(Constants.SECONDARY_COLOR));

        JButton openPdfButton = new JButton("Open PDF");
        openPdfButton.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        openPdfButton.setBackground(Color.decode("#3498DB"));
        openPdfButton.setForeground(Color.WHITE);
        openPdfButton.setFocusPainted(false);
        openPdfButton.setBorderPainted(false);
        openPdfButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        openPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPdfInSystem();
            }
        });

        pdfInfoPanel.add(fileLabel);
        pdfInfoPanel.add(Box.createHorizontalStrut(20));
        pdfInfoPanel.add(openPdfButton);

        JPanel pdfContentArea = new JPanel(new BorderLayout());
        pdfContentArea.setBackground(Color.decode(Constants.BACKGROUND_COLOR));
        pdfContentArea.setBorder(BorderFactory.createLoweredBevelBorder());

        JTextArea pdfPreview = new JTextArea();
        pdfPreview.setEditable(false);
        pdfPreview.setBackground(Color.WHITE);
        pdfPreview.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 11));
        pdfPreview.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pdfPreview.setLineWrap(true);
        pdfPreview.setWrapStyleWord(true);

        pdfPreview.setText("📄 PDF File: " + pdfFileName + "\n\n" +
                "Location: " + Constants.PDFS_FOLDER + pdfFileName + "\n\n" +
                "Click 'Open PDF' button above to view the full document in your default PDF viewer.\n\n" +
                "This is a PDF page in your cooking portfolio. You can:\n" +
                "• View the PDF externally\n" +
                "• Add notes about this recipe\n" +
                "• Reference it in other pages\n" +
                "• Save it as part of your portfolio\n\n" +
                "PDF files are automatically organized in your pdfs/ folder for easy access.");

        JScrollPane scrollPane = new JScrollPane(pdfPreview);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pdfContentArea.add(scrollPane, BorderLayout.CENTER);

        pdfPanel.add(pageHeader, BorderLayout.NORTH);
        pdfPanel.add(Box.createVerticalStrut(10));
        pdfPanel.add(pdfInfoPanel, BorderLayout.CENTER);
        pdfPanel.add(pdfContentArea, BorderLayout.SOUTH);

        return pdfPanel;
    }

    private void openPdfInSystem() {
        try {
            String pdfPath = Constants.PDFS_FOLDER + Constants.getLastUploadedPdf();
            File pdfFile = new File(pdfPath);

            if (pdfFile.exists()) {

                Desktop.getDesktop().open(pdfFile);
                updateStatus("Opening PDF: " + Constants.getLastUploadedPdf());
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "PDF file not found: " + pdfPath,
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error opening PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message + " - " + Constants.getLastSavedTime());
    }

    private void updateFooter() {
        JLabel pagesLabel = (JLabel) footerPanel.getComponent(1);
        pagesLabel.setText("Pages: " + Constants.getTotalPages() + "/" + Constants.MAX_PAGES);
    }

    private void showSettingsDialog() {
        String lastPdf = Constants.getLastUploadedPdf().isEmpty() ? "None" : Constants.getLastUploadedPdf();

        JOptionPane.showMessageDialog(mainFrame,
                "Settings panel coming soon!\n\n" +
                        "Current Settings:\n" +
                        "• Max Pages: " + Constants.MAX_PAGES + "\n" +
                        "• Author: " + Constants.AUTHOR + "\n" +
                        "• Version: " + Constants.APP_VERSION + "\n" +
                        "• Last PDF: " + lastPdf,
                "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    public void show() {
        mainFrame.setVisible(true);
        updateStatus("Welcome to your cooking portfolio!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client().show();
            }
        });
    }
}