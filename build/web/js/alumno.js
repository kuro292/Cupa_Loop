/**
 * ===== LOGOUT =====
 */
function logout() {
    Swal.fire({
        title: '¿Cerrar sesión?',
        text: '¿Estás seguro de que deseas salir de la aplicación?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, salir',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d'
    }).then((result) => {
        if (result.isConfirmed) {
            // Limpiar sesión
            sessionStorage.clear();
            localStorage.clear();
            // Redirigir a página de login
            window.location.href = '../../index.html';
        }
    });
}

async function cargarModulo(archivoHTML){
    let url = archivoHTML;
    let resp = await fetch(url);
    let contenido = await resp.text();
    document.getElementById("contenedor-principal").innerHTML = contenido;
    
    // Cargar el inventario si la página es inventario_alumno.html
    if(archivoHTML.includes('inventario_alumno')) {
        cargarInventarioAlumno();
    }
}

/**
 * Carga todos los productos disponibles del inventario y los muestra para que el alumno solicite piezas
 */
async function cargarInventarioAlumno() {
    try {
        let url = "/cupa_loop/api/admin/producto/getALL";
        let response = await fetch(url);
        let productos = await response.json();
        
        // Validar si hay error o excepción
        if(productos.error != null) {
            Swal.fire('Error', productos.error, 'error');
            return;
        }
        if(productos.exception != null) {
            Swal.fire('Error del servidor', productos.exception, 'error');
            return;
        }
        
        // Limpiar la tabla
        let tbody = document.getElementById('tbodyInventarioAlumno');
        tbody.innerHTML = '';
        
        // Si no hay productos, mostrar mensaje
        if(productos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No hay equipos disponibles en este momento</td></tr>`;
            return;
        }
        
        // Mostrar los productos usando la función reutilizable
        mostrarProductosAlumno(productos);
        
    } catch (error) {
        console.error('Error al cargar el inventario:', error);
        Swal.fire('Error crítico', 'No se pudo cargar el inventario', 'error');
    }
}

/**
 * Obtiene el nombre de la categoría basado en el ID para vista de alumno
 */
function obtenerNombreCategoriaAlumno(idCategoria) {
    const categorias = {
        1: 'Computadora',
        2: 'Monitor',
        3: 'Periférico',
        4: 'Servidor',
        5: 'Red'
    };
    return categorias[idCategoria] || 'Equipo';
}

/**
 * Obtiene el ícono de Bootstrap Icons según la categoría para vista de alumno
 */
function obtenerIconoProductoAlumno(idCategoria) {
    const iconos = {
        1: '<i class="bi bi-laptop me-1"></i>',
        2: '<i class="bi bi-display me-1"></i>',
        3: '<i class="bi bi-mouse2 me-1"></i>',
        4: '<i class="bi bi-server me-1"></i>',
        5: '<i class="bi bi-diagram-3 me-1"></i>'
    };
    return iconos[idCategoria] || '<i class="bi bi-box me-1"></i>';
}

/**
 * Formatea la fecha a un formato legible para alumno (ej: 05 Ene, 2026)
 */
function formatearFechaAlumno(fechaStr) {
    if(!fechaStr) return 'N/A';
    try {
        let fecha = new Date(fechaStr);
        return fecha.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
    } catch(e) {
        return fechaStr;
    }
}

/**
 * Maneja la solicitud de una pieza por parte del alumno
 */
function solicitarPieza(idProducto, nombreProducto) {
    let usuarioSesion = JSON.parse(sessionStorage.getItem('usuarioSesion'));
    
    if(!usuarioSesion) {
        Swal.fire('Sesión expirada', 'Por favor, inicia sesión nuevamente', 'warning');
        return;
    }
    
    Swal.fire({
        title: 'Solicitar pieza',
        html: `
            <p class="mb-3">¿Por qué necesitas este producto?</p>
            <p class="text-muted small mb-3"><strong>Producto:</strong> ${nombreProducto}</p>
            <textarea id="txtMotivo" class="form-control" placeholder="Explica brevemente tu motivo..." style="min-height: 100px; max-height: 150px; resize: vertical; padding: 10px; border-radius: 5px; border: 1px solid #ced4da;" required></textarea>
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Enviar solicitud',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#198754',
        preConfirm: () => {
            const motivo = document.getElementById('txtMotivo').value.trim();
            if (!motivo) {
                Swal.showValidationMessage('Por favor escribe un motivo');
                return false;
            }
            return motivo;
        }
    }).then((result) => {
        if (result.isConfirmed) {
            crearSolicitud(idProducto, usuarioSesion.id, result.value);
        }
    });
}

