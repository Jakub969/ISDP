package subory;

/**
 * Factory trieda pre vytváranie inštancií DataLoader podľa formátu.
 * Implementuje Factory pattern pre jednoduchú správu rôznych typov načítavačov.
 */
public class DataLoaderFactory {
    
    /**
     * Vytvorí a vráti inštanciu DataLoadera podľa špecifikovaného formátu.
     * @param format formát dát
     * @return inštancia IDataLoader pre daný formát
     * @throws IllegalArgumentException ak formát nie je podporovaný
     */
    public static IDataLoader createLoader(DataFormat format) {
        return switch (format) {
            case PLAIN_TEXT -> new PlainTextDataLoader();
            case CSV -> new CSVDataLoader();
            case XML -> new XMLDataLoader();
            case JSON -> new JSONDataLoader();
            default -> throw new IllegalArgumentException("Nepodporovaný formát: " + format);
        };
    }
    
    /**
     * Vytvorí a vráti inštanciu DataLoadera podľa názvu súboru.
     * Formát je určený z prípony súboru.
     * @param filename názov súboru
     * @return inštancia IDataLoader pre daný formát
     */
    public static IDataLoader createLoaderFromFilename(String filename) {
        DataFormat format = DataFormat.fromFilename(filename);
        return createLoader(format);
    }
    
    /**
     * Vráti pole všetkých podporovaných formátov.
     * @return pole DataFormat hodnôt
     */
    public static DataFormat[] getSupportedFormats() {
        return DataFormat.values();
    }
}
