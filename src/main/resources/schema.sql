CREATE TABLE IF NOT EXISTS usuario(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    email VARCHAR(255) UNIQUE NOT NULL 
);
