package mvp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import subory.DataFormat;
import udaje.*;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that verify calculations performed on loaded data
 * Tests business logic: DT/T matrices, possible connections, trip durations, etc.
 */
public class ModelCalculationTest {
    
    private Model model;
    
    @BeforeEach
    public void setUp() {
        model = new Model();
        // Set up constants for tests
        Model.DEPO = 59;
        Model.REZERVA_1 = 0;
        Model.REZERVA_2 = 0;
    }
    
    /**
     * Test that data loads correctly for all formats and Model can be initialized
     */
    @ParameterizedTest
    @EnumSource(DataFormat.class)
    public void testNacitanieUdajovAInicializaciaModelu(DataFormat format) {
        model.nastavDataFormat(format);
        String result = model.nacitajSpojeUseky(
            "test_useky." + format.getExtension(),
            "test_spoje." + format.getExtension()
        );
        
        assertEquals("Načítanie údajov bolo úspešné.", result);
        assertTrue(model.jeProstrediePripravene());
    }
    
    /**
     * Test that segments (úseky) are loaded correctly
     */
    @Test
    public void testNacitaneUseky() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        String[][] useky = model.getUdajeUseky();
        assertNotNull(useky);
        assertEquals(10, useky.length); // Changed from 3 to 10 - we now have bidirectional segments + self-loops
        
