package br.edu.ifspsaocarlos.sdm.cuidador.interfaces;

/**
 * Interface de callback genérica.
 *
 * @param <T> Instância da classe específica
 */
public interface ICallback<T> {
    void OnComplete(T value);
}
