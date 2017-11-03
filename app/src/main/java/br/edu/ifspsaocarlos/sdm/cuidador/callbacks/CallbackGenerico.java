package br.edu.ifspsaocarlos.sdm.cuidador.callbacks;

import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.ICallback;

/**
 * Created by ander on 02/11/2017.
 */

public abstract class CallbackGenerico<T> implements ICallback<T> {
    @Override
    public abstract void OnComplete(T value);
}
