// =========================================================
// CONFIGURACIÓN GLOBAL Y ESTADO
// =========================================================
const API_URL = 'http://localhost:8080/api'; // Configurado con /api para coincidir con Spring Boot

let currentUser = null;
let currentToken = localStorage.getItem('jwt_token');
let currentRole = localStorage.getItem('user_role');

// Elementos del DOM - Navegación y Layout
const mainNavbar = document.getElementById('mainNavbar');
const systemPanel = document.getElementById('systemPanel');
const sectionAuth = document.getElementById('section-auth');
const navLinks = document.querySelectorAll('.nav-link');

// =========================================================
// INICIALIZACIÓN
// =========================================================
document.addEventListener('DOMContentLoaded', () => {
    verificarSesion();
    configurarEventosNavegacion();
    configurarEventosAuth();
    configurarEventosModulos();
});

// =========================================================
// UTILIDADES PARA API (FETCH CON JWT)
// =========================================================
async function fetchAPI(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('jwt_token');
    const headers = { 'Content-Type': 'application/json' };
    
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    const response = await fetch(`http://localhost:8080/api${endpoint}`, config);

    if (!response.ok) {
        if (response.status === 401) {
            cerrarSesion(); 
            throw new Error('Sesión expirada. Vuelve a iniciar sesión.');
        } 
        
        if (response.status === 403) {
            throw new Error('Acceso denegado: No tienes permisos para esta acción.');
        }

        const errorText = await response.text();
        throw new Error(errorText);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : {};
}

// =========================================================
// NAVEGACIÓN Y SESIÓN (SPA LIGERA)
// =========================================================
function verificarSesion() {
    currentToken = localStorage.getItem('jwt_token');
    currentRole = localStorage.getItem('user_role');
    const username = localStorage.getItem('username');

    if (currentToken) {
        // Mostrar sistema, ocultar login
        if(sectionAuth) sectionAuth.classList.add('hidden');
        if(mainNavbar) mainNavbar.classList.remove('hidden');
        if(systemPanel) systemPanel.classList.remove('hidden');
        
        // Setear datos de usuario en Navbar
        const userLogueado = document.getElementById('userLogueado');
        const rolLogueado = document.getElementById('rolLogueado');
        if(userLogueado) userLogueado.textContent = username;
        if(rolLogueado) rolLogueado.textContent = currentRole;

        // Control de Rol para Auditoría y botones
        const navAuditoria = document.getElementById('navAuditoria');
        if(navAuditoria) {
            if (currentRole === 'ROLE_ADMIN' || currentRole === 'ADMIN') {
                navAuditoria.classList.remove('hidden');
            } else {
                navAuditoria.classList.add('hidden');
            }
        }

        cargarSeccion('dashboard');
    } else {
        // Mostrar login, ocultar sistema
        if(sectionAuth) sectionAuth.classList.remove('hidden');
        if(mainNavbar) mainNavbar.classList.add('hidden');
        if(systemPanel) systemPanel.classList.add('hidden');
    }
}

function cargarSeccion(seccionId) {
    // Ocultar todas las secciones
    document.querySelectorAll('.app-section').forEach(sec => sec.classList.add('hidden'));
    
    // Mostrar la seleccionada
    const seccionActiva = document.getElementById(`section-${seccionId}`);
    if(seccionActiva) seccionActiva.classList.remove('hidden');

    // Actualizar estado activo en navbar
    navLinks.forEach(link => {
        if (link.dataset.section === seccionId) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

        // Cargar datos según la sección
    if (seccionId === 'bodegas') {
        cargarBodegas();
    }

    if (seccionId === 'productos') {
        cargarBodegasSelect();
        cargarProductos();
    }

    if (seccionId === 'movimientos') {
        cargarMovimientosYFormulario();
    }

   if (seccionId === 'auditoria' && (currentRole === 'ROLE_ADMIN' || currentRole === 'ADMIN')) {
        cargarAuditoria();
        cargarReportesEnPantalla();
    }
}

function configurarEventosNavegacion() {
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            cargarSeccion(e.target.dataset.section);
        });
    });

    const btnLogout = document.getElementById('navBtnLogout');
    if(btnLogout) btnLogout.addEventListener('click', cerrarSesion);
}

