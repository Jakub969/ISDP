package optimalizaciaOld;

import com.gurobi.gurobi.*;
import dataOld.DvojicaOld;
import dataOld.SpojOld;
import dataOld.TurnusOld;
import dataOld.ZmenaOld;

import java.util.*;

public class MinimalizaciaAutobusovOld {
    private Map<DvojicaOld<Integer, Integer>, GRBVar> x;
    private int pocetAutobusov;
    public MinimalizaciaAutobusovOld(LinkedHashMap<Integer, SpojOld> pSpoje) throws GRBException
    {
        this.pripravModel(pSpoje);
        this.vypocitajModel(pSpoje);
    }

    private void pripravModel(LinkedHashMap<Integer, SpojOld> pSpoje)
    {
        // Reset previous and successor trip IDs for all trips
        for (SpojOld spoj_i : pSpoje.values())
        {
            spoj_i.setNasledujuci(null);
            spoj_i.setPredchadzajuci(null);
        }

        this.x = new LinkedHashMap<>();
    }

    private void vypocitajModel(Map<Integer, SpojOld> pSpoje) throws GRBException {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        env.set("logFile", "minBusov.log");
        GRBModel model = new GRBModel(env);

        // Vytvoriť všetky premenné x
        for (SpojOld spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for(SpojOld spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                x.put(new DvojicaOld<>(i, j), model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
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
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int j = spoj_j.getID();
            for(SpojOld spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = x.get(new DvojicaOld<>(i,j));
                expr.addTerm(1, x_ij);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1, "1_arrival_constraint_" + j);
        }

        // Pridať druhý typ podmienok - po každom spoji i bude nasledovať maximálne jeden spoj j
        for (SpojOld spoj_i : pSpoje.values())       // pre i = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int i = spoj_i.getID();
            for(SpojOld spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                expr.addTerm(1, x.get(new DvojicaOld<>(i,j)));
            }

            model.addConstr(expr, GRB.LESS_EQUAL, 1, "2_departure_constraint_" + i);
        }

        model.update();

        //optimalizuj model
        model.write("modelBus.lp");

        model.optimize();

        this.pocetAutobusov = pSpoje.size() - (int)model.get(GRB.DoubleAttr.ObjVal);
    }

    public ArrayList<TurnusOld> vytvorTurnusy(LinkedHashMap<Integer, SpojOld> pSpoje) {
        ArrayList<TurnusOld> turnusy = new ArrayList<>();

        // Z rozhodovacích premenných x_ij získaj všetky prepojenia spojov, a prepoj spoje
        for (DvojicaOld<Integer, Integer> x_ij : x.keySet()) {
            try {
                if (x.get(x_ij).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia x (0 či 1)
                    int spoj_i_id = x_ij.prva();
                    SpojOld spoj_i = pSpoje.get(spoj_i_id);

                    int spoj_j_id = x_ij.druha();
                    SpojOld spoj_j = pSpoje.get(spoj_j_id);

                    // If the pair (i, j) is selected, set j as the successor of i and i as the previous of j
                    spoj_i.setNasledujuci(spoj_j);  // Set j as the successor of i
                    spoj_j.setPredchadzajuci(spoj_i);  // Set i as the previous of j
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        //Vytvorenie turnusov
        for (SpojOld spoj_i : pSpoje.values())
        {
            if (spoj_i.getPredchadzajuci() == null)
            {
                turnusy.add(new TurnusOld(new ZmenaOld(spoj_i)));
            }
        }

        return turnusy;
    }
    public int getPocetAutobusov()
    {
        return this.pocetAutobusov;
    }
}
