package br.edu.ifspsaocarlos.sdm.cuidador.enums;

/**
 * Created by ander on 12/11/2017.
 */

public enum AlertaRemedio {
    ENVIO,
    CONFIRMACAO_IDOSO,
    CONFIRMACAO_CUIDADOR;

    public static String getString(AlertaRemedio alerta) {
        switch (alerta) {
            case ENVIO:
                return "envio";
            case CONFIRMACAO_IDOSO:
                return "confirmacaoIdoso";
            case CONFIRMACAO_CUIDADOR:
                return "confirmacaoCuidador";
            default:
                return "";
        }
    }
}
