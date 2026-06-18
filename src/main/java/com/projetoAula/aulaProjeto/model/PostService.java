package com.projetoAula.aulaProjeto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    public Post criarPost(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> buscarPorId(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> listarTodos() {
        return postRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<Post> listarPorCategoria(Post.Categoria categoria) {
        return postRepository.findByCategoriaOrderByDataCriacaoDesc(categoria);
    }

    public List<Post> listarPorAutor(java.util.UUID autorId) {
        return postRepository.findByAutorIdOrderByDataCriacaoDesc(autorId);
    }

    public Post atualizarPost(Long id, Post novosDados) {
        Post existente = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        existente.setTitulo(novosDados.getTitulo());
        existente.setConteudo(novosDados.getConteudo());
        existente.setCategoria(novosDados.getCategoria());
        return postRepository.save(existente);
    }

    @Transactional
    public void deletarPost(Long id) {
        comentarioRepository.deleteByPostId(id);
        postRepository.deleteById(id);
    }
}
