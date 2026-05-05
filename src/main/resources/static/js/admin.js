const API = '/api';

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

function showInfoModal({ title, fields, closeText = 'Cerrar' }) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px;';

    const modal = document.createElement('div');
    modal.style.cssText = 'width:100%;max-width:420px;background:#fff;border-radius:10px;box-shadow:0 18px 45px rgba(15,23,42,.28);padding:24px;color:#2c3e50;';

    const heading = document.createElement('h3');
    heading.textContent = title;
    heading.style.cssText = 'margin:0 0 16px;font-size:1.25rem;color:#2c3e50;';

    const list = document.createElement('div');
    list.style.cssText = 'display:grid;gap:10px;margin-bottom:20px;';
    fields.forEach(field => {
      const row = document.createElement('div');
      row.style.cssText = 'padding:10px 12px;background:#f4f7fa;border:1px solid #dbe4ee;border-radius:6px;';
      const label = document.createElement('strong');
      label.textContent = `${field.label}: `;
      const value = document.createElement('span');
      value.textContent = field.value || '';
      row.append(label, value);
      list.appendChild(row);
    });

    const actions = document.createElement('div');
    actions.style.cssText = 'display:flex;justify-content:flex-end;';

    const closeButton = document.createElement('button');
    closeButton.type = 'button';
    closeButton.textContent = closeText;
    closeButton.style.cssText = 'padding:10px 14px;border:1px solid #4a90e2;background:#4a90e2;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;';

    const close = () => {
      overlay.remove();
      resolve();
    };

    closeButton.addEventListener('click', close);
    overlay.addEventListener('click', event => {
      if (event.target === overlay) close();
    });

    actions.appendChild(closeButton);
    modal.append(heading, list, actions);
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    closeButton.focus();
  });
}

function showFormModal({ title, values, submitText = 'Guardar', cancelText = 'Cancelar' }) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px;';

    const modal = document.createElement('form');
    modal.style.cssText = 'width:100%;max-width:460px;background:#fff;border-radius:10px;box-shadow:0 18px 45px rgba(15,23,42,.28);padding:24px;color:#2c3e50;';

    const heading = document.createElement('h3');
    heading.textContent = title;
    heading.style.cssText = 'margin:0 0 18px;font-size:1.25rem;color:#2c3e50;';

    const nombreLabel = document.createElement('label');
    nombreLabel.textContent = 'Nombre';
    nombreLabel.style.cssText = 'display:block;margin-bottom:6px;font-weight:700;';
    const nombreInput = document.createElement('input');
    nombreInput.type = 'text';
    nombreInput.required = true;
    nombreInput.value = values.nombre || '';
    nombreInput.style.cssText = 'width:100%;padding:12px;border:1px solid #bdc3c7;border-radius:4px;font:inherit;margin-bottom:14px;';

    const descripcionLabel = document.createElement('label');
    descripcionLabel.textContent = 'Descripcion';
    descripcionLabel.style.cssText = 'display:block;margin-bottom:6px;font-weight:700;';
    const descripcionInput = document.createElement('input');
    descripcionInput.type = 'text';
    descripcionInput.value = values.descripcion || '';
    descripcionInput.style.cssText = 'width:100%;padding:12px;border:1px solid #bdc3c7;border-radius:4px;font:inherit;margin-bottom:20px;';

    const actions = document.createElement('div');
    actions.style.cssText = 'display:flex;gap:10px;justify-content:flex-end;';

    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.textContent = cancelText;
    cancelButton.style.cssText = 'padding:10px 14px;border:1px solid #bdc3c7;background:#fff;color:#2c3e50;border-radius:4px;cursor:pointer;font-weight:600;';

    const submitButton = document.createElement('button');
    submitButton.type = 'submit';
    submitButton.textContent = submitText;
    submitButton.style.cssText = 'padding:10px 14px;border:1px solid #4a90e2;background:#4a90e2;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;';

    const close = result => {
      overlay.remove();
      resolve(result);
    };

    cancelButton.addEventListener('click', () => close(null));
    overlay.addEventListener('click', event => {
      if (event.target === overlay) close(null);
    });
    modal.addEventListener('submit', event => {
      event.preventDefault();
      const nombre = nombreInput.value.trim();
      if (!nombre) {
        nombreInput.focus();
        return;
      }
      close({
        nombre,
        descripcion: descripcionInput.value.trim()
      });
    });

    actions.append(cancelButton, submitButton);
    modal.append(heading, nombreLabel, nombreInput, descripcionLabel, descripcionInput, actions);
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    nombreInput.focus();
  });
}

