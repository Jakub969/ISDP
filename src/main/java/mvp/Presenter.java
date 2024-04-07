package mvp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Presenter {
    private Model model;

    public Presenter()
    {
        this.model = new Model();
    }

    public String nacitajData(String pSuborUseky, String pSuborSpoje)
    {
        return this.model.nacitajData(pSuborUseky, pSuborSpoje);
    }

    public String[][] vypisVsetkySpoje() {
        return this.model.vypisVsetkySpoje();
    }
    public LinkedHashMap<Integer, String[][]> vypisVsetkyLinky() {
        return this.model.vypisVsetkyLinky();
    }
    public String vykonajMinimalizaciuAutobusov(ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        return this.model.vykonajMinimalizaciuAutobusov(pTurnusyUdaje, pSpojeUdaje);
    }

    public String vykonajMinimalizaciuPrazdnychPrejazdov(ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        return this.model.vykonajMinimalizaciuPrazdnychPrejazdov(pTurnusyUdaje, pSpojeUdaje);
    }

    public String vykonajMinimalizaciuVodicov(ArrayList<String[]> pTurnusyUdaje, ArrayList<String[][]> pSpojeUdaje)
    {
        return this.model.vykonajMinimalizaciuVodicov(pTurnusyUdaje, pSpojeUdaje);
    }
}
