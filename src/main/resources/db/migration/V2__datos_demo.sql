-- Datos demo: roles, departamentos, categorias y usuarios de prueba.
-- Password de todos los usuarios demo: Demo1234! (hash BCrypt, cost 10).

-- Roles
INSERT INTO helpdesk.rol (codigo, nombre, descripcion)
VALUES ('ADMIN', 'Administrador', 'Administra usuarios, catalogos y tickets'),
       ('SOPORTE', 'Soporte', 'Atiende y resuelve tickets asignados'),
       ('USUARIO_FINAL', 'Usuario final', 'Registra y da seguimiento a sus tickets')
ON CONFLICT (codigo) DO NOTHING;

-- Departamentos (sin restriccion unique en nombre: se evita duplicar con NOT EXISTS)
INSERT INTO helpdesk.departamento (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES ('Tecnologia', 'Departamento de tecnologias de la informacion'),
             ('Administracion', 'Departamento administrativo'),
             ('Recursos Humanos', 'Gestion del talento humano')) AS v(nombre, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM helpdesk.departamento d WHERE d.nombre = v.nombre);

-- Categorias de ticket
INSERT INTO helpdesk.categoria_ticket (nombre, descripcion)
SELECT v.nombre, v.descripcion
FROM (VALUES ('Hardware', 'Equipos, perifericos e impresoras'),
             ('Software', 'Instalacion y errores de aplicaciones'),
             ('Redes', 'Conectividad, internet y VPN'),
             ('Accesos', 'Cuentas, contrasenas y permisos')) AS v(nombre, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM helpdesk.categoria_ticket c WHERE c.nombre = v.nombre);

-- Usuarios demo (uno por rol)
INSERT INTO helpdesk.usuario (rol_id, departamento_id, nombres, apellidos, email, password_hash, telefono)
SELECT r.id, d.id, v.nombres, v.apellidos, v.email, v.password_hash, NULL
FROM (VALUES ('ADMIN', 'Tecnologia', 'Admin', 'Demo', 'admin@helpdesk.demo',
              '$2a$10$/0ASQZ.JdnWZhU/U31ID5.7R0rOZv0frj9w5..1WmaW8snEY7Gfwi'),
             ('SOPORTE', 'Tecnologia', 'Soporte', 'Demo', 'soporte@helpdesk.demo',
              '$2a$10$UIO2dlZPXEezVMPjqMvByufQUxVVLpUoBu8mj5PmHGlxd5qAiIGoW'),
             ('USUARIO_FINAL', 'Administracion', 'Usuario', 'Demo', 'usuario@helpdesk.demo',
              '$2a$10$Q4Q.aEzSb6XFsZ.0xPXq/eKjH7YGOKcuFognpUH5Ww2aS458tL3oS'))
         AS v(codigo_rol, departamento, nombres, apellidos, email, password_hash)
JOIN helpdesk.rol r ON r.codigo = v.codigo_rol
JOIN LATERAL (SELECT dd.id FROM helpdesk.departamento dd
              WHERE dd.nombre = v.departamento ORDER BY dd.id LIMIT 1) d ON TRUE
ON CONFLICT (email) DO NOTHING;
