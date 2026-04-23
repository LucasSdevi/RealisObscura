package com.projetoAula.aulaProjeto;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.projetoAula.aulaProjeto.model.Usuario;
import com.projetoAula.aulaProjeto.model.UsuarioService;

@Controller
public class MenuController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/")
    public String paginaInicial(){
        return "index";
    }
    @GetMapping("/itens")
    public String paginaItens(){
        return "itens";
    }
    @GetMapping("/teorias")
    public String paginaTeorias(){
        return "teorias";
    }
    @GetMapping("/form")
    public String paginaForm(Model model) {
        model.addAttribute("usuario", new Usuario()); 
        return "form";
    }
    
    @PostMapping("/form")
    public String postUsuario(@ModelAttribute Usuario usuario, 
                                            Model model){

        usuarioService.inserirUsuario(usuario);
        return "sucesso";
        
    }
 

	@GetMapping("/listar")
	public String listarUuario(Model model){
		ArrayList<Usuario> usuarios = (ArrayList<Usuario>) usuarioService.listarUsuarios();
		model.addAttribute("usuarios", usuarios);
		return "listar";
	}




}
