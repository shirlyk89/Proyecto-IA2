import 'dotenv/config';
import express from 'express';
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { SSEServerTransport } from '@modelcontextprotocol/sdk/server/sse.js';
import { registerTools } from './tools.js';

const app = express();
app.use(express.json());

// Una sesión (transport) por conexión SSE activa.
const transports = {};

app.get('/sse', async (req, res) => {
  const transport = new SSEServerTransport('/messages', res);
  transports[transport.sessionId] = transport;

  const server = new McpServer({ name: 'logitrack-mcp-server', version: '1.0.0' });
  registerTools(server);
  await server.connect(transport);

  res.on('close', () => {
    delete transports[transport.sessionId];
  });
});

app.post('/messages', async (req, res) => {
  const sessionId = req.query.sessionId;
  const transport = transports[sessionId];
  if (!transport) {
    res.status(400).send('No hay una sesión SSE activa con ese sessionId. Conéctate primero a /sse.');
    return;
  }
  await transport.handlePostMessage(req, res, req.body);
});

app.get('/health', (req, res) => res.json({ status: 'ok' }));

const PORT = process.env.MCP_HTTP_PORT || 3939;
app.listen(PORT, () => {
  console.log(`LogiTrack MCP server (HTTP/SSE) escuchando en el puerto ${PORT}`);
  console.log(`Endpoint SSE para n8n: http://localhost:${PORT}/sse`);
});