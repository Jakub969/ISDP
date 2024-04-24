package optimalizacia;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBLinExpr;
import com.gurobi.gurobi.GRBVar;

import udaje.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelMaxObsadenosti extends ModelMinVodicov {
    protected int pocetVodicov;
    protected LinkedHashMap<Integer, Linka> linky;
    protected Map<Integer, GRBVar> o;
    protected Map<Integer, Integer> obs;
    protected GRBVar h;
    public ModelMaxObsadenosti(LinkedHashMap<Integer, Linka> pLinky, int pPocetBusov, int pPocetVodicov,
                               LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky,
                               LinkedHashMap<Dvojica<Integer, Integer>, Integer> pDT,
                               LinkedHashMap<Dvojica<Integer, Integer>, Integer> pT,
                               ArrayList<String[]> pUdajeOiteraciach)
    {
        super(pPocetBusov, pSpoje, pUseky, pDT, pT, pUdajeOiteraciach);
        this.nazovSuboruModelu = "ModelMaxObsadenosti";
        this.pocetVodicov = pPocetVodicov;
        this.linky = pLinky;
        for (Spoj spoj : this.spoje.values())
        {
            spoj.nastavObsluzenost(false);
        }
        podmienkaTurnusy = 12;
    }

    @Override
    protected void vytvorPremenne() throws GRBException
    {
        super.vytvorPremenne();
        vytvorPremenneOsCenami();
    }

    protected void vytvorPremenneOsCenami() throws GRBException
    {
        // Vytvoriť všetky premenné t_j a z_j, o_j, obs_j
        this.o = new LinkedHashMap<>();
        this.obs = new LinkedHashMap<>();
        for (Spoj spoj_j : this.spoje.values())
        {
            int j = spoj_j.getID();
            this.o.put(j, model.addVar(0, 1, 0, GRB.BINARY, "o_" + j));
            this.obs.put(j, spoj_j.getObsadenost());
        }
    }

    @Override
    protected void vytvorUcelovuFunkciu() throws GRBException
    {
        GRBLinExpr objExpr = new GRBLinExpr();

        this.h = this.model.addVar(0, 1, 0, GRB.CONTINUOUS, "h");
        objExpr.addTerm(1, this.h);

        this.model.setObjective(objExpr, GRB.MAXIMIZE);
    }

    @Override
    protected void vytvorPodmienky() throws GRBException
    {
        vytvorPodmienkyPreLinky();
        vytvorPodmienkyPrePrichodSpojovDoSpoja(2);
        vytvorPodmienkyPreOdchodSpojovZoSpoja(3);
        vytvorPodmienkyPrePocetBusov(4);
        vytvorPodmienkyPrePocetVodicov();
        vytvorPodmienkyPreCasJazdyX(6);
        vytvorPodmienkyPreCasJazdyU(7);
        vytvorPodmienkyPreMaximalnyCasJazdy(8);
        vytvorPodmienkyPreTrvanieZmenyX(9);
        vytvorPodmienkyPreTrvanieZmenyU(10);
        vytvorPodmienkyPreMaximalneTrvanieZmeny(11);
    }

    protected void vytvorPodmienkyPreLinky() throws GRBException
    {
        // Pridať 1. typ podmienok - (∑_(i∈L_k) [OBS_i * o_i]) / (∑_(i∈L_k) [OBS_i]) ≥ y
        for (Linka linka_k: this.linky.values())
        {
            int linka_id = linka_k.getID();
            int obsLinky = linka_k.getObsadenost();
            GRBLinExpr expr = new GRBLinExpr();

            for (Spoj spoj_i: linka_k.getSpoje())
            {
                int i = spoj_i.getID();
                int obsSpoja = this.obs.get(i);

                GRBVar o_i = this.o.get(i);
                expr.addTerm((double) obsSpoja / obsLinky, o_i);
            }

            this.model.addConstr(expr, GRB.GREATER_EQUAL, this.h, "1_podmienka_linka_" + linka_id);
        }
    }
    @Override
    protected void vytvorPodmienkyPrePrichodSpojovDoSpoja(int pPoradie) throws GRBException
    {
        // Pridať 2. typ podmienok - u_j + ∑_(i,(i,j)∈E) [x_ij] + ∑_(i,(i,j)∈E) [y_ij] = o_j
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

            GRBVar o_j = this.o.get(j);

            this.model.addConstr(expr, GRB.EQUAL, o_j, pPoradie + "_podmienka_prichod_spoj_" + j);
        }
    }
    @Override
    protected void vytvorPodmienkyPreOdchodSpojovZoSpoja(int pPoradie) throws GRBException
    {
        // Pridať 3. typ podmienok - v_i + ∑_(i,(i,j)∈E) [x_ij] +∑_(i,(i,j)∈E) [y_ij] = o_i
        for (Spoj spoj_i : this.spoje.values())       // pre i = 1..n
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

            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpojeSVymenouVodica())
            {
                int j = spoj_j.getID();
                GRBVar y_ij = this.y.get(new Dvojica<>(i,j));
                expr.addTerm(1, y_ij);
            }

            GRBVar o_i = this.o.get(i);

            this.model.addConstr(expr, GRB.EQUAL, o_i, pPoradie + "_podmienka_odchod_spoj_" + i);
        }
    }
    @Override
    protected void vytvorPodmienkyPrePocetBusov(int pPoradie) throws GRBException
    {
        // Pridať 4. typ podmienok -> ∑_(j∈S) u_j = B
        GRBLinExpr expr = new GRBLinExpr();
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            int j = spoj_j.getID();
            GRBVar u_j = this.u.get(j);
            expr.addTerm(1, u_j);
        }
        model.addConstr(expr, GRB.EQUAL, this.pocetBusov, pPoradie + "_podmienka_pocet_busov");
    }
    protected void vytvorPodmienkyPrePocetVodicov() throws GRBException
    {
        // Pridať 5. typ podmienok - ∑_(j∈S) [u_j] + ∑_(ij,(i,j)∈F) [y_ij] = V
        GRBLinExpr expr = new GRBLinExpr();

        for (GRBVar var : this.u.values())
        {
            expr.addTerm(1, var);
        }

        for (GRBVar var : this.y.values())
        {
            expr.addTerm(1, var);
        }

        this.model.addConstr(expr, GRB.EQUAL, this.pocetVodicov, 5 + "_podmienka_pocet_vodicov");
    }

    @Override
    protected void vytvorTurnusy()
    {
        super.vytvorTurnusy();
        for(Spoj spoj : this.spoje.values())
        {
            if(spoj.getNasledujuciSpoj() != null || spoj.getPredchadzajuciSpoj() != null)
                spoj.nastavObsluzenost(true);
        }
    }

    @Override
    protected boolean prepocitajModel() throws GRBException
    {
        for (Spoj spoj : this.spoje.values())
        {
            spoj.nastavObsluzenost(false);
        }
        return super.prepocitajModel();
    }

    @Override
    public String getInformacieOmodeli() throws GRBException
    {
        int pocetObsluzenychCestujucich = 0;
        int pocetNeobsluzenychCestujucich = 0;
        int pocetObsluzenychSpojov = 0;
        for (Spoj spoj : this.spoje.values())
        {
            if(spoj.getObsluzenost())
            {
                pocetObsluzenychSpojov++;
                pocetObsluzenychCestujucich += spoj.getObsadenost();
            }
            else
                pocetNeobsluzenychCestujucich += spoj.getObsadenost();
        }
        return " Počet obslúžených spojov: " + pocetObsluzenychSpojov + "\n" +
                " Počet všetkých spojov: " + this.spoje.size() + "\n" +
                " Počet obslúžených cestujúcich: " + pocetObsluzenychCestujucich + "\n" +
                " Počet neobslúžených cestujúcich: " + pocetNeobsluzenychCestujucich + "\n" +
                " Hodnota účelovej funkcie (premennej h): " + String.format("%.6f", model.get(GRB.DoubleAttr.ObjVal))  + "\n" +
                " Hodnota GAP [%]: " + String.format("%.4f", model.get(GRB.DoubleAttr.MIPGap) * 100) + "\n" +
                " Čas potrebný na vyriešenie modelu [s]: " + String.format("%.2f", casVypoctuModelu) + "\n";
    }

    public double getVysledok() throws GRBException
    {
        return model.get(GRB.DoubleAttr.ObjVal);
    }

    public double getCas()
    {
        return this.casVypoctuModelu;
    }

    public double getGap() throws GRBException
    {
        return model.get(GRB.DoubleAttr.MIPGap) * 100;
    }
}