function cerrarSesion() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
    localStorage.removeItem('username');
    verificarSesion();
}

// =========================================================
// MÓDULO: AUTENTICACIÓN (LOGIN / REGISTRO)
// =========================================================
function configurarEventosAuth() {
    // Toggle entre Login y Registro
    const showRegister = document.getElementById('showRegister');
    const showLogin = document.getElementById('showLogin');
    
    if(showRegister) {
        showRegister.addEventListener('click', (e) => {
            e.preventDefault();
            document.getElementById('loginCard').classList.add('hidden');
            document.getElementById('registerCard').classList.remove('hidden');
        });
    }

    if(showLogin) {
        showLogin.addEventListener('click', (e) => {
            e.preventDefault();
            document.getElementById('registerCard').classList.add('hidden');
            document.getElementById('loginCard').classList.remove('hidden');
        });
    }

    // Submit Login
    const loginForm = document.getElementById('loginForm');
    if(loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;
            const mensaje = document.getElementById('loginMensaje');
            
            try {
                mensaje.textContent = "Conectando al servidor...";
                
                const res = await fetchAPI('/auth/login', 'POST', { 
                    username: username, 
                    password: password 
                });
                
                localStorage.setItem('jwt_token', res.token);
                localStorage.setItem('user_role', res.rol || 'ROLE_EMPLEADO');
                localStorage.setItem('username', username);
                
                mensaje.textContent = "";
                verificarSesion();
               
            } catch (error) {
                mensaje.textContent = error.message || 'Credenciales inválidas';
                alert('Error al iniciar sesión: ' + error.message);
            }
        });
    }

    // Submit Registro
    const registerForm = document.getElementById('registerForm');
    if(registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const regUsername = document.getElementById('regUsername').value;
            const regPassword = document.getElementById('regPassword').value;
            const regRol = document.getElementById('regRol')?.value || 'EMPLEADO';
            const mensaje = document.getElementById('registerMensaje') || document.createElement('div');

            try {
                mensaje.textContent = "Registrando usuario...";
                await fetchAPI('/auth/register', 'POST', { 
                    username: regUsername, 
                    password: regPassword,
                    rol: regRol // Valor por defecto
                });
                alert('Usuario registrado con éxito. Por favor inicia sesión.');
                document.getElementById('showLogin').click();
                mensaje.textContent = "";
            } catch (error) {
                mensaje.textContent = error.message;
                alert('Error al registrar: ' + error.message);
            }
        });
    }
}

// =========================================================
// MÓDULO: CONFIGURACIÓN DE EVENTOS DE MÓDULOS
// =========================================================
function configurarEventosModulos() {
    document.getElementById('btnNuevaBodega')?.addEventListener('click', () => {
        document.getElementById('formBodega').reset();
        document.getElementById('bodegaId').value = '';
        document.getElementById('formBodegaContainer').classList.toggle('hidden');
    });
    
    document.getElementById('btnCancelarBodega')?.addEventListener('click', () => {
        document.getElementById('formBodegaContainer').classList.add('hidden');
    });

    document.getElementById('btnNuevoProducto')?.addEventListener('click', async () => {
        await cargarBodegasSelect();

    document.getElementById('formProducto').reset();
    document.getElementById('productoId').value = '';
    document.getElementById('formProductoContainer').classList.remove('hidden');
    });

    document.getElementById('btnCancelarProducto')?.addEventListener('click', () => {
        document.getElementById('formProductoContainer').classList.add('hidden');
    });

    document.getElementById('filtroStockBajo')?.addEventListener('change', cargarProductos);
    document.getElementById('btnFiltrarMovimientos')?.addEventListener('click', cargarMovimientosYFormulario);
    document.getElementById('btnDescargarReporte')?.addEventListener('click', descargarReporteTxt);
}

