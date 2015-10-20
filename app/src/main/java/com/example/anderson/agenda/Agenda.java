package com.example.anderson.agenda;

import java.io.Serializable;

public class Agenda implements Serializable{


    private int id;
    private String nome;
    private int idade;
    private String cidade;
    private int uf;
    private String email;
    private byte[] foto;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getUf() {
        return uf;
    }
    public void setUf(int uf) {
        this.uf = uf;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getFoto() {
        return foto;
    }
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
