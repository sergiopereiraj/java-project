// Call the dataTables jQuery plugin
$(document).ready(function() {

  cargarUsuarios()
  $('#usuarios').DataTable();
  actualizarEmailDelUsuario();
});

function actualizarEmailDelUsuario(){
    document.getElementById('txt-email-usuario').outerHTML = localStorage.email;
}

async function cargarUsuarios() {

    const request = await fetch('api/usuarios', {
      method: 'GET',
      headers: getHeaders()
    });
    const usuarios = await request.json();

    let listadoHtml = '';
    for (let usuario of usuarios){

        let botonEliminar = '<a href="#" onclick="eliminarUsuario('+usuario.id +')" class="btn btn-danger btn-circle">\n' +
            '                                                    <i class="fas fa-trash"></i>\n' +
            '                                                </a>';
        let telefonoTexto = usuario.telefono == null ? "-" : usuario.telefono;
        let usuarioHtml = ' <tr>\n' +
          '                                            <td>'+ usuario.id +'</td>\n' +
          '                                            <td>'+usuario.nombre+' '+usuario.apellido+'</td>\n' +
          '                                            <td>'+usuario.email+'</td>\n' +
          '                                            <td>'+telefonoTexto+'</td>\n' +
          '                                            <td>'+ botonEliminar +'</td></tr>';
      listadoHtml += usuarioHtml
    }

document.querySelector('#usuarios tbody').outerHTML = listadoHtml;
}

function getHeaders(){
    return {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': localStorage.token
    };

}

async function eliminarUsuario(id) {
    if(!confirm('Do you want delete this user?')){
        return;
    }
    const request = await fetch('api/usuarios/' + id, {
        method: 'DELETE',
        headers: getHeaders()
    });
    location.reload();
}