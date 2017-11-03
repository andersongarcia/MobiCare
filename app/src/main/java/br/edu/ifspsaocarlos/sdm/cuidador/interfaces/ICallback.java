package br.edu.ifspsaocarlos.sdm.cuidador.interfaces;

/**
 * Created by ander on 02/11/2017.
 */

public interface ICallback<T> {
    void OnComplete(T value);
}
