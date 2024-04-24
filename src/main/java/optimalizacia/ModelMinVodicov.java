package optimalizacia;

import com.gurobi.gurobi.*;

import mvp.Model;
import udaje.Dvojica;
import udaje.Spoj;
import udaje.Turnus;
import udaje.Zmena;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class ModelMinVodicov extends MatematickyModel {
    protected int pocetBusov;

    protected LinkedHashMap<Dvojica<Integer, Integer>, Integer> DT;
    protected LinkedHashMap<Dvojica<Integer, Integer>, Integer> T;

    protected LinkedHashMap<Dvojica<Integer, Integer>, Integer> cx;
    protected LinkedHashMap<Dvojica<Integer, Integer>, GRBVar> y;
    protected LinkedHashMap<Dvojica<Integer, Integer>, Integer> cy;
    protected LinkedHashMap<Integer, GRBVar> u;
    protected LinkedHashMap<Integer, Integer> cu;
    protected LinkedHashMap<Integer, GRBVar> v;
    protected LinkedHashMap<Integer, Integer> cv;
    protected Map<Integer, GRBVar> t;
    protected Map<Integer, GRBVar> z;

    protected int podmienkaTurnusy;

    public ModelMinVodicov(int pPocetBusov,
                           LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky,
                           LinkedHashMap<Dvojica<Integer, Integer>, Integer> pDT,
                           LinkedHashMap<Dvojica<Integer, Integer>, Integer> pT,
                           ArrayList<String[]> pUdajeOiteraciach)
    {
        super(pSpoje, pUseky, pUdajeOiteraciach);
        this.nazovSuboruModelu = "ModelMinVodicov";
        this.pocetBusov = pPocetBusov;
        this.useky = pUseky;
        this.DT = pDT;
        this.T = pT;
        this.podmienkaTurnusy = 10;
    }

    @Override
    protected void vytvorPremenne() throws GRBException
    {
        vytvorPremenneXsCenami();
        vytvorPremenneYsCenami();
        vytvorPremenneUsCenami();
        vytvorPremenneVsCenami();
        vytvorPremenneTaZ();
    }
    protected void vytvorPremenneXsCenami() throws GRBException
    {
        // Vytvoriť všetky premenné x_ij aj s cenami
        this.x = new LinkedHashMap<>();
        this.cx = new LinkedHashMap<>();
        for (Spoj spoj_i : this.spoje.values())
        {
            int i = spoj_i.getID();
            for (Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                Dvojica<Integer, Integer> prechod = new Dvojica<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichodu();
                int mod_j = spoj_j.getMiestoOdchodu();
                int dist = this.useky.get(new Dvojica<>(mpr_i, mod_j));
                this.x.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
                this.cx.put(prechod, dist);
            }
        }
    }
    protected void vytvorPremenneYsCenami() throws GRBException
    {
        // Vytvoriť všetky premenné y_ij s cenami
        this.y = new LinkedHashMap<>();
        this.cy = new LinkedHashMap<>();
        for (Spoj spoj_i : this.spoje.values())
        {
            int i = spoj_i.getID();
            for (Spoj spoj_j : spoj_i.getMozneNasledujuceSpojeSVymenouVodica())
            {
                int j = spoj_j.getID();
                Dvojica<Integer, Integer> prechod = new Dvojica<>(i, j);
                int mpr_i = spoj_i.getMiestoPrichodu();
                int mod_j = spoj_j.getMiestoOdchodu();
                int dist = this.useky.get(new Dvojica<>(mpr_i, Model.DEPO));
                dist += this.useky.get(new Dvojica<>(Model.DEPO, mod_j));
                this.y.put(prechod, model.addVar(0, 1, 0, GRB.BINARY, "y_" + i + "_" + j));
                this.cy.put(prechod, dist);
            }
        }
    }
    protected void vytvorPremenneUsCenami() throws GRBException
    {
        // Vytvoriť všetky premenné u_j aj s cenami
        this.u = new LinkedHashMap<>();
        this.cu = new LinkedHashMap<>();
        for (Spoj spoj_j : this.spoje.values())
        {
            int j = spoj_j.getID();
            int mod_j = spoj_j.getMiestoOdchodu();
            int dist = this.useky.get(new Dvojica<>(Model.DEPO, mod_j));
            this.u.put(j, model.addVar(0, 1, 0, GRB.BINARY, "u_" + j));
            this.cu.put(j, dist);
        }
    }
    protected void vytvorPremenneVsCenami() throws GRBException
    {
        // Vytvoriť všetky premenné v_i aj s cenami
        this.v = new LinkedHashMap<>();
        this.cv = new LinkedHashMap<>();
        for (Spoj spoj_i : this.spoje.values())
        {
            int i = spoj_i.getID();
            int mpr_i = spoj_i.getMiestoPrichodu();
            int dist = this.useky.get(new Dvojica<>(mpr_i, Model.DEPO));
            this.v.put(i, model.addVar(0, 1, 0, GRB.BINARY, "v_" + i));
            this.cv.put(i, dist);
        }
    }
    protected void vytvorPremenneTaZ() throws GRBException
    {
        // Vytvoriť všetky premenné t_j a z_j
        this.t = new LinkedHashMap<>();
        this.z = new LinkedHashMap<>();
        for (Spoj spoj_j : this.spoje.values())
        {
            int j = spoj_j.getID();
            this.t.put(j, model.addVar(0, Model.DT_MAX, 0, GRB.INTEGER, "t_" + j));
            this.z.put(j, model.addVar(0, Model.T_MAX, 0, GRB.INTEGER, "z_" + j));
        }
    }

    @Override
    protected void vytvorUcelovuFunkciu() throws GRBException
    {
        // Nastaviť účelovú funkciu -
        //  Minimalizuj    c_vodič * (|S| - ∑_(ij,(i,j)∈E) [x_ij] )
        GRBLinExpr objExpr = new GRBLinExpr();

        objExpr.addConstant(Model.C_VODIC * this.spoje.size());
        for (GRBVar var : this.x.values())
        {
            objExpr.addTerm(-Model.C_VODIC, var);
        }

        //  + c_km * ( ∑_(ij,(i,j)∈E) [m(mpr_i, mod_j) * x_ij]
        //           + ∑_(ij,(i,j)∈F) [(m(mpr_i,D) + m(D,mod_j)) * y_ij]
        //           + ∑_(j∈S) [m(D,mod_j) * u_j]
        //           + ∑_(i∈S) [m(mpr_i,D) * v_i])
        for (Map.Entry<Dvojica<Integer, Integer>, GRBVar> entry : this.x.entrySet())
        {
            Dvojica<Integer, Integer> prechod = entry.getKey();
            GRBVar x_ij = entry.getValue();
            int cx_ij = this.cx.get(prechod);
            objExpr.addTerm(Model.C_KM * cx_ij, x_ij);
        }

        for (Map.Entry<Dvojica<Integer, Integer>, GRBVar> entry : this.y.entrySet())
        {
            Dvojica<Integer, Integer> prechod = entry.getKey();
            GRBVar y_ij = entry.getValue();
            int cy_ij = this.cy.get(prechod);
            objExpr.addTerm(Model.C_KM * cy_ij, y_ij);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar u_j = entry.getValue();
            int cu_j = this.cu.get(spoj_id);
            objExpr.addTerm(Model.C_KM * cu_j, u_j);
        }

        for (Map.Entry<Integer, GRBVar> entry : this.v.entrySet())
        {
            int spoj_id = entry.getKey();
            GRBVar v_i = entry.getValue();
            int cv_i = this.cv.get(spoj_id);
            objExpr.addTerm(Model.C_KM * cv_i, v_i);
        }

        this.model.setObjective(objExpr, GRB.MINIMIZE);
    }

    @Override
    protected void vytvorPodmienky() throws GRBException
    {
        vytvorPodmienkyPrePrichodSpojovDoSpoja(1);
        vytvorPodmienkyPreOdchodSpojovZoSpoja(2);
        vytvorPodmienkyPrePocetBusov(3);
        vytvorPodmienkyPreCasJazdyX(4);
        vytvorPodmienkyPreCasJazdyU(5);
        vytvorPodmienkyPreMaximalnyCasJazdy(6);
        vytvorPodmienkyPreTrvanieZmenyX(7);
        vytvorPodmienkyPreTrvanieZmenyU(8);
        vytvorPodmienkyPreMaximalneTrvanieZmeny(9);
    }

    protected void vytvorPodmienkyPrePrichodSpojovDoSpoja(int pPoradie) throws GRBException
    {
        // Pridať 1. typ podmienok - každý spoj j bude nasledovať po jednom spoji i (alebo bude prvým spojom)
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
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

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpojeSVymenouVodica())
            {
                int i = spoj_i.getID();
                GRBVar y_ij = this.y.get(new Dvojica<>(i,j));
                expr.addTerm(1, y_ij);
            }

            this.model.addConstr(expr, GRB.EQUAL, 1, pPoradie + "_podmienka_prichod_spoj_" + j);
        }
    }
    protected void vytvorPodmienkyPreOdchodSpojovZoSpoja(int pPoradie) throws GRBException
    {
        // Pridať 2. typ podmienok - po každom spoji i bude nasledovať jeden spoj j (alebo bude posledným spojom)
        for (Spoj spoj_i : this.spoje.values())       // pre i = 1..n
        {
            int i = spoj_i.getID();
            GRBLinExpr expr = new GRBLinExpr();

            GRBVar v_i = this.v.get(i);
            expr.addTerm(1, v_i);

            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }

            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpojeSVymenouVodica())
            {
                int j = spoj_j.getID();
                GRBVar y_ij = this.y.get(new Dvojica<>(i,j));
                expr.addTerm(1, y_ij);
            }

            this.model.addConstr(expr, GRB.EQUAL, 1, pPoradie + "_podmienka_odchod_spoj_" + i);
        }
    }
    protected void vytvorPodmienkyPrePocetBusov(int pPoradie) throws GRBException
    {
        // Pridať 3. typ podmienok -> |S| - ∑_(ij,(i,j)∈E) [x_ij] - ∑_(ij,(i,j)∈F)[y_ij] = B
        GRBLinExpr expr = new GRBLinExpr();
        for (GRBVar var : this.x.values())
        {
            expr.addTerm(1, var);
        }
        for (GRBVar var : this.y.values())
        {
            expr.addTerm(1, var);
        }
        this.model.addConstr(expr, GRB.EQUAL, this.spoje.size() - this.pocetBusov, pPoradie + "_podmienka_pocet_busov");
    }
    protected void vytvorPodmienkyPreCasJazdyX(int pPoradie) throws GRBException
    {
        // Pridať 4. typ podmienok - t_j ≥ t_i + DT_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();

            GRBVar t_j = this.t.get(j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar t_i = this.t.get(i);
                expr.addTerm(1, t_i);

                int dt_ij = this.DT.get(new Dvojica<>(i,j));
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr.addTerm(dt_ij, x_ij);

                expr.addConstant(trvanieJ);
                expr.addConstant(-Model.K);
                expr.addTerm(Model.K, x_ij);

                this.model.addConstr(expr, GRB.LESS_EQUAL, t_j, pPoradie + "_podmienka_cas_jazdy_x_" + i + "_" + j);
            }
        }
    }
    protected void vytvorPodmienkyPreCasJazdyU(int pPoradie) throws GRBException {
        // Pridať 5. typ podmienok - t_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar t_j = this.t.get(j);

            GRBLinExpr expr = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchodu();
            int dist = this.useky.get(new Dvojica<>(Model.DEPO, mod_j));
            expr.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr.addConstant(-Model.K);
            expr.addTerm(Model.K, u_j);

            this.model.addConstr(expr, GRB.LESS_EQUAL, t_j, pPoradie + "_podmienka_cas_jazdy_u_" + j);
        }
    }
    protected void vytvorPodmienkyPreMaximalnyCasJazdy(int pPoradie) throws GRBException
    {
        // Pridať 6. typ podmienok - t_j + m(mpr_j,D) ≤ DT_max  pre j ∈ S
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr = new GRBLinExpr();
            GRBVar t_j = this.t.get(j);
            expr.addTerm(1, t_j);

            int mpr_j = spoj_j.getMiestoPrichodu();
            int dist = this.useky.get(new Dvojica<>(mpr_j, Model.DEPO));
            expr.addConstant(dist);

            this.model.addConstr(expr, GRB.LESS_EQUAL, Model.DT_MAX, pPoradie + "_podmienka_max_cas_jazdy_t_" + j);
        }
    }
    protected void vytvorPodmienkyPreTrvanieZmenyX(int pPoradie) throws GRBException
    {
        // Pridať 7. typ podmienok - z_j ≥ z_i + T_ij*x_ij + (cpr_j - cod_j) - K*(1 - x_ij)  pre (i,j) ∈ E
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            int trvanieJ = spoj_j.getTrvanieSpoja();
            GRBVar z_j = this.z.get(j);

            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                GRBLinExpr expr = new GRBLinExpr();

                int i = spoj_i.getID();
                GRBVar z_i = this.z.get(i);
                expr.addTerm(1, z_i);

                int t_ij = this.T.get(new Dvojica<>(i,j));
                GRBVar x_ij = this.x.get(new Dvojica<>(i,j));
                expr.addTerm(t_ij, x_ij);

                expr.addConstant(trvanieJ);
                expr.addConstant(-Model.K);
                expr.addTerm(Model.K, x_ij);

                this.model.addConstr(expr, GRB.LESS_EQUAL, z_j, pPoradie + "_podmienka_trvanie_zmeny_x_" + i + "_" + j);
            }
        }
    }

    protected void vytvorPodmienkyPreTrvanieZmenyU(int pPoradie) throws GRBException
    {
        // Pridať 8. typ podmienok - z_j ≥ m(D, mod_j) + (cpr_j - cod_j) - K*(1 - u_j)   pre j ∈ S
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar z_j = this.z.get(j);

            GRBLinExpr expr = new GRBLinExpr();

            int mod_j = spoj_j.getMiestoOdchodu();
            int dist = this.useky.get(new Dvojica<>(Model.DEPO, mod_j));
            expr.addConstant(dist);

            int trvanieJ = spoj_j.getTrvanieSpoja();
            expr.addConstant(trvanieJ);

            GRBVar u_j = this.u.get(j);
            expr.addConstant(-Model.K);
            expr.addTerm(Model.K, u_j);

            this.model.addConstr(expr, GRB.LESS_EQUAL, z_j, pPoradie + "_podmienka_trvanie_zmeny_spoj_" + j);
        }
    }
    protected void vytvorPodmienkyPreMaximalneTrvanieZmeny(int pPoradie) throws GRBException {
        // Pridať 9. typ podmienok - z_j + m(mpr_j,D) ≤ T_max  pre j ∈ S
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();

            GRBLinExpr expr = new GRBLinExpr();
            GRBVar z_j = this.z.get(j);
            expr.addTerm(1, z_j);

            int mpr_j = spoj_j.getMiestoPrichodu();
            int dist = this.useky.get(new Dvojica<>(mpr_j, Model.DEPO));
            expr.addConstant(dist);

            this.model.addConstr(expr, GRB.LESS_EQUAL, Model.T_MAX, pPoradie + "_podmienka_max_trvanie_zmeny_z_" + j);
        }
    }

    @Override
    public String getInformacieOmodeli() throws GRBException
    {
        int sumX = 0;
        for (GRBVar var : this.x.values())
        {
            if(var.get(GRB.DoubleAttr.X) == 1)
                sumX++;
        }
        int pocetVodicov = this.spoje.size() - sumX;
        return " Počet vodičov: " + pocetVodicov + "\n" +
                super.getInformacieOmodeli();
    }

    @Override
    protected void vytvorTurnusy()
    {
        this.turnusy = new ArrayList<>();

        // Z rozhodovacích premenných x_ij získaj všetky prepojenia spojov, a prepoj spoje
        for (Dvojica<Integer, Integer> x_ij : this.x.keySet())
        {
            try
            {
                if (this.x.get(x_ij).get(GRB.DoubleAttr.X) == 1.0)
                {
                    int spoj_i_id = x_ij.prvyPrvok();
                    Spoj spoj_i = this.spoje.get(spoj_i_id);

                    int spoj_j_id = x_ij.druhyPrvok();
                    Spoj spoj_j = this.spoje.get(spoj_j_id);

                    spoj_i.setNasledujuciSpoj(spoj_j);
                    spoj_j.setPredchadzajuciSpoj(spoj_i);
                }
            }
            catch (GRBException e)
            {
                e.printStackTrace();
            }
        }

        // vytvor prve zmeny turnusov
        int pocetTurnusov = 0;
        for (Map.Entry<Integer, GRBVar> entry : this.u.entrySet())
        {
            int spoj_j_id = entry.getKey();
            GRBVar u_j = entry.getValue();

            try
            {
                if(u_j.get(GRB.DoubleAttr.X) == 1)
                {
                    pocetTurnusov++;
                    Spoj spoj_j = this.spoje.get(spoj_j_id);
                    this.turnusy.add(new Turnus(pocetTurnusov, new Zmena(pocetTurnusov, 1, spoj_j, this.useky)));
                }
            }
            catch (GRBException e)
            {
                throw new RuntimeException(e);
            }
        }

        // vytvor druhe zmeny turnusov
        for (Dvojica<Integer, Integer> y_ij : this.y.keySet())
        {
            try
            {
                if (this.y.get(y_ij).get(GRB.DoubleAttr.X) == 1.0)
                {
                    int spoj_i_id = y_ij.prvyPrvok();

                    for (Turnus turnus: this.turnusy)
                    {
                        int poslednySpoj_id = turnus.getPrvaZmena().getPoslednySpoj().getID();
                        if(poslednySpoj_id == spoj_i_id)
                        {
                            int spoj_j_id = y_ij.druhyPrvok();
                            Spoj spoj_j = this.spoje.get(spoj_j_id);
                            turnus.pridajDruhuZmenu(new Zmena(turnus.getID(), 2, spoj_j, this.useky));
                            break;
                        }
                    }
                }
            }
            catch (GRBException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int skontrolujTurnusy() throws GRBException
    {
        ArrayList<ArrayList<Integer>> porusenia;
        int pocetPoruseni = 0;
        for (Turnus turnus : this.turnusy)
        {
            porusenia = turnus.getPrvaZmena().ziskajPoruseniaPrestavok(this.useky, this.DT, this.T);
            if(!porusenia.isEmpty())
            {
                pocetPoruseni += porusenia.size();
                pridajPodmienky(porusenia);
            }
            if(turnus.getDruhaZmena() != null)
            {
                porusenia = turnus.getDruhaZmena().ziskajPoruseniaPrestavok(this.useky, this.DT, this.T);
                if(!porusenia.isEmpty())
                {
                    pocetPoruseni += porusenia.size();
                    pridajPodmienky(porusenia);
                }
            }
        }

        return pocetPoruseni;
    }

    private void pridajPodmienky(ArrayList<ArrayList<Integer>> porusenia) throws GRBException
    {
        for (ArrayList<Integer> spoje : porusenia)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < spoje.size() - 1; i++)
            {
                GRBVar x_ij = this.x.get(new Dvojica<>(spoje.get(i), spoje.get(i+1)));
                expr.addTerm(1, x_ij);
            }
            this.model.addConstr(expr, GRB.LESS_EQUAL, spoje.size() - 2,
                    podmienkaTurnusy + "_podmienka_" + expr.getVar(0).get(GRB.StringAttr.VarName)
                            + expr.getVar(expr.size()-1).get(GRB.StringAttr.VarName) + "_" + new Random().nextInt(100) + "_");
        }
    }

    protected boolean prepocitajModel() throws GRBException
    {
        for (Spoj spoj_i : this.spoje.values())
        {
            spoj_i.setNasledujuciSpoj(null);
            spoj_i.setPredchadzajuciSpoj(null);
        }
        this.model.update();
        this.model.write(this.nazovSuboruModelu + ".lp");
        this.model.optimize();
        this.casVypoctuModelu += this.model.get(GRB.DoubleAttr.Runtime);

        int status = this.model.get(GRB.IntAttr.Status);
        if(status == 3 || status == 4 || status == 5)
            return false;

        int pocetRieseni = model.get(GRB.IntAttr.SolCount);
        if(pocetRieseni == 0)
            return false;
        else
            return true;
    }

    @Override
    public boolean vyriesModel(GRBEnv pEnv, double pGap, int pCasLimit) throws GRBException
    {
        boolean jeVyrieseny = this.vytvorAvypocitajModel(pEnv, pGap, pCasLimit);
        int iteracia = 1;
        if(!jeVyrieseny)
            return jeVyrieseny;

        this.vytvorTurnusy();
        int pocetPoruseni = this.skontrolujTurnusy();
        this.udajeOiteraciach.add(new String[] {String.valueOf(iteracia),
                String.format("%.2f", this.model.get(GRB.DoubleAttr.Runtime)), String.valueOf(pocetPoruseni)});
        while(pocetPoruseni != 0)
        {
            jeVyrieseny = this.prepocitajModel();
            if(!jeVyrieseny)
                return jeVyrieseny;
            iteracia++;
            this.vytvorTurnusy();
            pocetPoruseni = skontrolujTurnusy();
            this.udajeOiteraciach.add(new String[] {String.valueOf(iteracia),
                    String.format("%.2f", this.model.get(GRB.DoubleAttr.Runtime)), String.valueOf(pocetPoruseni)});
        }

        return jeVyrieseny;
    }
}
