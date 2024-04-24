package optimalizaciaOld;

import com.gurobi.gurobi.*;
import dataOld.*;
import mvpOld.ModelOld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaximalizaciaObsluzenychSpojovOld {
    private Map<DvojicaOld<Integer, Integer>, GRBVar> x;
    private Map<DvojicaOld<Integer, Integer>, GRBVar> y;
    private Map<Integer, GRBVar> t;
    private Map<Integer, GRBVar> z;
    private Map<Integer, GRBVar> u;
    private Map<Integer, GRBVar> v;
    private Map<Integer, GRBVar> o;
    private ArrayList<TurnusOld> turnusy;
    private GRBModel model;
    private final double y_max;
    public MaximalizaciaObsluzenychSpojovOld(double y, int pPocetBusov, int pPocetVodicov,
                                             LinkedHashMap<Integer, LinkaOld> pLinky,
                                             LinkedHashMap<Integer, SpojOld> pSpoje,
                                             LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> pUseky,
                                             LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> DT,
                                             LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> T) throws GRBException
    {
        this.y_max = y;
        this.pripravModel(pSpoje);
        this.vypocitajModel(pPocetBusov, pPocetVodicov, pLinky, pSpoje, pUseky, DT, T);
    }

    private void pripravModel(LinkedHashMap<Integer, SpojOld> pSpoje)
    {
        // Reset previous and successor trip IDs for all trips
        for (SpojOld spoj_i : pSpoje.values())
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
        this.o = new LinkedHashMap<>();
    }

    private void vypocitajModel(int pPocetBusov, int pPocetVodicov,
                                Map<Integer, LinkaOld> pLinky,
                                Map<Integer, SpojOld> pSpoje,
                                LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> pUseky,
                                LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> DT,
                                LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> T) throws GRBException
    {
        // Create a new optimization model
        GRBEnv env = new GRBEnv();
        env.set("logFile", "maxObsSpojov.log");
        env.start();
        model = new GRBModel(env);
        //model.set(GRB.IntParam.Cuts, 0);
        //model.set(GRB.IntParam.MIPFocus, 2);
        //model.set(GRB.IntParam.Method, 2);
        //model.set(GRB.DoubleParam.MIPGap, 0.427);

        // Vytvoriť všetky premenné x_ij
        Map<DvojicaOld<Integer, Integer>, Integer> cx = new LinkedHashMap<>();
        for (SpojOld spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for (SpojOld spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                DvojicaOld<Integer, Integer> prechod = new DvojicaOld<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichoduID();
                int mod_j = spoj_j.getMiestoOdchoduID();
                int dist = pUseky.get(new DvojicaOld<>(mpr_i, mod_j)).getCasPrejazdu();
                this.x.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                cx.put(prechod, dist);
            }
        }

        // Vytvoriť všetky premenné y_ij
        Map<DvojicaOld<Integer, Integer>, Integer> cy = new LinkedHashMap<>();
        for (SpojOld spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for (SpojOld spoj_j : spoj_i.getMozneNasledujuceSpojeVodic())
            {
                int j = spoj_j.getID();
                DvojicaOld<Integer, Integer> prechod = new DvojicaOld<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichoduID();
                int mod_j = spoj_j.getMiestoOdchoduID();
                int dist = pUseky.get(new DvojicaOld<>(mpr_i, ModelOld.DEPO)).getCasPrejazdu();
                dist += pUseky.get(new DvojicaOld<>(ModelOld.DEPO, mod_j)).getCasPrejazdu();
                this.y.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "y_" + i + "_" + j));
                cy.put(prechod, dist);
            }
        }

        // Vytvoriť všetky premenné t_j a z_j, o_j, obs_j
        Map<Integer, Integer> obs = new LinkedHashMap<>();
        for (SpojOld spoj_j : pSpoje.values())
        {
            int j = spoj_j.getID();
            this.t.put(j, model.addVar(0, ModelOld.T_MAX, 0, GRB.INTEGER, "t_" + j));
            this.z.put(j, model.addVar(0, ModelOld.DT_MAX, 0, GRB.INTEGER, "z_" + j));
            this.o.put(j, model.addVar(0, 1, 0, GRB.BINARY, "o_" + j));
            obs.put(j, spoj_j.getObsadenost());
        }

        // Vytvoriť všetky premenné u_j
        Map<Integer, Integer> cu = new LinkedHashMap<>();
        for (SpojOld spoj_j : pSpoje.values())
        {
            int j = spoj_j.getID();
            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new DvojicaOld<>(ModelOld.DEPO, mod_j)).getCasPrejazdu();
            u.put(j, model.addVar(0, 1, 0, GRB.BINARY, "u_" + j));
            cu.put(j, dist);
        }

        // Vytvoriť všetky premenné v_i
        Map<Integer, Integer> cv = new LinkedHashMap<>();
        for (SpojOld spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            int mpr_i = spoj_i.getMiestoPrichoduID();
            int dist = pUseky.get(new DvojicaOld<>(mpr_i, ModelOld.DEPO)).getCasPrejazdu();
            v.put(i, model.addVar(0, 1, 0, GRB.BINARY, "v_" + i));
            cv.put(i, dist);
        }

        model.update();

        // Nastaviť účelovú funkciu -
        GRBLinExpr objExpr = new GRBLinExpr();

        objExpr.addConstant(ModelOld.C_VODIC * pSpoje.size());
        for (GRBVar var : this.o.values())
        {
            objExpr.addTerm(-ModelOld.C_VODIC, var);
        }

