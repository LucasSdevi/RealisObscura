package com.projetoAula.aulaProjeto.model;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioDAO usuarioDAO;

    public void inserirUsuario(Usuario usuario){
        
        usuarioDAO.inserirUsuario(usuario);

    }

     public Usuario mostrarUsuario(String uuid){
        
        return usuarioDAO.mostrarUsuario(uuid);

    }

    public ArrayList<Usuario> listarUsuarios(){
        
        return usuarioDAO.listarUsuarios();

    }

}
