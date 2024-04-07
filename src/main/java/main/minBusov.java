package main;

import com.gurobi.gurobi.*;
import data.Dvojica;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class minBusov {

    public static void runModel1() throws GRBException {
        Map<Dvojica<Integer, Integer>, Integer> distances = loadDistances("dist.txt");
        Map<Integer, int[]> trips = loadTrips("trips.txt");

        Map<Dvojica<Integer, Integer>, GRBVar> x = new LinkedHashMap<>();
        GRBModel model = createModel(trips, distances, x);
        model.optimize();

        int boards = trips.size() - (int)model.get(GRB.DoubleAttr.ObjVal);
        System.out.println("Boards: "+ boards);

        List<Integer> heads = getResult(trips,distances,model,x);

        printTurnusy(trips, distances, heads);

        // Tisk spojov pro každý hlavní spoj
        for (int k : heads) {
            int i = k;
            while (i != -1) {
                printTrip(trips.get(i));
                i = trips.get(i)[8];
            }
            System.out.println();
        }
    }

    public static Map<Dvojica<Integer, Integer>, Integer> loadDistances(String fileName) {
        Map<Dvojica<Integer, Integer>, Integer> distances = new LinkedHashMap<>();
        try {
            String decodedWay = URLDecoder.decode(minBusov.class.getResource("/" + fileName).getPath(), StandardCharsets.UTF_8);
            File file = new File(decodedWay);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split("\\s+");
                int u = Integer.parseInt(cols[0]);
                int v = Integer.parseInt(cols[1]);
                int c = Integer.parseInt(cols[2]);
                distances.put(new Dvojica<>(u,v), c);
                distances.put(new Dvojica<>(v,u), c);
                distances.put(new Dvojica<>(u,u), 0);
                distances.put(new Dvojica<>(v,v), 0);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return distances;
    }
    public static Map<Integer, int[]> loadTrips(String fileName) {
        // Create an empty HashMap to store trips
        Map<Integer, int[]> trips = new LinkedHashMap<>();

        try {
            // Open the file for reading
            String decodedWay = URLDecoder.decode(minBusov.class.getResource("/" + fileName).getPath(), StandardCharsets.UTF_8);
            File file = new File(decodedWay);
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
    public static GRBModel createModel(Map<Integer, int[]> trips, Map<Dvojica<Integer, Integer>, Integer> distances, Map<Dvojica<Integer, Integer>, GRBVar> x) throws GRBException {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        GRBModel model = new GRBModel(env);

        // Iterate over pairs of trips and create decision variables
        for (int i : trips.keySet()) {
            for (int j : trips.keySet()) {      //TODO radšej predvypočítať j
                int[] trip_i = trips.get(i);
                int arr_stop_i = trip_i[5];
                int arr_time_i = trip_i[6];
                int[] trip_j = trips.get(j);
                int dep_stop_j = trip_j[3];
                int dep_time_j = trip_j[4];
                Integer dist = distances.get(new Dvojica<>(arr_stop_i, dep_stop_j)); // Assuming distance is stored accordingly

                // Add binary decision variable x[i,j] if the constraint holds
                if (arr_time_i + dist <= dep_time_j) {
                    x.put(new Dvojica<>(i,j), model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                }
            }
        }

        model.update();

        // Set the objective function: maximize the number of selected pairs
        GRBLinExpr objExpr = new GRBLinExpr();
        for (GRBVar var : x.values()) {
            objExpr.addTerm(1, var);
        }
        model.setObjective(objExpr, GRB.MAXIMIZE);

        model.update();

        // Add constraints: each trip can be selected at most once as arrival
        for (int k : trips.keySet()) {      // pre j = 1..n
            GRBLinExpr expr = new GRBLinExpr();
            for (Dvojica<Integer, Integer> key : x.keySet()) {         //prechádzam cez všetky x
                int j = key.druha();
                if (k == j) {
                    expr.addTerm(1, x.get(key));
                }
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1, "arrival_constraint_" + k);
        }

        // Add constraints: each trip can be selected at most once as departure
        for (int k : trips.keySet()) {          // pre i = 1..n
            GRBLinExpr expr = new GRBLinExpr();
            for (Dvojica<Integer, Integer> key : x.keySet()) {
                int i = key.prva();
                if (k == i) {
                    expr.addTerm(1, x.get(key));
                }
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1, "departure_constraint_" + k);
        }

        model.update();

        // Return the model
        return model;
    }
    public static void printTurnusy(Map<Integer, int[]> trips, Map<Dvojica<Integer, Integer>, Integer> distances, List<Integer> heads) {
        // Výpis hlavičky tabulky
        System.out.println("Tur\tZac\tKon\tPrist\tOdst\tPrej");

        // Inicializace počítadla turnusů a celkové délky prejazdů
        int n = 1;
        int total_prej = 0;

        // Pro každý hlavní turnus
        for (int head : heads) {
            // Výpočet příjezdu na první zastávku
            int pristav = distances.get(new Dvojica<>(59, trips.get(head)[3]));
            int zaciatok = trips.get(head)[4] - pristav;

            // Najdi poslední spoj v turnusu
            int tail = head;
            while (trips.get(tail)[8] != -1) {
                tail = trips.get(tail)[8];
            }

            // Výpočet odjezdu z poslední zastávky
            int odstav = distances.get(new Dvojica<>(59, trips.get(tail)[5]));
            int koniec = trips.get(tail)[6] + odstav;

            // Výpočet délky přejezdu mezi jednotlivými zastávkami spojov
            int i = head;
            int prejazd = 0;
            while (trips.get(i)[8] != -1) {
                int u = trips.get(i)[5];
                i = trips.get(i)[8];
                int v = trips.get(i)[3];
                prejazd += distances.get(new Dvojica<>(u, v));
            }

            // Výpis informací o turnusu
            System.out.println(n + "\t" + strTime(zaciatok) + "\t" + strTime(koniec) + "\t" + pristav + "\t" + odstav + "\t" + prejazd);

            // Aktualizace celkové délky prejazdů
            total_prej += pristav + odstav + prejazd;
            n++;
        }

        // Výpis celkové délky prejazdů
        System.out.println("Prejazdy spolu: " + total_prej);
        System.out.println();
    }
    public static String strTime(int time) {
        // Převod času na hodiny a minuty
        int h = time / 60; // Počet hodin
        int m = time % 60; // Počet minut

        // Formátování hodin a minut na formát "hh:mm"
        return String.format("%02d:%02d", h, m);
    }
    public static void printTrip(int[] t) {
        // Tisk informací o spoji
        System.out.println(t[1] + "\t" + t[2] + "\t" + t[3] + "\t" + strTime(t[4]) + "\t" + t[5] + "\t" + strTime(t[6]));
    }
    public static List<Integer> getResult(Map<Integer, int[]> trips, Map<Dvojica<Integer, Integer>, Integer> distances, GRBModel m, Map<Dvojica<Integer, Integer>, GRBVar> x) {
        // Reset previous and successor trip IDs for all trips
        for (int i : trips.keySet()) {
            trips.get(i)[7] = -1;  // Previous trip ID
            trips.get(i)[8] = -1;  // Successor trip ID
        }

        // Determine previous and successor trip IDs for selected pairs
        for (Dvojica<Integer, Integer> x_i : x.keySet()) {
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
        List<Integer> heads = new ArrayList<>();
        for (int i : trips.keySet()) {
            if (trips.get(i)[7] == -1) {
                heads.add(i);
            }
        }

        // Return the list of head trips (ID začiatočných spojov)
        return heads;
    }
    public static void firstModel()
    {
            /* This example formulates and solves the following simple MIP model:
            maximize x + y + 2z
            subject to x + 2y + 3z <= 4
               x + y >= 1
             x, y, z binary
            */
        try
        {
            // ------------------------------------------------------------------------------------------------
            // 1. Create empty environment, set options, and start
            // In this call we requested an empty environment, choose a log file, and started the environment.
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "mip1.log");
            env.start();

            // ------------------------------------------------------------------------------------------------
            // 2. Create empty model
            GRBModel model = new GRBModel(env);

            // ------------------------------------------------------------------------------------------------
            // 3.Create variables
            //                  [1: lb (optional): Lower bound for new variable,
            //                   2: ub (optional): Upper bound for new variable,
            //                   3: obj (optional): Objective coefficient for new variable
            //                   - zero here - we’ll set the objective later,
            //                   4: vtype (optional): Variable type for new variable (GRB.CONTINUOUS, GRB.BINARY,
            //                   GRB.INTEGER, GRB.SEMICONT, or GRB.SEMIINT),
            //                   5: name (optional): Name for new variable (stored as an ASCII string),
            //                   6: column (optional): Column object that indicates the set of constraints
            //                   in which the new variable participates, and the associated coefficients.]
            GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x");
            GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y");
            GRBVar z = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z");

            // ------------------------------------------------------------------------------------------------
            // 4. Set objective: maximize x + y + 2z
            // We build our objective by prva constructing an empty linear expression and adding three terms to it.
            // AddTerm() - Add a single term into a linear expression.
            // [1: coeff: Coefficient for new term.
            //  2: var: Variable for new term.]
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            expr.addTerm(2.0, z);
            model.setObjective(expr, GRB.MAXIMIZE);
            // Model.setObjective(expr, sense=None) - Set the model objective equal to a linear or quadratic expression
            // [1: expr: New objective expression. Argument can be a linear or quadratic expression
            //           (an objective of type LinExpr or QuadExpr).
            // 2: sense (optional): Optimization sense (GRB.MINIMIZE for minimization, GRB.MAXIMIZE for maximization)]

            // ------------------------------------------------------------------------------------------------
            // 5. Add constraint : x + 2y + 3z <= 4
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(2.0, y);
            expr.addTerm(3.0, z);
            model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");
            // GRBModel.AddConstr() - Add a single linear constraint to a model.
            // [1: lhsExpr: Left-hand side expression for new linear constraint.
            //  2: sense: Sense for new linear constraint (GRB.LESS_EQUAL, GRB.EQUAL, or GRB.GREATER_EQUAL).
            //  3: rhsExpr: Right-hand side expression for new linear constraint.
            //  4: name: Name for new constraint.]

            // ------------------------------------------------------------------------------------------------
            // 6. Add constraint : x + y >= 1
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");

            // ------------------------------------------------------------------------------------------------
            // 7. Optimize model
            // GRBModel::optimize() - Optimize the model. The algorithm used for the optimization depends on the
            // model type (simplex or barrier for a continuous model; branch-and-cut for a MIP model).
            model.optimize();
            System.out.println(x.get(GRB.StringAttr.VarName) + " " + x.get(GRB.DoubleAttr.X));
            System.out.println(y.get(GRB.StringAttr.VarName) + " " + y.get(GRB.DoubleAttr.X));
            System.out.println(z.get(GRB.StringAttr.VarName) + " " + z.get(GRB.DoubleAttr.X));
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // ------------------------------------------------------------------------------------------------
            // 8. Dispose of model and environment
            model.dispose();

            env.dispose();
        }
        catch (GRBException e)
        {
            System.out.println("Error code : " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}