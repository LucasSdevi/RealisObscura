-- Seed: admin inicial (admin / admin123)
-- Só insere se não existir nenhum admin ainda
INSERT INTO usuario (id, nome, email, password, role)
SELECT gen_random_uuid(), 'admin', 'admin@realisobscura.com', '$2a$10$1guGxW48gGjpd7sCx5wD.uC8XhYWGODTdGMvfm/yEZk8o6iErLDJi', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE role = 'admin');
