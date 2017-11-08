package br.edu.ifspsaocarlos.sdm.cuidador.data;

import java.util.List;
import java.util.Observable;

/**
 * Created by Anderson on 08/11/2017.
 */

public abstract class Container<T> extends Observable {
    private Container<T> container;
    public List<T> lista;

    public void adicionar(T item) {
        lista.add(item);
    }
}
