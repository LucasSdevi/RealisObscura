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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetoAula.aulaProjeto.dto.ComentarioRequest;
import com.projetoAula.aulaProjeto.model.Comentario;
import com.projetoAula.aulaProjeto.model.ComentarioService;
import com.projetoAula.aulaProjeto.model.Post;
import com.projetoAula.aulaProjeto.model.Post.Categoria;
import com.projetoAula.aulaProjeto.model.PostService;
import com.projetoAula.aulaProjeto.model.Usuario;
import com.projetoAula.aulaProjeto.model.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private UsuarioService usuarioService;

    // ── Listagem de Teorias (todos os posts) ──────────────────────
    @GetMapping("/teorias")
    public String paginaTeorias(Model model) {
        List<Post> posts = postService.listarTodos();
        model.addAttribute("posts", posts);
        return "teorias";
    }

    // ── Itens (apenas categoria ARTEFATO) ─────────────────────────
    @GetMapping("/itens")
    public String paginaItens(Model model) {
        List<Post> artefatos = postService.listarPorCategoria(Categoria.ARTEFATO);
        model.addAttribute("posts", artefatos);
        return "itens";
    }

    // ── Detalhe do Post + Comentários ─────────────────────────────
    @GetMapping("/posts/{id}")
    public String detalhePost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        List<Comentario> comentarios = comentarioService.listarPorPost(id);

        // Verifica se o usuário logado pode editar/excluir este post
        boolean podeEditar = false;
        if (principal != null) {
            Usuario usuario = usuarioService.buscarPorNome(principal.getName()).orElse(null);
            if (usuario != null) {
                podeEditar = usuario.getId().equals(post.getAutor().getId())
                          || "admin".equals(usuario.getRole());
            }
        }

        model.addAttribute("post", post);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("comentarioRequest", new ComentarioRequest());
        model.addAttribute("podeEditar", podeEditar);
        return "post-detalhe";
    }

    // ── Criar Comentário ──────────────────────────────────────────
    @PostMapping("/posts/{id}/comentar")
    public String comentar(@PathVariable Long id,
                           @Valid @ModelAttribute("comentarioRequest") ComentarioRequest request,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Acesso restrito: faça login para comentar.");
            return "redirect:/login";
        }

        Usuario autor = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        comentarioService.criarComentario(id, autor.getId(), request.getConteudo());
        redirectAttributes.addFlashAttribute("successMsg", "Comentário adicionado ao arquivo.");
        return "redirect:/posts/" + id;
    }

    // ── Deletar Comentário (admin ou dono) ────────────────────────
    @PostMapping("/posts/{postId}/comentario/{comentarioId}/deletar")
    public String deletarComentario(@PathVariable Long postId,
                                    @PathVariable Long comentarioId,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        comentarioService.deletarComentario(comentarioId);
        redirectAttributes.addFlashAttribute("successMsg", "Comentário removido.");
        return "redirect:/posts/" + postId;
    }

    // ── Novo Post (usuário logado) ────────────────────────────────
    @GetMapping("/posts/novo")
    public String formNovoPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categorias", Post.Categoria.values());
        return "form-post";
    }

    @PostMapping("/posts/novo")
    public String criarPost(@ModelAttribute Post post,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Acesso restrito: faça login para publicar.");
            return "redirect:/login";
        }
        Usuario autor = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        post.setAutor(autor);
        postService.criarPost(post);
        redirectAttributes.addFlashAttribute("successMsg",
                "Arquivo \"" + post.getTitulo() + "\" submetido com sucesso.");
        return "redirect:/teorias";
    }

    // ── Editar Post (dono ou admin) ───────────────────────────────
    @GetMapping("/posts/editar/{id}")
    public String formEditarPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        // Só o autor ou admin pode editar
        Usuario usuario = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!post.getAutor().getId().equals(usuario.getId())
                && !"admin".equals(usuario.getRole())) {
            throw new RuntimeException("Acesso negado: apenas o autor pode editar este arquivo.");
        }

        model.addAttribute("post", post);
        model.addAttribute("categorias", Post.Categoria.values());
        return "form-post";
    }

    @PostMapping("/posts/editar/{id}")
    public String atualizarPost(@PathVariable Long id,
                                @ModelAttribute Post post,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        Post existente = postService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Usuario usuario = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!existente.getAutor().getId().equals(usuario.getId())
                && !"admin".equals(usuario.getRole())) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Acesso negado: apenas o autor pode editar este arquivo.");
            return "redirect:/posts/" + id;
        }

        postService.atualizarPost(id, post);
        redirectAttributes.addFlashAttribute("successMsg",
                "Arquivo \"" + post.getTitulo() + "\" atualizado.");
        return "redirect:/posts/" + id;
    }

    // ── Deletar Post (dono ou admin) ──────────────────────────────
    @PostMapping("/posts/deletar/{id}")
    public String deletarPost(@PathVariable Long id,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        Post post = postService.buscarPorId(id).orElse(null);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Arquivo não encontrado.");
            return "redirect:/teorias";
        }

        Usuario usuario = usuarioService.buscarPorNome(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!post.getAutor().getId().equals(usuario.getId())
                && !"admin".equals(usuario.getRole())) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Acesso negado: apenas o autor pode remover este arquivo.");
            return "redirect:/posts/" + id;
        }

        String titulo = post.getTitulo();
        postService.deletarPost(id);
        redirectAttributes.addFlashAttribute("successMsg",
                "Arquivo \"" + titulo + "\" removido permanentemente.");
        return "redirect:/teorias";
    }
}
