-- =========================================================================
-- 🚗 SEMILLERO DE VEHÍCULOS (Tabla: autos)
-- =========================================================================
INSERT INTO autos (placa, marca, modelo, anio, precio_por_dia, deposito, kilometraje, estado) 
VALUES ('JCH-777', 'Mazda', 'CX-30', 2026, 180000.0, 500000.0, 12500, 'DISPONIBLE');

INSERT INTO autos (placa, marca, modelo, anio, precio_por_dia, deposito, kilometraje, estado) 
VALUES ('ABC-123', 'Toyota', 'Corolla', 2025, 150000.0, 400000.0, 32000, 'RESERVADO');

INSERT INTO autos (placa, marca, modelo, anio, precio_por_dia, deposito, kilometraje, estado) 
VALUES ('XYZ-987', 'Chevrolet', 'Onix', 2024, 120000.0, 300000.0, 45100, 'MANTENIMIENTO');


-- =========================================================================
-- 👥 SEMILLERO DE CLIENTES (Tabla: clientes)
-- =========================================================================
-- Inserción con documento puramente numérico (Long) y tipo de documento como texto
INSERT INTO clientes (nombre, tipo_documento, documento, telefono, email, estado)
VALUES ('Carlos Pérez', 'CC', 1020304050, '3001234567', 'carlos@email.com', 'ACTIVO');

INSERT INTO clientes (nombre, tipo_documento, documento, telefono, email, estado)
VALUES ('Ana Martínez', 'CC', 987654321, '3159876543', 'ana@email.com', 'ACTIVO');

INSERT INTO clientes (nombre, tipo_documento, documento, telefono, email, estado)
VALUES ('John Doe', 'PA', 555444333, '3104445555', 'john@email.com', 'ACTIVO');


-- =========================================================================
-- 📑 SEMILLERO DE HISTORIAL DE CONTRATOS (Tabla: alquileres)
-- =========================================================================
-- 1. Contrato Histórico: Ya finalizado
INSERT INTO alquileres (cliente_id, auto_id, fecha_reserva, fecha_inicio, fecha_fin, cantidad_dias, valor_total, kilometraje_inicial, kilometraje_final, estado)
VALUES (2, 2, '2026-09-01', '2026-09-01', '2026-09-05', 4, 600000.0, 31500, 32000, 'FINALIZADO');

-- 2. Contrato Activo: Alquiler en curso
INSERT INTO alquileres (cliente_id, auto_id, fecha_reserva, fecha_inicio, fecha_fin, cantidad_dias, valor_total, kilometraje_inicial, kilometraje_final, estado)
VALUES (1, 1, '2026-10-01', '2026-10-10', '2026-10-15', 5, 900000.0, 12500, 0, 'ACTIVO');

-- 3. Contrato Histórico: Pendiente
INSERT INTO alquileres (cliente_id, auto_id, fecha_reserva, fecha_inicio, fecha_fin, cantidad_dias, valor_total, kilometraje_inicial, kilometraje_final, estado)
VALUES (2, 2, '2026-10-01', '2026-10-15', '2026-10-19', 4, 600000.0, 32000, 0, 'PENDIENTE');
