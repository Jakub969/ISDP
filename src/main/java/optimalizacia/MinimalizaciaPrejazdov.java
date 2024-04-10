package optimalizacia;

import com.gurobi.gurobi.*;
import data.*;
import mvp.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MinimalizaciaPrejazdov
{
    private Map<Dvojica<Integer, Integer>, GRBVar> x;
    private Map<Integer, GRBVar> u;
    private Map<Integer, GRBVar> v;
    private int prazdnePrejazdy;
    private GRBModel model;

    public MinimalizaciaPrejazdov(int pPocetBusov,
                                  LinkedHashMap<Integer, Spoj> pSpoje,
                                  LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky) throws GRBException
    {
        this.pripravModel(pSpoje);
        this.vypocitajModel(pPocetBusov, pSpoje, pUseky);
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
        this.u = new LinkedHashMap<>();
        this.v = new LinkedHashMap<>();
    }

    private void vypocitajModel(int pPocetBusov,
                                Map<Integer, Spoj> pSpoje,
                                LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky) throws GRBException
    {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        env.set("logFile", "minPrejazdov.log");
        env.start();
        model = new GRBModel(env);

        // Vytvoriť všetky premenné x_ij
        Map<Dvojica<Integer, Integer>, Integer> cx = new LinkedHashMap<>();
        for (Spoj spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for (Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                Dvojica<Integer, Integer> prechod = new Dvojica<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichoduID();
                int mod_j = spoj_j.getMiestoOdchoduID();
                int dist = pUseky.get(new Dvojica<>(mpr_i, mod_j)).getCasPrejazdu();
                x.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                cx.put(prechod, dist);
            }
        }

        // Vytvoriť všetky premenné u_j
        Map<Integer, Integer> cu = new LinkedHashMap<>();
        for (Spoj spoj_j : pSpoje.values())
        {
            int j = spoj_j.getID();
            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new Dvojica<>(Model.DEPO, mod_j)).getCasPrejazdu();
            u.put(j, model.addVar(0, 1, 0, GRB.BINARY, "u_" + j));
            cu.put(j, dist);
        }

        // Vytvoriť všetky premenné v_i
        Map<Integer, Integer> cv = new LinkedHashMap<>();
        for (Spoj spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            int mpr_i = spoj_i.getMiestoPrichoduID();
            int dist = pUseky.get(new Dvojica<>(mpr_i, Model.DEPO)).getCasPrejazdu();
            v.put(i, model.addVar(0, 1, 0, GRB.BINARY, "v_" + i));
            cv.put(i, dist);
        }

        model.update();

        // Nastaviť účelovú funkciu -
        // 1. súčet cx_ij * x_ij
        GRBLinExpr objExpr = new GRBLinExpr();

        for (Map.Entry<Dvojica<Integer, Integer>, GRBVar> entry : this.x.entrySet())
        {
            Dvojica<Integer, Integer> key = entry.getKey();
            GRBVar x_ij = entry.getValue();
            int cx_ij = cx.get(key);
            objExpr.addTerm(cx_ij, x_ij);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar u_j = entry.getValue();
            int cu_j = cu.get(spoj_id);
            objExpr.addTerm(cu_j, u_j);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.v.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar v_i = entry.getValue();
            int cv_i = cv.get(spoj_id);
            objExpr.addTerm(cv_i, v_i);
        }

        model.setObjective(objExpr, GRB.MINIMIZE);
        model.update();

        // Pridať prvý typ podmienok - každý spoj j bude nasledovať po jednom spoji i (alebo bude prvým spojom)
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr = new GRBLinExpr();
            GRBVar u_j = this.u.get(j);
            expr.addTerm(1, u_j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }

            model.addConstr(expr, GRB.EQUAL, 1, "1_arrival_constraint_" + j);
        }

        // Pridať druhý typ podmienok - po každom spoji i bude nasledovať jeden spoj j (alebo bude posledným spojom)
        for (Spoj spoj_i : pSpoje.values())       // pre i = 1..n
        {
            int i = spoj_i.getID();

            GRBLinExpr expr = new GRBLinExpr();
            GRBVar v_i = this.v.get(i);
            expr.addTerm(1, v_i);

            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                GRBVar x_ij = x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }

            model.addConstr(expr, GRB.EQUAL, 1, "2_departure_constraint_" + i);
        }

        // Přidání omezení pro celkový počet spojů
        GRBLinExpr expr = new GRBLinExpr();
        for (GRBVar var : this.x.values())
        {
            expr.addTerm(1, var);
        }
        model.addConstr(expr, GRB.EQUAL, pSpoje.size() - pPocetBusov, "3_total_connections");

        model.update();

        //optimalizuj model
        model.write("prazdnePrejazdy.lp");
        model.optimize();
        this.prazdnePrejazdy = (int)model.get(GRB.DoubleAttr.ObjVal);
    }

    public ArrayList<Turnus> vytvorTurnusy(LinkedHashMap<Integer, Spoj> pSpoje,
                                           LinkedHashMap<Dvojica<Integer, Integer>, Usek> useky,
                                           LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT,
                                           LinkedHashMap<Dvojica<Integer, Integer>, Integer> T) throws GRBException {
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

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_j_id = entry.getKey();
            GRBVar u_j = entry.getValue();

            try
            {
                if(u_j.get(GRB.DoubleAttr.X) == 1)
                {
                    Spoj spoj_j = pSpoje.get(spoj_j_id);
                    turnusy.add(new Turnus(new Zmena(spoj_j)));
                }
            }
            catch (GRBException e)
            {
                throw new RuntimeException(e);
            }
        }

        boolean bezChyby = false;
        for (Spoj spoj_i : pSpoje.values())
        {
            spoj_i.setNasledujuci(null);
            spoj_i.setPredchadzajuci(null);
        }

        model.update();
        model.write("mm.lp");
        return turnusy;
    }

    private void pridajPodmienky(GRBModel model, ArrayList<ArrayList<Integer>> porusenia) throws GRBException {
        for (ArrayList<Integer> spoje : porusenia)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < spoje.size() - 1; i++)
            {
                GRBVar x_ij = x.get(new Dvojica<>(spoje.get(i), spoje.get(i+1)));
                expr.addTerm(1, x_ij);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, spoje.size() - 2, "4_" + expr.getVar(0).get(GRB.StringAttr.VarName) + "_" + expr.getVar(expr.size()-1).get(GRB.StringAttr.VarName));
        }
    }

    public int getPrazdnePrejazdy() {
        return prazdnePrejazdy;
    }

}