/**
 * Crea una solicitud en la base de datos con motivo
 */
async function crearSolicitud(idProducto, idUsuario, motivo) {
    try {
        let url = "/cupa_loop/api/admin/solicitud/create";
        let params = {
            id_producto: idProducto,
            id_usuario: idUsuario,
            motivo: motivo
        };
        
        let config = {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
            body: new URLSearchParams(params)
        };
        
        let response = await fetch(url, config);
        let data = await response.json();
        
        if(data.error != null) {
            Swal.fire('Error', data.error, 'error');
        } else if(data.exception != null) {
            Swal.fire('Error del servidor', data.exception, 'error');
        } else {
            Swal.fire('Exito', 'Tu solicitud ha sido registrada correctamente', 'success');
            // Recargar el inventario despues de hacer la solicitud
            setTimeout(() => cargarInventarioAlumno(), 1000);
        }
    } catch (error) {
        console.error('Error al crear la solicitud:', error);
        Swal.fire('Error critico', 'No se pudo procesar tu solicitud', 'error');
    }
}

/**
 * Busca productos por nombre o descripción en la vista del alumno
 */
async function buscarProductosAlumno(filtro) {
    try {
        let url = "/cupa_loop/api/admin/producto/getALL?filtro=" + encodeURIComponent(filtro);
        let response = await fetch(url);
        let productos = await response.json();
        
        if(productos.error != null) {
            Swal.fire('Error', productos.error, 'error');
            return;
        }
        if(productos.exception != null) {
            Swal.fire('Error del servidor', productos.exception, 'error');
            return;
        }
        
        let tbody = document.getElementById('tbodyInventarioAlumno');
        tbody.innerHTML = '';
        
        if(productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No se encontraron equipos disponibles con ese criterio de búsqueda</td></tr>';
            return;
        }
        
        mostrarProductosAlumno(productos);
        
    } catch (error) {
        console.error('Error en búsqueda:', error);
        Swal.fire('Error crítico', 'No se pudo buscar', 'error');
    }
}

/**
 * Filtra productos por categoría en la vista del alumno
 */
async function filtrarPorCategoria(idCategoria) {
    try {
        let url = "/cupa_loop/api/admin/producto/getALL";
        let response = await fetch(url);
        let productos = await response.json();
        
        if(productos.error != null) {
            Swal.fire('Error', productos.error, 'error');
            return;
        }
        
        // Filtrar por categoría si se seleccionó una
        if(idCategoria !== '') {
            productos = productos.filter(p => p.id_categoria == parseInt(idCategoria));
        }
        
        let tbody = document.getElementById('tbodyInventarioAlumno');
        tbody.innerHTML = '';
        
        if(productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No hay equipos disponibles en esta categoría</td></tr>';
            return;
        }
        
        mostrarProductosAlumno(productos);
        
    } catch (error) {
        console.error('Error en filtrado:', error);
        Swal.fire('Error crítico', 'No se pudo filtrar', 'error');
    }
}

/**
 * Muestra los productos en la tabla (reutilizable para búsqueda y filtrado)
 */
function mostrarProductosAlumno(productos) {
    let tbody = document.getElementById('tbodyInventarioAlumno');
    
    productos.forEach(producto => {
        let fila = document.createElement('tr');
        
        // Ícono según categoría
        let icono = obtenerIconoProductoAlumno(producto.id_categoria);
        
        // Formatear condición física
        let condicionBadge = `<span class="badge bg-secondary bg-opacity-10 text-secondary">${producto.condicion_fisica || 'N/A'}</span>`;
        
        fila.innerHTML = `
            <td class="ps-4 fw-bold text-secondary">INV-${producto.id}</td>
            <td><span class="badge bg-dark bg-opacity-10 text-dark">${icono} ${obtenerNombreCategoriaAlumno(producto.id_categoria)}</span></td>
            <td><strong>${producto.nombre}</strong></td>
            <td class="small text-muted" title="${producto.descripcion}">${producto.descripcion.substring(0, 35)}${producto.descripcion.length > 35 ? '...' : ''}</td>
            <td class="text-center">${condicionBadge}</td>
            <td class="text-center"><span class="badge bg-success bg-opacity-10 text-success border border-success">Disponible</span></td>
            <td class="text-center pe-4">
                <button class="btn btn-sm btn-outline-success fw-bold" onclick="solicitarPieza(${producto.id}, '${producto.nombre.replace(/'/g, "\\'")}')">
                    Solicitar
                </button>
            </td>
        `;
        
        tbody.appendChild(fila);
    });
}

