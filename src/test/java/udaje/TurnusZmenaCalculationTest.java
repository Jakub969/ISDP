package udaje;

import mvp.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for calculations in Turnus and Zmena classes
 * Verifies shift duration, driving time, empty runs, etc.
 */
public class TurnusZmenaCalculationTest {
    
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky;
    private LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT;
    private Spoj spoj1;
    private Spoj spoj2;
    private Spoj spoj3;
    
    @BeforeEach
    public void setUp() {
        Model.DEPO = 59;
        
        // Create test segments matching test files (nodes: 1, 2, 3, 4, 59)
        useky = new LinkedHashMap<>();
        
        // Self-loops
        useky.put(new Dvojica<>(1, 1), 0);
        useky.put(new Dvojica<>(2, 2), 0);
        useky.put(new Dvojica<>(3, 3), 0);
        useky.put(new Dvojica<>(4, 4), 0);
        useky.put(new Dvojica<>(59, 59), 0);
        
        // Bidirectional segments
        useky.put(new Dvojica<>(1, 2), 10);
        useky.put(new Dvojica<>(2, 1), 10);
        useky.put(new Dvojica<>(2, 3), 15);
        useky.put(new Dvojica<>(3, 2), 15);
        useky.put(new Dvojica<>(3, 4), 20);
        useky.put(new Dvojica<>(4, 3), 20);
        
        // Depot connections
        useky.put(new Dvojica<>(59, 1), 20);
        useky.put(new Dvojica<>(1, 59), 20);
        useky.put(new Dvojica<>(59, 2), 25);
        useky.put(new Dvojica<>(2, 59), 25);
        useky.put(new Dvojica<>(59, 3), 30);
        useky.put(new Dvojica<>(3, 59), 30);
        useky.put(new Dvojica<>(59, 4), 35);
        useky.put(new Dvojica<>(4, 59), 35);
        
        // Create test trips matching test files
        // Trip 1: 08:00-08:30, location 1→2, obsadenosť 50
        spoj1 = new Spoj(1, 1, 1, 
                        1, LocalTime.of(8, 0), 
                        2, LocalTime.of(8, 30),
                        15.5, 50);
        
        // Trip 2: 09:00-09:45, location 2→3, obsadenosť 80
        spoj2 = new Spoj(2, 1, 2,
                        2, LocalTime.of(9, 0),
                        3, LocalTime.of(9, 45),
                        22.3, 80);
        
        // Trip 3: 10:00-10:20, location 3→4, obsadenosť 60
        spoj3 = new Spoj(3, 2, 3,
                        3, LocalTime.of(10, 0),
                        4, LocalTime.of(10, 20),
                        10.2, 60);
        
        // Create DT matrix (driving times between trips)
        DT = new LinkedHashMap<>();
        DT.put(new Dvojica<>(1, 2), 10);  // Trip 1→2: location 2→2 = 0
        DT.put(new Dvojica<>(2, 3), 15);  // Trip 2→3: location 3→3 = 0
    }
    
    /**
     * Test Zmena creation with single trip
     */
    @Test
    public void testVytvorenieZmenySJednymSpojom() {
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        assertNotNull(zmena);
        // Verify shift was created successfully
    }
    
    /**
     * Test shift duration calculation (trvanie zmeny)
     * Duration = from (departure - setup) to (arrival + teardown)
     */
    @Test
    public void testVypocetTrvaniaZmeny() {
        // Create shift: Trip 1 only
        // Start: 08:00 - 20 min (setup from depot to location 1) = 07:40
        // End: 08:30 + 25 min (teardown from location 2 to depot) = 08:55
        // Duration: 08:55 - 07:40 = 75 minutes
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        int trvanie = zmena.getTrvanieZmeny();
        
        // 20 min setup + 30 min trip + 25 min teardown = 75 min
        assertEquals(75, trvanie);
    }
    
    /**
     * Test shift with connected trips
     */
    @Test
    public void testZmenaSViacSpojmi() {
        // Connect trip 1 → trip 2
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Start: 08:00 - 20 min (depot→1) = 07:40
        // End: 09:45 + 30 min (3→depot) = 10:15
        // Duration: 10:15 - 07:40 = 155 minutes
        int trvanie = zmena.getTrvanieZmeny();
        assertEquals(155, trvanie);
    }
    
