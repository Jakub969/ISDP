package subory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import udaje.Dvojica;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class UsekDTO {
    public int u;
    public int v;
    public int c;

    public void nacitajUsekyJSON(
            File subor,
            LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        List<UsekDTO> useky = mapper.readValue(
                subor,
                new TypeReference<List<UsekDTO>>() {}
        );

        for (UsekDTO u : useky) {
            pUseky.put(new Dvojica<>(u.u, u.v), u.c);
            pUseky.put(new Dvojica<>(u.v, u.u), u.c);
            pUseky.put(new Dvojica<>(u.u, u.u), 0);
            pUseky.put(new Dvojica<>(u.v, u.v), 0);
        }
    }

}
