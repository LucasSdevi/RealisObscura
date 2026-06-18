package com.projetoAula.aulaProjeto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByDataCriacaoDesc();

    List<Post> findByCategoriaOrderByDataCriacaoDesc(Post.Categoria categoria);

    List<Post> findByAutorIdOrderByDataCriacaoDesc(java.util.UUID autorId);
}
