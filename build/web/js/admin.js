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
    
    // Cargar el inventario si la página es inventario_actual.html
    if(archivoHTML.includes('inventario_actual')) {
        cargarInventarioAdmin();
    }
    
    // Cargar categorias si la página es categorias.html
    if(archivoHTML.includes('categorias')) {
        cargarCategorias();
    }
    
    // Cargar bandeja de pedidos si la página es bandeja_de_pedidos.html
    if(archivoHTML.includes('bandeja')) {
        cargarBandeja();
    }
    
    // Llenar combobox de categorias si la página es registro_RAEE.html
    if(archivoHTML.includes('registro_RAEE')) {
        llenarComboCategorias();
    }
}

async function cargarInventarioAdmin() {
    try {
        let url = "/cupa_loop/api/admin/producto/getAllAdmin";
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
        let tbody = document.getElementById('tbodyInventario');
        tbody.innerHTML = '';
        
        // Si no hay productos, mostrar mensaje
        if(productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" class="text-center text-muted py-4">No hay productos en el inventario</td></tr>';
            return;
        }
        
        // Llenar la tabla con cada producto
        productos.forEach(producto => {
            let fila = document.createElement('tr');
            
            // Determinar el badge de estado
            let estadoBadge = producto.estado_inventario === 'DISPONIBLE' 
                ? `<span class="badge bg-success bg-opacity-10 text-success border border-success">Disponible</span>`
                : producto.estado_inventario === 'RESERVADO'
                ? `<span class="badge bg-warning bg-opacity-10 text-warning border border-warning">Reservado</span>`
                : `<span class="badge bg-danger bg-opacity-10 text-danger border border-danger">No disp.</span>`;
            
            // Determinar el icono según el tipo de producto
            let icono = obtenerIconoProducto(producto.id_categoria);
            
            // Formatear condición física
            let condicionBadge = `<span class="badge bg-secondary bg-opacity-10 text-secondary">${producto.condicion_fisica || 'N/A'}</span>`;
            
            // Formatear usuario asignado
            let usuario = producto.id_usuario_asignado || 'Sin asignar';
            
            fila.innerHTML = `
                <td class="ps-4 fw-bold">INV-${producto.id}</td>
                <td>${icono} ${obtenerNombreCategoria(producto.id_categoria)}</td>
                <td><strong>${producto.nombre}</strong></td>
                <td class="small text-muted" title="${producto.descripcion}">${producto.descripcion.substring(0, 30)}${producto.descripcion.length > 30 ? '...' : ''}</td>
                <td class="text-center">${condicionBadge}</td>
                <td class="text-center">${estadoBadge}</td>
                <td class="small">${formatearFecha(producto.fecha_ingreso)}</td>
                <td class="small">${producto.fecha_salida ? formatearFecha(producto.fecha_salida) : '-'}</td>
                <td class="small">${usuario}</td>
                <td class="text-center pe-4">
                    <button class="btn btn-sm btn-outline-warning me-1" onclick="editarDetallesProducto(${producto.id})" title="Editar"><i class="bi bi-pencil"></i></button>
                </td>
            `;
            
            tbody.appendChild(fila);
        });
        
    } catch (error) {
        console.error('Error al cargar el inventario:', error);
        Swal.fire('Error critico', 'No se pudo cargar el inventario', 'error');
    }
}

/**
 * Obtiene el nombre de la categoría basado en el ID
 */