/*
        for (Spoj spoj_i: pSpoje.values())
        {
            int i = spoj_i.getID();
            int obsSpoja = obs.get(i);
            GRBVar o_i = this.o.get(i);
            objExpr.addTerm(100 * obsSpoja, o_i);
        }
*/

        //  + c_km * ( ∑_(ij,(i,j)∈E) [m(mpr_i, mod_j) * x_ij]
        //           + ∑_(ij,(i,j)∈F) [(m(mpr_i,D) + m(D,mod_j)) * y_ij]
        //           + ∑_(j∈S) [m(D,mod_j) * u_j
        //           + ∑_(i∈S) [m(mpr_i,D) * v_i)


        model.setObjective(objExpr, GRB.MINIMIZE);
        model.update();

        // Pridať 1. typ podmienok - (∑_(i∈L_k) [OBS_i * o_i]) / (∑_(i∈L_k) [OBS_i]) ≥ y
        for (LinkaOld linka_k: pLinky.values())
        {
            int linka_id = linka_k.getID();
            int obsLinky = linka_k.getObsadenost();
            GRBLinExpr expr1 = new GRBLinExpr();

            for (SpojOld spoj_i: linka_k.getSpoje())
            {
                int i = spoj_i.getID();
                int obsSpoja = obs.get(i);

                GRBVar o_i = this.o.get(i);
                expr1.addTerm((double) obsSpoja / obsLinky, o_i);
            }

            model.addConstr(expr1, GRB.GREATER_EQUAL, this.y_max, "1_linka_constraint_" + linka_id);
        }

        // Pridať 2. typ podmienok - u_j + ∑_(i,(i,j)∈E) [x_ij] + ∑_(i,(i,j)∈E) [y_ij] = o_j
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr2 = new GRBLinExpr();
            GRBVar u_j = this.u.get(j);
            expr2.addTerm(1, u_j);

            for(SpojOld spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = this.x.get(new DvojicaOld<>(i,j));
                expr2.addTerm(1, x_ij);
            }

            for(SpojOld spoj_i : spoj_j.getMoznePredchadzajuceSpojeVodic())
            {
                int i = spoj_i.getID();
                GRBVar y_ij = this.y.get(new DvojicaOld<>(i,j));
                expr2.addTerm(1, y_ij);
            }

            GRBVar o_j = this.o.get(j);

            model.addConstr(expr2, GRB.EQUAL, o_j, "2_arrival_constraint_" + j);
        }

        // Pridať 3. typ podmienok - v_i + ∑_(i,(i,j)∈E) [x_ij] +∑_(i,(i,j)∈E) [y_ij] = o_i
        for (SpojOld spoj_i : pSpoje.values())       // pre i = 1..n
        {
            int i = spoj_i.getID();

            GRBLinExpr expr3 = new GRBLinExpr();
            GRBVar v_i = this.v.get(i);
            expr3.addTerm(1, v_i);

            for(SpojOld spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                GRBVar x_ij = x.get(new DvojicaOld<>(i,j));
                expr3.addTerm(1, x_ij);
            }

            for(SpojOld spoj_j : spoj_i.getMozneNasledujuceSpojeVodic())
            {
                int j = spoj_j.getID();
                GRBVar y_ij = this.y.get(new DvojicaOld<>(i,j));
                expr3.addTerm(1, y_ij);
            }

            GRBVar o_i = this.o.get(i);

            model.addConstr(expr3, GRB.EQUAL, o_i, "3_departure_constraint_" + i);
        }

        // Pridať 4. typ podmienok - Přidání omezení pro celkový počet spojů ∑_(j∈S) u_j ≤ B
        GRBLinExpr expr4 = new GRBLinExpr();
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar u_j = this.u.get(j);
            expr4.addTerm(1, u_j);
        }
        model.addConstr(expr4, GRB.EQUAL, pPocetBusov, "4_total_buses");

        // Pridať 5. typ podmienok - ∑_(j∈S) [u_j] + ∑_(ij,(i,j)∈F) [y_ij] ≤ V
        GRBLinExpr expr5 = new GRBLinExpr();

        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar u_j = this.u.get(j);
            expr5.addTerm(1, u_j);
        }

        for (SpojOld spoj_i : pSpoje.values())
        {
            int i = spoj_i.getID();
            for (SpojOld spoj_j : spoj_i.getMozneNasledujuceSpojeVodic())
            {
                int j = spoj_j.getID();
                GRBVar y_ij = this.y.get(new DvojicaOld<>(i,j));
                expr5.addTerm(1, y_ij);
            }
        }

        model.addConstr(expr5, GRB.EQUAL, pPocetVodicov, "5_total_drivers");

        // Pridať 6. typ podmienok - t_j ≥ t_i + DT_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();
            GRBVar t_j = this.t.get(j);

            for(SpojOld spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr6 = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar t_i = this.t.get(i);
                expr6.addTerm(1, t_i);

                int dt_ij = DT.get(new DvojicaOld<>(i,j));
                GRBVar x_ij = this.x.get(new DvojicaOld<>(i,j));
                expr6.addTerm(dt_ij, x_ij);

                expr6.addConstant(trvanieJ);
                expr6.addConstant(-ModelOld.K);
                expr6.addTerm(ModelOld.K, x_ij);

                model.addConstr(t_j, GRB.GREATER_EQUAL, expr6, "6_driving_time_constraint_" + i + "_" + j);
            }
        }

        // Pridať 7. typ podmienok - t_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar t_j = this.t.get(j);

            GRBLinExpr expr7 = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new DvojicaOld<>(ModelOld.DEPO, mod_j)).getCasPrejazdu();
            expr7.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr7.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr7.addConstant(-ModelOld.K);
            expr7.addTerm(ModelOld.K, u_j);

            model.addConstr(t_j, GRB.GREATER_EQUAL, expr7, "7_driving_time_depo_constraint_" + j);
        }

        // Pridať 8. typ podmienok - t_j + m(mpr_j,D) ≤ DT_max  pre j ∈ S
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr8 = new GRBLinExpr();
            GRBVar t_j = this.t.get(j);
            expr8.addTerm(1, t_j);

            int mpr_j = spoj_j.getMiestoPrichoduID();
            int dist = pUseky.get(new DvojicaOld<>(mpr_j, ModelOld.DEPO)).getCasPrejazdu();
            expr8.addConstant(dist);

            model.addConstr(expr8, GRB.LESS_EQUAL, ModelOld.DT_MAX, "8_driving_time_max_constraint_" + j);
        }

        // Pridať 9. typ podmienok - z_j ≥ z_i + T_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();
            GRBVar z_j = this.z.get(j);

            for(SpojOld spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr9 = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar z_i = this.z.get(i);
                expr9.addTerm(1, z_i);

                int t_ij = T.get(new DvojicaOld<>(i,j));
                GRBVar x_ij = this.x.get(new DvojicaOld<>(i,j));
                expr9.addTerm(t_ij, x_ij);

                expr9.addConstant(trvanieJ);

                expr9.addConstant(-ModelOld.K);
                expr9.addTerm(ModelOld.K, x_ij);

                model.addConstr(z_j, GRB.GREATER_EQUAL, expr9, "9_total_time_constraint_" + i + "_" + j);
            }
        }

        // Pridať 10. typ podmienok - z_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar z_j = this.z.get(j);

            GRBLinExpr expr10 = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchoduID();
            int dist = pUseky.get(new DvojicaOld<>(ModelOld.DEPO, mod_j)).getCasPrejazdu();
            expr10.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr10.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr10.addConstant(-ModelOld.K);
            expr10.addTerm(ModelOld.K, u_j);

            model.addConstr(z_j, GRB.GREATER_EQUAL, expr10, "10_total_time_depo_constraint_" + j);
        }

        // Pridať 11. typ podmienok - z_j + m(mpr_j,D) ≤ T_max  pre j ∈ S
        for (SpojOld spoj_j : pSpoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr11 = new GRBLinExpr();
            GRBVar z_j = this.z.get(j);
            expr11.addTerm(1, z_j);

            int mpr_j = spoj_j.getMiestoPrichoduID();
            int dist = pUseky.get(new DvojicaOld<>(mpr_j, ModelOld.DEPO)).getCasPrejazdu();
            expr11.addConstant(dist);

            model.addConstr(expr11, GRB.LESS_EQUAL, ModelOld.T_MAX, "11_total_time_max_constraint_" + j);
        }

        model.update();

        //optimalizuj model
        model.write("maxObsSpojov.lp");
        model.optimize();
    }

    public boolean vytvorSkontrolujTurnusy(LinkedHashMap<Integer, SpojOld> pSpoje,
                                           LinkedHashMap<DvojicaOld<Integer, Integer>, UsekOld> pUseky,
                                           LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> DT,
                                           LinkedHashMap<DvojicaOld<Integer, Integer>, Integer> T) throws GRBException {
        // Reset previous and successor trip IDs for all trips
        for (SpojOld spoj_i : pSpoje.values())
        {
            spoj_i.setNasledujuci(null);
            spoj_i.setPredchadzajuci(null);
        }

        this.turnusy = new ArrayList<>();

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

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_j_id = entry.getKey();
            GRBVar u_j = entry.getValue();

            try
            {
                if(u_j.get(GRB.DoubleAttr.X) == 1)
                {
                    SpojOld spoj_j = pSpoje.get(spoj_j_id);
                    turnusy.add(new TurnusOld(new ZmenaOld(spoj_j)));
                }
            }
            catch (GRBException e)
            {
                throw new RuntimeException(e);
            }
        }

        for (DvojicaOld<Integer, Integer> y_ij : y.keySet())
        {
            try {
                if (y.get(y_ij).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia y (0 či 1)
                    int spoj_i_id = y_ij.prva();

                    for (TurnusOld turnus: turnusy)
                    {
                        int poslednySpoj_id = turnus.getPrvaZmena().getPoslednySpoj().getID();
                        if(poslednySpoj_id == spoj_i_id)
                        {
                            int spoj_j_id = y_ij.druha();
                            SpojOld spoj_j = pSpoje.get(spoj_j_id);
                            turnus.pridajDruhuZmenu(new ZmenaOld(spoj_j));
                            break;
                        }
                    }
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        ArrayList<ArrayList<Integer>> porusenia;
        boolean bezPorusenia = true;
        for (int i = 0; i < turnusy.size(); i++)
        {
            porusenia = turnusy.get(i).getPrvaZmena().porusujeBP(i+1, pUseky, DT, T);
            if(!porusenia.isEmpty())
            {
                bezPorusenia = false;
                pridajPodmienky(this.model, porusenia);
            }
            if(turnusy.get(i).getDruhaZmena() != null)
            {
                porusenia = turnusy.get(i).getDruhaZmena().porusujeBP(i+1, pUseky, DT, T);
                if(!porusenia.isEmpty())
                {
                    bezPorusenia = false;
                    pridajPodmienky(this.model, porusenia);
                }
            }
        }

        if(bezPorusenia)
            return true;
        else
        {
            model.update();
            model.optimize();

            return false;
        }
    }

    public void vypisPremenne()
    {
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

    private void pridajPodmienky(GRBModel model, ArrayList<ArrayList<Integer>> porusenia) throws GRBException {
        for (ArrayList<Integer> spoje : porusenia)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < spoje.size() - 1; i++)
            {
                GRBVar x_ij = x.get(new DvojicaOld<>(spoje.get(i), spoje.get(i+1)));
                expr.addTerm(1, x_ij);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, spoje.size() - 2, "4_" + expr.getVar(0).get(GRB.StringAttr.VarName) + "_" + expr.getVar(expr.size()-1).get(GRB.StringAttr.VarName));
        }
    }
    public ArrayList<TurnusOld> getTurnusy() {
        return turnusy;
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

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_j_id = entry.getKey();
            GRBVar u_j = entry.getValue();

            try
            {
                if(u_j.get(GRB.DoubleAttr.X) == 1)
                {
                    SpojOld spoj_j = pSpoje.get(spoj_j_id);
                    turnusy.add(new TurnusOld(new ZmenaOld(spoj_j)));
                }
            }
            catch (GRBException e)
            {
                throw new RuntimeException(e);
            }
        }

        for (DvojicaOld<Integer, Integer> y_ij : y.keySet())
        {
            try {
                if (y.get(y_ij).get(GRB.DoubleAttr.X) == 1.0) {      //získaj hodnotu riešenia y (0 či 1)
                    int spoj_i_id = y_ij.prva();

                    for (TurnusOld turnus: turnusy)
                    {
                        int poslednySpoj_id = turnus.getPrvaZmena().getPoslednySpoj().getID();
                        if(poslednySpoj_id == spoj_i_id)
                        {
                            int spoj_j_id = y_ij.druha();
                            SpojOld spoj_j = pSpoje.get(spoj_j_id);
                            turnus.pridajDruhuZmenu(new ZmenaOld(spoj_j));
                            break;
                        }
                    }
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }

        return turnusy;
    }

    public int pocetObsSpojov() throws GRBException {
        int count = 0;
        for (GRBVar var : this.o.values())
        {
            if(var.get(GRB.DoubleAttr.X) == 1)
                count++;
        }
        return count;
    }
}