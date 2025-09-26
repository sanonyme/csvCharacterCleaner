import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * CSV Character Cleaner - Removes accented characters and special characters from CSV files
 * Replaces them with their ASCII equivalents (e.g., á -> a, é -> e, ñ -> n)
 */
public class CSVCharacterCleaner {
    
    private static final Pattern DIACRITICAL_MARKS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private int replacementsCount = 0;
    private Set<String> replacedCharacters = new HashSet<>();
    
    /**
     * Normalizes a string by removing accents and converting to ASCII equivalents
     */
    public String normalizeText(String input) {
        if (input == null) return null;
        
        String original = input;
        
        // Normalize to NFD (decomposed form) and remove diacritical marks
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = DIACRITICAL_MARKS.matcher(normalized).replaceAll("");
        
        // Fix CSV quote escaping issues first
        String cleaned = withoutDiacritics
            // Handle problematic quotes in measurements (inch symbol)
            .replaceAll("(\\d+)\"\\s", "$1 inch ")  // "3" " -> "3 inch "
            .replaceAll("(\\d+)\"([A-Za-z])", "$1 inch $2")  // "3"One" -> "3 inch One"
            .replaceAll("(\\d+)\"$", "$1 inch")     // "3"" at end -> "3 inch"
            // Replace other problematic quotes with single quotes where appropriate
            .replace("\"", "'");          // Replace remaining quotes with single quotes
            
        // Additional character replacements for common special characters
        cleaned = cleaned
            .replace("ß", "ss")           // German eszett
            .replace("æ", "ae")          // ae ligature
            .replace("œ", "oe")          // oe ligature
            .replace("ø", "o")           // o with stroke
            .replace("þ", "th")          // thorn
            .replace("ð", "d")           // eth
            .replace("ł", "l")           // l with stroke (lowercase)
            .replace("Ł", "L")           // L with stroke (uppercase)
            .replace("đ", "d")           // d with stroke
            .replace("ħ", "h")           // h with stroke
            .replace("ŧ", "t")           // t with stroke
            // Greek letter transliterations
            .replace("α", "a").replace("Α", "A")
            .replace("β", "b").replace("Β", "B") 
            .replace("γ", "g").replace("Γ", "G")
            .replace("δ", "d").replace("Δ", "D")
            .replace("ε", "e").replace("Ε", "E")
            .replace("ζ", "z").replace("Ζ", "Z")
            .replace("η", "i").replace("Η", "I")
            .replace("θ", "th").replace("Θ", "Th")
            .replace("ι", "i").replace("Ι", "I")
            .replace("κ", "k").replace("Κ", "K")
            .replace("λ", "l").replace("Λ", "L")
            .replace("μ", "m").replace("Μ", "M")
            .replace("ν", "n").replace("Ν", "N")
            .replace("ξ", "x").replace("Ξ", "X")
            .replace("ο", "o").replace("Ο", "O")
            .replace("π", "p").replace("Π", "P")
            .replace("ρ", "r").replace("Ρ", "R")
            .replace("σ", "s").replace("ς", "s").replace("Σ", "S")
            .replace("τ", "t").replace("Τ", "T")
            .replace("υ", "y").replace("Υ", "Y")
            .replace("φ", "f").replace("Φ", "F")
            .replace("χ", "ch").replace("Χ", "Ch")
            .replace("ψ", "ps").replace("Ψ", "Ps")
            .replace("ω", "o").replace("Ω", "O")
            .replace("←", "<-")          // left arrow
            .replace("→", "->")          // right arrow
            .replace("↑", "^")           // up arrow
            .replace("↓", "v")           // down arrow
            .replace("•", "*")           // bullet
            .replace("…", "...")         // ellipsis
            .replace("\u201C", "\"")     // left double quotation mark
            .replace("\u201D", "\"")     // right double quotation mark
            .replace("\u2018", "'")      // left single quotation mark
            .replace("\u2019", "'")      // right single quotation mark
            .replace("–", "-")           // en dash
            .replace("—", "-")           // em dash
            .replace("€", "EUR")         // Euro symbol
            .replace("£", "GBP")         // Pound symbol
            .replace("¥", "JPY")         // Yen symbol
            .replace("©", "(c)")         // Copyright
            .replace("®", "(r)")         // Registered trademark
            .replace("™", "(tm)")        // Trademark
            .replace("°", " degrees");   // Degree symbol
        
        // Remove any remaining non-ASCII characters (keep only letters, numbers, common punctuation)
        cleaned = cleaned.replaceAll("[^\\x00-\\x7F]", "");
        
        // Track what was replaced - simpler approach: count any line that changed
        if (!original.equals(cleaned)) {
            replacementsCount++;
            
            // Track specific changes we made
            if (original.contains("\"")) {
                replacedCharacters.add("\" (double quote) -> (converted to safe format)");
            }
            if (original.matches(".*\\d+\".*")) {
                replacedCharacters.add("Measurement quotes (3\") -> (3 inch)");
            }
            
            // Track non-ASCII character replacements
            for (char c : original.toCharArray()) {
                if (c > 127) { // Non-ASCII character
                    String charName = "";
                    if (c == 'á' || c == 'à' || c == 'ä') charName = "accented a";
                    else if (c == 'é' || c == 'è' || c == 'ë') charName = "accented e";
                    else if (c == '€') charName = "Euro symbol";
                    else if (c == '°') charName = "degree symbol";
                    else charName = "special character (" + c + ")";
                    
                    replacedCharacters.add(charName + " -> ASCII equivalent");
                    break; // Only track one per line to avoid spam
                }
            }
        }
        
        return cleaned;
    }
    
    /**
     * Processes a CSV file and cleans all character data
     */
    public void cleanCSVFile(String inputFilePath, String outputFilePath) throws IOException {
        System.out.println("Processing CSV file: " + inputFilePath);
        replacementsCount = 0;
        replacedCharacters.clear();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8))) {
            
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
            
            System.out.println("Completed processing " + lineNumber + " lines.");
            System.out.println("Total character replacements: " + replacementsCount);
            
            if (!replacedCharacters.isEmpty()) {
                System.out.println("Character replacements made:");
                replacedCharacters.stream().sorted().forEach(replacement -> 
                    System.out.println("  " + replacement));
            } else {
                System.out.println("No character replacements were needed.");
            }
        }
    }
    
    /**
     * Main method - handles command line arguments and file processing
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java CSVCharacterCleaner <input_csv_file> [output_csv_file]");
            System.out.println("If output file is not specified, '_cleaned' will be added to input filename");
            System.exit(1);
        }
        
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
    }
    
    /**
     * Utility method to test the character normalization functionality
     */
    public static void testNormalization() {
        CSVCharacterCleaner cleaner = new CSVCharacterCleaner();
        
        String[] testStrings = {
            "José María García-Fernández",
            "Café, naïve, résumé, piñata",
            "Zürich, München, São Paulo",
            "Price: €29.99, Weight: 5°C",
            "Special chars: \u2022\u2192\u2190\u2191\u2193\u2026\u201C\u201D\u2018\u2019\u2013\u2014",
            "Normal text with numbers 12345"
        };
        
        System.out.println("Testing character normalization:");
        System.out.println("================================");
        
        for (String test : testStrings) {
            String normalized = cleaner.normalizeText(test);
            System.out.println("Original:   " + test);
            System.out.println("Cleaned:    " + normalized);
            System.out.println();
        }
    }
}