        // Verify specific segments
        // Segment 1→2 with cost 10
        boolean found1to2 = false;
        for (String[] usek : useky) {
            if (usek[0].equals("1") && usek[1].equals("2")) {
                assertEquals("10", usek[2]);
                found1to2 = true;
            }
        }
        assertTrue(found1to2, "Segment 1→2 should exist");
    }
    
    /**
     * Test that trips (spoje) are loaded correctly with all attributes
     */
    @Test
    public void testNacitaneSpoje() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        String[][] spoje = model.getUdajeSpoje();
        assertNotNull(spoje);
        assertEquals(3, spoje.length);
        
        // Each trip should have 9 attributes
        for (String[] spoj : spoje) {
            assertEquals(9, spoj.length);
            assertNotNull(spoj[0]); // idLinky
            assertNotNull(spoj[1]); // idSpoja
            assertNotNull(spoj[2]); // miestoOdchodu
            assertNotNull(spoj[3]); // casOdchodu
            assertNotNull(spoj[4]); // miestoPrichodu
            assertNotNull(spoj[5]); // casPrichodu
            assertNotNull(spoj[6]); // dlzka
            assertNotNull(spoj[7]); // trvanie
            assertNotNull(spoj[8]); // obsadenost
        }
    }
    
    /**
     * Test that lines (linky) are created correctly with proper trip assignment
     */
    @Test
    public void testVytvorenieLiniek() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        assertNotNull(linky);
        assertEquals(2, linky.size());
        
        // Line 1 should have 2 trips
        assertTrue(linky.containsKey(1));
        Linka linka1 = linky.get(1);
        assertEquals(2, linka1.getSpoje().size());
        assertEquals(130, linka1.getObsadenost()); // 60 + 70
        
        // Line 2 should have 1 trip
        assertTrue(linky.containsKey(2));
        Linka linka2 = linky.get(2);
        assertEquals(1, linka2.getSpoje().size());
        assertEquals(60, linka2.getObsadenost());
    }
    
    /**
     * Test trip duration calculation
     */
    @Test
    public void testVypocetTrvaniaSpoja() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        Linka linka1 = linky.get(1);
        Spoj spoj1 = linka1.getSpoje().get(0);
        
        // Trip from 08:00 to 08:30 should be 30 minutes
        int trvanie = spoj1.getTrvanieSpoja();
        assertEquals(30, trvanie);
        
        // Trip from 09:00 to 09:45 should be 45 minutes
        Spoj spoj2 = linka1.getSpoje().get(1);
        assertEquals(45, spoj2.getTrvanieSpoja());
    }
    
    /**
     * Test calculation of possible next trips (E set)
     * These are trips that can follow after a given trip within the same shift
     */
    @Test
    public void testVypocetMoznychNasledujucichSpojov() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        Linka linka1 = linky.get(1);
        Spoj spoj1 = linka1.getSpoje().get(0); // 08:00-08:15, location 1→2
        
        // Check if possible next trips were calculated
        assertNotNull(spoj1.getMozneNasledujuceSpoje());
        
        // With REZERVA_1 = 0:
        // Trip 1 ends at 08:15 at location 2
        // Trip 2 starts at 08:30 at location 2 → segment cost 0
        // 08:15 + 0 + 0 <= 08:30 → YES, trip 2 can follow
        // Trip 3 starts at 09:00 at location 3 → segment cost 15
        // 08:15 + 15 + 0 <= 09:00 → YES, trip 3 can follow
        
        assertTrue(spoj1.getMozneNasledujuceSpoje().size() >= 1, 
                  "Trip 1 should have at least one possible next trip");
    }
    
    /**
     * Test calculation of possible trips with driver change (F set)
     */
    @Test
    public void testVypocetMoznychSpojovSVymenouVodica() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        Linka linka1 = linky.get(1);
        Spoj spoj1 = linka1.getSpoje().get(0);
        
        // Check if possible trips with driver change were calculated
        assertNotNull(spoj1.getMozneNasledujuceSpojeSVymenouVodica());
        // This set should contain trips that can follow after returning to depot
    }
    
    /**
     * Test data consistency - all formats should produce identical results
     */
    @Test
    public void testKonzistenciaVypooctovPreVsetkyFormaty() {
        String[][][] spojePerFormat = new String[4][][];
        String[][][] usekyPerFormat = new String[4][][];
        int[] linkyCountPerFormat = new int[4];
        
        DataFormat[] formats = DataFormat.values();
        for (int i = 0; i < formats.length; i++) {
            Model testModel = new Model();
            testModel.nastavDataFormat(formats[i]);
            testModel.nacitajSpojeUseky(
                "test_useky." + formats[i].getExtension(),
                "test_spoje." + formats[i].getExtension()
            );
            
            spojePerFormat[i] = testModel.getUdajeSpoje();
            usekyPerFormat[i] = testModel.getUdajeUseky();
            linkyCountPerFormat[i] = testModel.getLinky().size();
        }
        
        // Verify all formats produce same number of trips
        for (int i = 1; i < 4; i++) {
            assertEquals(spojePerFormat[0].length, spojePerFormat[i].length,
                        "All formats should load same number of trips");
        }
        
        // Verify all formats produce same number of segments
        for (int i = 1; i < 4; i++) {
            assertEquals(usekyPerFormat[0].length, usekyPerFormat[i].length,
                        "All formats should load same number of segments");
        }
        
        // Verify all formats produce same number of lines
        for (int i = 1; i < 4; i++) {
            assertEquals(linkyCountPerFormat[0], linkyCountPerFormat[i],
                        "All formats should create same number of lines");
        }
    }
    
    /**
     * Test calculation of trip chain in a shift
     * When trips are connected, verify the chain is built correctly
     */
    @Test
    public void testVypocetRetazcaSpojoVZmene() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        Linka linka1 = linky.get(1);
        
        // Simulate connecting trips (this is done by optimization, but we can test the structure)
        Spoj spoj1 = linka1.getSpoje().get(0);
        Spoj spoj2 = linka1.getSpoje().get(1);
        
        // Verify trips have the connection methods
        assertNotNull(spoj1.getMozneNasledujuceSpoje());
        
        // If trip 2 is in possible next trips, it means they can be connected
        boolean canConnect = false;
        for (Spoj nasledujuci : spoj1.getMozneNasledujuceSpoje()) {
            if (nasledujuci.getID() == spoj2.getID()) {
                canConnect = true;
                break;
            }
        }
        
        if (canConnect) {
            // Connection is possible based on time and location constraints
            assertTrue(spoj1.getCasPrichoduVMinutach() <= spoj2.getCasOdchoduVMinutach(),
                      "Previous trip should end before next trip starts");
        }
    }
    
    /**
     * Test occupancy (obsadenosť) calculations for lines
     */
    @Test
    public void testVypocetObsadenostiLinky() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        
        // Line 1: trips with occupancy 60 + 70 = 130
        Linka linka1 = linky.get(1);
        assertEquals(130, linka1.getObsadenost());
        
        // Line 2: one trip with occupancy 60
        Linka linka2 = linky.get(2);
        assertEquals(60, linka2.getObsadenost());
    }
    
    /**
     * Test that time calculations use minutes correctly
     */
    @Test
    public void testPrepocetCasuNaMinuty() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        LinkedHashMap<Integer, Linka> linky = model.getLinky();
        Linka linka1 = linky.get(1);
        Spoj spoj1 = linka1.getSpoje().get(0);
        
        // 08:00 = 8*60 = 480 minutes
        assertEquals(480, spoj1.getCasOdchoduVMinutach());
        
        // 08:30 = 8*60 + 30 = 510 minutes
        assertEquals(510, spoj1.getCasPrichoduVMinutach());
    }
    
    /**
     * Test error handling when loading invalid data
     */
    @Test
    public void testChyboveSpravaniePreNeplatneUdaje() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        
        // Try to load non-existent file
        String result = model.nacitajSpojeUseky(
            "neexistujuci_subor.txt",
            "src/test/resources/test_spoje.txt"
        );
        
        assertEquals("Chyba pri načítavaní úsekov!", result);
    }
    
    /**
     * Test loading segments separately
     */
    @Test
    public void testOddeleneNacitanieUsekov() {
        model.nastavDataFormat(DataFormat.CSV);
        
        String result = model.nacitajUseky(new java.io.File("src/test/resources/test_useky.csv"));
        assertEquals("Načítanie úsekov bolo úspešné.", result);
        
        String[][] useky = model.getUdajeUseky();
        assertNotNull(useky);
        assertEquals(10, useky.length); // Changed from 3 to 10
    }
    
    /**
     * Test loading trips separately
     */
    @Test
    public void testOddeleneNacitanieSpojev() {
        model.nastavDataFormat(DataFormat.XML);
        
        String result = model.nacitajSpoje(new java.io.File("src/test/resources/test_spoje.xml"));
        assertEquals("Načítanie spojov bolo úspešné.", result);
        
        String[][] spoje = model.getUdajeSpoje();
        assertNotNull(spoje);
        assertEquals(3, spoje.length);
    }
    
    /**
     * Test that Model properly stores constants
     */
    @Test
    public void testNastaveniaKonstant() {
        String[] konstanty = new String[13];
        model.ziskajKonstanty(konstanty);
        
        assertEquals("59", konstanty[0]); // DEPO
        assertEquals("0", konstanty[1]);  // REZERVA_1
        assertEquals("0", konstanty[2]);  // REZERVA_2
        assertEquals("50", konstanty[3]); // C_VODIC
        assertEquals("2", konstanty[4]);  // C_KM
    }
    
    /**
     * Test changing constants
     */
    @Test
    public void testZmenaKonstant() {
        model.nastavDataFormat(DataFormat.PLAIN_TEXT);
        model.nacitajSpojeUseky(
            "test_useky.txt",
            "test_spoje.txt"
        );
        
        String result = model.nastavKonstanty(
            50, 5, 10, 60, 3, 
            11*60, 14*60, 
            20, 45, 
            40, 50, 
            5*60, 7*60
        );
        
        assertEquals("Zmeny boli uložené", result);
        
        // Verify constants were changed
        String[] konstanty = new String[13];
        model.ziskajKonstanty(konstanty);
        assertEquals("50", konstanty[0]); // DEPO changed to 50
        assertEquals("5", konstanty[1]);  // REZERVA_1 changed to 5
        assertEquals("60", konstanty[3]); // C_VODIC changed to 60
    }
}
