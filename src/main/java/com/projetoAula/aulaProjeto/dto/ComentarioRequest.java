package com.projetoAula.aulaProjeto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioRequest {

    @NotBlank(message = "O comentário não pode estar vazio")
    @Size(max = 2000, message = "Comentário muito longo (máx. 2000 caracteres)")
    private String conteudo;

    public ComentarioRequest() {
    }

    public ComentarioRequest(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
