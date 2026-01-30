package subory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.io.File;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testy pre načítavanie dát z rôznych formátov.
 * Testuje PlainText, CSV, XML a JSON loadery.
 */
class DataLoadersTest {

    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky;
    private LinkedHashMap<Integer, Spoj> spoje;
    private LinkedHashMap<Integer, Linka> linky;

    @BeforeEach
    void setUp() {
        useky = new LinkedHashMap<>();
        spoje = new LinkedHashMap<>();
        linky = new LinkedHashMap<>();
    }

    /**
     * Parametrizovaný test pre načítanie úsekov zo všetkých formátov.
     */
    @ParameterizedTest
    @EnumSource(DataFormat.class)
    void testNacitajUseky_VsetkyFormaty(DataFormat format) throws Exception {
        // Arrange
        IDataLoader loader = DataLoaderFactory.createLoader(format);
        File suborUseky = new File("src/test/resources/test_useky." + format.getExtension());
        
        // Act
        loader.nacitajUseky(suborUseky, useky);
        
        // Assert
        assertNotNull(useky, "Useky by nemali byť null");
        assertFalse(useky.isEmpty(), "Useky by nemali byť prázdne");
        
        // Overenie správnosti načítania - očakávame 3 úseky
        assertTrue(useky.containsKey(new Dvojica<>(1, 2)), "Mal by existovať úsek 1->2");
        assertTrue(useky.containsKey(new Dvojica<>(2, 1)), "Mal by existovať spätný úsek 2->1");
        
        assertEquals(10, useky.get(new Dvojica<>(1, 2)), "Cena úseku 1->2 by mala byť 10");
        assertEquals(15, useky.get(new Dvojica<>(2, 3)), "Cena úseku 2->3 by mala byť 15");
        assertEquals(20, useky.get(new Dvojica<>(3, 4)), "Cena úseku 3->4 by mala byť 20");
        
        // Overenie symetrických úsekov
        assertEquals(useky.get(new Dvojica<>(1, 2)), useky.get(new Dvojica<>(2, 1)),
                    "Symetrické úseky by mali mať rovnakú cenu");
    }

    /**
     * Parametrizovaný test pre načítanie spojov zo všetkých formátov.
     */
    @ParameterizedTest
    @EnumSource(DataFormat.class)
    void testNacitajSpoje_VsetkyFormaty(DataFormat format) throws Exception {
        // Arrange
        IDataLoader loader = DataLoaderFactory.createLoader(format);
        File suborSpoje = new File("src/test/resources/test_spoje." + format.getExtension());
        
        // Act
        loader.nacitajSpoje(suborSpoje, spoje, linky);
        
        // Assert
        assertNotNull(spoje, "Spoje by nemali byť null");
        assertNotNull(linky, "Linky by nemali byť null");
        assertFalse(spoje.isEmpty(), "Spoje by nemali byť prázdne");
        assertFalse(linky.isEmpty(), "Linky by nemali byť prázdne");
        
        // Overenie počtu spojov - očakávame 3 spoje
        assertEquals(3, spoje.size(), "Mal by sa načítať 3 spoje");
        
        // Overenie počtu liniek - očakávame 2 linky (linka 1 a linka 2)
        assertEquals(2, linky.size(), "Mal by sa načítať 2 linky");
        
        // Overenie existencie liniek
        assertTrue(linky.containsKey(1), "Linka 1 by mala existovať");
        assertTrue(linky.containsKey(2), "Linka 2 by mala existovať");
        
        // Overenie počtu spojov na linkách
        assertEquals(2, linky.get(1).getSpoje().size(), "Linka 1 by mala mať 2 spoje");
        assertEquals(1, linky.get(2).getSpoje().size(), "Linka 2 by mala mať 1 spoj");
    }

    @Test
    void testPlainTextLoader_Useky() throws Exception {
        // Arrange
        PlainTextDataLoader loader = new PlainTextDataLoader();
        File suborUseky = new File("src/test/resources/test_useky.txt");
        
        // Act
        loader.nacitajUseky(suborUseky, useky);
        
        // Assert
        assertEquals("Plain Text", loader.getFormatName());
        assertEquals("txt", loader.getFileExtension());
        assertTrue(useky.size() > 0, "Plain text loader by mal načítať úseky");
    }

    @Test
    void testCSVLoader_Useky() throws Exception {
        // Arrange
        CSVDataLoader loader = new CSVDataLoader();
        File suborUseky = new File("src/test/resources/test_useky.csv");
        
        // Act
        loader.nacitajUseky(suborUseky, useky);
        
        // Assert
        assertEquals("CSV", loader.getFormatName());
        assertEquals("csv", loader.getFileExtension());
        assertTrue(useky.size() > 0, "CSV loader by mal načítať úseky");
    }

    @Test
    void testXMLLoader_Useky() throws Exception {
        // Arrange
        XMLDataLoader loader = new XMLDataLoader();
        File suborUseky = new File("src/test/resources/test_useky.xml");
        
        // Act
        loader.nacitajUseky(suborUseky, useky);
        
        // Assert
        assertEquals("XML", loader.getFormatName());
        assertEquals("xml", loader.getFileExtension());
        assertTrue(useky.size() > 0, "XML loader by mal načítať úseky");
    }

    @Test
    void testJSONLoader_Useky() throws Exception {
        // Arrange
        JSONDataLoader loader = new JSONDataLoader();
        File suborUseky = new File("src/test/resources/test_useky.json");
        
        // Act
        loader.nacitajUseky(suborUseky, useky);
        
        // Assert
        assertEquals("JSON", loader.getFormatName());
        assertEquals("json", loader.getFileExtension());
        assertTrue(useky.size() > 0, "JSON loader by mal načítať úseky");
    }

