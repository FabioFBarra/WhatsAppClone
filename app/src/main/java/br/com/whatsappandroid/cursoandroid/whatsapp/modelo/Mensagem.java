package br.com.whatsappandroid.cursoandroid.whatsapp.modelo;

/**
 * Created by 204054947 on 24/11/2017.
 */

public class Mensagem {
    private String idUsuario;
    private String mensagem;

    public Mensagem() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
