# CSV Character Cleaner

A Java application that cleans CSV files by replacing accented characters and special characters with their ASCII equivalents. Available in both command-line and modern GUI versions.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)

## Features

- 🧹 **Character Cleaning**: Removes accents from characters (á → a, é → e, ñ → n, etc.)
- 🔄 **Special Character Replacement**: Converts special characters to ASCII equivalents (€ → EUR, © → (c), etc.)
- 📊 **CSV Structure Preservation**: Maintains CSV format while cleaning content
- 📈 **Detailed Reporting**: Shows comprehensive report of all replacements made
- 🌐 **UTF-8 Support**: Properly handles UTF-8 encoded files
- 🖱️ **Drag & Drop GUI**: Modern interface with drag-and-drop functionality
- ⚡ **Batch Processing**: Process multiple files at once via GUI

## Supported Character Replacements

### Accented Letters
- **Latin**: á→a, é→e, í→i, ó→o, ú→u, ñ→n, ç→c
- **German**: ß→ss, ä→a, ö→o, ü→u
- **Nordic**: å→a, æ→ae, ø→o, þ→th, ð→d

### Special Symbols
- **Currency**: €→EUR, £→GBP, ¥→JPY
- **Legal**: ©→(c), ®→(r), ™→(tm)
- **Typography**: "→", '→', –→-, —→-, …→..., •→*
- **Arrows**: ←→<-, →→->, ↑→^, ↓→v
- **Temperature**: °→ degrees

## Usage

### Command Line Interface

1. **Compile the program:**
   ```bash
   javac CSVCharacterCleaner.java
   ```

2. **Run with input CSV file:**
   ```bash
   java CSVCharacterCleaner input.csv
   # Creates input_cleaned.csv
   ```

3. **Or specify output file:**
   ```bash
   java CSVCharacterCleaner input.csv output.csv
   ```

### GUI Interface

1. **Compile the GUI program:**
   ```bash
   javac CSVCharacterCleanerGUI.java CSVCharacterCleaner.java
   ```

2. **Run the GUI:**
   ```bash
   java CSVCharacterCleanerGUI
   ```

3. **Use the application:**
   - Drag and drop CSV files onto the interface
   - Files are automatically processed and saved to your Desktop
   - View real-time progress and detailed logs

### Pre-compiled JAR Files

For convenience, pre-compiled JAR files are available:

- `CSVCharacterCleanerCLI.jar` - Command line version
- `CSVCharacterCleanerGUI.jar` - GUI version

Run with:
```bash
java -jar CSVCharacterCleanerGUI.jar
```

## Example

Process a sample CSV file:

```bash
javac CSVCharacterCleaner.java
java CSVCharacterCleaner sample_data.csv
# Check the generated sample_data_cleaned.csv
```

## Project Structure

```
csv-character-cleaner/
├── src/
│   ├── CSVCharacterCleaner.java      # Core cleaning logic
│   └── CSVCharacterCleanerGUI.java   # GUI interface
├── README.md                         # This file
├── .gitignore                        # Git ignore rules
└── CSV_Character_Cleaner_Explanation.md  # Detailed explanation
```

## Requirements

- Java 8 or higher
- Any operating system supporting Java

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is open source and available under the [MIT License](LICENSE).

## About

This tool is particularly useful for:
- Preparing CSV data for systems that only accept ASCII characters
- Cleaning international data for database import
- Standardizing character encoding across datasets
- Converting legacy data with encoding issues

The program automatically removes any remaining non-ASCII characters that cannot be converted, ensuring clean, standardized output.