function showDeleteOptionsModal({ title, message, options, cancelText = 'Cancelar' }) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px;';

    const modal = document.createElement('div');
    modal.style.cssText = 'width:100%;max-width:460px;background:#fff;border-radius:10px;box-shadow:0 18px 45px rgba(15,23,42,.28);padding:24px;color:#2c3e50;';

    const heading = document.createElement('h3');
    heading.textContent = title;
    heading.style.cssText = 'margin:0 0 10px;font-size:1.25rem;color:#2c3e50;';

    const text = document.createElement('p');
    text.textContent = message;
    text.style.cssText = 'margin:0 0 18px;color:#5f6f7f;line-height:1.4;';

    const actions = document.createElement('div');
    actions.style.cssText = 'display:flex;gap:10px;justify-content:flex-end;flex-wrap:wrap;';

    const close = result => {
      overlay.remove();
      resolve(result);
    };

    options.forEach(option => {
      const button = document.createElement('button');
      button.type = 'button';
      button.textContent = option.label;
      button.style.cssText = option.danger
        ? 'padding:10px 14px;border:1px solid #c0392b;background:#e74c3c;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;'
        : 'padding:10px 14px;border:1px solid #4a90e2;background:#4a90e2;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;';
      button.addEventListener('click', () => close(option.value));
      actions.appendChild(button);
    });

    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.textContent = cancelText;
    cancelButton.style.cssText = 'padding:10px 14px;border:1px solid #bdc3c7;background:#fff;color:#2c3e50;border-radius:4px;cursor:pointer;font-weight:600;';
    cancelButton.addEventListener('click', () => close(null));

    overlay.addEventListener('click', event => {
      if (event.target === overlay) close(null);
    });

    actions.appendChild(cancelButton);
    modal.append(heading, text, actions);
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    cancelButton.focus();
  });
}

function showInputModal({ title, label, initialValue = '', submitText = 'Aceptar', cancelText = 'Cancelar' }) {
  return new Promise(resolve => {
    const overlay = document.createElement('div');
    overlay.style.cssText = 'position:fixed;inset:0;background:rgba(15,23,42,.42);display:flex;align-items:center;justify-content:center;z-index:9999;padding:20px;';

    const modal = document.createElement('form');
    modal.style.cssText = 'width:100%;max-width:420px;background:#fff;border-radius:10px;box-shadow:0 18px 45px rgba(15,23,42,.28);padding:24px;color:#2c3e50;';

    const heading = document.createElement('h3');
    heading.textContent = title;
    heading.style.cssText = 'margin:0 0 16px;font-size:1.25rem;color:#2c3e50;';

    const inputLabel = document.createElement('label');
    inputLabel.textContent = label;
    inputLabel.style.cssText = 'display:block;margin-bottom:6px;font-weight:700;';
    const input = document.createElement('input');
    input.type = 'number';
    input.required = true;
    input.min = '1';
    input.value = initialValue;
    input.style.cssText = 'width:100%;padding:12px;border:1px solid #bdc3c7;border-radius:4px;font:inherit;margin-bottom:20px;';

    const actions = document.createElement('div');
    actions.style.cssText = 'display:flex;gap:10px;justify-content:flex-end;';

    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.textContent = cancelText;
    cancelButton.style.cssText = 'padding:10px 14px;border:1px solid #bdc3c7;background:#fff;color:#2c3e50;border-radius:4px;cursor:pointer;font-weight:600;';

    const submitButton = document.createElement('button');
    submitButton.type = 'submit';
    submitButton.textContent = submitText;
    submitButton.style.cssText = 'padding:10px 14px;border:1px solid #4a90e2;background:#4a90e2;color:#fff;border-radius:4px;cursor:pointer;font-weight:700;';

    const close = result => {
      overlay.remove();
      resolve(result);
    };

    cancelButton.addEventListener('click', () => close(null));
    overlay.addEventListener('click', event => {
      if (event.target === overlay) close(null);
    });
    modal.addEventListener('submit', event => {
      event.preventDefault();
      close(input.value.trim());
    });

    actions.append(cancelButton, submitButton);
    modal.append(heading, inputLabel, input, actions);
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    input.focus();
  });
}

