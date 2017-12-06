package br.edu.ifspsaocarlos.sdm.cuidador.callbacks;

import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.ICallbackSimples;

/**
 * Classe abstrata para implementação de interface de callback simples
 *
 * @author Anderson Canale Garcia
 */
public abstract class CallbackSimples implements ICallbackSimples {
    @Override
    public abstract void OnComplete();
}
