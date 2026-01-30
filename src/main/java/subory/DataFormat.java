package subory;

/**
 * Enum reprezentujúci podporované formáty dát.
 */
public enum DataFormat {
    PLAIN_TEXT("Plain Text", "txt"),
    CSV("CSV", "csv"),
    XML("XML", "xml"),
    JSON("JSON", "json");
    
    private final String displayName;
    private final String extension;
    
    DataFormat(String displayName, String extension) {
        this.displayName = displayName;
        this.extension = extension;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getExtension() {
        return extension;
    }
    
    /**
     * Získa formát podľa prípony súboru.
     * @param filename názov súboru
     * @return DataFormat alebo PLAIN_TEXT ako default
     */
    public static DataFormat fromFilename(String filename) {
        if (filename == null) {
            return PLAIN_TEXT;
        }
        
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".csv")) {
            return CSV;
        } else if (lowerFilename.endsWith(".xml")) {
            return XML;
        } else if (lowerFilename.endsWith(".json")) {
            return JSON;
        } else {
            return PLAIN_TEXT;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