function obtenerNombreCategoria(idCategoria) {
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
 * Obtiene el ícono de Bootstrap Icons según la categoría
 */
function obtenerIconoProducto(idCategoria) {
    const iconos = {
        1: '<i class="bi bi-laptop text-secondary me-2"></i>',
        2: '<i class="bi bi-display text-secondary me-2"></i>',
        3: '<i class="bi bi-mouse2 text-secondary me-2"></i>',
        4: '<i class="bi bi-server text-secondary me-2"></i>',
        5: '<i class="bi bi-diagram-3 text-secondary me-2"></i>'
    };
    return iconos[idCategoria] || '<i class="bi bi-box text-secondary me-2"></i>';
}

/**
 * Formatea la fecha a un formato legible (ej: 15 Ene, 2026)
 */
function formatearFecha(fechaStr) {
    if(!fechaStr) return 'N/A';
    try {
        let fecha = new Date(fechaStr);
        return fecha.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch(e) {
        return fechaStr;
    }
}

/**
 * ===== CATEGORIAS =====
 */
async function llenarComboCategorias() {
    try {
        let url = "/cupa_loop/api/admin/categoria/getALL";
        let response = await fetch(url);
        let categorias = await response.json();
        
        if(categorias.error != null) {
            Swal.fire('Error', categorias.error, 'error');
            return;
        }
        if(categorias.exception != null) {
            Swal.fire('Error del servidor', categorias.exception, 'error');
            return;
        }
        
        let cmb = document.getElementById('cmbTipo');
        if(!cmb) return;
        
        cmb.innerHTML = '<option selected disabled>Selecciona una categoria...</option>';
        
        categorias.forEach(cat => {
            let option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.nombre;
            cmb.appendChild(option);
        });
        
    } catch (error) {
        console.error('Error al llenar combobox:', error);
    }
}

async function cargarCategorias() {
    try {
        let url = "/cupa_loop/api/admin/categoria/getALL";
        let response = await fetch(url);
        let categorias = await response.json();
        
        if(categorias.error != null) {
            Swal.fire('Error', categorias.error, 'error');
            return;
        }
        if(categorias.exception != null) {
            Swal.fire('Error del servidor', categorias.exception, 'error');
            return;
        }
        
        let tbody = document.getElementById('tbodyCategorias');
        tbody.innerHTML = '';
        
        if(categorias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-4">No hay categorias registradas</td></tr>';
            return;
        }
        
        categorias.forEach((cat, index) => {
            let fila = document.createElement('tr');
            fila.innerHTML = `
                <td class="ps-4 fw-bold">CAT-${String(index + 1).padStart(2, '0')}</td>
                <td><span class="badge bg-dark">${cat.nombre}</span></td>
                <td class="text-muted small pe-4">${cat.descripcion || 'Sin descripcion'}</td>
            `;
            tbody.appendChild(fila);
        });
        
    } catch (error) {
        console.error('Error al cargar categorias:', error);
        Swal.fire('Error critco', 'No se pudo cargar las categorias', 'error');
    }
}

async function guardarCategoria() {
    try {
        let nombre = document.getElementById('txtNombreCategoria').value.trim();
        let descripcion = document.getElementById('txtDescripcionCategoria').value.trim();
        
        if(nombre === '') {
            Swal.fire('Atenccion', 'Por favor, ingresa el nombre de la categoria.', 'warning');
            return;
        }
        
        let url = "/cupa_loop/api/admin/categoria/save";
        let categoria = {
            id: 0,
            nombre: nombre,
            descripcion: descripcion,
            activa: '1'
        };
        
        let params = {
            categoria: JSON.stringify(categoria)
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
            Swal.fire('Exito', 'Categoria guardada correctamente', 'success');
            document.getElementById('txtNombreCategoria').value = '';
            document.getElementById('txtDescripcionCategoria').value = '';
            cargarCategorias();
        }
    } catch (error) {
        console.error('Error al guardar categoria:', error);
        Swal.fire('Error critico', 'No se pudo guardar la categoria', 'error');
    }
}

/**
 * ===== BANDEJA DE PEDIDOS =====
 */
async function cargarBandeja() {
    try {
        let url = "/cupa_loop/api/admin/solicitud/getPendientes";
        let response = await fetch(url);
        
        if(!response.ok) {
            throw new Error('Error en la respuesta del servidor: ' + response.status);
        }
        
        let responseText = await response.text();
        console.log('Respuesta texto getPendientes:', responseText);
        
        let solicitudes;
        try {
            solicitudes = JSON.parse(responseText);
        } catch(parseError) {
            console.error('Error al parsear JSON:', parseError);
            Swal.fire('Error de parseo', 'La respuesta del servidor no es JSON válido: ' + responseText.substring(0, 100), 'error');
            return;
        }
        
        // Manejar caso donde la respuesta es una excepción
        if(solicitudes.exception != null) {
            console.error('Excepción del servidor:', solicitudes.exception);
            Swal.fire('Error del servidor', 'Excepción: ' + solicitudes.exception, 'error');
            return;
        }
        
        if(solicitudes.error != null) {
            Swal.fire('Error', solicitudes.error, 'error');
            return;
        }
        
        // Si no es un array, algo anda mal
        if(!Array.isArray(solicitudes)) {
            console.error('La respuesta no es un array:', solicitudes);
            Swal.fire('Error', 'La respuesta no tiene el formato esperado', 'error');
            return;
        }
        
        let tbody = document.getElementById('tbodyBandeja');
        tbody.innerHTML = '';
        
        if(solicitudes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No hay solicitudes pendientes</td></tr>';
            return;
        }
        
        solicitudes.forEach((sol, index) => {
            let fila = document.createElement('tr');
            
            // Botón para ver motivo completo
            let botonMotivo = sol.motivo 
                ? `<button class="btn btn-sm btn-link text-primary p-0" onclick="verMotivoCompletoAdmin('${sol.motivo.replace(/'/g, "\\'")}')"><i class="bi bi-eye"></i> Ver</button>`
                : '<span class="text-muted">-</span>';
            
            fila.innerHTML = `
                <td class="ps-4 fw-bold text-secondary">#PD-${String(index + 1).padStart(3, '0')}</td>
                <td>${sol.producto_nombre}</td>
                <td>
                    <div class="d-flex align-items-center">
                        <div class="bg-light rounded-circle p-2 me-2"><i class="bi bi-person text-success"></i></div>
                        <div>
                            <div class="fw-bold">${sol.nombre}</div>
                            <div class="small text-muted">${sol.identificador}</div>
                        </div>
                    </div>
                </td>
                <td class="small">${botonMotivo}</td>
                <td>${formatearFecha(sol.fecha)}</td>
                <td class="text-center"><span class="badge bg-warning text-dark">Pendiente</span></td>
                <td class="text-center pe-4">
                    <button class="btn btn-sm btn-success me-1" onclick="aprobarSolicitud(${sol.id})" title="Aprobar"><i class="bi bi-check-lg"></i></button>
                    <button class="btn btn-sm btn-danger" onclick="rechazarSolicitud(${sol.id})" title="Rechazar"><i class="bi bi-x-lg"></i></button>
                </td>
            `;
            tbody.appendChild(fila);
        });
        
    } catch (error) {
        console.error('Error al cargar bandeja:', error);
        Swal.fire('Error critico', 'No se pudo cargar la bandeja de pedidos', 'error');
    }
}

async function aprobarSolicitud(idSolicitud) {
    Swal.fire({
        title: 'Aprobar solicitud',
        text: 'Esta seguro que deseas aprobar esta solicitud?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Si, aprobar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#198754'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                let url = "/cupa_loop/api/admin/solicitud/update";
                let params = {
                    id: idSolicitud,
                    estado: 'APROBADA',
                    comentario: 'Solicitud aprobada por el administrador'
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
                    Swal.fire('Exito', 'Solicitud aprobada correctamente', 'success');
                    cargarBandeja();
                }
            } catch (error) {
                console.error('Error al aprobar:', error);
                Swal.fire('Error critico', 'No se pudo aprobar la solicitud', 'error');
            }
        }
    });
}