/**
 * Carga todas las solicitudes del usuario actual - MIS PEDIDOS
 */
async function cargarMisPedidos() {
    try {
        // Obtener usuario de sessionStorage
        let usuarioJSON = sessionStorage.getItem('usuarioSesion');
        if(!usuarioJSON) {
            Swal.fire('Error', 'No hay sesión activa', 'error');
            return;
        }
        
        let usuario = JSON.parse(usuarioJSON);
        let url = "/cupa_loop/api/admin/solicitud/getByUsuario?idUsuario=" + usuario.id;
        let response = await fetch(url);
        let solicitudes = await response.json();
        
        if(solicitudes.error != null) {
            Swal.fire('Error', solicitudes.error, 'error');
            return;
        }
        if(solicitudes.exception != null) {
            Swal.fire('Error del servidor', solicitudes.exception, 'error');
            return;
        }
        
        // Limpiar la tabla
        let tbody = document.getElementById('tbodyMisPedidos');
        tbody.innerHTML = '';
        
        // Si no hay solicitudes
        if(solicitudes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No tienes solicitudes aún</td></tr>';
            return;
        }
        
        // Llenar la tabla con solicitudes
        solicitudes.forEach(solicitud => {
            let fila = document.createElement('tr');
            
            // Badge de estado
            let estadoBadge = '';
            if(solicitud.estado_solicitud === 'PENDIENTE') {
                estadoBadge = '<span class="badge bg-warning bg-opacity-10 text-warning border border-warning">Pendiente</span>';
            } else if(solicitud.estado_solicitud === 'APROBADA') {
                estadoBadge = '<span class="badge bg-success bg-opacity-10 text-success border border-success">Aprobada</span>';
            } else if(solicitud.estado_solicitud === 'RECHAZADA') {
                estadoBadge = '<span class="badge bg-danger bg-opacity-10 text-danger border border-danger">Rechazada</span>';
            } else if(solicitud.estado_solicitud === 'ENTREGADA') {
                estadoBadge = '<span class="badge bg-info bg-opacity-10 text-info border border-info">Entregada</span>';
            }
            
            // Botón para ver motivo completo
            let botonMotivo = solicitud.motivo 
                ? `<button class="btn btn-sm btn-link text-primary p-0" onclick="verMotivoCompleto('${solicitud.motivo.replace(/'/g, "\\'")}')"><i class="bi bi-eye"></i> Ver</button>`
                : '<span class="text-muted">-</span>';
            
            fila.innerHTML = `
                <td class="ps-4 fw-bold text-secondary">SOL-${solicitud.id}</td>
                <td><strong>${solicitud.producto_nombre}</strong></td>
                <td class="small">${formatearFecha(solicitud.fecha)}</td>
                <td class="small">${botonMotivo}</td>
                <td class="text-center">${estadoBadge}</td>
                <td class="text-center pe-4 small">${solicitud.comentario || '-'}</td>
            `;
            
            tbody.appendChild(fila);
        });
        
    } catch (error) {
        console.error('Error al cargar solicitudes:', error);
        Swal.fire('Error crítico', 'No se pudieron cargar las solicitudes', 'error');
    }
}

/**
 * Formatea una fecha al formato DD/MM/YYYY
 */
function formatearFecha(fecha) {
    if(!fecha) return '-';
    const date = new Date(fecha);
    const dia = String(date.getDate()).padStart(2, '0');
    const mes = String(date.getMonth() + 1).padStart(2, '0');
    const año = date.getFullYear();
    return `${dia}/${mes}/${año}`;
}

/**
 * Muestra el motivo completo en un modal
 */
function verMotivoCompleto(motivo) {
    Swal.fire({
        title: 'Motivo de la Solicitud',
        html: `<div style="text-align: left; background-color: #f8f9fa; padding: 15px; border-radius: 5px; max-height: 300px; overflow-y: auto; border-left: 4px solid #198754;">${motivo}</div>`,
        icon: 'info',
        confirmButtonText: 'Cerrar',
        confirmButtonColor: '#198754'
    });
}