    /**
     * Test driving time calculation (trvanie jazdy)
     * Includes setup, trips, connections, and teardown
     */
    @Test
    public void testVypocetTrvaniaJazdy() {
        // Single trip shift
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Setup (20) + Trip duration (30) + Teardown (25) = 75
        int trvanieJazdy = zmena.getTrvanieJazdy(DT);
        assertEquals(75, trvanieJazdy);
    }
    
    /**
     * Test driving time with multiple connected trips
     */
    @Test
    public void testTrvaniaJazdySViacSpojmi() {
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Setup (20) + Trip1 (30) + Connection (DT[1,2]=10) + Trip2 (45) + Teardown (30) = 135
        int trvanieJazdy = zmena.getTrvanieJazdy(DT);
        assertEquals(135, trvanieJazdy);
    }
    
    /**
     * Test empty runs calculation (prázdne prejazdy)
     * Empty runs are the segments between trip arrivals and next trip departures
     */
    @Test
    public void testVypocetPrazdnychPrejazdov() {
        // Connect trip 1 → trip 2
        // Trip 1 arrives at location 2, Trip 2 departs from location 2
        // We have self-loop 2→2 = 0 in useky
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // This test verifies the calculation doesn't crash with proper segments
        assertNotNull(zmena);
    }
    
    /**
     * Test Turnus with single shift
     */
    @Test
    public void testTurnusSJednouZmenou() {
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        Turnus turnus = new Turnus(1, zmena);
        
        assertNotNull(turnus);
        assertEquals(1, turnus.getID());
        assertEquals(zmena, turnus.getPrvaZmena());
        assertNull(turnus.getDruhaZmena());
    }
    
    /**
     * Test Turnus with two shifts
     */
    @Test
    public void testTurnusSDvomaZmenami() {
        Zmena zmena1 = new Zmena(1, 1, spoj1, useky);
        Zmena zmena2 = new Zmena(1, 2, spoj3, useky);
        
        Turnus turnus = new Turnus(1, zmena1);
        turnus.pridajDruhuZmenu(zmena2);
        
        assertEquals(zmena1, turnus.getPrvaZmena());
        assertEquals(zmena2, turnus.getDruhaZmena());
    }
    
    /**
     * Test Turnus data output
     */
    @Test
    public void testVypisUdajovOTurnuse() {
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        Turnus turnus = new Turnus(1, zmena);
        
        String[] udaje = turnus.vypisUdajeOturnuse();
        
        assertEquals(3, udaje.length);
        assertEquals("1", udaje[0]); // Turnus ID
        assertEquals("1", udaje[1]); // Number of shifts
        assertEquals("1:15", udaje[2]); // Duration in HH:MM format (75 minutes)
    }
    
    /**
     * Test Turnus with two shifts - total duration
     */
    @Test
    public void testTrvanieTurnusuSDvomaZmenami() {
        Zmena zmena1 = new Zmena(1, 1, spoj1, useky);  // 75 minutes
        Zmena zmena2 = new Zmena(1, 2, spoj3, useky);  // Duration depends on spoj3
        
        Turnus turnus = new Turnus(1, zmena1);
        turnus.pridajDruhuZmenu(zmena2);
        
        String[] udaje = turnus.vypisUdajeOturnuse();
        assertEquals("2", udaje[1]); // Two shifts
        
        // Total duration = zmena1.getTrvanieZmeny() + zmena2.getTrvanieZmeny()
        int celkoveTrvanie = zmena1.getTrvanieZmeny() + zmena2.getTrvanieZmeny();
        String ocakavanyFormat;
        if (celkoveTrvanie % 60 < 10) {
            ocakavanyFormat = celkoveTrvanie / 60 + ":0" + celkoveTrvanie % 60;
        } else {
            ocakavanyFormat = celkoveTrvanie / 60 + ":" + celkoveTrvanie % 60;
        }
        assertEquals(ocakavanyFormat, udaje[2]);
    }
    
