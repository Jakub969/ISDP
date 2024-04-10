package optimalizacia;

import com.gurobi.gurobi.*;
import data.Dvojica;
import data.Spoj;
import data.Turnus;
import data.Zmena;

import java.util.*;

public class MinimalizaciaAutobusov {
    private Map<Dvojica<Integer, Integer>, GRBVar> x;
    private int pocetAutobusov;
    public MinimalizaciaAutobusov(LinkedHashMap<Integer, Spoj> pSpoje) throws GRBException
    {
        this.pripravModel(pSpoje);
        this.vypocitajModel(pSpoje);
    }

    private void pripravModel(LinkedHashMap<Integer, Spoj> pSpoje)
    {
        // Reset previous and successor trip IDs for all trips
        for (Spoj spoj_i : pSpoje.values())
        {
            spoj_i.setNasledujuci(null);
            spoj_i.setPredchadzajuci(null);
        }

        this.x = new LinkedHashMap<>();
    }

    private void vypocitajModel(Map<Integer, Spoj> pSpoje) throws GRBException {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        env.set("logFile", "minBusov.log");
        env.start();
        GRBModel model = new GRBModel(env);

        // Vytvoriť všetky premenné x
        for (Spoj spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                x.put(new Dvojica<>(i, j), model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
            }
        }

        model.update();

        // Nastaviť účelovú funkciu - maximalizácia súčtu všetkých premenných x
        GRBLinExpr objExpr = new GRBLinExpr();
        for (GRBVar var : this.x.values())
        {
            objExpr.addTerm(1, var);
        }
        model.setObjective(objExpr, GRB.MAXIMIZE);

        model.update();

        // Pridať prvý typ podmienok - každý spoj j bude nasledovať po maximálne jednom spoji i
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int j = spoj_j.getID();
            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1, "arrival_constraint_" + j);
        }

        // Pridať druhý typ podmienok - po každom spoji i bude nasledovať maximálne jeden spoj j
        for (Spoj spoj_i : pSpoje.values())       // pre i = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int i = spoj_i.getID();
            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                expr.addTerm(1, x.get(new Dvojica<>(i,j)));
            }

            model.addConstr(expr, GRB.LESS_EQUAL, 1, "departure_constraint_" + i);
        }

        model.update();

        //optimalizuj model
        model.write("modelBus.lp");
        model.optimize();

        this.pocetAutobusov = pSpoje.size() - (int)model.get(GRB.DoubleAttr.ObjVal);
    }

    public ArrayList<Turnus> vytvorTurnusy(LinkedHashMap<Integer, Spoj> pSpoje) {
        ArrayList<Turnus> turnusy = new ArrayList<>();

        // Z rozhodovacích premenných x_ij získaj všetky prepojenia spojov, a prepoj spoje
        for (Dvojica<Integer, Integer> x_ij : x.keySet()) {
            try {
                if (x.get(x_ij).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia x (0 či 1)
                    int spoj_i_id = x_ij.prva();
                    Spoj spoj_i = pSpoje.get(spoj_i_id);

                    int spoj_j_id = x_ij.druha();
                    Spoj spoj_j = pSpoje.get(spoj_j_id);

                    // If the pair (i, j) is selected, set j as the successor of i and i as the previous of j
                    spoj_i.setNasledujuci(spoj_j);  // Set j as the successor of i
                    spoj_j.setPredchadzajuci(spoj_i);  // Set i as the previous of j
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        //Vytvorenie turnusov
        for (Spoj spoj_i : pSpoje.values())
        {
            if (spoj_i.getPredchadzajuci() == null)
            {
                turnusy.add(new Turnus(new Zmena(spoj_i)));
            }
        }

        return turnusy;
    }
    public int getPocetAutobusov()
    {
        return this.pocetAutobusov;
    }
}
