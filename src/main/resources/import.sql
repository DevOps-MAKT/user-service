-- Insert locations
INSERT INTO locations (city, country) VALUES
('Novi Sad', 'Serbia'),
('Subotica', 'Serbia');

INSERT INTO USERS (username, email, password, firstName, lastName, role, location_id) VALUES
-- password: admin123
('admin', 'admin@gmail.com', '$2a$12$XlgKd3zOFrYYrjANJQzYJOTxTtMptJ93ICmHvmrnidzWz.TbvzZMe', 'admin', 'admin', 'admin', 1),
-- password pera123
('pera', 'pera@gmail.com','$2a$12$uIjkE3hHR5xMJFKEFcBqw.LpKXKIK7HWs6nYXC/foShQvNq673bH2', 'pera', 'peric', 'host', 2);