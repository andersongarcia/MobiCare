package br.edu.ifspsaocarlos.sdm.cuidador.entities;

/**
 * Entidade para item de menu
 *
 * @author Anderson Canale Garcia
 */

public class MenuItem {
    private int titulo;
    private int idImagem;

    public MenuItem(int titulo, int idImagem) {
        this.titulo = titulo;
        this.idImagem = idImagem;
    }

    public int getTitulo() { return titulo; }

    public void setTitulo(int titulo) {
        this.titulo = titulo;
    }

    public int getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(int idImagem) {
        this.idImagem = idImagem;
    }
}