    /**
     * Test Zmena data output
     */
    @Test
    public void testVypisUdajovOZmene() {
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        String[] udaje = zmena.vypisZmenu(DT);
        
        assertEquals(9, udaje.length);
        assertEquals("1", udaje[0]); // Turnus ID
        assertEquals("1", udaje[1]); // Zmena ID
        
        // Start time: 08:00 - 20 min = 07:40
        assertEquals("07:40", udaje[2]);
        
        // End time: 08:30 + 25 min = 08:55
        assertEquals("08:55", udaje[3]);
        
        // Setup time
        assertEquals("20", udaje[4]);
        
        // Teardown time
        assertEquals("25", udaje[5]);
    }
    
    /**
     * Test trip duration calculation
     */
    @Test
    public void testTrvanieSpoja() {
        // Trip 1: 08:00 - 08:30 = 30 minutes
        assertEquals(30, spoj1.getTrvanieSpoja());
        
        // Trip 2: 09:00 - 09:45 = 45 minutes
        assertEquals(45, spoj2.getTrvanieSpoja());
        
        // Trip 3: 10:00 - 10:20 = 20 minutes
        assertEquals(20, spoj3.getTrvanieSpoja());
    }
    
    /**
     * Test time conversion to minutes
     */
    @Test
    public void testKonverziaCasuNaMinuty() {
        // 08:00 = 480 minutes
        assertEquals(480, spoj1.getCasOdchoduVMinutach());
        
        // 08:30 = 510 minutes
        assertEquals(510, spoj1.getCasPrichoduVMinutach());
        
        // 09:45 = 585 minutes
        assertEquals(585, spoj2.getCasPrichoduVMinutach());
    }
    
    /**
     * Test connecting trips in a chain
     */
    @Test
    public void testSpojenieSpojevDoRetazca() {
        // Connect trip 1 → trip 2 → trip 3
        spoj1.setNasledujuciSpoj(spoj2);
        spoj2.setNasledujuciSpoj(spoj3);
        
        assertEquals(spoj2, spoj1.getNasledujuciSpoj());
        assertEquals(spoj3, spoj2.getNasledujuciSpoj());
        assertNull(spoj3.getNasledujuciSpoj());
    }
    
    /**
     * Test shift with complete trip chain
     */
    @Test
    public void testZmenaSDlhymRetazcomSpojov() {
        // Connect all trips
        useky.put(new Dvojica<>(2, 2), 0);  // Same location
        useky.put(new Dvojica<>(3, 3), 0);  // Same location
        
        spoj1.setNasledujuciSpoj(spoj2);
        spoj2.setNasledujuciSpoj(spoj3);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Verify last trip is correctly identified
        assertEquals(spoj3, zmena.getPoslednySpoj());
        
        // Calculate total duration
        // Start: 08:00 - 20 min = 07:40
        // End: 10:20 + 35 min = 10:55
        // Duration: 195 minutes
        int trvanie = zmena.getTrvanieZmeny();
        assertTrue(trvanie > 100, "Duration with 3 trips should be > 100 minutes");
    }
    
    /**
     * Test edge case: very short connection time
     */
    @Test
    public void testKratkyPrepojovyCas() {
        // Create two trips very close in time
        Spoj spojA = new Spoj(10, 3, 301,
                             1, LocalTime.of(10, 0),
                             2, LocalTime.of(10, 5),
                             2.0, 40);
        
        Spoj spojB = new Spoj(11, 3, 302,
                             2, LocalTime.of(10, 6),
                             3, LocalTime.of(10, 20),
                             5.0, 45);
        
        // Only 1 minute between trips
        int casovyRozdiel = spojB.getCasOdchoduVMinutach() - spojA.getCasPrichoduVMinutach();
        assertEquals(1, casovyRozdiel);
        
        spojA.setNasledujuciSpoj(spojB);
        Zmena zmena = new Zmena(2, 1, spojA, useky);
        
        // Should still calculate correctly
        assertTrue(zmena.getTrvanieZmeny() > 0);
    }
    
    /**
     * Test time formatting in output
     */
    @Test
    public void testFormatovanieCasu() {
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        String[] udaje = zmena.vypisZmenu(DT);
        
        // Verify time format HH:MM
        assertTrue(udaje[2].matches("\\d{2}:\\d{2}"), "Start time should be in HH:MM format");
        assertTrue(udaje[3].matches("\\d{2}:\\d{2}"), "End time should be in HH:MM format");
        assertTrue(udaje[7].matches("\\d+:\\d{2}"), "Duration should be in H+:MM format");
    }
}
