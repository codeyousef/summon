-- Insert demo users
INSERT INTO users (username, email, password_hash, language, theme, created_at) VALUES 
('demo', 'demo@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'en', 'light', CURRENT_TIMESTAMP),
('alice', 'alice@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'es', 'dark', CURRENT_TIMESTAMP),
('bob', 'bob@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'fr', 'light', CURRENT_TIMESTAMP);

-- Insert demo todos for demo user (ID 1)
INSERT INTO todos (text, completed, user_id, created_at, updated_at) VALUES 
('Welcome to the Spring Boot Todo App!', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Try creating a new todo', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Test the JWT authentication', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Switch between themes (light/dark)', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Try different languages (EN/ES/FR)', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('This todo is already completed', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Edit this todo by clicking the edit button', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Delete this todo when you are done testing', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert some todos for alice (ID 2)
INSERT INTO todos (text, completed, user_id, created_at, updated_at) VALUES 
('¡Hola! Esta es una tarea en español', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Probar la funcionalidad de temas', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tarea completada', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert some todos for bob (ID 3)
INSERT INTO todos (text, completed, user_id, created_at, updated_at) VALUES 
('Bonjour! Ceci est une tâche en français', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tester l''authentification JWT', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tâche terminée', true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);