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

async function fetchCategories() {
  try {
    const res = await fetch('/api/categorias');
    if (!res.ok) throw new Error('No se pudo obtener categorías');
    const data = await res.json();
    renderCategories(data);
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

function renderCategories(items) {
  const body = document.getElementById('categoriesBody');
  body.innerHTML = '';
  items.forEach(cat => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${cat.id}</td>
      <td>${escapeHtml(cat.nombre || '')}</td>
      <td>${escapeHtml(cat.descripcion || '')}</td>
      <td>${escapeHtml(cat.tipo || '')}</td>
      <td class="actions">
        <button class="btn small" data-id="${cat.id}" data-action="view">Ver</button>
        <button class="btn small" data-id="${cat.id}" data-action="edit">Editar</button>
        <button class="btn small danger" data-id="${cat.id}" data-action="delete">Eliminar</button>
      </td>
    `;
    body.appendChild(tr);
  });

  // attach handlers
  body.querySelectorAll('button').forEach(btn => {
    const action = btn.getAttribute('data-action');
    const id = btn.getAttribute('data-id');
    btn.addEventListener('click', async () => {
      const row = btn.closest('tr');
      const nombre = row.children[1].textContent;
      const descripcion = row.children[2].textContent;
      const tipo = row.children[3].textContent;
      const cat = { id: parseInt(id,10), nombre, descripcion, tipo };
      if (action === 'view') return viewCategory(cat);
      if (action === 'edit') return editCategory(cat);
      if (action === 'delete') return deleteCategory(cat);
    });
  });
}

function viewCategory(cat) {
  alert(`ID: ${cat.id}\nNombre: ${cat.nombre}\nDescripción: ${cat.descripcion}\nTipo: ${cat.tipo}`);
}

async function editCategory(cat) {
  try {
    const nombre = prompt('Nombre:', cat.nombre);
    if (nombre === null) return;
    const descripcion = prompt('Descripción:', cat.descripcion || '');
    if (descripcion === null) return;
    const tipo = prompt('Tipo (GASTO/INGRESO):', cat.tipo || 'GASTO');
    if (tipo === null) return;

    const body = {};
    if (nombre !== cat.nombre) body.nombre = nombre;
    if (descripcion !== cat.descripcion) body.descripcion = descripcion;
    if (tipo !== cat.tipo) body.tipo = tipo;

    if (Object.keys(body).length === 0) {
      showMessage('No hubo cambios');
      return;
    }

    const res = await fetch(`/api/categorias/${cat.id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    if (res.ok) {
      showMessage('Categoría actualizada');
      fetchCategories();
    } else {
      const err = await res.json().catch(() => ({}));
      showMessage(err.detalles || err.mensaje || 'Error al actualizar', true);
    }
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

async function deleteCategory(cat) {
  try {
    const refRes = await fetch(`/api/categorias/${cat.id}/referencias`);
    if (!refRes.ok) throw new Error('No se pudo obtener información de referencias');
    const info = await refRes.json();

    if (info.puedeEliminar) {
      if (!confirm(`Eliminar categoría "${cat.nombre}"?`)) return;
      const res = await fetch(`/api/categorias/${cat.id}`, { method: 'DELETE' });
      if (res.status === 204) { showMessage('Categoría eliminada'); fetchCategories(); }
      else { showMessage('Error eliminando categoría', true); }
      return;
    }

    // Si no puede eliminarse directamente, ofrecer opciones
    const total = (info.gastosVinculados || 0) + (info.ingresosVinculados || 0);
    const opt = prompt(`La categoría tiene ${total} transacciones vinculadas. Escriba 'reasignar' para reasignar, 'borrar' para eliminar transacciones, o cancelar:`);
    if (!opt) return;
    if (opt.toLowerCase() === 'reasignar') {
      const target = prompt('ID de categoría destino para reasignar:');
      if (!target) return;
      const res = await fetch(`/api/categorias/${cat.id}/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ confirmar: true, reasignarCategoriaId: parseInt(target,10) })
      });
      if (res.status === 204) { showMessage('Reasignadas y categoría eliminada'); fetchCategories(); }
      else { const err = await res.json().catch(()=>({})); showMessage(err.detalles||err.mensaje||'Error', true); }
      return;
    }
    if (opt.toLowerCase() === 'borrar') {
      if (!confirm('Esto eliminará las transacciones vinculadas. Confirmar?')) return;
      const res = await fetch(`/api/categorias/${cat.id}/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ confirmar: true, eliminarTransacciones: true })
      });
      if (res.status === 204) { showMessage('Transacciones y categoría eliminadas'); fetchCategories(); }
      else { const err = await res.json().catch(()=>({})); showMessage(err.detalles||err.mensaje||'Error', true); }
      return;
    }

  } catch (err) {
    showMessage(err.message || err, true);
  }
}

async function createCategory(event) {
  event.preventDefault();
  try {
    const nombre = document.getElementById('newNombre').value.trim();
    const descripcion = document.getElementById('newDescripcion').value.trim();
    const tipo = document.getElementById('newTipo').value;
    if (!nombre) { showMessage('El nombre es obligatorio', true); return; }

    const res = await fetch('/api/categorias', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nombre, descripcion, tipo })
    });

    if (res.status === 201) {
      showMessage('Categoría creada');
      document.getElementById('createForm').reset();
      fetchCategories();
    } else {
      const err = await res.json().catch(()=>({}));
      showMessage(err.detalles||err.mensaje||'Error creando categoría', true);
    }
  } catch (err) {
    showMessage(err.message || err, true);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('createForm').addEventListener('submit', createCategory);
  fetchCategories();
});
