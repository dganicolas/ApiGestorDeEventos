INSERT INTO `usuarios`(
`id`, `password`, `roles`, `username`
) VALUES (
1,'$2a$10$DBc2FPq.4XperQMRTGpYnufwdTFxFCJtRZj1zsX.7vFo9YVe9rCyW','USER','luis'
)

INSERT INTO `usuarios`(
`id`, `password`, `roles`, `username`
) VALUES (
2,'$2a$10$DBc2FPq.4XperQMRTGpYnufwdTFxFCJtRZj1zsX.7vFo9YVe9rCyW','ADMIN','nico'
)

INSERT INTO `usuarios`(`id`, `password`, `roles`, `username`)
VALUES (
3, '2a$10$DBc2FPq.4XperQMRTGpYnufwdTFxFCJtRZj1zsX.7vFo9YVe9rCyW', 'USER', 'cura_pedro'
);

INSERT INTO `local_de_eventos`(
    `idLocal`, `nombre`, `tipoDeLocal`, `direccion`, `descripcion`,
    `aforoMaximo`, `precioInvitados`, `menuDisponible`, `precioMenu`,
    `descripcionMenu`, `id_propietario`
)
VALUES (
    NULL, -- El idLocal se generará automáticamente
    'El Gran Salón', -- Nombre del local
    'SALON_DE_CELEBRACIONES', -- Tipo de local (Enum)
    'Calle Mayor 123, Ciudad', -- Dirección
    'Un espacioso salón para todo tipo de celebraciones.', -- Descripción
    150, -- Aforo máximo
    25.50, -- Precio por invitado
    true, -- Menú disponible
    15.75, -- Precio del menú
    'Incluye entrada, plato principal y postre.', -- Descripción del menú
    2 -- ID del propietario (referencia a la tabla de usuarios)
);

INSERT INTO `local_de_eventos`(
    `idLocal`, `nombre`, `tipoDeLocal`, `direccion`, `descripcion`,
    `aforoMaximo`, `precioInvitados`, `menuDisponible`, `precioMenu`,
    `descripcionMenu`, `id_propietario`
)
VALUES (
    NULL, -- El idLocal se generará automáticamente
    'Iglesia de San Pedro', -- Nombre del local
    'IGLESIA', -- Tipo de local (Enum)
    'Calle de la Paz 45, Ciudad', -- Dirección
    'Un lugar sagrado perfecto para ceremonias religiosas.', -- Descripción
    200, -- Aforo máximo
    5.00, -- Precio por invitado (puede ser 0 si no aplica)
    false, -- Menú disponible (no tiene menú)
    NULL, -- Precio del menú (NULL porque no aplica)
    NULL, -- Descripción del menú (NULL porque no aplica)
    3 -- ID del propietario (el cura, referencia a la tabla de usuarios)
);