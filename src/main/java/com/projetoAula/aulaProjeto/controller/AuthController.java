package com.projetoAula.aulaProjeto.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetoAula.aulaProjeto.dto.LoginRequest;
import com.projetoAula.aulaProjeto.dto.RegistroRequest;
import com.projetoAula.aulaProjeto.model.Usuario;
import com.projetoAula.aulaProjeto.model.UsuarioService;
import com.projetoAula.aulaProjeto.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public String paginaLogin(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout,
                              Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        if (error != null) {
            model.addAttribute("errorMsg", "Credenciais inválidas. Arquivo confidencial negado.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Sessão encerrada. Os arquivos foram trancados novamente.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                             BindingResult result,
                             HttpServletResponse response,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorNome(loginRequest.getNome());

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("errorMsg", "Agente não encontrado na base de dados.");
            return "login";
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar senha com BCrypt
        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .matches(loginRequest.getPassword(), usuario.getPassword())) {
            model.addAttribute("errorMsg", "Senha de acesso incorreta.");
            return "login";
        }

        String role = usuario.getRole() != null ? usuario.getRole() : "user";

        String token = jwtUtil.gerarToken(usuario.getNome(), role);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 24 horas
        response.addCookie(jwtCookie);

        redirectAttributes.addFlashAttribute("successMsg", "Acesso concedido, " + usuario.getNome() + ".");
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String paginaRegistro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroRequest") RegistroRequest registroRequest,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "registro";
        }

        if (usuarioService.nomeExiste(registroRequest.getNome())) {
            model.addAttribute("errorMsg", "Este codinome já está em uso por outro agente.");
            return "registro";
        }

        if (usuarioService.emailExiste(registroRequest.getEmail())) {
            model.addAttribute("errorMsg", "Este canal de contato já está registrado.");
            return "registro";
        }

        // Bloqueio de idade: mínimo 18 anos
        LocalDate dataNascimento = registroRequest.getDataNascimento();
        if (dataNascimento == null) {
            model.addAttribute("errorMsg", "A data de nascimento é obrigatória.");
            return "registro";
        }
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (idade < 18) {
            model.addAttribute("errorMsg",
                    "Registro negado: é necessário ter no mínimo 18 anos para se tornar um agente.");
            return "registro";
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registroRequest.getNome());
        novoUsuario.setEmail(registroRequest.getEmail());
        novoUsuario.setDataNascimento(registroRequest.getDataNascimento());
        novoUsuario.setPassword(registroRequest.getPassword());

        Usuario salvo = usuarioService.inserirUsuario(novoUsuario);

        // Auto-login após registro
        String token = jwtUtil.gerarToken(salvo.getNome(), "user");

        // Vamos redirecionar para login com mensagem
        redirectAttributes.addFlashAttribute("logoutMsg",
                "Registro concluído. Sua identidade foi criptografada. Faça login para acessar os arquivos.");
        return "redirect:/login";
    }
}
