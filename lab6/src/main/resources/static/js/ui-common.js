const uiState = {
    authHeader: localStorage.getItem('authHeader') || null,
    factoryType: localStorage.getItem('factoryType') || 'array',
};

const toast = (() => {
    let node;
    const ensure = () => {
        if (!node) {
            node = document.getElementById('toast');
        }
        return node;
    };
    return (text) => {
        const ref = ensure();
        if (!ref) return;
        ref.textContent = text;
        ref.style.display = 'block';
        setTimeout(() => ref.style.display = 'none', 2600);
    };
})();

const errorModal = (() => {
    let modal, title, message, details;
    const ensure = () => {
        modal = modal || document.getElementById('errorModal');
        title = title || document.getElementById('errorTitle');
        message = message || document.getElementById('errorMessage');
        details = details || document.getElementById('errorDetails');
    };
    return {
        show: (heading, text, extra = '') => {
            ensure();
            if (!modal) return alert(text);
            title.textContent = heading;
            message.textContent = text;
            details.textContent = extra;
            if (!modal.open) modal.showModal();
        },
        close: () => {
            ensure();
            if (modal && modal.open) modal.close();
        }
    };
})();

const authHeaders = () => {
    const headers = { 'Content-Type': 'application/json' };
    if (uiState.authHeader) headers['Authorization'] = uiState.authHeader;
    return headers;
};

const fetchSafe = async (url, options = {}) => {
    const config = { ...options, headers: { ...(options.headers || {}), ...authHeaders() } };
    const resp = await fetch(url, config);
    if (!resp.ok) {
        let payload;
        try { payload = await resp.json(); } catch (_) { payload = {}; }
        const message = payload.message || `HTTP ${resp.status}`;
        throw new Error(message);
    }
    return resp.json();
};

const safeNumber = (value) => {
    const num = Number(value);
    return Number.isFinite(num) ? num : null;
};

const persistAuth = (login, password) => {
    uiState.authHeader = 'Basic ' + btoa(`${login}:${password}`);
    localStorage.setItem('authHeader', uiState.authHeader);
};

const updateFactoryType = (type) => {
    uiState.factoryType = type;
    localStorage.setItem('factoryType', type);
};

const factoryPills = (containerId, onChange) => {
    const container = document.getElementById(containerId);
    const render = (active) => {
        container.innerHTML = '';
        ['array', 'linked_list'].forEach(type => {
            const pill = document.createElement('button');
            pill.type = 'button';
            pill.className = `pill ${type === active ? 'active' : ''}`;
            pill.textContent = type === 'array' ? 'Массив' : 'Связный список';
            pill.onclick = () => {
                updateFactoryType(type);
                render(type);
                if (onChange) onChange(type);
                toast(`Фабрика: ${type === 'array' ? 'массив' : 'связный список'}`);
            };
            container.appendChild(pill);
        });
    };
    render(uiState.factoryType);
    return { render };
};

let mathFunctionRegistry = [];

const loadMathRegistry = async () => {
    if (mathFunctionRegistry.length > 0) {
        return mathFunctionRegistry;
    }
    const list = await fetchSafe('/api/v1/math-functions');
    mathFunctionRegistry = list
        .map(item => ({ key: item.key, label: item.label, priority: item.priority }))
        .sort((a, b) => a.priority === b.priority
            ? a.label.localeCompare(b.label)
            : a.priority - b.priority);
    return mathFunctionRegistry;
};

const previewMathPoints = async ({ key, pointsCount, leftBound, rightBound }) => {
    if (!(Number.isFinite(pointsCount) && pointsCount >= 2)) {
        throw new Error('Минимум две точки');
    }
    if (!(Number.isFinite(leftBound) && Number.isFinite(rightBound))) {
        throw new Error('Границы не заданы');
    }
    if (leftBound >= rightBound) {
        throw new Error('Левая граница должна быть меньше правой');
    }
    const payload = await fetchSafe('/api/v1/math-functions/preview', {
        method: 'POST',
        body: JSON.stringify({
            mathFunctionKey: key,
            pointsCount,
            leftBound,
            rightBound
        })
    });
    if (!payload || !Array.isArray(payload.points)) {
        throw new Error('Сервис не вернул точки для предпросмотра');
    }
    return payload.points.map(p => ({ x: p.x, y: p.y }));
};

const downloadJson = (filename, data) => {
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
};

const readJsonFile = (file) => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
        try { resolve(JSON.parse(reader.result)); }
        catch (e) { reject(e); }
    };
    reader.onerror = reject;
    reader.readAsText(file);
});
