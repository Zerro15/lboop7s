(function () {
    const arrayName = document.getElementById('arrayName');
    const arrayFactory = document.getElementById('arrayFactory');
    const pointsInput = document.getElementById('pointsInput');
    const submitArrays = document.getElementById('submitArrays');
    const clearArrays = document.getElementById('clearArrays');
    const arraySuccess = document.getElementById('arraySuccess');

    const mathName = document.getElementById('mathName');
    const mathKey = document.getElementById('mathKey');
    const pointsCount = document.getElementById('pointsCount');
    const leftBound = document.getElementById('leftBound');
    const rightBound = document.getElementById('rightBound');
    const mathFactory = document.getElementById('mathFactory');
    const submitMath = document.getElementById('submitMath');
    const clearMath = document.getElementById('clearMath');
    const mathSuccess = document.getElementById('mathSuccess');

    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const userId = document.getElementById('userId');

    const errorModal = document.getElementById('errorModal');
    const errorMessage = document.getElementById('errorMessage');
    const closeError = document.getElementById('closeError');

    function showError(message) {
        errorMessage.textContent = message || 'Неизвестная ошибка.';
        errorModal.style.display = 'flex';
    }

    function hideError() {
        errorModal.style.display = 'none';
    }

    closeError.addEventListener('click', hideError);
    errorModal.addEventListener('click', function (event) {
        if (event.target === errorModal) {
            hideError();
        }
    });

    function getAuthHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        if (username.value && password.value) {
            const token = btoa(`${username.value}:${password.value}`);
            headers['Authorization'] = `Basic ${token}`;
        }
        return headers;
    }

    function parsePoints(text) {
        if (!text.trim()) {
            throw new Error('Укажите хотя бы одну точку вида x,y на строку.');
        }
        return text
            .split(/\n+/)
            .map(line => line.trim())
            .filter(Boolean)
            .map(line => {
                const [xRaw, yRaw] = line.split(',');
                if (xRaw === undefined || yRaw === undefined) {
                    throw new Error(`Строка "${line}" должна содержать x и y, разделённые запятой.`);
                }
                const x = parseFloat(xRaw);
                const y = parseFloat(yRaw);
                if (Number.isNaN(x) || Number.isNaN(y)) {
                    throw new Error(`Строка "${line}" содержит некорректные числа.`);
                }
                return { x, y };
            });
    }

    async function request(url, payload, successHandler) {
        const ownerId = Number.parseInt(userId.value, 10);
        if (!ownerId) {
            showError('Укажите ID пользователя, для которого создаётся функция.');
            return;
        }

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify({ ...payload, userId: ownerId })
            });

            if (!response.ok) {
                let message = 'Не удалось выполнить запрос.';
                try {
                    const errorBody = await response.json();
                    message = errorBody.message || message;
                } catch (parseError) {
                    const text = await response.text();
                    if (text) {
                        message = text;
                    }
                }
                showError(message);
                return;
            }

            const data = await response.json();
            successHandler(data);
        } catch (networkError) {
            showError(`Ошибка отправки запроса: ${networkError.message}`);
        }
    }

    submitArrays.addEventListener('click', function () {
        arraySuccess.textContent = '';
        try {
            const points = parsePoints(pointsInput.value);
            const payload = {
                name: arrayName.value || 'Tabulated from arrays',
                points,
                factoryType: arrayFactory.value
            };
            request('/api/v1/functions/create-from-arrays', payload, function (data) {
                arraySuccess.textContent = `Функция создана (ID: ${data.id}).`;
            });
        } catch (error) {
            showError(error.message);
        }
    });

    clearArrays.addEventListener('click', function () {
        arrayName.value = '';
        pointsInput.value = '';
        arrayFactory.value = 'ARRAY';
        arraySuccess.textContent = '';
    });

    submitMath.addEventListener('click', function () {
        mathSuccess.textContent = '';
        const left = parseFloat(leftBound.value);
        const right = parseFloat(rightBound.value);
        const count = Number.parseInt(pointsCount.value, 10);

        if (!Number.isFinite(left) || !Number.isFinite(right)) {
            showError('Границы должны быть числовыми.');
            return;
        }
        if (!Number.isInteger(count) || count < 2) {
            showError('Количество точек должно быть целым числом не меньше 2.');
            return;
        }
        if (left >= right) {
            showError('Левая граница должна быть меньше правой.');
            return;
        }

        const payload = {
            name: mathName.value || 'Tabulated from math function',
            mathFunctionKey: mathKey.value,
            pointsCount: count,
            leftBound: left,
            rightBound: right,
            factoryType: mathFactory.value
        };

        request('/api/v1/functions/create-from-math', payload, function (data) {
            mathSuccess.textContent = `Функция создана (ID: ${data.id}).`;
        });
    });

    clearMath.addEventListener('click', function () {
        mathName.value = '';
        mathKey.value = 'sqr';
        pointsCount.value = '10';
        leftBound.value = '0';
        rightBound.value = '6.28';
        mathFactory.value = 'ARRAY';
        mathSuccess.textContent = '';
    });
})();
