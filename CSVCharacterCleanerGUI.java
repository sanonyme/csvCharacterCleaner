import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private JLabel outputFolderLabel;
    private JCheckBox addCleanedCheckbox;
    private JTextField prefixToRemoveField;
    private CSVCharacterCleaner cleaner;
    private boolean isProcessing = false;
    private int filesProcessed = 0;
    private File outputFolder;
    
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
        outputFolder = new File(System.getProperty("user.home") + File.separator + "Desktop");
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
        add(createToolbarPanel(), BorderLayout.PAGE_START);
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
    
    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new BorderLayout(0, 8));
        toolbar.setBackground(BACKGROUND_COLOR);
        toolbar.setBorder(new EmptyBorder(0, 20, 8, 20));
        
        // Top row: buttons and output folder
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setBackground(BACKGROUND_COLOR);
        
        JButton browseBtn = new JButton("Browse Files...");
        browseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        browseBtn.setBackground(PRIMARY_COLOR);
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setFocusPainted(false);
        browseBtn.setBorderPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseBtn.addActionListener(e -> browseForFiles());
        
        JButton outputBtn = new JButton("Choose Output Folder...");
        outputBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        outputBtn.setBackground(ACCENT_COLOR);
        outputBtn.setForeground(Color.WHITE);
        outputBtn.setFocusPainted(false);
        outputBtn.setBorderPainted(false);
        outputBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outputBtn.addActionListener(e -> chooseOutputFolder());
        
        outputFolderLabel = new JLabel("Output: " + outputFolder.getAbsolutePath());
        outputFolderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        outputFolderLabel.setForeground(TEXT_SECONDARY);
        outputFolderLabel.setToolTipText(outputFolder.getAbsolutePath());
        
        topRow.add(browseBtn);
        topRow.add(outputBtn);
        topRow.add(outputFolderLabel);
        
        // Options row: add _cleaned checkbox and prefix removal
        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        optionsRow.setBackground(BACKGROUND_COLOR);
        
        addCleanedCheckbox = new JCheckBox("Add \"_cleaned\" to output filenames", true);
        addCleanedCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addCleanedCheckbox.setBackground(BACKGROUND_COLOR);
        
        optionsRow.add(addCleanedCheckbox);
        optionsRow.add(new JLabel("  Remove prefix:"));
        prefixToRemoveField = new JTextField(12);
        prefixToRemoveField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prefixToRemoveField.setToolTipText("e.g. _IN_ - removes this prefix from output filenames");
        prefixToRemoveField.setColumns(12);
        optionsRow.add(prefixToRemoveField);
        
        toolbar.add(topRow, BorderLayout.NORTH);
        toolbar.add(optionsRow, BorderLayout.CENTER);
        return toolbar;
    }
    
    private void browseForFiles() {
        if (isProcessing) return;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        chooser.setDialogTitle("Select CSV files to clean");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = chooser.getSelectedFiles();
            if (selectedFiles.length > 0) {
                List<File> csvFiles = new ArrayList<>();
                for (File f : selectedFiles) {
                    if (f.getName().toLowerCase().endsWith(".csv")) {
                        csvFiles.add(f);
                    }
                }
                if (!csvFiles.isEmpty()) {
                    processCSVFiles(csvFiles);
                } else {
                    showError("No CSV files selected. Please select .csv files.");
                }
            }
        }
    }
    
    private void chooseOutputFolder() {
        JFileChooser chooser = new JFileChooser(outputFolder);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose output folder for cleaned files");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputFolder = chooser.getSelectedFile();
            outputFolderLabel.setText("Output: " + outputFolder.getAbsolutePath());
            outputFolderLabel.setToolTipText(outputFolder.getAbsolutePath());
        }
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
        JLabel descLabel = new JLabel("Files will be cleaned and saved to your chosen output folder");
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
        logArea.setText("Ready to process CSV files. Drop files or click 'Browse Files' to select multiple files.\n");
        
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
            statusLabel.setText("Drop CSV files to process...");
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
                
                List<File> csvFiles = new ArrayList<>();
                for (File file : droppedFiles) {
                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        csvFiles.add(file);
                    }
                }
                
                if (!csvFiles.isEmpty()) {
                    processCSVFiles(csvFiles);
                } else {
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
    
    private void processCSVFiles(List<File> inputFiles) {
        if (isProcessing || inputFiles.isEmpty()) return;
        
        isProcessing = true;
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Processing " + inputFiles.size() + " file(s)...");
        
        int totalFiles = inputFiles.size();
        final boolean addCleaned = addCleanedCheckbox.isSelected();
        final String prefixToRemove = prefixToRemoveField.getText().trim();
        
        SwingWorker<Integer, String> worker = new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if (!outputFolder.exists()) {
                    outputFolder.mkdirs();
                }
                
                int successCount = 0;
                for (int i = 0; i < inputFiles.size(); i++) {
                    File inputFile = inputFiles.get(i);
                    String fileName = inputFile.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    String baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
                    String extension = dotIndex > 0 ? fileName.substring(dotIndex) : ".csv";
                    
                    // Remove prefix from base name if specified
                    if (!prefixToRemove.isEmpty() && baseName.startsWith(prefixToRemove)) {
                        baseName = baseName.substring(prefixToRemove.length());
                    }
                    
                    // Add _cleaned suffix if option is enabled
                    String outputFileName = addCleaned ? baseName + "_cleaned" + extension : baseName + extension;
                    String outputPath = outputFolder.getAbsolutePath() + File.separator + outputFileName;
                    
                    publish("🔄 [" + (i + 1) + "/" + totalFiles + "] Processing: " + inputFile.getName());
                    publish("📁 Output: " + new File(outputPath).getName());
                    
                    try {
                        long lineCount = getLineCount(inputFile.getAbsolutePath());
                        publish("📊 Starting processing of " + lineCount + " lines...");
                        
                        cleaner.cleanCSVFile(inputFile.getAbsolutePath(), outputPath);
                        
                        publish("✅ Completed: " + inputFile.getName());
                        publish("💾 Saved to: " + outputPath);
                        publish(""); // Empty line for spacing
                        successCount++;
                    } catch (IOException e) {
                        publish("❌ Error processing " + inputFile.getName() + ": " + e.getMessage());
                        publish("");
                    }
                }
                return successCount;
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
                    int successCount = get();
                    filesProcessed += successCount;
                    fileCountLabel.setText("Files processed: " + filesProcessed);
                    statusLabel.setText(successCount + " of " + totalFiles + " file(s) processed successfully!");
                    if (successCount > 0) {
                        showSuccess(successCount, totalFiles);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error processing files");
                    showError("Error: " + e.getMessage());
                } finally {
                    isProcessing = false;
                    progressBar.setVisible(false);
                    
                    Timer resetTimer = new Timer(3000, ev -> {
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
    
    private void showSuccess(int successCount, int totalFiles) {
        String message = successCount == 1
            ? "1 file cleaned successfully!"
            : successCount + " of " + totalFiles + " files cleaned successfully!";
        message += "\n\nFiles have been saved to:\n" + outputFolder.getAbsolutePath();
        message += "\n\nWould you like to open the output folder?";
        
        int result = JOptionPane.showConfirmDialog(
            this,
            message,
            "Success",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(outputFolder);
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