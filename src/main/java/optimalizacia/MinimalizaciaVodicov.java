package optimalizacia;

import com.gurobi.gurobi.*;
import data.Dvojica;
import data.Spoj;
import data.Turnus;
import data.Usek;
import mvp.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class MinimalizaciaVodicov
{
    private Map<Dvojica<Integer, Integer>, GRBVar> x;
    private Map<Dvojica<Integer, Integer>, GRBVar> y;
    private Map<Integer, GRBVar> t;
    private Map<Integer, GRBVar> z;
    private Map<Integer, GRBVar> u;
    private Map<Integer, GRBVar> v;
    private ArrayList<Turnus> turnusy;
    private int pocetVodicov;

    public MinimalizaciaVodicov(LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky,
                                LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT,
                                LinkedHashMap<Dvojica<Integer, Integer>, Integer> T) throws GRBException
    {
        this.pripravModel(pSpoje);
        this.vypocitajModel(pSpoje, pUseky, DT, T);
        this.vytvorTurnusy(pSpoje);
    }

    private void pripravModel(LinkedHashMap<Integer, Spoj> pSpoje)
    {
        // Reset previous and successor trip IDs for all trips
        for (Spoj spoj_i : pSpoje.values())
        {
            spoj_i.setNasledujuci(null);
            spoj_i.setPredchadzajuci(null); //TODO
        }

        this.x = new LinkedHashMap<>();
        this.y = new LinkedHashMap<>();
        this.u = new LinkedHashMap<>();
        this.v = new LinkedHashMap<>();
        this.t = new LinkedHashMap<>();
        this.z = new LinkedHashMap<>();
        this.turnusy = new ArrayList<>();
    }
    private void vypocitajModel(Map<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Usek> pUseky,
                                LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT,
                                LinkedHashMap<Dvojica<Integer, Integer>, Integer> T) throws GRBException
    {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        env.set("logFile", "minVodicov.log");
        env.start();
        GRBModel model = new GRBModel(env);
        //model.set(GRB.IntParam.ConcurrentMethod, 1);
        //model.set(GRB.IntParam.MIPFocus, 1);

        //model.getEnv().set(GRB.DoubleParam.TimeLimit, 100.0);
       // model.set(GRB.DoubleParam.MIPGap, 0.15);

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
                this.x.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                cx.put(prechod, dist);
            }
        }

        // Vytvoriť všetky premenné y_ij
        Map<Dvojica<Integer, Integer>, Integer> cy = new LinkedHashMap<>();
        for (Spoj spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for (Spoj spoj_j : spoj_i.getMozneNasledujuceSpojeVodic())
            {
                int j = spoj_j.getID();
                Dvojica<Integer, Integer> prechod = new Dvojica<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichoduID();
                int mod_j = spoj_j.getMiestoOdchoduID();
                int dist = pUseky.get(new Dvojica<>(mpr_i, Model.DEPO)).getCasPrejazdu();
                dist += pUseky.get(new Dvojica<>(Model.DEPO, mod_j)).getCasPrejazdu();
                this.y.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "y_" + i + "_" + j));
                cy.put(prechod, dist);
            }
        }

        // Vytvoriť všetky premenné t_j a z_j
        for (Spoj spoj_j : pSpoje.values())
        {
            int j = spoj_j.getID();
            this.t.put(j, model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "t_" + j));
            this.z.put(j, model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "z_" + j));
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
        //  Minimalizuj    c_vodič * (|S| - ∑_(ij,(i,j)∈E) [x_ij] )
        GRBLinExpr objExpr = new GRBLinExpr();

        objExpr.addConstant(Model.C_VODIC * pSpoje.size());

        for (GRBVar var : this.x.values())
        {
            objExpr.addTerm(-Model.C_VODIC, var);
        }

        //  + c_km * ( ∑_(ij,(i,j)∈E) [m(mpr_i, mod_j) * x_ij]
        //           + ∑_(ij,(i,j)∈F) [(m(mpr_i,D) + m(D,mod_j)) * y_ij]
        //           + ∑_(j∈S) [m(D,mod_j) * u_j
        //           + ∑_(i∈S) [m(mpr_i,D) * v_i)

        for (Map.Entry<Dvojica<Integer, Integer>, GRBVar> entry : this.x.entrySet())
        {
            Dvojica<Integer, Integer> key = entry.getKey();
            GRBVar x_ij = entry.getValue();
            int cx_ij = cx.get(key);
            objExpr.addTerm(Model.C_KM * cx_ij, x_ij);
        }

        for (Map.Entry<Dvojica<Integer, Integer>, GRBVar> entry : this.y.entrySet())
        {
            Dvojica<Integer, Integer> key = entry.getKey();
            GRBVar y_ij = entry.getValue();
            int cy_ij = cy.get(key);
            objExpr.addTerm(Model.C_KM * cy_ij, y_ij);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar u_j = entry.getValue();
            int cu_j = cu.get(spoj_id);
            objExpr.addTerm(Model.C_KM * cu_j, u_j);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.v.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar v_i = entry.getValue();
            int cv_i = cv.get(spoj_id);
            objExpr.addTerm(Model.C_KM * cv_i, v_i);
        }

        model.setObjective(objExpr, GRB.MINIMIZE);
        model.update();

        // Pridať 1. typ podmienok - každý spoj j bude nasledovať po jednom spoji i (alebo bude prvým spojom)
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr = new GRBLinExpr();
            GRBVar u_j = this.u.get(j);
            expr.addTerm(1, u_j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpojeVodic())
            {
                int i = spoj_i.getID();
                GRBVar y_ij = this.y.get(new Dvojica<>(i,j));
                expr.addTerm(1, y_ij);
            }

            model.addConstr(expr, GRB.EQUAL, 1, "1_arrival_constraint_" + j);
        }

        // Pridať 2. typ podmienok - po každom spoji i bude nasledovať jeden spoj j (alebo bude posledným spojom)
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

            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpojeVodic())
            {
                int j = spoj_j.getID();
                GRBVar y_ij = this.y.get(new Dvojica<>(i,j));
                expr.addTerm(1, y_ij);
            }

            model.addConstr(expr, GRB.EQUAL, 1, "2_departure_constraint_" + i);
        }

        // Pridať 3. typ podmienok - Přidání omezení pro celkový počet spojů |S| - ∑_(ij,(i,j)∈E) [x_ij] -∑_(ij,(i,j)∈F)[y_ij] = B
        GRBLinExpr expr3 = new GRBLinExpr();
        for (GRBVar var : this.x.values())
        {
            expr3.addTerm(1, var);
        }
        for (GRBVar var : this.y.values())
        {
            expr3.addTerm(1, var);
        }
        model.addConstr(expr3, GRB.EQUAL, pSpoje.size() - 4, "3_total_connections");

        // Pridať 4. typ podmienok - t_j ≥ t_i + DT_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();
            GRBVar t_j = this.t.get(j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr4 = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar t_i = this.t.get(i);
                expr4.addTerm(1, t_i);

                int dt_ij = DT.get(new Dvojica<>(i,j));
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr4.addTerm(dt_ij, x_ij);

                expr4.addConstant(trvanieJ);
                expr4.addConstant(-Model.K);
                expr4.addTerm(Model.K, x_ij);

                model.addConstr(t_j, GRB.GREATER_EQUAL, expr4, "4_driving_time_constraint_" + i + "_" + j);
            }
        }

        // Pridať 5. typ podmienok - t_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar t_j = this.t.get(j);

            GRBLinExpr expr5 = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new Dvojica<>(Model.DEPO, mod_j)).getCasPrejazdu();
            expr5.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr5.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr5.addConstant(-Model.K);
            expr5.addTerm(Model.K, u_j);

            model.addConstr(t_j, GRB.GREATER_EQUAL, expr5, "5_driving_time_depo_constraint_" + j);
        }

        // Pridať 6. typ podmienok - t_j + m(mpr_j,D) ≤ DT_max  pre j ∈ S
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr6 = new GRBLinExpr();
            GRBVar t_j = this.t.get(j);
            expr6.addTerm(1, t_j);

            int mpr_j = spoj_j.getMiestoPrichoduID();
            int dist = pUseky.get(new Dvojica<>(mpr_j, Model.DEPO)).getCasPrejazdu();
            expr6.addConstant(dist);

            model.addConstr(expr6, GRB.LESS_EQUAL, Model.DT_MAX, "6_driving_time_max_constraint_" + j);
        }

        // Pridať 7. typ podmienok - z_j ≥ z_i + T_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();
            GRBVar z_j = this.z.get(j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr7 = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar z_i = this.z.get(i);
                expr7.addTerm(1, z_i);

                int t_ij = T.get(new Dvojica<>(i,j));
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr7.addTerm(t_ij, x_ij);

                expr7.addConstant(trvanieJ);

                expr7.addConstant(-Model.K);
                expr7.addTerm(Model.K, x_ij);

                model.addConstr(z_j, GRB.GREATER_EQUAL, expr7, "7_total_time_constraint_" + i + "_" + j);
            }
        }

        // Pridať 8. typ podmienok - z_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar z_j = this.z.get(j);

            GRBLinExpr expr8 = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new Dvojica<>(Model.DEPO, mod_j)).getCasPrejazdu();
            expr8.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr8.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr8.addConstant(-Model.K);
            expr8.addTerm(Model.K, u_j);

            model.addConstr(z_j, GRB.GREATER_EQUAL, expr8, "8_total_time_depo_constraint_" + j);
        }

        // Pridať 9. typ podmienok - z_j + m(mpr_j,D) ≤ T_max  pre j ∈ S
        for (Spoj spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr9 = new GRBLinExpr();
            GRBVar z_j = this.z.get(j);
            expr9.addTerm(1, z_j);

            int mpr_j = spoj_j.getMiestoPrichoduID();
            int dist = pUseky.get(new Dvojica<>(mpr_j, Model.DEPO)).getCasPrejazdu();
            expr9.addConstant(dist);

            model.addConstr(expr9, GRB.LESS_EQUAL, Model.T_MAX, "9_total_time_max_constraint_" + j);
        }

        model.update();

        //optimalizuj model
        model.write("minVodicov.lp");
        model.optimize();

        //
        int sumX = 0;
        for (GRBVar var : this.x.values())
        {
            if(var.get(GRB.DoubleAttr.X) == 1)
                sumX++;
        }
        this.pocetVodicov = pSpoje.size() - sumX;

        // ------
        HashSet<String> printedVars = new HashSet<>();
        GRBVar[] vars = model.getVars();

        System.out.println("Nenulové rozhodovací proměnné:");
        for (GRBVar var : vars) {
            try {
                double value = var.get(GRB.DoubleAttr.X);
                if (value != 0 && !printedVars.contains(var.get(GRB.StringAttr.VarName))) {
                    System.out.println(var.get(GRB.StringAttr.VarName) + " = " + value);
                    printedVars.add(var.get(GRB.StringAttr.VarName));
                }
            } catch (GRBException e) {
                // Handle exception
                e.printStackTrace();
            }
        }

    }
    public void vytvorTurnusy(LinkedHashMap<Integer, Spoj> pSpoje) {
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

        for (Dvojica<Integer, Integer> y_ij : y.keySet())
        {
            try {
                if (y.get(y_ij).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia y (0 či 1)
                    int spoj_i_id = y_ij.prva();
                    Spoj spoj_i = pSpoje.get(spoj_i_id);

                    int spoj_j_id = y_ij.druha();
                    Spoj spoj_j = pSpoje.get(spoj_j_id);

                    // If the pair (i, j) is selected, set j as the successor of i and i as the previous of j
                    spoj_i.setNasledujuci(spoj_j);  // Set j as the successor of i
                    spoj_j.setPredchadzajuci(spoj_i);  // Set i as the previous of j
                    spoj_i.setKoniecZmeny(true);
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        //Vytvorenie turnusov
        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_j_id = entry.getKey();
            GRBVar u_j = entry.getValue();
            try {
                if(u_j.get(GRB.DoubleAttr.X) == 1)
                {
                    Spoj spoj_j = pSpoje.get(spoj_j_id);
                    this.turnusy.add(new Turnus(spoj_j));
                }
            } catch (GRBException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public int getPocetVodicov() {
        return pocetVodicov;
    }
    public ArrayList<Turnus> getTurnusy()
    {
        return this.turnusy;
    }
}
