# 02 — Especificación

## Reglas de inventario

- Zona horaria de backend, n8n y datos de prueba: `America/Bogota`.
- La capacidad de una bodega se mide en unidades y debe ser mayor que 0.
- El stock se calcula **solo** a partir de los movimientos; el campo
  `Producto.stock` del reto anterior no se usa para estos cálculos nuevos.
- `ENTRADA` suma unidades a la bodega destino; `SALIDA` resta en la bodega
  origen; `TRANSFERENCIA` resta en origen y suma en destino.
- El stock total de un producto es la suma de sus existencias en todas las
  bodegas.

## Cálculos de riesgo (implementados en `InventarioService`)

| Cálculo | Regla exacta |
|---|---|
| Consumo diario promedio | Suma de `SALIDA` de los últimos 30 días calendario (incluida la fecha de consulta) / 30 |
| Punto de reorden | `consumoDiarioPromedio * diasEntrega * 1.5` |
| Días de cobertura | `stockTotal / consumoDiarioPromedio` |
| Estado de cobertura | `SIN_CONSUMO` si el consumo es 0; `EN_RIESGO` si `stockTotal < puntoReorden`; `NORMAL` en otro caso |
| Producto en riesgo | Tiene proveedor principal **y** `stockTotal < puntoReorden` (igual no cuenta) |
| Bodega crítica | Ocupación ≥ 90% |

Un producto **sin proveedor principal nunca puede estar en riesgo** ni
generar una orden automática — es la primera condición que se evalúa.

## Estados de la orden de compra

```
BORRADOR  → APROBADA | CANCELADA
APROBADA  → RECIBIDA  | CANCELADA
RECIBIDA  → (ninguno)
CANCELADA → (ninguno)
```

Cualquier transición no listada responde `400 Bad Request`. Al pasar de
`APROBADA` a `RECIBIDA`, el sistema crea automáticamente un movimiento
`ENTRADA` para el producto, cantidad y bodega destino de la orden — la
actualización de la orden y la creación del movimiento ocurren en una sola
transacción (`@Transactional`).

Al cambiar el estado de una orden, el PDF guardado se invalida (se borra) y
debe regenerarse.

## Contrato del resumen del panel (`POST /panel/resumen`)

```json
{
  "fecha": "2026-08-24",
  "narrativa": "Hay productos en riesgo y una orden pendiente de aprobación.",
  "alertas": [
    { "severidad": "ALTA", "titulo": "...", "detalle": "...", "productoId": 12, "ordenId": null, "bodegaId": 3 }
  ],
  "accionesSugeridas": [
    { "tipo": "REVISAR_ORDEN", "descripcion": "...", "ordenId": 14, "productoId": null, "bodegaId": null }
  ]
}
```

- `fecha`: `YYYY-MM-DD`.
- `narrativa`: 20–500 caracteres.
- `severidad`: `BAJA | MEDIA | ALTA`.
- `tipo`: `REVISAR_ORDEN | REVISAR_PRODUCTO | REVISAR_BODEGA`.
- Cada **alerta** enlaza al menos un identificador real; cada **acción**
  enlaza **exactamente uno**. Todo identificador informado debe existir.
- JSON inválido (estructura, enum, o ID inexistente) → `400`, y el resumen
  válido anterior permanece disponible sin cambios.
- Publicar para una fecha que ya tenía resumen **reemplaza** el contenido
  (no crea un registro nuevo).

## Ejemplo fijo de `GET /kpis`

```json
{
  "calculadoEn": "2026-08-24T06:00:00-05:00",
  "ocupacionPorBodega": [{ "bodegaId": 1, "nombre": "Bogota", "porcentaje": 92.5 }],
  "productosEnQuiebre": 1,
  "productosEnRiesgo": 2,
  "ordenesPorAprobar": { "cantidad": 1, "montoTotal": 45000.0 },
  "movimientosAyer": { "entrada": 2, "salida": 3, "transferencia": 1 }
}
```

## Seguridad

| Acción | AGENTE | ADMIN |
|---|---|---|
| Consultar KPIs, stock, riesgos, bodegas críticas | Sí | Sí |
| Crear orden en BORRADOR | Sí | Sí |
| Publicar resumen | Sí | Sí |
| Aprobar, recibir o cancelar una orden | No (403) | Sí |
| Registrar movimientos manualmente | No (403) | Sí |

Errores: `400` validaciones/transiciones inválidas, `404` recursos
inexistentes, `403` acciones prohibidas por rol, `401` sesión inválida.
