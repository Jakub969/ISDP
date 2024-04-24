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

public class ModelMinNeobsluzenychCestujucich extends ModelMaxObsluzenychSpojov
{
    public ModelMinNeobsluzenychCestujucich(double h_max, LinkedHashMap<Integer, Linka> pLinky, int pPocetBusov, int pPocetVodicov, LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pDT, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pT, ArrayList<String[]> pUdajeOiteraciach)
    {
        super(h_max, pLinky, pPocetBusov, pPocetVodicov, pSpoje, pUseky, pDT, pT, pUdajeOiteraciach);
    }

    protected void vytvorUcelovuFunkciu() throws GRBException
    {
        GRBLinExpr objExpr = new GRBLinExpr();

        for (Spoj spoj_j: this.spoje.values())
        {
            int j = spoj_j.getID();
            GRBVar o_j = this.o.get(j);
            objExpr.addConstant(Model.C_VODIC * spoj_j.getObsadenost());
            objExpr.addTerm(-Model.C_VODIC * spoj_j.getObsadenost(), o_j);
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
}
