package br.edu.ifspsaocarlos.sdm.cuidador.enums;

/**
 * Created by ander on 10/11/2017.
 */

public enum NO {
    CONTATOS,
    CUIDADORES,
    IDOSOS,
    REMEDIOS,
    CHAT,
    INSTRUCOES,
    FOTOS,
    PROGRAMAS,
    MENSAGENS,
    CONTADOR_ALARME,
    FOTO_URI,
    INSTRUCAO_URI,
    ALERTA_REMEDIO,
    HORARIO;

    public static String getNo(NO no) {
        switch (no) {
            case CONTATOS:
                return "contatos";
            case CUIDADORES:
                return "cuidadores";
            case IDOSOS:
                return "idosos";
            case REMEDIOS:
                return "remedios";
            case CHAT:
                return "chat";
            case INSTRUCOES:
                return "instrucoes";
            case FOTOS:
                return "fotos";
            case PROGRAMAS:
                return "programas";
            case MENSAGENS:
                return "mensagens";
            case CONTADOR_ALARME:
                return "contadorAlarme";
            case FOTO_URI:
                return "fotoUri";
            case INSTRUCAO_URI:
                return "instrucaoUri";
            case ALERTA_REMEDIO:
                return "alertaRemedio";
            case HORARIO:
                return "horario";
            default:
                return "";
        }
    }
}
