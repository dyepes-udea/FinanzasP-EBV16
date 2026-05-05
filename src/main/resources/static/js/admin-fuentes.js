function showMessage(msg, isError) {
  const el = document.getElementById('message');
  el.textContent = msg;
  el.className = isError ? 'message error' : 'message success';
  el.classList.remove('hidden');
  setTimeout(() => el.classList.add('hidden'), 4000);
}

function escapeHtml(str = '') {
  return str.replace(/[&<>"'`]/g, s => ({
    '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;','`':'&#96;'
  })[s]);
}

async function fetchFuentes() {
  try {
    const res = await fetch('/api/fuentes-ingreso');
    if (!res.ok) throw new Error('No se pudo obtener fuentes de ingreso');
    const data = await res.json();
    renderFuentes(data);
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

function renderFuentes(items) {
  const body = document.getElementById('fuentesBody');
  body.innerHTML = '';
  if (items.length === 0) {
    body.innerHTML = '<tr><td colspan="4" class="empty">No hay fuentes de ingreso crear una</td></tr>';
    return;
  }
  items.forEach(fuente => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${fuente.id}</td>
      <td>${escapeHtml(fuente.nombre || '')}</td>
      <td>${escapeHtml(fuente.descripcion || '')}</td>
      <td class="actions">
        <button class="btn small" data-id="${fuente.id}" data-action="edit">Editar</button>
        <button class="btn small danger" data-id="${fuente.id}" data-action="delete">Eliminar</button>
      </td>
    `;
    body.appendChild(tr);
  });

  body.querySelectorAll('button').forEach(btn => {
    const action = btn.getAttribute('data-action');
    const id = btn.getAttribute('data-id');
    btn.addEventListener('click', async () => {
      const row = btn.closest('tr');
      const nombre = row.children[1].textContent;
      const descripcion = row.children[2].textContent;
      const fuente = { id: parseInt(id,10), nombre, descripcion };
      if (action === 'edit') return editFuente(fuente);
      if (action === 'delete') return deleteFuente(fuente);
    });
  });
}

async function editFuente(fuente) {
  try {
    const nombre = prompt('Nombre:', fuente.nombre);
    if (nombre === null) return;
    const descripcion = prompt('Descripción:', fuente.descripcion || '');
    if (descripcion === null) return;

    const body = {};
    if (nombre !== fuente.nombre) body.nombre = nombre;
    if (descripcion !== fuente.descripcion) body.descripcion = descripcion;

    if (Object.keys(body).length === 0) {
      showMessage('No hubo cambios');
      return;
    }

    const res = await fetch(`/api/fuentes-ingreso/${fuente.id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    if (res.ok) {
      showMessage('Fuente de ingreso actualizada');
      fetchFuentes();
    } else {
      const err = await res.json().catch(() => ({}));
      showMessage(err.detalles || err.mensaje || 'Error al actualizar', true);
    }
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

async function deleteFuente(fuente) {
  try {
    const refRes = await fetch(`/api/fuentes-ingreso/${fuente.id}/referencias`);
    if (!refRes.ok) throw new Error('No se pudo obtener información de referencias');
    const info = await refRes.json();

    if (info.puedeEliminar) {
      if (!confirm(`Eliminar fuente "${fuente.nombre}"?`)) return;
      const res = await fetch(`/api/fuentes-ingreso/${fuente.id}`, { method: 'DELETE' });
      if (res.status === 204) { showMessage('Fuente de ingreso eliminada'); fetchFuentes(); }
      else { showMessage('Error eliminando fuente', true); }
      return;
    }

    const total = (info.ingresosVinculados || 0);
    const opt = prompt(`La fuente tiene ${total} ingresos vinculados. Escriba 'borrar' para eliminar ingresos y la fuente, o cancelar:`);
    if (!opt) return;
    if (opt.toLowerCase() === 'borrar') {
      if (!confirm('Esto eliminará los ingresos vinculados. Confirmar?')) return;
      const res = await fetch(`/api/fuentes-ingreso/${fuente.id}/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ confirmar: true, eliminarTransacciones: true })
      });
      if (res.status === 204) { showMessage('Ingresos y fuente eliminados'); fetchFuentes(); }
      else { const err = await res.json().catch(()=>({})); showMessage(err.detalles||err.mensaje||'Error', true); }
      return;
    }

  } catch (err) {
    showMessage(err.message || err, true);
  }
}

async function createFuente(event) {
  event.preventDefault();
  try {
    const nombre = document.getElementById('newNombre').value.trim();
    const descripcion = document.getElementById('newDescripcion').value.trim();
    if (!nombre) { showMessage('El nombre es obligatorio', true); return; }

    const res = await fetch('/api/fuentes-ingreso', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nombre, descripcion })
    });

    if (res.status === 201) {
      showMessage('Fuente de ingreso creada');
      document.getElementById('createForm').reset();
      fetchFuentes();
    } else {
      const err = await res.json().catch(()=>({}));
      showMessage(err.detalles||err.mensaje||'Error creando fuente', true);
    }
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('createForm').addEventListener('submit', createFuente);
  fetchFuentes();
});
