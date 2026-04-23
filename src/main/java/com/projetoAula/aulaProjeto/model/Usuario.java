package com.projetoAula.aulaProjeto.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Usuario {

    String  id, nome, email;
    java.sql.Date dataNascimento;

    //CONSTRUTOR PARA FORMS
    public Usuario() {

    }


    public Usuario(String email, String nome) {
        this.email = email;
        this.nome = nome;
    }

    public Usuario(java.sql.Date dataNascimento, String email, String id, String nome) {
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public java.sql.Date getDataNascimento() {
        return dataNascimento;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDataNascimento(java.sql.Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    
    public static Usuario converter(Map<String,Object> registro){
        String nome = (String) registro.get("nome");
        UUID id = (UUID) registro.get("id");
        java.sql.Date dataNascimento = (java.sql.Date) registro.get("data_nascimento");
        String email = (String) registro.get("email");
        return new Usuario(dataNascimento, email, id.toString(), nome);
    }

    public static ArrayList<Usuario> converterTodos(List<Map<String,Object>> registros){
        ArrayList<Usuario> aux = new ArrayList<>();
        for(Map<String,Object> registro : registros){
            aux.add(converter(registro));
        }
        return aux;
    }



}
