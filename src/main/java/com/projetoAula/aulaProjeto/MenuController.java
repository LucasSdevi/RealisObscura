package com.projetoAula.aulaProjeto;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetoAula.aulaProjeto.model.Post;
import com.projetoAula.aulaProjeto.model.PostService;
import com.projetoAula.aulaProjeto.model.Usuario;
import com.projetoAula.aulaProjeto.model.UsuarioService;

@Controller
public class MenuController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String paginaInicial(Model model) {
        List<Post> postsRecentes = postService.listarTodos();
        // Limitar a 5 posts mais recentes para o feed
        if (postsRecentes.size() > 5) {
            postsRecentes = postsRecentes.subList(0, 5);
        }
        model.addAttribute("posts", postsRecentes);
        return "index";
    }

    // ── Cadastro (apenas admin) ──────────────────────────────────────
    @GetMapping("/form")
    public String paginaForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form";
    }

    @PostMapping("/form")
    public String postUsuario(@ModelAttribute Usuario usuario,
                              RedirectAttributes redirectAttributes) {
        usuarioService.inserirUsuario(usuario);
        redirectAttributes.addFlashAttribute("successMsg",
                "Novo agente registrado na base de dados.");
        return "redirect:/listar";
    }

    // ── Listagem ─────────────────────────────────────────────────────
    @GetMapping("/listar")
    public String listarUsuario(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listar";
    }

    // ── Edição ───────────────────────────────────────────────────────
    @GetMapping("/listar/editar/{id}")
    public String editarUsuario(@PathVariable UUID id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "form";
    }

    @PostMapping("/listar/editar/{id}")
    public String atualizarUsuario(@PathVariable UUID id,
                                   @ModelAttribute Usuario usuario,
                                   RedirectAttributes redirectAttributes) {
        usuarioService.atualizarUsuario(id, usuario);
        redirectAttributes.addFlashAttribute("successMsg",
                "Registro do agente atualizado.");
        return "redirect:/listar";
    }

    // ── Exclusão ─────────────────────────────────────────────────────
    @PostMapping("/listar/deletar/{id}")
    public String deletarUsuario(@PathVariable UUID id,
                                 RedirectAttributes redirectAttributes) {
        usuarioService.deletarUsuario(id);
        redirectAttributes.addFlashAttribute("successMsg",
                "Agente removido permanentemente dos arquivos.");
        return "redirect:/listar";
    }
}
