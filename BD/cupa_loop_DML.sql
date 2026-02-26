-- ======================================================
-- DML: INSERCION DE DATOS DE PRUEBA - CUPA LOOP
-- ======================================================

-- 0. IMPORTANTE: Si la base de datos ya existe, ejecutar este ALTER TABLE
-- para agregar el campo 'motivo' a la tabla solicitudes
-- ALTER TABLE `solicitudes` ADD COLUMN `motivo` text AFTER `fecha_resolucion`;

-- 1. Insertar Usuarios (Contrasenias en texto plano)
INSERT INTO `usuarios` (`id`, `identificador`, `correo`, `contrasenia`, `rol`, `nombre`, `apellido`) VALUES 
(1, 'admin_utl', 'admin@utleon.edu.mx', '123456', 'ADMIN', 'Administrador', 'General'),
(2, 'Claudia_user', 'claudia@alumno.utleon.edu.mx', '123456', 'ALUMNO', 'Claudia', 'Contreras'),
(3, 'cruz_user', 'cruz@alumno.utleon.edu.mx', '123456', 'ALUMNO', 'Cruz', 'Fernandez'),
(4, 'isaac_user', 'isaac@alumno.utleon.edu.mx', '123456', 'ALUMNO', 'Isaac', 'Rico'),
(5, 'antonio_user', 'antonio@alumno.utleon.edu.mx', '123456', 'ALUMNO', 'Antonio', 'Robledo');
