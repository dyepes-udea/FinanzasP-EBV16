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

async function fetchFuentes() {
  try {
    const res = await fetch(`${API}/fuentes-ingreso`);
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
    const values = await showFormModal({
      title: 'Editar fuente de ingreso',
      values: {
        nombre: fuente.nombre,
        descripcion: fuente.descripcion
      }
    });
    if (!values) return;

    const body = {};
    if (values.nombre !== fuente.nombre) body.nombre = values.nombre;
    if (values.descripcion !== fuente.descripcion) body.descripcion = values.descripcion;

    if (Object.keys(body).length === 0) {
      showMessage('No hubo cambios');
      return;
    }

    const res = await fetch(`${API}/fuentes-ingreso/${fuente.id}`, {
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
    const refRes = await fetch(`${API}/fuentes-ingreso/${fuente.id}/referencias`);
    if (!refRes.ok) throw new Error('No se pudo obtener información de referencias');
    const info = await refRes.json();

    if (info.puedeEliminar) {
      const confirmado = await showConfirmModal({
        title: 'Eliminar fuente de ingreso',
        message: 'Se eliminará la fuente de ingreso:',
        itemName: fuente.nombre,
        confirmText: 'Eliminar'
      });
      if (!confirmado) return;
      const res = await fetch(`${API}/fuentes-ingreso/${fuente.id}`, { method: 'DELETE' });
      if (res.status === 204) { showMessage('Fuente de ingreso eliminada'); fetchFuentes(); }
      else { showMessage('Error eliminando fuente', true); }
      return;
    }

    const total = (info.ingresosVinculados || 0);
    const opt = await showDeleteOptionsModal({
      title: 'Fuente con ingresos',
      message: `La fuente tiene ${total} ingresos vinculados. Elige que quieres hacer.`,
      options: [
        { label: 'Eliminar ingresos', value: 'borrar', danger: true }
      ]
    });
    if (!opt) return;
    if (opt === 'borrar') {
      const confirmado = await showConfirmModal({
        title: 'Eliminar ingresos vinculados',
        message: 'Esto eliminará los ingresos vinculados a:',
        itemName: fuente.nombre,
        confirmText: 'Eliminar ingresos'
      });
      if (!confirmado) return;
      const res = await fetch(`${API}/fuentes-ingreso/${fuente.id}/eliminar`, {
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

    const res = await fetch(`${API}/fuentes-ingreso`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nombre, descripcion })
    });

    if (res.status === 201) {
      showMessage('Fuente de ingreso creada');
      document.getElementById('createForm').reset();
      fetchFuentes();
    } else {
      const err = { detalles: await getBackendErrorMessage(res, 'Error creando fuente') };
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
