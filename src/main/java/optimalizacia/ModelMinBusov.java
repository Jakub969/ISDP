package optimalizacia;

import com.gurobi.gurobi.*;
import udaje.Dvojica;
import udaje.Spoj;
import udaje.Turnus;
import udaje.Zmena;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ModelMinBusov extends MatematickyModel
{
    public ModelMinBusov(LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky,
                         ArrayList<String[]> pUdajeOiteraciach)
    {
        super(pSpoje, pUseky, pUdajeOiteraciach);
        this.nazovSuboruModelu = "ModelMinBusov";
    }

    @Override
    public boolean vyriesModel(GRBEnv pEnv, double pGap, int pCasLimit) throws GRBException
    {
        boolean jeVyrieseny = this.vytvorAvypocitajModel(pEnv, pGap, pCasLimit);
        if(!jeVyrieseny)
            return jeVyrieseny;

        vytvorTurnusy();
        return jeVyrieseny;
    }

    @Override
    protected void vytvorPremenne() throws GRBException
    {
        // Vytvoriť všetky premenné x
        this.x = new LinkedHashMap<>();
        for (Spoj spoj_i : this.spoje.values())
        {
            int i = spoj_i.getID();
            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                x.put(new Dvojica<>(i, j), model.addVar(0, 1, 0, GRB.BINARY, "x_" + i + "_" + j));
            }
        }
    }

    @Override
    protected void vytvorUcelovuFunkciu() throws GRBException
    {
        // Nastaviť účelovú funkciu - maximalizácia súčtu všetkých premenných x
        GRBLinExpr objExpr = new GRBLinExpr();
        for (GRBVar var : this.x.values())
        {
            objExpr.addTerm(1, var);
        }
        this.model.setObjective(objExpr, GRB.MAXIMIZE);
    }

    @Override
    protected void vytvorPodmienky() throws GRBException
    {
        this.vytvorPodmienkyPrePrichodSpojovDoSpoja();
        this.vytvorPodmienkyPreOdchodSpojovZoSpoja();
    }

    private void vytvorPodmienkyPrePrichodSpojovDoSpoja() throws GRBException
    {
        // Pridať prvý typ podmienok - každý spoj j bude nasledovať po maximálne jednom spoji i
        for (Spoj spoj_j : this.spoje.values())       // pre j = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int j = spoj_j.getID();
            for(Spoj spoj_i : spoj_j.getMoznePredchadzajuceSpoje())
            {
                int i = spoj_i.getID();
                GRBVar x_ij = x.get(new Dvojica<>(i,j));
                expr.addTerm(1, x_ij);
            }
            this.model.addConstr(expr, GRB.LESS_EQUAL, 1, "1_podmienka_prichod_spoj_" + j);
        }
    }

    private void vytvorPodmienkyPreOdchodSpojovZoSpoja() throws GRBException
    {
        // Pridať druhý typ podmienok - po každom spoji i bude nasledovať maximálne jeden spoj j
        for (Spoj spoj_i : this.spoje.values())       // pre i = 1..n
        {
            GRBLinExpr expr = new GRBLinExpr();
            int i = spoj_i.getID();
            for(Spoj spoj_j : spoj_i.getMozneNasledujuceSpoje())
            {
                int j = spoj_j.getID();
                expr.addTerm(1, x.get(new Dvojica<>(i,j)));
            }
            this.model.addConstr(expr, GRB.LESS_EQUAL, 1, "2_podmienka_odchod_spoj_" + i);
        }
    }

    @Override
    public String getInformacieOmodeli() throws GRBException {
        return " Počet autobusov: " + (this.spoje.size() - (int) model.get(GRB.DoubleAttr.ObjVal)) + "\n" +
                super.getInformacieOmodeli();
    }
    @Override
    protected void vytvorTurnusy()
    {
        this.turnusy = new ArrayList<>();

        // Z rozhodovacích premenných x_ij získaj všetky prepojenia spojov, a prepoj spoje
        for (Dvojica<Integer, Integer> x_ij : x.keySet())
        {
            try
            {
                if (x.get(x_ij).get(GRB.DoubleAttr.X) == 1.0)       //získaj hodnotu riešenia x (0 či 1)
                {
                    int spoj_i_id = x_ij.prvyPrvok();
                    Spoj spoj_i = this.spoje.get(spoj_i_id);

                    int spoj_j_id = x_ij.druhyPrvok();
                    Spoj spoj_j = this.spoje.get(spoj_j_id);

                    // If the pair (i, j) is selected, set j as the successor of i and i as the previous of j
                    spoj_i.setNasledujuciSpoj(spoj_j);  // Set j as the successor of i
                    spoj_j.setPredchadzajuciSpoj(spoj_i);  // Set i as the previous of j
                }
            }
            catch (GRBException e)
            {
                e.printStackTrace();
            }
        }

        //Vytvorenie turnusov
        int pocetTurnusov = 1;
        for (Spoj spoj_i : this.spoje.values())
        {
            if (spoj_i.getPredchadzajuciSpoj() == null)
            {
                this.turnusy.add(new Turnus(pocetTurnusov, new Zmena(pocetTurnusov, 1, spoj_i, this.useky)));
                pocetTurnusov++;
            }
        }
    }
}
