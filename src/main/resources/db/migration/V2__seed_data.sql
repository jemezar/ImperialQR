-- ===================================================
-- Migración V2: Semilla de datos iniciales
-- Restaurante Imperial (Comida China, Mesas, Personal)
-- Contraseñas encriptadas con BCrypt: "Imperial123*" ($2a$10$wU05z8nZ3x.aP5F74eY/4Oa0n5yRsmK.mGy8Q28Lrqf.Wz38Jv7dO)
-- ===================================================

-- 1. Usuarios con roles (Admin, Cocinero, Mesero, Domiciliario)
-- Hash de 'Imperial123*': $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
INSERT INTO usuario (nombre, email, password_hash, rol, activo) VALUES
('Carlos Méndez (Admin)', 'admin@imperial.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN', true),
('Chef Lin (Cocinero)', 'cocina@imperial.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'COCINERO', true),
('Andrés Gómez (Mesero)', 'mesero@imperial.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'MESERO', true),
('Mateo Silva (Domiciliario)', 'domicilio@imperial.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'DOMICILIARIO', true)
ON CONFLICT (email) DO NOTHING;

-- 2. Mesas con código QR (UUID v4)
INSERT INTO mesa (numero, capacidad, codigo_qr, estado) VALUES
(1, 2, '550e8400-e29b-41d4-a716-446655440001', 'DISPONIBLE'),
(2, 4, '550e8400-e29b-41d4-a716-446655440002', 'DISPONIBLE'),
(3, 4, '550e8400-e29b-41d4-a716-446655440003', 'DISPONIBLE'),
(4, 6, '550e8400-e29b-41d4-a716-446655440004', 'DISPONIBLE'),
(5, 4, '550e8400-e29b-41d4-a716-446655440005', 'DISPONIBLE')
ON CONFLICT (numero) DO NOTHING;

-- 3. Categorías de la carta
INSERT INTO categoria (nombre, descripcion, activo) VALUES
('Arroces Especiales', 'Arroces tradicionales al wok con vegetales frescos y carnes selectas', true),
('Fideos y Chop Suey', 'Fideos salteados y vegetales crujientes en salsa de soya tradicional', true),
('Entradas y Dumplings', 'Rollos primavera, empanaditas al vapor y bocados crocantes', true),
('Bebidas y Tés', 'Té verde, jazmín, bebidas naturales y gaseosas', true)
ON CONFLICT (nombre) DO NOTHING;

-- 4. Ingredientes con precio extra
INSERT INTO ingrediente (nombre, unidad, precio_extra, disponible) VALUES
('Cebolla blanca en julianas', 'porción', 0.00, true),
('Cebollín fresco', 'porción', 0.00, true),
('Raíz china crujiente', 'porción', 0.00, true),
('Pollo en cubos extra', 'porción', 5000.00, true),
('Cerdo agridulce extra', 'porción', 6000.00, true),
('Camarones salteados extra', 'porción', 8000.00, true),
('Huevo frito adicional', 'unidad', 2500.00, true),
('Salsa agridulce especial', 'porción', 2000.00, true),
('Picante Szechuan suave', 'porción', 1500.00, true)
ON CONFLICT (nombre) DO NOTHING;

-- 5. Platos
-- Plato 1: Arroz Chino Especial (Cat 1) - 22,000 COP
INSERT INTO plato (categoria_id, nombre, descripcion, precio_base, imagen_url, disponible) VALUES
(1, 'Arroz Chino Especial', 'Arroz frito al wok con pollo, raíces chinas, cebollín y tortilla de huevo', 22000.00, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b', true),
(1, 'Arroz Mixto Tres Carnes', 'Arroz tradicional con pollo, carne de res y cerdo asado estilo cantones', 25000.00, 'https://images.unsplash.com/photo-1512058564366-18510be2db19', true),
(2, 'Chop Suey Mixto', 'Vegetales salteados al vapor con julianas de pollo y salsa de soya artesanal', 20000.00, 'https://images.unsplash.com/photo-1541832676-9b763b0239ab', true),
(2, 'Fideos Lo Mein Cantones', 'Fideos de trigo salteados al wok con salsa oscura y brotes tiernos', 21000.00, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624', true),
(3, 'Lumpia Primavera Crocante (3 uds)', 'Rollitos crocantes rellenos de vegetales y carne molida con dip agridulce', 12000.00, 'https://images.unsplash.com/photo-1544025162-d76694265947', true),
(4, 'Té Verde con Jazmín Frío', 'Infusión natural perfumada servida con hielo y rodaja de limón', 6000.00, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc', true);

-- 6. Receta de Arroz Chino Especial (Plato ID 1)
-- Cebollín (removible, adicionable), Cebolla (removible), Raíz china (removible), Pollo extra (adicionable), Huevo frito (adicionable)
INSERT INTO plato_ingrediente (plato_id, ingrediente_id, cantidad, removible, adicionable) VALUES
(1, 1, 1.00, true, false), -- Cebolla blanca (removible)
(1, 2, 1.00, true, true),  -- Cebollín (removible y adicionable)
(1, 3, 1.00, true, false), -- Raíz china (removible)
(1, 4, 1.00, false, true), -- Pollo extra (adicionable con costo extra 5000)
(1, 7, 1.00, false, true)  -- Huevo frito (adicionable con costo extra 2500)
ON CONFLICT DO NOTHING;