async function rechazarSolicitud(idSolicitud) {
    const { value: comentario } = await Swal.fire({
        title: 'Rechazar solicitud',
        input: 'textarea',
        inputLabel: 'Motivo del rechazo',
        inputPlaceholder: 'Ingresa el motivo por el cual rechazas esta solicitud...',
        inputAttributes: {
            'aria-label': 'Motivo del rechazo'
        },
        showCancelButton: true,
        confirmButtonText: 'Rechazar',
        confirmButtonColor: '#dc3545',
        cancelButtonText: 'Cancelar'
    });
    
    if (comentario !== undefined) {
        try {
            let url = "/cupa_loop/api/admin/solicitud/update";
            let params = {
                id: idSolicitud,
                estado: 'RECHAZADA',
                comentario: comentario
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
                Swal.fire('Exito', 'Solicitud rechazada correctamente', 'success');
                cargarBandeja();
            }
        } catch (error) {
            console.error('Error al rechazar:', error);
            Swal.fire('Error critico', 'No se pudo rechazar la solicitud', 'error');
        }
    }
}

/**
 * ===== REGISTRO DE PRODUCTO =====
 */
async function registrarProducto() {
    try {
        let categoria = document.getElementById('cmbTipo').value;
        let nombre = document.getElementById('txtNombre').value.trim();
        let estado = document.getElementById('cmbEstado').value;
        let descripcion = document.getElementById('txtDescripcion').value.trim();
        
        if(categoria === '' || nombre === '' || estado === '' || descripcion === '') {
            Swal.fire('Atenccion', 'Por favor, completa todos los campos.', 'warning');
            return;
        }
        
        let url = "/cupa_loop/api/admin/producto/save";
        let hoy = new Date().toISOString().split('T')[0];
        let producto = {
            id: 0,
            id_categoria: parseInt(categoria),
            nombre: nombre,
            descripcion: descripcion,
            condicion_fisica: estado.toUpperCase(),
            estado_inventario: 'DISPONIBLE',
            fecha_ingreso: hoy,
            fecha_salida: null,
            id_usuario_asignado: null
        };
        
        let params = {
            producto: JSON.stringify(producto)
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
            Swal.fire('Exito', 'Producto registrado correctamente', 'success');
            // Limpiar campos individuales
            document.getElementById('cmbTipo').value = '';
            document.getElementById('txtNombre').value = '';
            document.getElementById('cmbEstado').value = '';
            document.getElementById('txtDescripcion').value = '';
            setTimeout(() => cargarModulo('inventario_actual.html'), 1500);
        }
    } catch (error) {
        console.error('Error al registrar producto:', error);
        Swal.fire('Error critico', 'No se pudo registrar el producto', 'error');
    }
}

