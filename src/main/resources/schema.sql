CREATE TABLE IF NOT EXISTS usuario(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS perfil (
    id SERIAL PRIMARY KEY,
    usuario_id UUID NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    CONSTRAINT fk_perfil_usuario FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT perfil_unique UNIQUE(usuario_id)
);

CREATE TABLE IF NOT EXISTS post (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    conteudo TEXT NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    autor_id UUID NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_autor FOREIGN KEY(autor_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comentario (
    id BIGSERIAL PRIMARY KEY,
    conteudo TEXT NOT NULL,
    autor_id UUID NOT NULL,
    post_id BIGINT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comentario_autor FOREIGN KEY(autor_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_post FOREIGN KEY(post_id) REFERENCES post(id) ON DELETE CASCADE
);
