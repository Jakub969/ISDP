package mvp;

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
}
