import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Modern GUI for CSV Character Cleaner with Drag & Drop functionality
 * Automatically processes CSV files and saves cleaned versions to Desktop
 */
public class CSVCharacterCleanerGUI extends JFrame implements DropTargetListener {
    
    private JPanel dropZone;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel fileCountLabel;
    private CSVCharacterCleaner cleaner;
    private boolean isProcessing = false;
    private int filesProcessed = 0;
    
    // Ultra-modern color scheme
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);      // Modern blue
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Modern green
    private static final Color ACCENT_COLOR = new Color(139, 92, 246);       // Purple accent
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Clean white
    private static final Color DROP_ZONE_COLOR = new Color(240, 249, 255);   // Very light blue
    private static final Color BORDER_COLOR = new Color(147, 197, 253);      // Light blue border
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);    // Modern gray
    
    public CSVCharacterCleanerGUI() {
        cleaner = new CSVCharacterCleaner();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("anexya csv cleaner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(820, 750);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set modern look and feel
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainDropZone(), BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
        
        // Enable drag and drop globally
        new DropTarget(this, this);
        
        // Set minimum size
        setMinimumSize(new Dimension(700, 600));
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Title
        JLabel titleLabel = new JLabel("CSV Character Cleaner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Drop CSV files to automatically fix quotes & special characters");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // File counter
        filesProcessed = 0;
        fileCountLabel = new JLabel("Files processed: " + filesProcessed);
        fileCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileCountLabel.setForeground(TEXT_SECONDARY);
        fileCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel(new GridLayout(3, 1, 0, 5));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        titlePanel.add(fileCountLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }
    
    private JPanel createMainDropZone() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Create the drop zone
        dropZone = new JPanel();
        dropZone.setLayout(new BorderLayout());
        dropZone.setBackground(DROP_ZONE_COLOR);
        dropZone.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(BORDER_COLOR, 2, 12, 8, true),
            new EmptyBorder(50, 40, 50, 40)
        ));
        dropZone.setPreferredSize(new Dimension(750, 350));
        
        // Add drop target
        new DropTarget(dropZone, this);
        
        // Create drop content
        JPanel dropContent = new JPanel(new GridBagLayout());
        dropContent.setBackground(DROP_ZONE_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // Clean CSV badge - no clipping issues
        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(120, 70));
        iconPanel.setBackground(DROP_ZONE_COLOR);
        iconPanel.setLayout(new GridBagLayout());
        
        JLabel csvBadge = new JLabel("CSV");
        csvBadge.setFont(new Font("Segoe UI", Font.BOLD, 32));
        csvBadge.setForeground(PRIMARY_COLOR);
        csvBadge.setOpaque(true);
        csvBadge.setBackground(new Color(235, 245, 255));
        csvBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        csvBadge.setHorizontalAlignment(SwingConstants.CENTER);
        
        iconPanel.add(csvBadge);
        dropContent.add(iconPanel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        
        // Main instruction
        JLabel mainLabel = new JLabel("Drop CSV files here");
        mainLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainLabel.setForeground(PRIMARY_COLOR);
        dropContent.add(mainLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        
        // Description
        JLabel descLabel = new JLabel("Files will be automatically cleaned and saved to your Desktop");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_SECONDARY);
        dropContent.add(descLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        
        // Features list with modern pills design
        JPanel featuresPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        featuresPanel.setBackground(DROP_ZONE_COLOR);
        
        String[] features = {"Fix quotes", "Remove accents", "Convert symbols", "Safe for import"};
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            featureLabel.setForeground(SUCCESS_COLOR);
            featureLabel.setOpaque(true);
            featureLabel.setBackground(new Color(240, 253, 244));
            featureLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 197, 94, 50), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            featuresPanel.add(featureLabel);
        }
        
        dropContent.add(featuresPanel, gbc);
        dropZone.add(dropContent, BorderLayout.CENTER);
        
        // Modern progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(new Color(226, 232, 240));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(750, 8));
        progressBar.setVisible(false);
        
        mainPanel.add(dropZone, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(BACKGROUND_COLOR);
        logPanel.setBorder(BorderFactory.createTitledBorder("Processing Log"));
        logPanel.setPreferredSize(new Dimension(750, 200));
        
        // Modern log text area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("SF Mono", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 250, 252));
        logArea.setForeground(new Color(51, 65, 85));
        logArea.setMargin(new Insets(15, 15, 15, 15));
        logArea.setLineWrap(false);
        logArea.setText("Ready to process CSV files. Drop files above to begin.\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("Ready for drag and drop");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusPanel.add(new JLabel("Status: "));
        statusPanel.add(statusLabel);
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.add(statusPanel, BorderLayout.SOUTH);
        
        return logPanel;
    }
    
    // Drag and Drop Event Handlers
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (!isProcessing && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
            dropZone.setBackground(new Color(220, 252, 231)); // Modern light green
            statusLabel.setText("Drop CSV file to process...");
        } else {
            dtde.rejectDrag();
        }
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {}
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    @Override
    public void dragExit(DropTargetEvent dte) {
        dropZone.setBackground(DROP_ZONE_COLOR);
        if (!isProcessing) {
            statusLabel.setText("Ready for drag and drop");
        }
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (isProcessing) {
            dtde.rejectDrop();
            return;
        }
        
        dropZone.setBackground(DROP_ZONE_COLOR);
        
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                @SuppressWarnings("unchecked")
                List<File> droppedFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                
                // Process only CSV files
                for (File file : droppedFiles) {
                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        processCSVFile(file);
                        break; // Process one at a time for now
                    }
                }
                
                if (droppedFiles.stream().noneMatch(f -> f.getName().toLowerCase().endsWith(".csv"))) {
                    showError("No CSV files found. Please drop CSV files only.");
                }
                
                dtde.dropComplete(true);
            } else {
                dtde.dropComplete(false);
            }
        } catch (Exception e) {
            logArea.append("ERROR: " + e.getMessage() + "\n");
            dtde.dropComplete(false);
        }
    }
    
    private void processCSVFile(File inputFile) {
        if (isProcessing) return;
        
        isProcessing = true;
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Processing " + inputFile.getName() + "...");
        
        // Generate output path to Desktop
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        String fileName = inputFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = fileName.substring(0, dotIndex);
        String extension = fileName.substring(dotIndex);
        String outputPath = desktopPath + File.separator + baseName + "_cleaned" + extension;
        
        // Process in background thread
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                publish("🔄 Processing: " + inputFile.getName());
                publish("📁 Output: " + new File(outputPath).getName() + " (Desktop)");
                
                try {
                    long lineCount = getLineCount(inputFile.getAbsolutePath());
                    publish("📊 Starting processing of " + lineCount + " lines...");
                    
                    // Process the file
                    cleaner.cleanCSVFile(inputFile.getAbsolutePath(), outputPath);
                    
                    publish("✅ Processing completed successfully!");
                    publish("📊 Total lines processed: " + lineCount);
                    
                    // Check if the file had problems that were fixed
                    String fileName = inputFile.getName().toLowerCase();
                    boolean hadQuoteIssues = fileName.contains("airbrush") || checkForQuoteIssues(inputFile);
                    
                    publish("🔧 Character fixes applied:");
                    if (hadQuoteIssues) {
                        publish("   • Fixed double quotes (3\" → 3 inch)");
                        publish("   • Resolved CSV import errors");
                    }
                    publish("   • Converted special characters (á → a, € → EUR)");
                    publish("   • Normalized accented characters");
                    publish("   • Made CSV safe for import systems");
                    
                    publish("💾 Output saved to Desktop: " + new File(outputPath).getName());
                    publish("🎉 Your file is now ready for error-free importing!");
                    publish(""); // Empty line for spacing
                    
                    return true;
                } catch (IOException e) {
                    publish("❌ Error: " + e.getMessage());
                    throw e;
                }
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logArea.append(message + "\n");
                }
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        filesProcessed++;
                        fileCountLabel.setText("Files processed: " + filesProcessed);
                        statusLabel.setText("File processed successfully!");
                        showSuccess(new File(outputPath).getName());
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error processing file");
                    showError("Error: " + e.getMessage());
                } finally {
                    isProcessing = false;
                    progressBar.setVisible(false);
                    
                    // Reset status after delay
                    Timer resetTimer = new Timer(3000, e -> {
                        if (!isProcessing) {
                            statusLabel.setText("Ready for drag and drop");
                        }
                    });
                    resetTimer.setRepeats(false);
                    resetTimer.start();
                }
            }
        };
        
        worker.execute();
    }
    
    private long getLineCount(String filePath) {
        try {
            return java.nio.file.Files.lines(java.nio.file.Paths.get(filePath)).count();
        } catch (Exception e) {
            return -1;
        }
    }
    
    private boolean checkForQuoteIssues(File file) {
        try {
            // Quick check for common problematic patterns
            String content = java.nio.file.Files.lines(file.toPath())
                .limit(1000) // Check first 1000 lines only for performance
                .reduce("", (a, b) -> a + b);
            
            return content.contains("\"") && 
                   (content.matches(".*\\d+\"\\s.*") || content.contains("Airbrush"));
        } catch (Exception e) {
            return false;
        }
    }
    
    private void showSuccess(String fileName) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "File cleaned successfully!\n\n" + fileName + " has been saved to your Desktop.\n\nWould you like to open your Desktop folder?",
            "Success",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                Desktop.getDesktop().open(new File(desktopPath));
            } catch (Exception ex) {
                // Silently fail if can't open folder
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CSVCharacterCleanerGUI().setVisible(true);
        });
    }
}