/**
 * Muestra el motivo del alumno en un modal (versión para admin)
 */
function verMotivoCompletoAdmin(motivo) {
    Swal.fire({
        title: 'Motivo de la Solicitud del Alumno',
        html: `<div style="text-align: left; background-color: #f8f9fa; padding: 15px; border-radius: 5px; max-height: 300px; overflow-y: auto; border-left: 4px solid #0d6efd;">${motivo}</div>`,
        icon: 'info',
        confirmButtonText: 'Cerrar',
        confirmButtonColor: '#0d6efd'
    });
}

/**
 * Muestra el motivo del alumno en un modal (versión para admin)
 */
function verMotivoCompletoAdmin(motivo) {
    Swal.fire({
        title: 'Motivo de la Solicitud del Alumno',
        html: `<div style="text-align: left; background-color: #f8f9fa; padding: 15px; border-radius: 5px; max-height: 300px; overflow-y: auto; border-left: 4px solid #0d6efd;">${motivo}</div>`,
        icon: 'info',
        confirmButtonText: 'Cerrar',
        confirmButtonColor: '#0d6efd'
    });
}

/**
 * ===== EDICION DE PRODUCTOS =====
 */
async function editarEstadoProducto(idProducto) {
    Swal.fire({
        title: 'Cambiar estado del producto',
        html: `
            <select id="nuevoEstado" class="form-select mb-3">
                <option selected disabled>Selecciona nuevo estado...</option>
                <option value="DISPONIBLE">Disponible</option>
                <option value="RESERVADO">Reservado</option>
                <option value="ENTREGADO">Entregado</option>
                <option value="RECICLADO">Reciclado / Dado de baja</option>
            </select>
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Actualizar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#198754',
        preConfirm: () => {
            let estado = document.getElementById('nuevoEstado').value;
            if (!estado || estado === 'Selecciona nuevo estado...') {
                Swal.showValidationMessage('Por favor, selecciona un estado');
                return false;
            }
            return estado;
        }
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                let url = "/cupa_loop/api/admin/producto/updateEstado";
                let params = {
                    id: idProducto,
                    estado_inventario: result.value
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
                    Swal.fire('Exito', 'Producto actualizado correctamente', 'success');
                    cargarInventarioAdmin();
                }
            } catch (error) {
                console.error('Error al actualizar:', error);
                Swal.fire('Error critico', 'No se pudo actualizar el producto', 'error');
            }
        }
    });
}

/**
 * ===== BUSQUEDA DE PRODUCTOS =====
 */
async function buscarProductos(filtro) {
    try {
        let url = "/cupa_loop/api/admin/producto/getAllAdmin?filtro=" + encodeURIComponent(filtro);
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
        
        let tbody = document.getElementById('tbodyInventario');
        tbody.innerHTML = '';
        
        if(productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" class="text-center text-muted py-4">No se encontraron productos</td></tr>';
            return;
        }
        
        productos.forEach(producto => {
            let fila = document.createElement('tr');
            
            let estadoBadge = producto.estado_inventario === 'DISPONIBLE' 
                ? `<span class="badge bg-success bg-opacity-10 text-success border border-success">Disponible</span>`
                : producto.estado_inventario === 'RESERVADO'
                ? `<span class="badge bg-warning bg-opacity-10 text-warning border border-warning">Reservado</span>`
                : `<span class="badge bg-danger bg-opacity-10 text-danger border border-danger">No disp.</span>`;
            
            let icono = obtenerIconoProducto(producto.id_categoria);
            
            let condicionBadge = `<span class="badge bg-secondary bg-opacity-10 text-secondary">${producto.condicion_fisica || 'N/A'}</span>`;
            
            let usuario = producto.id_usuario_asignado || 'Sin asignar';
            
            fila.innerHTML = `
                <td class="ps-4 fw-bold">INV-${producto.id}</td>
                <td>${icono} ${obtenerNombreCategoria(producto.id_categoria)}</td>
                <td><strong>${producto.nombre}</strong></td>
                <td class="small text-muted" title="${producto.descripcion}">${producto.descripcion.substring(0, 30)}${producto.descripcion.length > 30 ? '...' : ''}</td>
                <td class="text-center">${condicionBadge}</td>
                <td class="text-center">${estadoBadge}</td>
                <td class="small">${formatearFecha(producto.fecha_ingreso)}</td>
                <td class="small">${producto.fecha_salida ? formatearFecha(producto.fecha_salida) : '-'}</td>
                <td class="small">${usuario}</td>
                <td class="text-center pe-4">
                    <button class="btn btn-sm btn-outline-warning me-1" onclick="editarDetallesProducto(${producto.id})" title="Editar"><i class="bi bi-pencil"></i></button>
                </td>
            `;
            
            tbody.appendChild(fila);
        });
        
    } catch (error) {
        console.error('Error en busqueda:', error);
        Swal.fire('Error critico', 'No se pudo buscar', 'error');
    }
}

/**
 * Edita los detalles completos de un producto (descripción, condición y estado)
 */
async function editarDetallesProducto(idProducto) {
    Swal.fire({
        title: 'Editar detalles del producto',
        html: `
            <div class="mb-3">
                <label class="form-label text-start d-block">Descripción</label>
                <textarea id="editDescripcion" class="form-control" rows="3" placeholder="Ingresa la descripción del producto..."></textarea>
            </div>
            <div class="mb-3">
                <label class="form-label text-start d-block">Condición Física</label>
                <select id="editCondicion" class="form-select">
                    <option selected disabled>Selecciona la condición...</option>
                    <option value="FUNCIONAL">Funcional (Donación)</option>
                    <option value="REPARABLE">Dañado (Reparable)</option>
                    <option value="OBSOLETO">Obsoleto (Solo piezas/Reciclaje)</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label text-start d-block">Estado del Inventario</label>
                <select id="editEstado" class="form-select">
                    <option selected disabled>Selecciona el estado...</option>
                    <option value="DISPONIBLE">Disponible</option>
                    <option value="RESERVADO">Reservado</option>
                    <option value="ENTREGADO">Entregado</option>
                    <option value="RECICLADO">Reciclado / Dado de baja</option>
                </select>
            </div>
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Actualizar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#198754',
        preConfirm: () => {
            let descripcion = document.getElementById('editDescripcion').value.trim();
            let condicion = document.getElementById('editCondicion').value;
            let estado = document.getElementById('editEstado').value;
            
            if (!descripcion || !condicion || !estado) {
                Swal.showValidationMessage('Por favor, completa todos los campos');
                return false;
            }
            
            return { descripcion, condicion, estado };
        }
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                let url = "/cupa_loop/api/admin/producto/updateCompleto";
                let params = {
                    id: idProducto,
                    descripcion: result.value.descripcion,
                    condicion_fisica: result.value.condicion,
                    estado_inventario: result.value.estado
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
                    Swal.fire('Exito', 'Producto actualizado correctamente', 'success');
                    cargarInventarioAdmin();
                }
            } catch (error) {
                console.error('Error al actualizar:', error);
                Swal.fire('Error critico', 'No se pudo actualizar el producto', 'error');
            }
        }
    });
}