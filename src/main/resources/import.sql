INSERT INTO USERS (username, email, password, firstName, lastName, role, city, country, automaticReservationAcceptance, noCancellations) VALUES
-- password: admin123
('admin', 'admin@gmail.com', '$2a$12$XlgKd3zOFrYYrjANJQzYJOTxTtMptJ93ICmHvmrnidzWz.TbvzZMe', 'admin', 'admin', 'admin', 'Subotica', 'Serbia', false, 0),
-- password pera123
('pera', 'pera@gmail.com','$2a$12$uIjkE3hHR5xMJFKEFcBqw.LpKXKIK7HWs6nYXC/foShQvNq673bH2', 'pera', 'peric', 'host', 'Novi Sad', 'Serbia', false, 0),
-- password pera123
('gost', 'gost@gmail.com','$2a$12$uIjkE3hHR5xMJFKEFcBqw.LpKXKIK7HWs6nYXC/foShQvNq673bH2', 'Marko', 'Markovic', 'guest', 'Novi Sad', 'Serbia', false, 0);