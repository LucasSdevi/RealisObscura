

package com.projetoAula.aulaProjeto.model;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class UsuarioDAO {

@Autowired
DataSource dataSource;

JdbcTemplate jdbc;

@PostConstruct
private void initialize() {
	jdbc = new JdbcTemplate(dataSource);
}

public void inserirUsuario(Usuario usuario){

	String sql = "INSERT INTO usuario (nome, data_nascimento, email)" + " values(?,?,?)";
	Object obj[] = new Object[3]; 

	obj[0] = usuario.getNome();
	obj[1] = usuario.getDataNascimento();
	obj[2] = usuario.getEmail();

	jdbc.update(sql, obj);

}

public Usuario mostrarUsuario(String uuid){
		String sql = "SELECT * FROM usuario where id=?::uuid";
		return Usuario.converter(jdbc.queryForMap(sql,uuid));
	}


	public ArrayList<Usuario> listarUsuarios(){
		String sql = "SELECT * FROM usuario";
		return Usuario.converterTodos(jdbc.queryForList(sql));
	}


}
