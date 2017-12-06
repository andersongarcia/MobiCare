package br.edu.ifspsaocarlos.sdm.cuidador.callbacks;

import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.ICallback;

/**
 * Classe abstrata para implementação de interface de callback genérico
 * @param <T> Instância da classe específica
 *
 * @author Anderson Canale Garcia
 */
public abstract class CallbackGenerico<T> implements ICallback<T> {
    @Override
    public abstract void OnComplete(T value);
}
