package optimalizacia;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBLinExpr;
import com.gurobi.gurobi.GRBVar;
import mvp.Model;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelMaxObsluzenychSpojov extends ModelMaxObsadenosti {
    private double h_max;
    public ModelMaxObsluzenychSpojov(double h_max, LinkedHashMap<Integer, Linka> pLinky, int pPocetBusov, int pPocetVodicov,
                                     LinkedHashMap<Integer, Spoj> pSpoje,
                                     LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky,
                                     LinkedHashMap<Dvojica<Integer, Integer>, Integer> pDT,
                                     LinkedHashMap<Dvojica<Integer, Integer>, Integer> pT,
                                     ArrayList<String[]> pUdajeOiteraciach)
    {
        super(pLinky, pPocetBusov, pPocetVodicov, pSpoje, pUseky, pDT, pT, pUdajeOiteraciach);
        this.h_max = h_max;
        this.nazovSuboruModelu = "ModelMaxObsluzenychSpojov";
    }

    @Override
    protected void vytvorUcelovuFunkciu() throws GRBException
    {
        GRBLinExpr objExpr = new GRBLinExpr();

        /*
        for (Spoj spoj_j: this.spoje.values())
        {
            int j = spoj_j.getID();
            GRBVar o_j = this.o.get(j);
            objExpr.addConstant(Model.C_VODIC * spoj_j.getObsadenost());
            objExpr.addTerm(-Model.C_VODIC * spoj_j.getObsadenost(), o_j);
        }
         */

        objExpr.addConstant(Model.C_VODIC * this.spoje.size());
        for (GRBVar var : this.o.values())
        {
            objExpr.addTerm(-Model.C_VODIC, var);
        }

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

            this.model.addConstr(expr, GRB.GREATER_EQUAL, this.h_max, "1_podmienka_linka_" + linka_id);
        }
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
                " Hodnota účelovej funkcie: " + model.get(GRB.DoubleAttr.ObjVal) + "\n" +
                " Hodnota GAP [%]: " + String.format("%.4f", model.get(GRB.DoubleAttr.MIPGap) * 100) + "\n" +
                " Čas potrebný na vyriešenie modelu [s]: " + String.format("%.2f", casVypoctuModelu) + "\n";
    }

    public int getPocetObsluzenychSpojov()
    {
        int pocetObsluzenychSpojov = 0;
        for (Spoj spoj : this.spoje.values())
        {
            if(spoj.getObsluzenost())
                pocetObsluzenychSpojov++;
        }
        return pocetObsluzenychSpojov;
    }
}
