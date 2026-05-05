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

async function getBackendErrorMessage(res, fallback) {
  const text = await res.text().catch(() => '');
  if (!text) return fallback;

  try {
    const err = JSON.parse(text);
    return err.detalles || err.mensaje || text;
  } catch (e) {
    return text;
  }
}

function showConfirmModal({ title, message, itemName, confirmText = 'Confirmar', cancelText = 'Cancelar' }) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px;';

    const modal = document.createElement('div');
    modal.style.cssText = 'width:100%;max-width:420px;background:#fff;border-radius:10px;box-shadow:0 18px 45px rgba(15,23,42,.28);padding:24px;color:#2c3e50;';

    const heading = document.createElement('h3');
    heading.textContent = title;
    heading.style.cssText = 'margin:0 0 10px;font-size:1.25rem;color:#2c3e50;';

    const text = document.createElement('p');
    text.textContent = message;
    text.style.cssText = 'margin:0 0 12px;color:#5f6f7f;line-height:1.4;';

    const name = document.createElement('div');
    name.textContent = itemName;
    name.style.cssText = 'margin:0 0 20px;padding:12px;background:#f4f7fa;border:1px solid #dbe4ee;border-radius:6px;font-weight:700;';

    const actions = document.createElement('div');
    actions.style.cssText = 'display:flex;gap:10px;justify-content:flex-end;';

    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.textContent = cancelText;
    cancelButton.style.cssText = 'padding:10px 14px;border:1px solid #bdc3c7;background:#fff;color:#2c3e50;border-radius:4px;cursor:pointer;font-weight:600;';

    const confirmButton = document.createElement('button');
    confirmButton.type = 'button';
    confirmButton.textContent = confirmText;
    confirmButton.style.cssText = 'padding:10px 14px;border:1px solid #c0392b;background:#e74c3c;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;';

    const close = result => {
      overlay.remove();
      resolve(result);
    };

    cancelButton.addEventListener('click', () => close(false));
    confirmButton.addEventListener('click', () => close(true));
    overlay.addEventListener('click', event => {
      if (event.target === overlay) close(false);
    });

    actions.append(cancelButton, confirmButton);
    modal.append(heading, text, name, actions);
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    cancelButton.focus();
  });
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
  if (items.length === 0) {
    body.innerHTML = '<tr><td colspan="5" class="empty">No hay categorías de gasto, crea una</td></tr>';
    return;
  }
  items.forEach((cat, index) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${index + 1}</td>
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
      const confirmado = await showConfirmModal({
        title: 'Eliminar categoría',
        message: 'Se eliminará la categoría:',
        itemName: cat.nombre,
        confirmText: 'Eliminar'
      });
      if (!confirmado) return;
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
      const confirmado = await showConfirmModal({
        title: 'Eliminar transacciones vinculadas',
        message: 'Esto eliminará las transacciones vinculadas a:',
        itemName: cat.nombre,
        confirmText: 'Eliminar transacciones'
      });
      if (!confirmado) return;
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
    const tipo = 'GASTO';
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
      const err = { detalles: await getBackendErrorMessage(res, 'Error creando categoria') };
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
