package com.projetoAula.aulaProjeto.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario inserirUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean nomeExiste(String nome) {
        return usuarioRepository.existsByNome(nome);
    }

    public void deletarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario atualizarUsuario(UUID id, Usuario novosDados) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        existente.setNome(novosDados.getNome());
        existente.setEmail(novosDados.getEmail());
        existente.setDataNascimento(novosDados.getDataNascimento());
        if (novosDados.getPassword() != null && !novosDados.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(novosDados.getPassword()));
        }
        return usuarioRepository.save(existente);
    }
}