async function fetchCategories() {
  try {
    const res = await fetch(`${API}/categorias`);
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
  showInfoModal({
    title: 'Detalle de categoria',
    fields: [
      { label: 'ID', value: String(cat.id) },
      { label: 'Nombre', value: cat.nombre },
      { label: 'Descripcion', value: cat.descripcion },
      { label: 'Tipo', value: 'GASTO' }
    ]
  });
}

async function editCategory(cat) {
  try {
    const values = await showFormModal({
      title: 'Editar categoria',
      values: {
        nombre: cat.nombre,
        descripcion: cat.descripcion
      }
    });
    if (!values) return;

    const body = {};
    if (values.nombre !== cat.nombre) body.nombre = values.nombre;
    if (values.descripcion !== cat.descripcion) body.descripcion = values.descripcion;
    if (cat.tipo !== 'GASTO') body.tipo = 'GASTO';

    if (Object.keys(body).length === 0) {
      showMessage('No hubo cambios');
      return;
    }

    const res = await fetch(`${API}/categorias/${cat.id}`, {
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
    const refRes = await fetch(`${API}/categorias/${cat.id}/referencias`);
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
      const res = await fetch(`${API}/categorias/${cat.id}`, { method: 'DELETE' });
      if (res.status === 204) { showMessage('Categoría eliminada'); fetchCategories(); }
      else { showMessage('Error eliminando categoría', true); }
      return;
    }

    // Si no puede eliminarse directamente, ofrecer opciones
    const total = (info.gastosVinculados || 0) + (info.ingresosVinculados || 0);
    const opt = await showDeleteOptionsModal({
      title: 'Categoria con transacciones',
      message: `La categoria tiene ${total} transacciones vinculadas. Elige que quieres hacer.`,
      options: [
        { label: 'Reasignar', value: 'reasignar' },
        { label: 'Eliminar transacciones', value: 'borrar', danger: true }
      ]
    });
    if (!opt) return;
    if (opt === 'reasignar') {
      const target = await showInputModal({
        title: 'Reasignar transacciones',
        label: 'ID de categoria destino'
      });
      if (!target) return;
      const res = await fetch(`${API}/categorias/${cat.id}/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ confirmar: true, reasignarCategoriaId: parseInt(target,10) })
      });
      if (res.status === 204) { showMessage('Reasignadas y categoría eliminada'); fetchCategories(); }
      else { const err = await res.json().catch(()=>({})); showMessage(err.detalles||err.mensaje||'Error', true); }
      return;
    }
    if (opt === 'borrar') {
      const confirmado = await showConfirmModal({
        title: 'Eliminar transacciones vinculadas',
        message: 'Esto eliminará las transacciones vinculadas a:',
        itemName: cat.nombre,
        confirmText: 'Eliminar transacciones'
      });
      if (!confirmado) return;
      const res = await fetch(`${API}/categorias/${cat.id}/eliminar`, {
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

    const res = await fetch(`${API}/categorias`, {
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
