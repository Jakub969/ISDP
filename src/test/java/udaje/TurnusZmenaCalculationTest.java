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
        
        // Create test segments
        useky = new LinkedHashMap<>();
        // Depot connections
        useky.put(new Dvojica<>(59, 1), 20);  // Depot → location 1
        useky.put(new Dvojica<>(2, 59), 25);  // Location 2 → Depot
        useky.put(new Dvojica<>(3, 59), 30);  // Location 3 → Depot
        useky.put(new Dvojica<>(4, 59), 35);  // Location 4 → Depot
        
        // Between locations
        useky.put(new Dvojica<>(1, 2), 0);   // Location 1 → 2 (same location)
        useky.put(new Dvojica<>(2, 3), 15);  // Location 2 → 3
        useky.put(new Dvojica<>(3, 4), 10);  // Location 3 → 4
        
        // Create test trips
        // Trip 1: 08:00-08:15, location 1→2, obsadenosť 60
        spoj1 = new Spoj(1, 1, 101, 
                        1, LocalTime.of(8, 0), 
                        2, LocalTime.of(8, 15),
                        5.0, 60);
        
        // Trip 2: 08:30-09:00, location 2→3, obsadenosť 70
        spoj2 = new Spoj(2, 1, 102,
                        2, LocalTime.of(8, 30),
                        3, LocalTime.of(9, 0),
                        7.5, 70);
        
        // Trip 3: 09:15-09:45, location 3→4, obsadenosť 50
        spoj3 = new Spoj(3, 2, 201,
                        3, LocalTime.of(9, 15),
                        4, LocalTime.of(9, 45),
                        6.0, 50);
        
        // Create DT matrix (driving times between trips)
        DT = new LinkedHashMap<>();
        DT.put(new Dvojica<>(1, 2), 0);   // Trip 1 → 2: same location
        DT.put(new Dvojica<>(2, 3), 10);  // Trip 2 → 3: 15 min segment, but only 10 used
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
        // Start: 08:00 - 20 min (setup from depot) = 07:40
        // End: 08:15 + 25 min (teardown to depot) = 08:40
        // Duration: 08:40 - 07:40 = 60 minutes
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        int trvanie = zmena.getTrvanieZmeny();
        
        // 20 min setup + 15 min trip + 25 min teardown = 60 min
        assertEquals(60, trvanie);
    }
    
    /**
     * Test shift with connected trips
     */
    @Test
    public void testZmenaSViacSpojmi() {
        // Connect trip 1 → trip 2
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Start: 08:00 - 20 min = 07:40
        // End: 09:00 + 30 min = 09:30
        // Duration: 09:30 - 07:40 = 110 minutes
        int trvanie = zmena.getTrvanieZmeny();
        assertEquals(110, trvanie);
    }
    
    /**
     * Test driving time calculation (trvanie jazdy)
     * Includes setup, trips, connections, and teardown
     */
    @Test
    public void testVypocetTrvaniaJazdy() {
        // Single trip shift
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Setup (20) + Trip duration (15) + Teardown (25) = 60
        int trvanieJazdy = zmena.getTrvanieJazdy(DT);
        assertEquals(60, trvanieJazdy);
    }
    
    /**
     * Test driving time with multiple connected trips
     */
    @Test
    public void testTrvaniaJazdySViacSpojmi() {
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Setup (20) + Trip1 (15) + Connection (0 from DT) + Trip2 (30) + Teardown (30) = 95
        int trvanieJazdy = zmena.getTrvanieJazdy(DT);
        assertEquals(95, trvanieJazdy);
    }
    
    /**
     * Test empty runs calculation (prázdne prejazdy)
     * Empty runs are the segments between trip arrivals and next trip departures
     */
    @Test
    public void testVypocetPrazdnychPrejazdov() {
        // Connect trip 1 → trip 2
        // Trip 1 arrives at location 2, Trip 2 departs from location 2
        // Segment 2→2 doesn't exist, so empty run should be 0 from the map
        spoj1.setNasledujuciSpoj(spoj2);
        
        Zmena zmena = new Zmena(1, 1, spoj1, useky);
        
        // Empty run is the segment between arrival and departure locations
        // Location 2 → Location 2 = useky.get((2,2)) 
        // Since we didn't define (2,2), it would be 0 or cause error
        // The real calculation uses actual segments defined in useky
        
        // With our data: trip1 ends at 2, trip2 starts at 2 → segment (2,2)
        // We need to add this segment
        useky.put(new Dvojica<>(2, 2), 0);
        
        Zmena zmena2 = new Zmena(1, 1, spoj1, useky);
        // This test verifies the calculation doesn't crash
        assertNotNull(zmena2);
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
        assertEquals("1:00", udaje[2]); // Duration in HH:MM format (60 minutes)
    }
    
    /**
     * Test Turnus with two shifts - total duration
     */
    @Test
    public void testTrvanieTurnusuSDvomaZmenami() {
        Zmena zmena1 = new Zmena(1, 1, spoj1, useky);  // 60 minutes
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
        
        // End time: 08:15 + 25 min = 08:40
        assertEquals("08:40", udaje[3]);
        
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
        // Trip 1: 08:00 - 08:15 = 15 minutes
        assertEquals(15, spoj1.getTrvanieSpoja());
        
        // Trip 2: 08:30 - 09:00 = 30 minutes
        assertEquals(30, spoj2.getTrvanieSpoja());
        
        // Trip 3: 09:15 - 09:45 = 30 minutes
        assertEquals(30, spoj3.getTrvanieSpoja());
    }
    
    /**
     * Test time conversion to minutes
     */
    @Test
    public void testKonverziaCasuNaMinuty() {
        // 08:00 = 480 minutes
        assertEquals(480, spoj1.getCasOdchoduVMinutach());
        
        // 08:15 = 495 minutes
        assertEquals(495, spoj1.getCasPrichoduVMinutach());
        
        // 09:00 = 540 minutes
        assertEquals(540, spoj2.getCasPrichoduVMinutach());
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
        // End: 09:45 + 35 min = 10:20
        // Duration: 160 minutes
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
