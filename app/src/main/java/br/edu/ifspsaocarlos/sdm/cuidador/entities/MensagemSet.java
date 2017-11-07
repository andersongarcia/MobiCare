package br.edu.ifspsaocarlos.sdm.cuidador.entities;

/**
 * Created by ander on 07/11/2017.
 */

public class MensagemSet {
    private Mensagem mensagem;
    private Contato emissor;
    private Contato destinatario;

    public MensagemSet(Mensagem mensagem, Contato emissor, Contato destinatario){
        this.mensagem = mensagem;
        this.emissor = emissor;
        this.destinatario = destinatario;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public Contato getEmissor() {
        return emissor;
    }

    public void setEmissor(Contato emissor) {
        this.emissor = emissor;
    }

    public Contato getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Contato destinatario) {
        this.destinatario = destinatario;
    }
}