// =========================================================
// MÓDULO: BODEGAS
// =========================================================
async function cargarBodegas() {
    const tbody = document.getElementById('tablaBodegasBody');
    if (!tbody) return;
    tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Cargando...</td></tr>';

    try {
        const bodegas = await fetchAPI('/bodegas', 'GET');
        tbody.innerHTML = '';

        if (!bodegas || bodegas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No hay bodegas registradas</td></tr>';
            return;
        }

        bodegas.forEach(bodega => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${bodega.id}</td>
                <td>${bodega.nombre}</td>
                <td>${bodega.ubicacion}</td>
                <td>${bodega.capacidad}</td>
                <td>${bodega.encargado}</td>
                <td>
                <td class="col-acciones">
                    <div class="acciones-flex">
                        <button class="btn-sm btn-outline" onclick="editarBodega(${bodega.id})">Editar</button>
                        <button class="btn-sm btn-danger" onclick="eliminarBodega(${bodega.id})">Eliminar</button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error al cargar bodegas:', error);
        alert('Error al cargar bodegas: ' + error.message);
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Error al cargar datos</td></tr>';
    }
}

async function cargarBodegasSelect() {
    console.log("Se ejecutó cargarBodegasSelect");
    const selectBodega = document.getElementById('prodBodega');
    if (!selectBodega) return;

    try {
        const bodegas = await fetchAPI('/bodegas');
        selectBodega.innerHTML = '<option value="">Seleccione una bodega...</option>';

        if (bodegas) {
            bodegas.forEach(bodega => {
                const option = document.createElement('option');
                option.value = bodega.id;
                option.textContent = bodega.nombre;
                selectBodega.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error al poblar el select de bodegas:', error);
        alert('No se pudieron cargar las bodegas: ' + error.message);
    }
}

document.getElementById('formBodega')?.addEventListener('submit', async function(event) {
    event.preventDefault();

    const id = document.getElementById('bodegaId').value;
    const esEdicion = id !== "";

    const bodegaData = {
       nombre: document.getElementById('bodegaNombre').value.trim(),
        ubicacion: document.getElementById('bodegaUbicacion').value.trim(),
        capacidad: parseInt(document.getElementById('bodegaCapacidad').value, 10),
        encargado: document.getElementById('bodegaEncargado').value.trim()
    };

    const endpoint = esEdicion ? `/bodegas/${id}` : '/bodegas';
    const metodo = esEdicion ? 'PUT' : 'POST';

    try {
        await fetchAPI(endpoint, metodo, bodegaData);

        alert(esEdicion ? '¡Bodega actualizada exitosamente!' : '¡Bodega creada exitosamente!');
        
        document.getElementById('formBodega').reset();
        document.getElementById('bodegaId').value = '';
        document.getElementById('formBodegaContainer').classList.add('hidden');
        
        cargarBodegas();
        cargarBodegasSelect();

    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar bodega: ' + error.message);
    }
});

async function editarBodega(id) {
    try {
        const bodega = await fetchAPI(`/bodegas/${id}`);

        document.getElementById('bodegaId').value = bodega.id;
        document.getElementById('bodegaNombre').value = bodega.nombre;
        document.getElementById('bodegaUbicacion').value = bodega.ubicacion || '';
        document.getElementById('bodegaCapacidad').value = bodega.capacidad || 0;
        document.getElementById('bodegaEncargado').value = bodega.encargado || '';

        document.getElementById('formBodegaContainer').classList.remove('hidden');
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar datos para edición: ' + error.message);
    }
}

async function eliminarBodega(id) {
    if (!confirm('¿Estás seguro de eliminar esta bodega? Podría afectar a los productos asociados.')) {
        return;
    }

    try {
        await fetchAPI(`/bodegas/${id}`, 'DELETE');
        alert('Bodega eliminada correctamente');
        cargarBodegas();
        cargarBodegasSelect();
    } catch (error) {
        console.error('Error:', error);
        alert('Error al eliminar bodega: ' + error.message);
    }
}

// =========================================================
// MÓDULO: PRODUCTOS
// =========================================================
async function cargarProductos() {
    const tbody = document.getElementById('tablaProductosBody');
    if (!tbody) return;

    const checkboxStockBajo = document.getElementById('filtroStockBajo');
    const soloStockBajo = checkboxStockBajo ? checkboxStockBajo.checked : false;

    try {
        const endpoint = soloStockBajo ? '/productos/stock-bajo' : '/productos';
        const productos = await fetchAPI(endpoint);
        
        tbody.innerHTML = '';

        if (!productos || productos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center" style="text-align: center;">No se encontraron productos</td></tr>`;
            return;
        }

        productos.forEach(producto => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
         <td>${producto.id}</td>
                <td>${producto.nombre}</td>
                <td>${producto.descripcion || ''}</td>
                <td>${producto.categoria || 'Sin Categoría'}</td>  
                <td>${producto.stock}</td>
                <td>$${producto.precio}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="editarProducto(${producto.id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarProducto(${producto.id})">Eliminar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (error) {
        console.error('Error al cargar productos:', error);
        alert('Error al cargar productos: ' + error.message);
    }
}

document.getElementById('formProducto')?.addEventListener('submit', async function(event) {
    event.preventDefault(); 

    const id = document.getElementById('productoId').value;
    const esEdicion = id !== ""; 

    const productoData = {
        nombre: document.getElementById('prodNombre').value.trim(),
        categoria: document.getElementById('prodCategoria').value.trim(),
        descripcion: document.getElementById('prodDescripcion')?.value.trim() || '',
        precio: parseFloat(document.getElementById('prodPrecio').value),
        stock: parseInt(document.getElementById('prodStock').value, 10),
        bodega: {
            id: parseInt(document.getElementById('prodBodega').value, 10)
        }
    };

    const endpoint = esEdicion ? `/productos/${id}` : '/productos';
    const metodo = esEdicion ? 'PUT' : 'POST';

    try {
        await fetchAPI(endpoint, metodo, productoData);

        alert(esEdicion ? '¡Producto actualizado exitosamente!' : '¡Producto creado exitosamente!');
        
        // Reset limpia todos los inputs del formulario (incluida la categoría)
        document.getElementById('formProducto').reset();
        document.getElementById('productoId').value = '';
        document.getElementById('formProductoContainer').classList.add('hidden');
        
        cargarProductos();

    } catch (error) {
        console.error('Error en el guardado:', error);
        alert('Error al guardar producto: ' + error.message);
    }
});

async function eliminarProducto(id) {
    if (!confirm('¿Estás seguro de que deseas eliminar este producto?')) {
        return;
    }

    try {
        await fetchAPI(`/productos/${id}`, 'DELETE');
        alert('Producto eliminado correctamente');
        cargarProductos();
    } catch (error) {
        console.error('Error:', error);
        alert('Error al eliminar producto: ' + error.message);
    }
}

async function editarProducto(id) {
    try {
          await cargarBodegasSelect();

        const producto = await fetchAPI(`/productos/${id}`);

        document.getElementById('productoId').value = producto.id;
        document.getElementById('prodNombre').value = producto.nombre;
        document.getElementById('prodCategoria').value = producto.categoria || '';
        document.getElementById('prodDescripcion').value = producto.descripcion || '';
        document.getElementById('prodPrecio').value = producto.precio;
        document.getElementById('prodStock').value = producto.stock;

        if (producto.bodega) {
            document.getElementById('prodBodega').value = producto.bodega.id;
        }

        document.getElementById('formProductoContainer').classList.remove('hidden');


    } catch (error) {
        console.error('Error:', error);
        alert('Error al obtener datos del producto: ' + error.message);
    }
}

// =========================================================
// MÓDULO: MOVIMIENTOS
// =========================================================
async function cargarMovimientosYFormulario() {
    const movProducto = document.getElementById('movProducto');
    const movBodegaOrigen = document.getElementById('movBodegaOrigen');
    const movBodegaDestino = document.getElementById('movBodegaDestino');
    
    if (movProducto) {
        try {
            const productos = await fetchAPI('/productos');
            if (productos) {
                movProducto.innerHTML = '<option value="">Seleccione un producto...</option>';
                productos.forEach(prod => {
                    movProducto.innerHTML += `<option value="${prod.id}">${prod.nombre} (Stock: ${prod.stock})</option>`;
                });
            }
        } catch (error) {
            console.error('Error al cargar productos:', error);
            alert('Error al cargar productos para movimientos: ' + error.message);
        }
    }

    if (movBodegaOrigen && movBodegaDestino) {
        try {
            const bodegas = await fetchAPI('/bodegas');
            if (bodegas) {
                let optionsOrigen = '<option value="">Ninguna / Seleccione...</option>';
                let optionsDestino = '<option value="">Seleccione una bodega...</option>';
                
                bodegas.forEach(bodega => {
                    optionsOrigen += `<option value="${bodega.id}">${bodega.nombre}</option>`;
                    optionsDestino += `<option value="${bodega.id}">${bodega.nombre}</option>`;
                });

                movBodegaOrigen.innerHTML = optionsOrigen;
                movBodegaDestino.innerHTML = optionsDestino;
            }
        } catch (error) {
            console.error('Error al cargar bodegas:', error);
            alert('Error al cargar bodegas para movimientos: ' + error.message);
        }
    }

    const tbody = document.getElementById('tablaMovimientosBody');
    if (!tbody) return;

    try {
        const movimientos = await fetchAPI('/movimientos');
        tbody.innerHTML = '';

        if (!movimientos || movimientos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No hay movimientos registrados</td></tr>';
            return;
        }

        movimientos.forEach(m => {
            const origenNombre = m.bodegaOrigen ? m.bodegaOrigen.nombre : '-';
            const destinoNombre = m.bodegaDestino ? m.bodegaDestino.nombre : '-';
            const productoNombre = m.producto ? m.producto.nombre : 'N/D';
            const fechaFormateada = m.fechaHora ? new Date(m.fechaHora).toLocaleString() : '-';

            tbody.innerHTML += `
                <tr>
                    <td>${fechaFormateada}</td>
                    <td><span class="badge ${m.tipo ? m.tipo.toLowerCase() : ''}">${m.tipo}</span></td>
                    <td>${productoNombre}</td>
                    <td>${origenNombre}</td>
                    <td>${destinoNombre}</td>
                    <td>${m.cantidad}</td>
                </tr>
            `;
        });
    } catch (error) {
        console.error('Error al cargar tabla de movimientos:', error);
        alert('Error al cargar movimientos: ' + error.message);
    }
}

document.getElementById('formMovimiento')?.addEventListener('submit', async function(event) {
    event.preventDefault();

    const tipo = document.getElementById('movTipo').value;
    const productoId = document.getElementById('movProducto').value;
    const bodegaOrigenId = document.getElementById('movBodegaOrigen').value;
    const bodegaDestinoId = document.getElementById('movBodegaDestino').value;
    const cantidad = document.getElementById('movCantidad').value;

    const movimientoData = {
        tipo: tipo,
        cantidad: parseInt(cantidad, 10),
        fechaHora: document.getElementById('movFechaHora').value,
        producto: { id: parseInt(productoId, 10) },
        bodegaOrigen: bodegaOrigenId ? { id: parseInt(bodegaOrigenId, 10) } : null,
        bodegaDestino: bodegaDestinoId ? { id: parseInt(bodegaDestinoId, 10) } : null
    };

    try {
        await fetchAPI('/movimientos', 'POST', movimientoData);

        alert('¡Movimiento registrado exitosamente!');
        this.reset();
        
        cargarMovimientosYFormulario();
        if (typeof cargarProductos === 'function') cargarProductos();

    } catch (error) {
        console.error('Error:', error);
        alert('Error al registrar movimiento: ' + error.message);
    }
});


// =========================================================
// MÓDULO: AUDITORÍA Y REPORTES
// =========================================================

// 1. Carga los logs de auditoría desde el Backend
let auditoriasData = []; // Guardará el listado original para poder filtrarlo sin volver a llamar a la API

async function cargarAuditoria() {
    const tbody = document.getElementById('tablaAuditoriaBody');
    if (!tbody) return;

    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Cargando registros de auditoría...</td></tr>';

    try {
        auditoriasData = await fetchAPI('/auditoria'); 
        
        // Asignamos eventos de filtro una sola vez al cargar la sección
        configurarFiltrosAuditoria();
        
        // Renderizamos los datos completos
        renderizarTablaAuditoria(auditoriasData);

    } catch (error) {
        console.error('Error al cargar auditoría:', error);
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--danger-color, red);">Error al cargar auditoría: ${error.message}</td></tr>`;
    }
}

// 2. Renderiza la tabla filtrada o completa
function renderizarTablaAuditoria(lista) {
    const tbody = document.getElementById('tablaAuditoriaBody');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (!lista || lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">No hay registros de auditoría.</td></tr>';
        return;
    }

    lista.forEach(a => {
        // Formato de fecha
        const fechaFormateada = a.fecha 
            ? new Date(a.fecha).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'medium' }) 
            : '-';

        // Detalle: Usamos idEntidad (ej. "ID: 37") o detalle si llegaras a agregarlo en el backend
        const detalleTexto = a.detalle ? a.detalle : (a.idEntidad ? `ID Afectado: ${a.idEntidad}` : '-');

        tbody.innerHTML += `
            <tr>
                <td>${fechaFormateada}</td>
                <td>${a.usuario || 'Sistema'}</td>
                <td><span class="badge ${a.operacion ? a.operacion.toLowerCase() : ''}">${a.operacion}</span></td>
                <td>${a.entidadAfectada || '-'}</td> <!-- CORREGIDO: entidadAfectada -->
                <td>${detalleTexto}</td>
            </tr>
        `;
    });
}

// 3. Lógica para filtrar en tiempo real por Usuario y Tipo de Operación

function configurarFiltrosAuditoria() {
    // Seleccionamos los inputs especificando la sección de auditoría para evitar conflictos con otros selects del HTML
    const sectionAuditoria = document.getElementById('section-auditoria');
    if (!sectionAuditoria) return;

    const inputBuscarUsuario = sectionAuditoria.querySelector('input[placeholder*="Buscar usuario"]') || 
                               sectionAuditoria.querySelector('input[type="text"]');
    const selectOperacion = sectionAuditoria.querySelector('select');

    const aplicarFiltros = () => {
        const busquedaUsuario = inputBuscarUsuario ? inputBuscarUsuario.value.toLowerCase().trim() : '';
        const operacionSeleccionada = selectOperacion ? selectOperacion.value.toUpperCase() : 'TODOS';

        const resultadosFiltrados = auditoriasData.filter(a => {
            // Obtenemos el nombre del usuario o 'sistema' si es nulo
            const nombreUsuario = (a.usuario || 'sistema').toLowerCase();
            const tipoOperacion = (a.operacion || '').toUpperCase();

            // Comprobamos si coincide el texto digitado
            const coincideUsuario = nombreUsuario.includes(busquedaUsuario);
            
            // Comprobamos si coincide la operación
            const coincideOperacion = (operacionSeleccionada === 'TODOS' || operacionSeleccionada === '') 
                                     || tipoOperacion === operacionSeleccionada
                                     || (operacionSeleccionada === 'CREATE' && tipoOperacion === 'INSERT');

            return coincideUsuario && coincideOperacion;
        });

        renderizarTablaAuditoria(resultadosFiltrados);
    };

    // Escuchamos el evento de escritura/cambio
    if (inputBuscarUsuario) {
        inputBuscarUsuario.removeEventListener('input', aplicarFiltros);
        inputBuscarUsuario.addEventListener('input', aplicarFiltros);
    }

    if (selectOperacion) {
        selectOperacion.removeEventListener('change', aplicarFiltros);
        selectOperacion.addEventListener('change', aplicarFiltros);
    }
}

// 4. Carga los resúmenes visuales de reportes en pantalla
async function cargarReportesEnPantalla() {
    const reporteStock = document.getElementById('reporteStockBodegas');
    const reporteMovidos = document.getElementById('reporteProductosMovidos');

    try {
        const bodegas = await fetchAPI('/bodegas');
        if (reporteStock && bodegas) {
            reporteStock.innerHTML = bodegas
                .map(b => `<p><strong>${b.nombre}:</strong> Capacidad ${b.capacidad} items</p>`)
                .join('');
        }

        const productos = await fetchAPI('/productos');
        if (reporteMovidos && productos) {
            const topProductos = productos.slice(0, 5);
            reporteMovidos.innerHTML = topProductos
                .map((p, i) => `<p>${i + 1}. ${p.nombre} (Stock: ${p.stock})</p>`)
                .join('');
        }
    } catch (error) {
        console.error('Error al cargar reportes visuales:', error);
    }
}





// 3. Genera la descarga del archivo TXT
function descargarReporteTxt() {
    const texto = `--- REPORTE DE SISTEMA DE GESTIÓN ---
Generado el: ${new Date().toLocaleString('es-CO')}

Reporte exportado correctamente desde el módulo de administración.`;
    
    const blob = new Blob([texto], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reporte_inventario_${new Date().toISOString().slice(0, 10)}.txt`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
}