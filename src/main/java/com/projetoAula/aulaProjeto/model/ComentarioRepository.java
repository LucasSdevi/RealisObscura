package com.projetoAula.aulaProjeto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByPostIdOrderByDataCriacaoAsc(Long postId);

    void deleteByPostId(Long postId);
}