    @Test
    void testDataLoaderFactory_CreateLoaders() {
        // Act & Assert
        assertInstanceOf(PlainTextDataLoader.class, 
                        DataLoaderFactory.createLoader(DataFormat.PLAIN_TEXT),
                        "Factory by mal vytvoriť PlainTextDataLoader");
        
        assertInstanceOf(CSVDataLoader.class, 
                        DataLoaderFactory.createLoader(DataFormat.CSV),
                        "Factory by mal vytvoriť CSVDataLoader");
        
        assertInstanceOf(XMLDataLoader.class, 
                        DataLoaderFactory.createLoader(DataFormat.XML),
                        "Factory by mal vytvoriť XMLDataLoader");
        
        assertInstanceOf(JSONDataLoader.class, 
                        DataLoaderFactory.createLoader(DataFormat.JSON),
                        "Factory by mal vytvoriť JSONDataLoader");
    }

    @Test
    void testDataFormat_FromFilename() {
        // Act & Assert
        assertEquals(DataFormat.PLAIN_TEXT, DataFormat.fromFilename("data.txt"));
        assertEquals(DataFormat.CSV, DataFormat.fromFilename("data.csv"));
        assertEquals(DataFormat.XML, DataFormat.fromFilename("data.xml"));
        assertEquals(DataFormat.JSON, DataFormat.fromFilename("data.json"));
        assertEquals(DataFormat.PLAIN_TEXT, DataFormat.fromFilename("data.unknown"));
        assertEquals(DataFormat.PLAIN_TEXT, DataFormat.fromFilename(null));
    }

    @Test
    void testNacitavacUdajov_AutomaticFormatDetection() throws Exception {
        // Test automatickej detekcie formátu v NacitavacUdajov
        
        // Plain Text
        NacitavacUdajov nacitavac = new NacitavacUdajov();
        File txtFile = new File("src/test/resources/test_useky.txt");
        nacitavac.nacitajUseky(txtFile, useky);
        assertTrue(useky.size() > 0, "Mal by sa automaticky detekovať TXT formát");
        
        // CSV
        useky.clear();
        File csvFile = new File("src/test/resources/test_useky.csv");
        nacitavac.nacitajUseky(csvFile, useky);
        assertTrue(useky.size() > 0, "Mal by sa automaticky detekovať CSV formát");
        
        // XML
        useky.clear();
        File xmlFile = new File("src/test/resources/test_useky.xml");
        nacitavac.nacitajUseky(xmlFile, useky);
        assertTrue(useky.size() > 0, "Mal by sa automaticky detekovať XML formát");
        
        // JSON
        useky.clear();
        File jsonFile = new File("src/test/resources/test_useky.json");
        nacitavac.nacitajUseky(jsonFile, useky);
        assertTrue(useky.size() > 0, "Mal by sa automaticky detekovať JSON formát");
    }

    @Test
    void testNacitavacUdajov_ManualFormatSetting() throws Exception {
        // Test manuálneho nastavenia formátu
        NacitavacUdajov nacitavac = new NacitavacUdajov(DataFormat.CSV);
        
        assertEquals(DataFormat.CSV, nacitavac.getCurrentFormat(),
                    "Formát by mal byť nastavený na CSV");
        
        nacitavac.setDataFormat(DataFormat.JSON);
        assertEquals(DataFormat.JSON, nacitavac.getCurrentFormat(),
                    "Formát by mal byť zmenený na JSON");
    }

    @Test
    void testDataConsistency_AcrossFormats() throws Exception {
        // Test konzistencie dát medzi rôznymi formátmi
        LinkedHashMap<Dvojica<Integer, Integer>, Integer> usekyTxt = new LinkedHashMap<>();
        LinkedHashMap<Dvojica<Integer, Integer>, Integer> usekyCsv = new LinkedHashMap<>();
        LinkedHashMap<Dvojica<Integer, Integer>, Integer> usekyXml = new LinkedHashMap<>();
        LinkedHashMap<Dvojica<Integer, Integer>, Integer> usekyJson = new LinkedHashMap<>();
        
        // Načítaj rovnaké dáta zo všetkých formátov
        DataLoaderFactory.createLoader(DataFormat.PLAIN_TEXT)
                .nacitajUseky(new File("src/test/resources/test_useky.txt"), usekyTxt);
        DataLoaderFactory.createLoader(DataFormat.CSV)
                .nacitajUseky(new File("src/test/resources/test_useky.csv"), usekyCsv);
        DataLoaderFactory.createLoader(DataFormat.XML)
                .nacitajUseky(new File("src/test/resources/test_useky.xml"), usekyXml);
        DataLoaderFactory.createLoader(DataFormat.JSON)
                .nacitajUseky(new File("src/test/resources/test_useky.json"), usekyJson);
        
        // Porovnaj kľúčové hodnoty
        Dvojica<Integer, Integer> testKey = new Dvojica<>(1, 2);
        assertEquals(usekyTxt.get(testKey), usekyCsv.get(testKey),
                    "TXT a CSV by mali mať rovnaké hodnoty");
        assertEquals(usekyCsv.get(testKey), usekyXml.get(testKey),
                    "CSV a XML by mali mať rovnaké hodnoty");
        assertEquals(usekyXml.get(testKey), usekyJson.get(testKey),
                    "XML a JSON by mali mať rovnaké hodnoty");
    }
}
