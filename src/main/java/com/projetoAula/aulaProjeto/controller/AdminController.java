package com.projetoAula.aulaProjeto.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetoAula.aulaProjeto.model.Post;
import com.projetoAula.aulaProjeto.model.PostService;
import com.projetoAula.aulaProjeto.model.Usuario;
import com.projetoAula.aulaProjeto.model.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @Autowired
    private UsuarioService usuarioService;

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping
    public String dashboard(Model model) {
        List<Post> posts = postService.listarTodos();
        model.addAttribute("posts", posts);
        return "admin/dashboard";
    }

    // ── Novo Post ──────────────────────────────────────────────────
    @GetMapping("/posts/novo")
    public String formNovoPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categorias", Post.Categoria.values());
        return "admin/form-post";
    }

    @PostMapping("/posts/novo")
    public String criarPost(@ModelAttribute Post post,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        Usuario autor = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        post.setAutor(autor);
        postService.criarPost(post);
        redirectAttributes.addFlashAttribute("successMsg",
                "Post \"" + post.getTitulo() + "\" publicado nos arquivos.");
        return "redirect:/admin";
    }

    // ── Editar Post ────────────────────────────────────────────────
    @GetMapping("/posts/editar/{id}")
    public String formEditarPost(@PathVariable Long id, Model model) {
        Post post = postService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        model.addAttribute("post", post);
        model.addAttribute("categorias", Post.Categoria.values());
        return "admin/form-post";
    }

    @PostMapping("/posts/editar/{id}")
    public String atualizarPost(@PathVariable Long id,
                                @ModelAttribute Post post,
                                RedirectAttributes redirectAttributes) {
        postService.atualizarPost(id, post);
        redirectAttributes.addFlashAttribute("successMsg",
                "Post \"" + post.getTitulo() + "\" atualizado.");
        return "redirect:/admin";
    }

    // ── Excluir Post ───────────────────────────────────────────────
    @PostMapping("/posts/deletar/{id}")
    public String deletarPost(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        Post post = postService.buscarPorId(id).orElse(null);
        String titulo = post != null ? post.getTitulo() : "Desconhecido";
        postService.deletarPost(id);
        redirectAttributes.addFlashAttribute("successMsg",
                "Post \"" + titulo + "\" removido permanentemente.");
        return "redirect:/admin";
    }
}
