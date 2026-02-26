
/* Artifact:   login.js

    Version:    1.0
    Date:       2026-02-25 19:00:00
    Author:     Claudia Estefania Contreras Portugal
    Email:      88014@alumnos.utloen.edu.mx
    Comments:   Este js gestiona el control de acceso y el registro de 
                usuarios para el sistema mediante peticiones asíncronas al 
                API. Se encarga de validar credenciales, manejar sesiones en el 
                navegador y redirigir dinámicamente a los usuarios según su rol 
                asignado (admin o estudiante)
*/

async function login() {
    let identificadorUsuario = document.getElementById("txtIdentificador").value.trim();
    let contrasenia = document.getElementById("txtPassword").value.trim();
    
    if(identificadorUsuario === "" || contrasenia === "") {
        Swal.fire('Atención', 'Por favor, ingresa tu usuario y contraseña.', 'warning');
        return; 
    }
    
let url = "/cupa_loop/api/usuario/login";
    
    let params = {
        identificador : identificadorUsuario,
        contrasenia : contrasenia
    };
    
    let confServ = {
        method : "POST",
        headers : {"Content-Type" : "application/x-www-form-urlencoded;charset=UTF-8"},
        body : new URLSearchParams(params) 
    };
    
    try {
        let response = await fetch(url, confServ);
        let data = await response.json();
        
        if(data.error != null){
            Swal.fire('Acceso denegado', data.error, 'warning'); 
            return;
        }
        else if (data.exception != null){
            Swal.fire('Error del servidor', data.exception, 'error');
            return;
        } 
        else {
            sessionStorage.setItem("usuarioSesion", JSON.stringify(data));

            let rolUsuario = data.rol;

            if(rolUsuario === 'ADMIN') {
                window.location.href = '../modules/admin/admin.html'; 
            } 
            else if(rolUsuario === 'ESTUDIANTE') {
                window.location.href = '../modules/estudiante/estudiante.html'; 
            }
        }
    } catch (error) {
        console.error("Fallo en la petición:", error);
        Swal.fire('Error crítico', 'No se pudo conectar con el servidor.', 'error');
    }
}

/**
 * Registra un nuevo usuario en el sistema
 */
async function registrarUsuario(event) {
    event.preventDefault();
    
    let nombre = document.getElementById('txtNombre').value.trim();
    let apellido = document.getElementById('txtApellido').value.trim();
    let email = document.getElementById('txtEmail').value.trim();
    let password = document.getElementById('txtPassword').value.trim();
    
    // Validar formulario
    if(nombre === '' || apellido === '' || email === '' || password === '') {
        Swal.fire('Atención', 'Por favor completa todos los campos.', 'warning');
        return;
    }
    
    // Validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if(!emailRegex.test(email)) {
        Swal.fire('Atención', 'Por favor ingresa un correo válido.', 'warning');
        return;
    }
    
    // Usar el email como identificador para alumnos
    let identificador = email.split('@')[0];
    
    let url = "/cupa_loop/api/usuario/register";
    
    let params = {
        identificador: identificador,
        correo: email,
        nombre: nombre,
        apellido: apellido,
        contrasenia: password
    };
    
    let config = {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"},
        body: new URLSearchParams(params)
    };
    
    try {
        let response = await fetch(url, config);
        let data = await response.json();
        
        if(data.error != null) {
            Swal.fire('Error', data.error, 'error');
            return;
        }
        
        Swal.fire({
            title: '¡Cuenta creada!',
            text: 'Tu registro fue exitoso. Ahora puedes iniciar sesión.',
            icon: 'success',
            confirmButtonText: 'Ir al login'
        }).then(() => {
            window.location.href = 'login.html';
        });
        
    } catch (error) {
        console.error("Error al registrar:", error);
        Swal.fire('Error crítico', 'No se pudo conectar con el servidor.', 'error');
    }
}