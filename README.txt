CSV Character Cleaner - README
================================

This Java program cleans CSV files by replacing accented characters and special characters with their ASCII equivalents.

FEATURES:
---------
- Removes accents from characters (á → a, é → e, ñ → n, etc.)
- Replaces special characters with ASCII equivalents (€ → EUR, © → (c), etc.)
- Preserves CSV structure while cleaning content
- Shows detailed report of replacements made
- Handles UTF-8 encoded files properly

USAGE:
------
1. Compile the Java program:
   javac CSVCharacterCleaner.java

2. Run with input CSV file:
   java CSVCharacterCleaner input.csv
   (Creates input_cleaned.csv)

   OR specify output file:
   java CSVCharacterCleaner input.csv output.csv

EXAMPLE:
--------
To test with the provided sample file:
1. javac CSVCharacterCleaner.java
2. java CSVCharacterCleaner sample_data.csv
3. Check the generated sample_data_cleaned.csv

SUPPORTED CHARACTER REPLACEMENTS:
----------------------------------
- Accented letters: á→a, é→e, í→i, ó→o, ú→u, ñ→n, ç→c, etc.
- German: ß→ss, ä→a, ö→o, ü→u
- Nordic: å→a, æ→ae, ø→o, þ→th, ð→d
- Special symbols: €→EUR, £→GBP, ¥→JPY, ©→(c), ®→(r), ™→(tm)
- Typography: "→", '→', –→-, —→-, …→..., •→*
- Arrows: ←→<-, →→->, ↑→^, ↓→v
- Temperature: °→ degrees

The program removes any remaining non-ASCII characters that cannot be converted.

FILES:
------
- CSVCharacterCleaner.java  : Main program
- sample_data.csv          : Test CSV file with various accented characters
- README.txt               : This file
