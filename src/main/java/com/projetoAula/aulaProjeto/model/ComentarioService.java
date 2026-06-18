package com.projetoAula.aulaProjeto.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Comentario criarComentario(Long postId, java.util.UUID autorId, String conteudo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comentario comentario = new Comentario(conteudo, autor, post);
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listarPorPost(Long postId) {
        return comentarioRepository.findByPostIdOrderByDataCriacaoAsc(postId);
    }

    public void deletarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }
}
