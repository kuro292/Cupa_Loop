-- ======================================================
-- DDL: CREACION DE ESTRUCTURA - CUPA LOOP
-- ======================================================

-- 1. Eliminar tablas en orden inverso para no romper llaves foraneas
DROP TABLE IF EXISTS `solicitudes`;
DROP TABLE IF EXISTS `productos`;
DROP TABLE IF EXISTS `usuarios`;
DROP TABLE IF EXISTS `categorias`;

-- 2. Tabla de Categorias (Sin cambios, estaba perfecta)
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `activa` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Tabla de Usuarios (Anadidos: apellido y correo)
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `identificador` varchar(20) NOT NULL,
  `correo` varchar(100) NOT NULL,      
  `contrasenia` varchar(255) NOT NULL,   
  `rol` enum('ALUMNO','ADMIN') NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `identificador` (`identificador`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Tabla de Productos (Normalizada y dividida en Condicion Fisica vs Estado Logistico)
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_categoria` int NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `condicion_fisica` enum('FUNCIONAL','REPARABLE','OBSOLETO') NOT NULL DEFAULT 'FUNCIONAL',
  `estado_inventario` enum('DISPONIBLE','RESERVADO','ENTREGADO','RECICLADO') NOT NULL DEFAULT 'DISPONIBLE',
  `fecha_ingreso` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_salida` datetime DEFAULT NULL,
  `id_usuario_asignado` int DEFAULT NULL, -- Reemplaza a 'matricula_alumno'
  PRIMARY KEY (`id`),
  KEY `fk_productos_categorias` (`id_categoria`),
  KEY `fk_productos_usuarios` (`id_usuario_asignado`),
  CONSTRAINT `fk_productos_categorias` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_productos_usuarios` FOREIGN KEY (`id_usuario_asignado`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Tabla de Solicitudes (Con campo motivo agregado)
CREATE TABLE `solicitudes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `id_usuario` int NOT NULL,
  `estado_solicitud` enum('PENDIENTE','APROBADA','RECHAZADA') DEFAULT 'PENDIENTE',
  `fecha_solicitud` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_resolucion` datetime DEFAULT NULL,
  `motivo` text,
  `comentario` text,
  PRIMARY KEY (`id`),
  KEY `id_producto` (`id_producto`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `solicitudes_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `solicitudes_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;