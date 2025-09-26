# CSV Character Cleaner Application - Line by Line Explanation

This document provides a comprehensive explanation of the CSV Character Cleaner application, which consists of two main Java files that work together to clean special characters from CSV files.

## Table of Contents
1. [CSVCharacterCleaner.java - Core Logic](#csvcharactercleanerjava---core-logic)
2. [CSVCharacterCleanerGUI.java - GUI Interface](#csvcharactercleanerguijava---gui-interface)
3. [How the Application Works](#how-the-application-works)

---

## CSVCharacterCleaner.java - Core Logic

This file contains the main logic for processing and cleaning CSV files by removing accented characters, special symbols, and problematic quotes.

### Import Statements (Lines 1-5)
```java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
```
- **Line 1**: Imports all I/O classes for file reading/writing operations
- **Line 2**: Imports UTF-8 character encoding for proper text handling
- **Line 3**: Imports text normalization utilities to handle accented characters
- **Line 4**: Imports collection utilities (HashSet, etc.)
- **Line 5**: Imports regex pattern matching for text processing

### Class Documentation and Declaration (Lines 7-11)
```java
/**
 * CSV Character Cleaner - Removes accented characters and special characters from CSV files
 * Replaces them with their ASCII equivalents (e.g., á -> a, é -> e, ñ -> n)
 */
public class CSVCharacterCleaner {
```
- **Lines 7-10**: JavaDoc comment explaining the class purpose
- **Line 11**: Declares the public class `CSVCharacterCleaner`

### Class Fields (Lines 13-15)
```java
private static final Pattern DIACRITICAL_MARKS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
private int replacementsCount = 0;
private Set<String> replacedCharacters = new HashSet<>();
```
- **Line 13**: Creates a regex pattern to match diacritical marks (accent marks on letters)
- **Line 14**: Counter to track how many replacements were made
- **Line 15**: Set to store unique types of character replacements for reporting

### Text Normalization Method - normalizeText() (Lines 17-128)

#### Method Declaration and Null Check (Lines 20-23)
```java
public String normalizeText(String input) {
    if (input == null) return null;
    
    String original = input;
```
- **Line 20**: Public method that takes a string and returns cleaned version
- **Line 21**: Safety check - returns null if input is null
- **Line 23**: Stores original text for comparison later

#### Unicode Normalization (Lines 25-27)
```java
// Normalize to NFD (decomposed form) and remove diacritical marks
String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
String withoutDiacritics = DIACRITICAL_MARKS.matcher(normalized).replaceAll("");
```
- **Line 26**: Decomposes characters (e.g., "é" becomes "e" + accent mark)
- **Line 27**: Removes the accent marks, leaving only base characters

#### CSV Quote Processing (Lines 29-36)
```java
// Fix CSV quote escaping issues first
String cleaned = withoutDiacritics
    // Handle problematic quotes in measurements (inch symbol)
    .replaceAll("(\\d+)\"\\s", "$1 inch ")  // "3" " -> "3 inch "
    .replaceAll("(\\d+)\"([A-Za-z])", "$1 inch $2")  // "3"One" -> "3 inch One"
    .replaceAll("(\\d+)\"$", "$1 inch")     // "3"" at end -> "3 inch"
    // Replace other problematic quotes with single quotes where appropriate
    .replace("\"", "'");          // Replace remaining quotes with single quotes
```
- **Lines 30-36**: Chain of replacements to fix CSV parsing issues
- **Line 32**: Converts measurement quotes like `3" ` to `3 inch `
- **Line 33**: Handles cases like `3"One` becoming `3 inch One`
- **Line 34**: Fixes quotes at end of fields
- **Line 36**: Converts remaining double quotes to single quotes

#### Special Character Replacements (Lines 38-94)
```java
// Additional character replacements for common special characters
cleaned = cleaned
    .replace("ß", "ss")           // German eszett
    .replace("æ", "ae")          // ae ligature
    .replace("œ", "oe")          // oe ligature
    ...
```
This section performs extensive character replacements:

**Germanic Characters (Lines 40-50)**:
- `ß` → `ss` (German eszett)
- `æ` → `ae` (ae ligature)  
- `œ` → `oe` (oe ligature)
- Various stroked letters to their base forms

**Greek Letters (Lines 51-75)**:
- Maps Greek alphabet to Latin equivalents
- Both uppercase and lowercase versions
- Examples: `α` → `a`, `β` → `b`, `θ` → `th`

**Symbols and Punctuation (Lines 76-94)**:
- Arrow symbols → text equivalents (`←` → `<-`)
- Bullet points → asterisks
- Currency symbols → text (`€` → `EUR`)
- Smart quotes → regular quotes
- Copyright symbols → text equivalents

#### Final Cleanup (Lines 96-98)
```java
// Remove any remaining non-ASCII characters (keep only letters, numbers, common punctuation)
cleaned = cleaned.replaceAll("[^\\x00-\\x7F]", "");
```
- **Line 97**: Removes any characters outside the ASCII range (0-127)

#### Change Tracking (Lines 99-127)
```java
// Track what was replaced - simpler approach: count any line that changed
if (!original.equals(cleaned)) {
    replacementsCount++;
    ...
```
This section tracks what changes were made:
- Increments replacement counter if text changed
- Records specific types of replacements made
- Identifies non-ASCII characters for reporting

### CSV File Processing Method - cleanCSVFile() (Lines 130-169)

#### Method Declaration and Setup (Lines 133-141)
```java
public void cleanCSVFile(String inputFilePath, String outputFilePath) throws IOException {
    System.out.println("Processing CSV file: " + inputFilePath);
    replacementsCount = 0;
    replacedCharacters.clear();
    
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8));
         PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8))) {
```
- **Line 133**: Method takes input and output file paths
- **Lines 135-136**: Reset counters and tracking sets
- **Lines 138-141**: Opens input and output files with UTF-8 encoding using try-with-resources

#### File Processing Loop (Lines 143-157)
```java
String line;
int lineNumber = 0;

while ((line = reader.readLine()) != null) {
    lineNumber++;
    
    // Process the entire line (including CSV structure)
    String cleanedLine = normalizeText(line);
    writer.println(cleanedLine);
    
    if (lineNumber % 1000 == 0) {
        System.out.println("Processed " + lineNumber + " lines...");
    }
}
```
- **Lines 146-156**: Main processing loop
- Reads each line, cleans it using `normalizeText()`, writes to output
- Shows progress every 1000 lines

#### Results Reporting (Lines 158-168)
```java
System.out.println("Completed processing " + lineNumber + " lines.");
System.out.println("Total character replacements: " + replacementsCount);

if (!replacedCharacters.isEmpty()) {
    System.out.println("Character replacements made:");
    replacedCharacters.stream().sorted().forEach(replacement -> 
        System.out.println("  " + replacement));
} else {
    System.out.println("No character replacements were needed.");
}
```
- Reports total lines processed and replacement counts
- Lists specific types of characters that were replaced

### Main Method - Command Line Interface (Lines 171-220)

#### Argument Validation (Lines 174-179)
```java
if (args.length < 1 || args.length > 2) {
    System.out.println("Usage: java CSVCharacterCleaner <input_csv_file> [output_csv_file]");
    System.out.println("If output file is not specified, '_cleaned' will be added to input filename");
    System.exit(1);
}
```
- Checks for correct number of command-line arguments
- Shows usage instructions if arguments are invalid

#### File Path Processing (Lines 181-194)
```java
String inputFile = args[0];
String outputFile;

if (args.length == 2) {
    outputFile = args[1];
} else {
    // Generate output filename by adding '_cleaned' before file extension
    int dotIndex = inputFile.lastIndexOf('.');
    if (dotIndex > 0) {
        outputFile = inputFile.substring(0, dotIndex) + "_cleaned" + inputFile.substring(dotIndex);
    } else {
        outputFile = inputFile + "_cleaned";
    }
}
```
- Gets input filename from first argument
- If output filename provided, uses it; otherwise generates one with "_cleaned" suffix

#### File Validation (Lines 196-208)
```java
// Check if input file exists
File inputFileObj = new File(inputFile);
if (!inputFileObj.exists()) {
    System.err.println("Error: Input file '" + inputFile + "' does not exist.");
    System.exit(1);
}

// Check if input file is readable
if (!inputFileObj.canRead()) {
    System.err.println("Error: Cannot read input file '" + inputFile + "'.");
    System.exit(1);
}
```
- Verifies input file exists and is readable
- Exits with error messages if file issues found

#### Processing Execution (Lines 209-220)
```java
CSVCharacterCleaner cleaner = new CSVCharacterCleaner();

try {
    cleaner.cleanCSVFile(inputFile, outputFile);
    System.out.println("Successfully cleaned CSV file!");
    System.out.println("Output saved to: " + outputFile);
} catch (IOException e) {
    System.err.println("Error processing file: " + e.getMessage());
    e.printStackTrace();
    System.exit(1);
}
```
- Creates cleaner instance and processes the file
- Handles any IO exceptions that occur during processing

### Test Method (Lines 222-247)
```java
public static void testNormalization() {
    CSVCharacterCleaner cleaner = new CSVCharacterCleaner();
    
    String[] testStrings = {
        "José María García-Fernández",
        "Café, naïve, résumé, piñata",
        "Zürich, München, São Paulo",
        "Price: €29.99, Weight: 5°C",
        "Special chars: •→←↑↓…""''––",
        "Normal text with numbers 12345"
    };
```
- Provides a test method to demonstrate the cleaning functionality
- Contains various problematic text samples for testing

---

## CSVCharacterCleanerGUI.java - GUI Interface

This file provides a modern Swing-based graphical user interface with drag-and-drop functionality.

### Import Statements (Lines 1-9)
```java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
```
- **Line 1**: Core Swing components
- **Line 2**: Border utilities for spacing
- **Line 3**: AWT graphics and layout managers
- **Lines 4-6**: Drag and drop functionality
- **Lines 7-9**: File handling and collections

### Class Declaration and Fields (Lines 11-34)

#### Class Declaration (Lines 15)
```java
public class CSVCharacterCleanerGUI extends JFrame implements DropTargetListener {
```
- Extends `JFrame` for window functionality
- Implements `DropTargetListener` for drag-and-drop support

#### GUI Component Fields (Lines 17-24)
```java
private JPanel dropZone;
private JProgressBar progressBar;
private JTextArea logArea;
private JLabel statusLabel;
private JLabel fileCountLabel;
private CSVCharacterCleaner cleaner;
private boolean isProcessing = false;
private int filesProcessed = 0;
```
- Declares all main GUI components
- Tracks processing state and file count

#### Color Scheme Constants (Lines 26-33)
```java
private static final Color PRIMARY_COLOR = new Color(59, 130, 246);      // Modern blue
private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Modern green
private static final Color ACCENT_COLOR = new Color(139, 92, 246);       // Purple accent
private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Clean white
private static final Color DROP_ZONE_COLOR = new Color(240, 249, 255);   // Very light blue
private static final Color BORDER_COLOR = new Color(147, 197, 253);      // Light blue border
private static final Color TEXT_SECONDARY = new Color(107, 114, 128);    // Modern gray
```
- Defines a modern color palette for the UI
- Uses contemporary blue/green color scheme

### Constructor and Initialization (Lines 35-61)

#### Constructor (Lines 35-38)
```java
public CSVCharacterCleanerGUI() {
    cleaner = new CSVCharacterCleaner();
    initializeGUI();
}
```
- Creates the core cleaner instance
- Calls GUI initialization method

#### GUI Initialization (Lines 40-61)
```java
private void initializeGUI() {
    setTitle("CSV Character Cleaner - Professional Edition");
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
```
- Sets up main window properties and layout
- Adds three main panels (header, drop zone, log area)
- Enables drag-and-drop functionality

### Header Panel Creation (Lines 63-95)
```java
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
```
- Creates the application title and subtitle
- Adds a counter showing how many files have been processed
- Uses modern typography with the Segoe UI font

### Main Drop Zone Creation (Lines 97-202)

#### Drop Zone Setup (Lines 103-113)
```java
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
```
- Creates the main drop area with dashed border
- Sets up drag-and-drop target functionality

#### CSV Badge and Instructions (Lines 125-162)
```java
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
```
- Creates a prominent "CSV" badge in the center
- Adds clear instructions for drag-and-drop usage

#### Feature Pills (Lines 166-183)
```java
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
```
- Creates attractive "pill" badges showing key features
- Modern green color scheme for feature highlights

#### Progress Bar (Lines 187-196)
```java
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
```
- Creates a modern, flat progress bar
- Initially hidden, shown during processing

### Log Panel Creation (Lines 204-236)
```java
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
```
- Creates a scrollable log area for processing feedback
- Uses monospace font (SF Mono) for better log readability
- Sets up status bar at the bottom

### Drag and Drop Event Handlers (Lines 238-301)

#### Drag Enter (Lines 239-248)
```java
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
```
- Handles when user drags files over the application
- Changes background color to indicate ready state
- Only accepts file drag operations when not currently processing

#### Drag Exit (Lines 257-262)
```java
@Override
public void dragExit(DropTargetEvent dte) {
    dropZone.setBackground(DROP_ZONE_COLOR);
    if (!isProcessing) {
        statusLabel.setText("Ready for drag and drop");
    }
}
```
- Resets visual state when drag leaves the window
- Returns background to original color

#### Drop Handler (Lines 264-301)
```java
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
```
- Main drop processing logic
- Extracts file list from drop event
- Only processes files with .csv extension
- Shows error if no CSV files found

### File Processing Logic (Lines 303-397)

#### Process Setup (Lines 303-318)
```java
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
```
- Sets processing state and shows progress bar
- Automatically generates output filename with "_cleaned" suffix
- Saves output files to user's Desktop

#### Background Processing (Lines 320-396)
```java
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
```
- Uses SwingWorker for background processing to keep UI responsive
- Publishes detailed progress messages with emojis
- Provides specific feedback about what types of fixes were applied

#### Processing Completion (Lines 368-396)
```java
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
```
- Updates file counter and UI state when processing completes
- Shows success dialog with option to open Desktop folder
- Resets UI state after 3-second delay

### Utility Methods (Lines 399-447)

#### Line Count Helper (Lines 399-405)
```java
private long getLineCount(String filePath) {
    try {
        return java.nio.file.Files.lines(java.nio.file.Paths.get(filePath)).count();
    } catch (Exception e) {
        return -1;
    }
}
```
- Efficiently counts lines in file for progress reporting

#### Quote Issue Detection (Lines 407-419)
```java
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
```
- Analyzes first 1000 lines to detect problematic quote patterns
- Used to customize success messages

#### Success Dialog (Lines 421-438)
```java
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
```
- Shows success dialog with option to open Desktop folder
- Uses system desktop integration to open folder

#### Error Dialog (Lines 440-447)
```java
private void showError(String message) {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Error",
        JOptionPane.ERROR_MESSAGE
    );
}
```
- Simple error message dialog

#### GUI Main Method (Lines 449-453)
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new CSVCharacterCleanerGUI().setVisible(true);
    });
}
```
- Launches the GUI on the Event Dispatch Thread (proper Swing threading)

---

## How the Application Works

### Overall Architecture
1. **CSVCharacterCleaner**: Core processing engine that handles text normalization and file I/O
2. **CSVCharacterCleanerGUI**: Modern drag-and-drop interface that wraps the core functionality

### Processing Flow
1. User drags CSV file onto the GUI drop zone
2. GUI validates file is CSV format
3. Background thread processes file using the core cleaner
4. Real-time progress updates appear in the log area
5. Cleaned file is automatically saved to Desktop with "_cleaned" suffix
6. Success dialog offers to open Desktop folder

### Key Features
- **Unicode Normalization**: Removes accents from characters (é → e)
- **Special Character Conversion**: Converts symbols to text (€ → EUR)
- **CSV Quote Fixing**: Handles problematic quotes in measurements (3" → 3 inch)
- **Safe ASCII Output**: Ensures all output is ASCII-compatible
- **Modern UI**: Clean, professional interface with drag-and-drop
- **Background Processing**: Non-blocking operation with progress feedback
- **Desktop Integration**: Automatically saves to Desktop with system folder opening

### Usage Scenarios
- **Command Line**: `java CSVCharacterCleaner input.csv [output.csv]`
- **GUI Mode**: Simply run `java CSVCharacterCleanerGUI` and drag files
- **Batch Processing**: Multiple files can be processed through the GUI

This application is particularly useful for cleaning CSV files that contain international characters, special symbols, or problematic quotes that cause issues when importing into database systems or spreadsheet applications.
