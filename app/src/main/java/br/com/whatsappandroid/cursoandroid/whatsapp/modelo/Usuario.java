package br.com.whatsappandroid.cursoandroid.whatsapp.modelo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;

/**
 * Created by 204054947 on 23/10/2017.
 */

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;

    public Usuario(){

    }

    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("usuarios").child(getId()).setValue(this); //o this seta os valores do objeto, ou seja, o nome, email e id!

        //teste
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
