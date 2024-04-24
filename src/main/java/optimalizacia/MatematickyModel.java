package optimalizacia;

import com.gurobi.gurobi.*;
import udaje.Dvojica;
import udaje.Spoj;
import udaje.Turnus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class MatematickyModel
{
    protected double casVypoctuModelu;
    protected ArrayList<Turnus> turnusy;

    protected ArrayList<String[]> udajeOiteraciach;

    protected LinkedHashMap<Integer, Spoj> spoje;
    protected LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky;

    protected GRBModel model;
    protected LinkedHashMap<Dvojica<Integer, Integer>, GRBVar> x;
    protected String nazovSuboruModelu;

    protected abstract void vytvorPremenne() throws GRBException;
    protected abstract void vytvorUcelovuFunkciu() throws GRBException;
    protected abstract void vytvorPodmienky() throws GRBException;
    protected abstract void vytvorTurnusy();

    public MatematickyModel(LinkedHashMap<Integer, Spoj> pSpoje, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky,
                            ArrayList<String[]> pUdajeOiteraciach)
    {
        this.spoje = pSpoje;
        this.useky = pUseky;
        for (Spoj spoj_i : this.spoje.values())
        {
            spoj_i.setNasledujuciSpoj(null);
            spoj_i.setPredchadzajuciSpoj(null);
        }
        this.casVypoctuModelu = 0;
        this.udajeOiteraciach = pUdajeOiteraciach;
    }

    abstract public boolean vyriesModel(GRBEnv pEnv, double pGap, int pCasLimit) throws GRBException;

    protected boolean vytvorAvypocitajModel(GRBEnv pEnv, double pGap, int pCasLimit) throws GRBException
    {

        this.model = new GRBModel(pEnv);
        this.model.set(GRB.StringParam.LogFile, this.nazovSuboruModelu + ".log");
        this.model.set(GRB.DoubleParam.MIPGap, pGap);

        if(pCasLimit > 0)
            this.model.set(GRB.DoubleParam.TimeLimit, pCasLimit);

        //vytvorenie premennych
        this.vytvorPremenne();
        this.model.update();

        //vytvorenie účelovej funkcie
        this.vytvorUcelovuFunkciu();
        this.model.update();

        //vytvorenie všetkých podmienok
        this.vytvorPodmienky();
        this.model.update();

        //zapísanie modelu do súboru
        this.model.write(this.nazovSuboruModelu + ".lp");
        //System.out.println(model.get(GRB.IntAttr.NumVars));
        //System.out.println(model.get(GRB.IntAttr.NumConstrs));
        this.model.optimize();

        this.casVypoctuModelu = this.model.get(GRB.DoubleAttr.Runtime);

        int status = this.model.get(GRB.IntAttr.Status);
        if(status == 3 || status == 4 || status == 5)
            return false;

        int pocetRieseni = model.get(GRB.IntAttr.SolCount);
        if(pocetRieseni == 0)
            return false;
        else
            return true;
    }

    public String getInformacieOmodeli() throws GRBException
    {
        return " Hodnota účelovej funkcie: " + model.get(GRB.DoubleAttr.ObjVal) + "\n" +
                " Hodnota GAP [%]: " + String.format("%.4f", model.get(GRB.DoubleAttr.MIPGap) * 100) + "\n" +
                " Čas potrebný na vyriešenie modelu [s]: " + String.format("%.2f", casVypoctuModelu) + "\n";
    }

    protected LinkedHashMap<Dvojica<Integer, Integer>, GRBVar> getX()
    {
        return this.x;
    }
    public void zrusModel()
    {
        this.model.dispose();
    }

    public ArrayList<Turnus> getTurnusy()
    {
        return this.turnusy;
    }
}
