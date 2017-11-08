package br.edu.ifspsaocarlos.sdm.cuidador.data;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;

/**
 * Created by Anderson on 08/11/2017.
 */

public class RemediosContainer extends Container<Remedio> {
    private static RemediosContainer container;

    public static Container newInstance() {
        if(container == null)
            container = new RemediosContainer();
        return container;
    }
}
