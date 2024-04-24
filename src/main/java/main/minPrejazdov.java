package main;

import com.gurobi.gurobi.*;
import dataOld.DvojicaOld;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class minPrejazdov {
    public static void runModel1() throws GRBException {
        try {
            HashMap<DvojicaOld<Integer, Integer>, Integer> distances = loadDistances("dist.txt");
            Map<Integer, Integer[]> trips = loadTrips("trips.txt");
            System.out.println("Trips: " + trips.size());

            long startTime = System.nanoTime();
            Map<DvojicaOld<Integer, Integer>, GRBVar> x = new TreeMap<>();
            GRBModel model = createModel(trips, distances, x);

            long finishTime = System.nanoTime();
            System.out.println("Model vytvoreny za " + TimeUnit.NANOSECONDS.toSeconds(finishTime - startTime) + " sekund.");

            startTime = finishTime;
            model.optimize();

            int iteracia = 1;
            List<List<Integer[]>>  rotations;
            while (true) {
                finishTime = System.nanoTime();
                System.out.println("Iteracia " + iteracia + ", model vypocitany za " + TimeUnit.NANOSECONDS.toSeconds(finishTime - startTime) + " sekund.");

                startTime = finishTime;
                rotations = getResult(trips, distances, model, x);

                ArrayList<Integer[]> spoje = new ArrayList<>();
                for (int k = 0; k < rotations.size(); k++) {
                    for (ArrayList<Integer[]> ss : kontrolujTurnus(k, rotations.get(k), distances)) {
                        for (Integer[] s : ss) {
                            spoje.add(s);
                        }
                    }
                }

                if (spoje.isEmpty()) {
                    System.out.println("Iteracia: " + iteracia + ", HOTOVO ");
                    break;
                } else {
                    System.out.println("Iteracia: " + iteracia + ", pocet zistenych poruseni BP: " + spoje.size());
                    System.out.println("Porusenia BP:");
                    for (Integer[] ss : spoje) {
                        printTrip(ss);
                        System.out.println();
                    }

                    for (Integer[] ss : spoje) {
                        GRBLinExpr expr = new GRBLinExpr();
                        for (int i = 0; i < ss.length - 1; i++) {
                            expr.addTerm(1.0, x.get(new DvojicaOld<>(ss[i], ss[i+1])));
                        }
                        model.addConstr(expr, GRB.LESS_EQUAL, ss.length - 2, "");
                    }

                    iteracia++;
                    model.update();

                    finishTime = System.nanoTime();
                    System.out.println("Iteracia " + iteracia + ", model upraveny za " + TimeUnit.NANOSECONDS.toSeconds(finishTime - startTime) + " sekund.");

                    startTime = finishTime;
                    model.optimize();
                }
            }

            printTurnusy(rotations, distances);

            for (int k = 0; k < rotations.size(); k++) {
                printTurnus(k, rotations.get(k), distances);
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public static Map<String, Integer> loadDistances(String fileName) {
        Map<String, Integer> distances = new HashMap<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");
                int u = Integer.parseInt(cols[0]);
                int v = Integer.parseInt(cols[1]);
                int c = Integer.parseInt(cols[2]);
                distances.put(u + ";" + v, c);
                distances.put(v + ";" + u, c);
                distances.put(u + ";" + u, 0);
                distances.put(v + ";" + v, 0);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return distances;
    }
    public static Map<Integer, int[]> loadTrips(String fileName) {
        // Create an empty HashMap to store trips
        Map<Integer, int[]> trips = new TreeMap<>();

        try {
            // Open the file for reading
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            // Read each line in the file
            while (scanner.hasNextLine()) {
                // Split the line into columns
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");

                // Extract trip information
                int lineNumber = Integer.parseInt(cols[0]);
                int tripNumber = Integer.parseInt(cols[1]);
                // Unique ID for the trip
                int id = 10000 * lineNumber + tripNumber;
                int depStop = Integer.parseInt(cols[2]);
                // Parse departure time
                String depTime = cols[3];
                int arrStop = Integer.parseInt(cols[4]);
                // Parse arrival time
                String arrTime = cols[5];

                // Initialize previous and successor trip IDs to null
                int prev = -1;
                int succ = -1;

                // Create an array containing trip information
                int[] tripData = {id, lineNumber, tripNumber, depStop, parseTime(depTime), arrStop, parseTime(arrTime), prev, succ};

                // Add trip to the trips HashMap with ID as key
                trips.put(id, tripData);
            }
            // Close the file
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Return the HashMap containing trips
        return trips;
    }
    public static int parseTime(String time) {
        // Rozdělit řetězec podle dvojtečky
        String[] parts = time.split(":");

        // Získání hodin a minut jako celých čísel
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // Převod na počet minut od půlnoci
        int totalMinutes = hours * 60 + minutes;

        return totalMinutes;
    }
    public static GRBModel createModel(Map<Integer, int[]> trips, Map<String, Integer> distances, Map<String, GRBVar> x) throws GRBException {
        // Inicializace modelu
        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env);

        Map<String, Integer> cx = new TreeMap<>();

        int count = 0;
        int count2 = 0;
        // Procházení všech dvojic výletů
        for (Integer i : trips.keySet()) {
            for (Integer j : trips.keySet()) {
                int[] trip_i = trips.get(i);
                int arr_stop_i = trip_i[5];
                int arr_time_i = trip_i[6];
                int[] trip_j = trips.get(j);
                int dep_stop_j = trip_j[3];
                int dep_time_j = trip_j[4];
                int dist = distances.get(arr_stop_i + ";" + dep_stop_j);

                // Podmínka pro spojení výletů
                if (arr_time_i + dist <= dep_time_j) {
                    count++;
                    // Přidání binární proměnné do modelu pro spojení výletů i a j
                    x.put(i + "_" + j, model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                    // Přidání vzdálenosti mezi zastávkami do slovníku cx
                    cx.put(i + "_" + j, dist);
                }
                count2++;
            }
        }

        // Inicializace proměnných pro spoje na začátku výletu a jejich vzdálenosti
        Map<Integer, GRBVar> u = new TreeMap<>();
        Map<Integer, Integer> cu = new TreeMap<>();
        for (Integer j : trips.keySet()) {
            int[] trip_j = trips.get(j);
            int dep_stop_j = trip_j[3];
            int dist = distances.get(59 + ";" + dep_stop_j);
            // Přidání binární proměnné do modelu pro spojení na začátku výletu
            GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "u" + j);
            u.put(j, var);
            // Přidání vzdálenosti od začátku do slovníku cu
            cu.put(j, dist);
        }

        // Inicializace proměnných pro spoje na konci výletu a jejich vzdálenosti
        Map<Integer, GRBVar> v = new TreeMap<>();
        Map<Integer, Integer> cv = new TreeMap<>();
        for (Integer i : trips.keySet()) {
            int[] trip_i = trips.get(i);
            int arr_stop_i = trip_i[5];
            int dist = distances.get(arr_stop_i + ";59");
            // Přidání binární proměnné do modelu pro spojení na konci výletu
            GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "v" + i);
            v.put(i, var);
            // Přidání vzdálenosti od konce do slovníku cv
            cv.put(i, dist);
        }

        // Aktualizace modelu
        model.update();

        // Vytvoření cílové funkce modelu
        GRBLinExpr expr = new GRBLinExpr();
        for (String pair : x.keySet()) {
            expr.addTerm(cx.get(pair), x.get(pair));
        }
        for (Integer j : u.keySet()) {
            expr.addTerm(cu.get(j), u.get(j));
        }
        for (Integer i : v.keySet()) {
            expr.addTerm(cv.get(i), v.get(i));
        }
        model.setObjective(expr, GRB.MINIMIZE);

        // Přidání omezení pro spojení na začátku výletu
        for (Integer k : trips.keySet()) {
            expr = new GRBLinExpr();
            expr.addTerm(1.0, u.get(k));
            for (String pair : x.keySet()) {
                String[] indices = pair.split("_");
                int j = Integer.parseInt(indices[1]);
                if (j == k) {
                    expr.addTerm(1.0, x.get(pair));
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "start_" + k);
        }

        // Přidání omezení pro spojení na konci výletu
        for (Integer k : trips.keySet()) {
            expr = new GRBLinExpr();
            expr.addTerm(1.0, v.get(k));
            for (String pair : x.keySet()) {
                String[] indices = pair.split("_");
                int i = Integer.parseInt(indices[1]);
                if (i == k) {
                    expr.addTerm(1.0, x.get(pair));
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "end_" + k);
        }

        // Přidání omezení pro celkový počet spojů
        expr = new GRBLinExpr();
        for (String pair : x.keySet()) {
            expr.addTerm(1.0, x.get(pair));
        }
        model.addConstr(expr, GRB.EQUAL, trips.size() - 39, "total_connections");

        // Aktualizace modelu
        model.update();

        // Návrat modelu
        return model;
    }
     */
    public static HashMap<DvojicaOld<Integer, Integer>, Integer> loadDistances(String fileName) {
        HashMap<DvojicaOld<Integer, Integer>, Integer> distances = new HashMap<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");
                int u = Integer.parseInt(cols[0]);
                int v = Integer.parseInt(cols[1]);
                int c = Integer.parseInt(cols[2]);
                distances.put(new DvojicaOld<>(u,v), c);
                distances.put(new DvojicaOld<>(v,u), c);
                distances.put(new DvojicaOld<>(u,u), 0);
                distances.put(new DvojicaOld<>(v,v), 0);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return distances;
    }
    public static Map<Integer, Integer[]> loadTrips(String fileName) {
        // Create an empty HashMap to store trips
        Map<Integer, Integer[]> trips = new TreeMap<>();

        try {
            // Open the file for reading
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            // Read each line in the file
            while (scanner.hasNextLine()) {
                // Split the line into columns
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");

                // Extract trip information
                int lineNumber = Integer.parseInt(cols[0]);
                int tripNumber = Integer.parseInt(cols[1]);
                // Unique ID for the trip
                int id = 10000 * lineNumber + tripNumber;
                int depStop = Integer.parseInt(cols[2]);
                // Parse departure time
                String depTime = cols[3];
                int arrStop = Integer.parseInt(cols[4]);
                // Parse arrival time
                String arrTime = cols[5];

                // Initialize previous and successor trip IDs to null
                int prev = -1;
                int succ = -1;

                int prejazd_pred = 0;
                int prejazd_po = 0;
                int statie_po = 0;

                // Create an array containing trip information
                Integer[] tripData = {id, lineNumber, tripNumber, depStop, parseTime(depTime), arrStop, parseTime(arrTime), prev, succ,
                                    prejazd_pred, prejazd_po, statie_po};

                // Add trip to the trips HashMap with ID as key
                trips.put(id, tripData);
            }
            // Close the file
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Return the HashMap containing trips
        return trips;
    }
    public static int parseTime(String time) {
        // Rozdělit řetězec podle dvojtečky
        String[] parts = time.split(":");

        // Získání hodin a minut jako celých čísel
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // Převod na počet minut od půlnoci
        int totalMinutes = hours * 60 + minutes;

        return totalMinutes;
    }
    public static GRBModel createModel(Map<Integer, Integer[]> trips, Map<DvojicaOld<Integer, Integer>, Integer> distances, Map<DvojicaOld<Integer, Integer>, GRBVar> x) throws GRBException {
        // Inicializace modelu
        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env);

        Map<DvojicaOld<Integer, Integer>, Integer> cx = new TreeMap<>();

        // Procházení všech dvojic výletů
        for (Integer i : trips.keySet()) {
            for (Integer j : trips.keySet()) {
                Integer[] trip_i = trips.get(i);
                int arr_stop_i = trip_i[5];
                int arr_time_i = trip_i[6];
                Integer[] trip_j = trips.get(j);
                int dep_stop_j = trip_j[3];
                int dep_time_j = trip_j[4];
                int dist = distances.get(new DvojicaOld<>(arr_stop_i, dep_stop_j));

                // Podmínka pro spojení výletů
                if (arr_time_i + dist <= dep_time_j) {
                    // Přidání binární proměnné do modelu pro spojení výletů i a j
                    x.put(new DvojicaOld<>(i,j), model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                    // Přidání vzdálenosti mezi zastávkami do slovníku cx
                    cx.put(new DvojicaOld<>(i,j), dist);
                }
            }
        }

        // Inicializace proměnných pro spoje na začátku výletu a jejich vzdálenosti
        Map<Integer, GRBVar> u = new TreeMap<>();
        Map<Integer, Integer> cu = new TreeMap<>();
        for (Integer j : trips.keySet()) {
            Integer[] trip_j = trips.get(j);
            int dep_stop_j = trip_j[3];
            int dist = distances.get(new DvojicaOld<>(59,dep_stop_j));
            // Přidání binární proměnné do modelu pro spojení na začátku výletu
            GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "u" + j);
            u.put(j, var);
            // Přidání vzdálenosti od začátku do slovníku cu
            cu.put(j, dist);
        }

        // Inicializace proměnných pro spoje na konci výletu a jejich vzdálenosti
        Map<Integer, GRBVar> v = new TreeMap<>();
        Map<Integer, Integer> cv = new TreeMap<>();
        for (Integer i : trips.keySet()) {
            Integer[] trip_i = trips.get(i);
            int arr_stop_i = trip_i[5];
            int dist = distances.get(new DvojicaOld<>(arr_stop_i,59));
            // Přidání binární proměnné do modelu pro spojení na konci výletu
            GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "v" + i);
            v.put(i, var);
            // Přidání vzdálenosti od konce do slovníku cv
            cv.put(i, dist);
        }

        // Aktualizace modelu
        model.update();

        // Vytvoření cílové funkce modelu
        GRBLinExpr expr = new GRBLinExpr();
        for (DvojicaOld dvojica : x.keySet()) {
            expr.addTerm(cx.get(dvojica), x.get(dvojica));
        }
        for (Integer j : u.keySet()) {
            expr.addTerm(cu.get(j), u.get(j));
        }
        for (Integer i : v.keySet()) {
            expr.addTerm(cv.get(i), v.get(i));
        }
        model.setObjective(expr, GRB.MINIMIZE);

        // Přidání omezení pro spojení na začátku výletu
        for (Integer k : trips.keySet()) {
            expr = new GRBLinExpr();
            expr.addTerm(1.0, u.get(k));
            for (DvojicaOld<Integer, Integer> dvojica : x.keySet()) {
                int j = dvojica.druha();
                if (j == k) {
                    expr.addTerm(1.0, x.get(dvojica));
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "start_" + k);
        }

        // Přidání omezení pro spojení na konci výletu
        for (Integer k : trips.keySet()) {
            expr = new GRBLinExpr();
            expr.addTerm(1.0, v.get(k));
            for (DvojicaOld<Integer, Integer> dvojica : x.keySet()) {
                int i = dvojica.prva();
                if (i == k) {
                    expr.addTerm(1.0, x.get(dvojica));
                }
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "end_" + k);
        }

        // Přidání omezení pro celkový počet spojů
        expr = new GRBLinExpr();
        for (DvojicaOld<Integer, Integer> dvojica : x.keySet()) {
            expr.addTerm(1.0, x.get(dvojica));
        }
        model.addConstr(expr, GRB.EQUAL, trips.size() - 39, "total_connections");

        // Aktualizace modelu
        model.update();

        // Návrat modelu
        return model;
    }
    public static List<List<Integer[]>> getResult(Map<Integer, Integer[]> trips, Map<DvojicaOld<Integer, Integer>, Integer> distances, GRBModel m, Map<DvojicaOld<Integer, Integer>, GRBVar> x) {
        // Reset previous and successor trip IDs for all trips
        for (int i : trips.keySet()) {
            trips.get(i)[7] = -1;  // Previous trip ID
            trips.get(i)[8] = -1;  // Successor trip ID
        }

        // Determine previous and successor trip IDs for selected pairs
        for (DvojicaOld<Integer, Integer> x_i : x.keySet()) {
            int i = x_i.prva();  // Obsahuje "13"
            int j = x_i.druha(); // Obsahuje "56"

            try {
                if (x.get(x_i).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia x (0 či 1)
                    // If the pair (i, j) is selected, set j as the successor of i and i as the previous of j
                    trips.get(i)[8] = j;  // Set j as the successor of i
                    trips.get(j)[7] = i;  // Set i as the previous of j
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        // Find the head trips (trips with no previous trip)
        List<List<Integer[]>> rotations = new ArrayList<>();
        List<Integer> heads = new ArrayList<>();
        for (int i : trips.keySet()) {
            if (trips.get(i)[7] == -1) {
                heads.add(i);
            }
        }

        for (int i : heads) {
            List<Integer[]> rotation = new ArrayList<>();
            while (i != -1) {
                rotation.add(trips.get(i));  // Přidání spoje do rotace
                i = trips.get(i)[8];  // Následující spoj
            }
            rotations.add(rotation);
        }

        return rotations;
    }
    public static void printTurnusy(List<List<Integer[]>> rotations, Map<DvojicaOld<Integer, Integer>, Integer> distances) {
        // Výpis hlavičky tabulky
        System.out.println("Tur\tZac\tKon\tPrist\tOdst\tPrej");

        // Inicializace proměnných pro celkové přejezdy a přestávky na přistavení a odstavení
        int total_pristav = 0;
        int total_odstav = 0;
        int total_prejazd = 0;

        // Pro každou rotaci
        for (int k = 0; k < rotations.size(); k++) {
            List<Integer[]> rot = rotations.get(k);

            // Výpočet přestávky na přistavení a odstavení
            int pristav = distances.get(new DvojicaOld<>(59,rot.get(0)[3]));
            int odstav = distances.get(new DvojicaOld<>(rot.get(rot.size() - 1)[5],59));

            // Výpočet celkové délky přejezdů v rotaci
            int prejazd = 0;
            for (int i = 0; i < rot.size() - 1; i++) {
                int u = rot.get(i)[5];
                int v = rot.get(i + 1)[3];
                prejazd += distances.get(new DvojicaOld<>(u,v));
            }

            // Výpočet začátku a konce turnusu
            int zaciatok = rot.get(0)[4] - pristav;
            int koniec = rot.get(rot.size() - 1)[6] + odstav;

            // Výpis informací o turnusu
            System.out.println((k + 1) + "\t" + strTime(zaciatok) + "\t" + strTime(koniec) + "\t" + pristav + "\t" + odstav + "\t" + prejazd);

            // Aktualizace celkových přejezdů a přestávek
            total_pristav += pristav;
            total_odstav += odstav;
            total_prejazd += prejazd;
        }

        // Výpis celkových přejezdů a přestávek
        System.out.println("\t\t\t" + total_pristav + "\t" + total_odstav + "\t" + total_prejazd);
        System.out.println();
    }
    public static String strTime(int time) {
        int h = time / 60;
        int m = time % 60;
        return String.format("%02d:%02d", h, m);
    }
    public static ArrayList<ArrayList<Integer[]>> kontrolujTurnus(int tur, List<Integer[]> rotation, HashMap<DvojicaOld<Integer, Integer>, Integer> distances) {
        // Inicializace seznamu spojů
        ArrayList<ArrayList<Integer[]>> spoje = new ArrayList<>();

        //Výpočet přestávek na přistavení a odstavení na začátku a konci turnusu
        int pristav = distances.get(new DvojicaOld<>(59, rotation.get(0)[3]));
        rotation.get(0)[9] = pristav;   //čas na přistavení na začátku turnusu

        int odstav = distances.get(new DvojicaOld<>(rotation.get(rotation.size() - 1)[5], 59));
        rotation.get(rotation.size() - 1)[10] = odstav;     //čas na odstavení na konci turnusu

        // Výpočet celkové délky přejezdu a začátku a konce turnusu
        int zaciatok = rotation.get(0)[4] - pristav;
        int koniec = rotation.get(rotation.size() - 1)[6] + odstav;

        //Výpočet přejezdů mezi jednotlivými spoji v rotaci
        int prejazd_spolu = 0;
        for (int i = 0; i < rotation.size() - 1; i++) {
            int u = rotation.get(i)[5];
            int v = rotation.get(i + 1)[3];
            int prejazd = distances.get(new DvojicaOld<>(u,v));
            rotation.get(i)[10] = prejazd;  //Délka přejezdu mezi spoji
            rotation.get(i)[11] = rotation.get(i + 1)[4] - rotation.get(i)[6] - prejazd;    //Přestávka mezi spoji
            prejazd_spolu += prejazd;       //Celková délka přejezdů
        }

        //Výpočet začátku a konce turnusu podle přestávek na přistavení a odstavení
        zaciatok = rotation.get(0)[4] - rotation.get(0)[9];
        koniec = rotation.get(rotation.size() - 1)[6] + rotation.get(rotation.size() - 1)[10];

        //Vytvoření seznamu nepretržitých jízd v rámci turnusu (prestávky kratšie ako 10 minút)
        ArrayList<Integer[]> jazda = new ArrayList<>();
        int i = 0;
        while (i < rotation.size()) {
            int z = rotation.get(i)[4] - rotation.get(i)[9];    //Začátek spoja
            int k;
            while (true) {
                k = rotation.get(i)[6] + rotation.get(i)[10];   //Konec spoja - čas príchodu spoja i + čas prejazdu po spoji i
                if (i == rotation.size() - 1) {     //ak je i posledný spoj v turnuse
                    break;
                }
                int p = rotation.get(i + 1)[4] - rotation.get(i + 1)[9] - k;    //Délka přestávky mezi spoji
                if (p >= 10) {  //Pokud je přestávka delší než 10 minut, ukončíme jízdu
                    break;
                }
                i++;
            }
            Integer[] interval = {z, k};    //Přidání intervalu jízdy do seznamu jízd
            jazda.add(interval);
            i++;
        }

        //Kontrola jízd v rámci turnusu a přidání případných porušení BP do seznamu spojů
        for (int j = 0; j < jazda.size(); j++) {
            int z = jazda.get(j)[0];    //Začátek jízdy
            int k = z + 270;        //Konec jízdy (270 minut)
            int jaz = spocitajJazdu(jazda, z, k);   //Vypočítání délky jízdy
            if (jaz > 240) {        //Pokud je délka jízdy delší než 240 minut, jedná se o porušení BP
                System.out.println("Porusenie BP v turnuse " + (tur + 1) + " v case od " + strTime(z) + " do " + strTime(k) + ", jazda = " + jaz + "!");
                ArrayList<Integer[]> sp = dajSpojeBP(rotation, z, k);      //Získání spojů, které porušují BP
                if (spoje.isEmpty() || !spoje.get(spoje.size() - 1).equals(sp)) {
                    spoje.add(sp);      // Přidání spojů do seznamu spojů
                }
            }
            k = jazda.get(j)[1];    // Konec jízdy
            z = k - 270;            //Začátek jízdy (270 minut před koncem)
            jaz = spocitajJazdu(jazda, z, k);       //Vypočítání délky jízdy
            if (jaz > 240) {            //Pokud je délka jízdy delší než 240 minut, jedná se o porušení BP
                System.out.println("Porusenie BP v turnuse " + (tur + 1) + " v case od " + strTime(z) + " do " + strTime(k) + ", jazda = " + jaz + "!");
                ArrayList<Integer[]> sp = dajSpojeBP(rotation, z, k);      //Získání spojů, které porušují BP
                if (spoje.isEmpty() || !spoje.get(spoje.size() - 1).equals(sp)) {
                    spoje.add(sp);      //Přidání spojů do seznamu spojů
                }
            }
        }

        return spoje;
    }
    public static int spocitajJazdu(ArrayList<Integer[]> jazda, int z, int k) {
        int spolu = 0;
        for (Integer[] interval : jazda) {
            int jzac = interval[0];
            int jkon = interval[1];
            if (jkon < z || jzac > k) {
                continue;
            }
            spolu += Math.min(k, jkon) - Math.max(z, jzac);
        }
        return spolu;
    }
    public static ArrayList<Integer[]> dajSpojeBP(List<Integer[]> rotation, int zac, int kon) {
        ArrayList<Integer[]> spoje = new ArrayList<>();
        for (Integer[] s : rotation) {
            int z = s[4] - s[9];
            int k = s[6] + s[10];
            if (k > zac && z < kon) {
                spoje.add(s);
            }
        }
        return spoje;
    }
    public static void printTurnus(int tur, List<Integer[]> rotation, HashMap<DvojicaOld<Integer, Integer>, Integer> distances) {
        // Výpočet přestávky na přistavení na začátku turnusu
        int pristav = distances.get(new DvojicaOld<>(59, rotation.get(0)[3]));
        rotation.get(0)[9] = pristav;

        // Výpočet přestávky na odstavení na konci turnusu
        int odstav = distances.get(new DvojicaOld<>(rotation.get(rotation.size() - 1)[5], 59));
        rotation.get(rotation.size() - 1)[10] = odstav;

        // Výpočet začátku a konce turnusu
        int zaciatok = rotation.get(0)[4] - pristav;
        int koniec = rotation.get(rotation.size() - 1)[6] + odstav;

        // Inicializace proměnné pro celkovou délku přejezdů
        int prejazd_spolu = 0;

        // Výpočet délky přejezdu a přestávky mezi jednotlivými spoji v turnusu
        for (int i = 0; i < rotation.size() - 1; i++) {
            int u = rotation.get(i)[5];
            int v = rotation.get(i + 1)[3];
            int prejazd = distances.get(new DvojicaOld<>(u, v));
            rotation.get(i)[10] = prejazd;  // Délka přejezdu
            rotation.get(i)[11] = rotation.get(i + 1)[4] - rotation.get(i)[6] - prejazd;  // Přestávka mezi spoji
            prejazd_spolu += prejazd;  // Celková délka přejezdů
        }

        // Výpis informací o turnusu
        System.out.println("Turnus: " + (tur + 1));
        for (Integer[] t : rotation) {
            printTrip(t);
        }
        System.out.println();
    }
    public static void printTrip(Integer[] t) {
        System.out.println(t[1] + "\t" + t[2] + "\t" + t[3] + "\t" + strTime(t[4]) + "\t" + t[5] + "\t" + strTime(t[6]) + "\t" + t[9] + "\t" + t[10] + "\t" + t[11]);
    }
}
