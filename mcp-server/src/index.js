import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { z } from 'zod';
import { apiClient } from './apiClient.js';

const server = new McpServer({
  name: 'logitrack-mcp-server',
  version: '1.0.0',
});

function textResult(data) {
  return {
    content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
  };
}

// 1. consultar_stock_producto(productoId)
server.tool(
  'consultar_stock_producto',
  'Consulta el stock total y el desglose por bodega de un producto, calculado desde los movimientos reales.',
  { productoId: z.number().int().describe('ID del producto') },
  async ({ productoId }) => {
    const data = await apiClient.get(`/productos/${productoId}/stock`);
    return textResult(data);
  }
);

// 2. consultar_bodegas_criticas()
server.tool(
  'consultar_bodegas_criticas',
  'Lista las bodegas cuya ocupación es mayor o igual al 90%.',
  {},
  async () => {
    const data = await apiClient.get('/bodegas/criticas');
    return textResult(data);
  }
);

// 3. consultar_productos_en_riesgo()
server.tool(
  'consultar_productos_en_riesgo',
  'Lista los productos cuyo stock total está por debajo de su punto de reorden, con consumo, cobertura y bodega destino sugerida.',
  {},
  async () => {
    const data = await apiClient.get('/productos/riesgo');
    return textResult(data);
  }
);

// 4. consultar_kpis()
server.tool(
  'consultar_kpis',
  'Devuelve los cuatro indicadores del dashboard: ocupación por bodega, productos en quiebre, productos en riesgo y órdenes por aprobar, más los movimientos de ayer.',
  {},
  async () => {
    const data = await apiClient.get('/kpis');
    return textResult(data);
  }
);

// 5. crear_orden_borrador(productoId, proveedorId, bodegaDestinoId, cantidad, precioUnitario)
server.tool(
  'crear_orden_borrador',
  'Crea una orden de compra en estado BORRADOR para un producto en riesgo. No aprueba ni recibe la orden.',
  {
    productoId: z.number().int(),
    proveedorId: z.number().int(),
    bodegaDestinoId: z.number().int(),
    cantidad: z.number().int().positive(),
    precioUnitario: z.number().positive(),
  },
  async ({ productoId, proveedorId, bodegaDestinoId, cantidad, precioUnitario }) => {
    const data = await apiClient.post('/ordenes', {
      productoId,
      proveedorId,
      bodegaDestinoId,
      cantidad,
      precioUnitario,
    });
    return textResult(data);
  }
);

// 6. publicar_resumen(resumen)
server.tool(
  'publicar_resumen',
  'Valida y publica el resumen estructurado del panel para una fecha. Reemplaza el resumen existente de esa fecha si ya había uno.',
  {
    resumen: z
      .object({
        fecha: z.string().describe('Fecha en formato YYYY-MM-DD'),
        narrativa: z.string().min(20).max(500),
        alertas: z.array(
          z.object({
            severidad: z.enum(['BAJA', 'MEDIA', 'ALTA']),
            titulo: z.string(),
            detalle: z.string(),
            productoId: z.number().int().nullable().optional(),
            ordenId: z.number().int().nullable().optional(),
            bodegaId: z.number().int().nullable().optional(),
          })
        ),
        accionesSugeridas: z.array(
          z.object({
            tipo: z.enum(['REVISAR_ORDEN', 'REVISAR_PRODUCTO', 'REVISAR_BODEGA']),
            descripcion: z.string(),
            ordenId: z.number().int().nullable().optional(),
            productoId: z.number().int().nullable().optional(),
            bodegaId: z.number().int().nullable().optional(),
          })
        ),
      })
      .describe('Objeto que cumple exactamente el contrato de POST /panel/resumen'),
  },
  async ({ resumen }) => {
    const data = await apiClient.post('/panel/resumen', resumen);
    return textResult(data);
  }
);

const transport = new StdioServerTransport();
await server.connect(transport);
console.error('LogiTrack MCP server corriendo (stdio)');