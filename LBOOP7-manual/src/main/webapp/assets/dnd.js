(() => {
    const nameInput = document.getElementById('charName');
    const backgroundInput = document.getElementById('charBackground');
    const classSelect = document.getElementById('charClass');
    const groupInput = document.getElementById('charGroup');
    const levelValue = document.getElementById('levelValue');
    const classDetails = document.getElementById('classDetails');
    const validationMessage = document.getElementById('validationMessage');
    const statusMessage = document.getElementById('statusMessage');
    const rollAllBtn = document.getElementById('rollAll');
    const saveBtn = document.getElementById('saveCharacter');
    const newBtn = document.getElementById('newCharacter');
    const statsGrid = document.getElementById('statsGrid');
    const freeRollBtn = document.getElementById('freeRoll');
    const freeRollValue = document.getElementById('freeRollValue');
    const assignSelect = document.getElementById('assignSelect');
    const assignBtn = document.getElementById('assignRoll');
    const hpValue = document.getElementById('hpValue');
    const damageIntValue = document.getElementById('damageIntValue');
    const damageChaValue = document.getElementById('damageChaValue');
    const rollDamageIntBtn = document.getElementById('rollDamageInt');
    const rollDamageChaBtn = document.getElementById('rollDamageCha');

    const STORAGE_KEY = 'grunge-sheet-character';

    const classMap = {
        leader: {
            name: 'Староста',
            mods: { charisma: 2, luck: 1 },
            blurb: 'Ответственный студент, организующий мероприятия и помогающий группе.',
        },
        nerd: {
            name: 'Умник',
            mods: { intelligence: 2, luck: -1, charisma: -1 },
            blurb: 'Отличник, знающий все предметы и живущий между парами и лабораторками.',
        },
        vibe: {
            name: 'Вайбкодер',
            mods: { dexterity: 2, luck: 2 },
            blurb: 'Расслабленный программист, пишущий код под хорошую музыку.',
        },
    };

    const statKeys = [
        { key: 'strength', label: 'Сила' },
        { key: 'dexterity', label: 'Ловкость' },
        { key: 'constitution', label: 'Выносливость' },
        { key: 'intelligence', label: 'Интеллект' },
        { key: 'charisma', label: 'Харизма' },
        { key: 'luck', label: 'Удача' },
    ];

    const state = {
        level: 1,
        stats: Object.fromEntries(statKeys.map(({ key }) => [key, { base: null, total: null }])),
        classId: '',
        freeRoll: null,
    };

    function updateClassCallout() {
        const classInfo = classMap[state.classId];
        if (!classInfo) {
            classDetails.textContent = 'Выберите класс, чтобы увидеть бонусы.';
            return;
        }
        const mods = classInfo.mods;
        const parts = Object.entries(mods).map(([k, v]) => {
            const sign = v > 0 ? '+' : '';
            const label = statKeys.find(s => s.key === k)?.label || k;
            return `${label}: ${sign}${v}`;
        });
        classDetails.textContent = `${classInfo.name}: ${classInfo.blurb}\nМодификаторы: ${parts.join(', ')}`;
    }

    function randomD20() {
        return Math.floor(Math.random() * 20) + 1;
    }

    function renderStats() {
        if (!statsGrid) return;
        statKeys.forEach(({ key }) => {
            const card = statsGrid.querySelector(`[data-stat="${key}"]`);
            if (!card) return;
            const baseEl = card.querySelector('.stat-base');
            const modEl = card.querySelector('.stat-mod');
            const totalEl = card.querySelector('.stat-total');
            const classInfo = classMap[state.classId];
            const modValue = classInfo?.mods[key] ?? 0;

            if (state.stats[key].base === null) {
                baseEl.textContent = '—';
                totalEl.textContent = '—';
            } else {
                baseEl.textContent = state.stats[key].base;
                const total = state.stats[key].base + modValue;
                state.stats[key].total = total;
                totalEl.textContent = total;
            }
            const sign = modValue > 0 ? '+' : '';
            modEl.textContent = `${sign}${modValue}`;
        });
        updateMetrics();
    }

    function rollStat(key) {
        state.stats[key].base = randomD20();
        renderStats();
        statusMessage.textContent = `Характеристика «${statKeys.find(s => s.key === key)?.label}» брошена.`;
    }

    function rollAllStats() {
        statKeys.forEach(({ key }) => rollStat(key));
        statusMessage.textContent = 'Все характеристики брошены d20.';
    }

    function rollFree() {
        state.freeRoll = randomD20();
        if (freeRollValue) freeRollValue.textContent = state.freeRoll;
        statusMessage.textContent = 'Свободный бросок готов — выберите характеристику и примените.';
    }

    function assignFreeRoll() {
        if (state.freeRoll === null) {
            statusMessage.textContent = 'Сначала выполните свободный бросок d20.';
            return;
        }
        const target = assignSelect?.value;
        if (!target) {
            statusMessage.textContent = 'Выберите характеристику для назначения броска.';
            return;
        }
        state.stats[target].base = state.freeRoll;
        renderStats();
        statusMessage.textContent = `Свободный бросок ${state.freeRoll} применён к «${statKeys.find(s => s.key === target)?.label}».`;
    }

    function calcTotals() {
        const classInfo = classMap[state.classId];
        return Object.fromEntries(statKeys.map(({ key }) => {
            const base = state.stats[key].base;
            const mod = classInfo?.mods[key] ?? 0;
            return [key, base === null ? null : base + mod];
        }));
    }

    function updateMetrics() {
        const totals = calcTotals();
        const strength = totals.strength;
        const constitution = totals.constitution;
        const intelligence = totals.intelligence;
        const charisma = totals.charisma;
        const luck = totals.luck;

        if (hpValue) {
            if (strength === null || constitution === null) {
                hpValue.textContent = '—';
            } else {
                hpValue.textContent = constitution * 2 + strength;
            }
        }

        if (damageIntValue) {
            damageIntValue.textContent = intelligence === null ? '—' : `${Math.floor(intelligence / 2)} + 1d6`;
        }
        if (damageChaValue) {
            if (charisma === null || luck === null) {
                damageChaValue.textContent = '—';
            } else {
                const base = Math.floor(charisma / 2) + Math.floor(luck / 4);
                damageChaValue.textContent = `${base} + 1d6`;
            }
        }
    }

    function rollDamage(type) {
        const totals = calcTotals();
        const d6 = Math.floor(Math.random() * 6) + 1;
        if (type === 'int') {
            if (totals.intelligence === null) {
                statusMessage.textContent = 'Сначала бросьте Интеллект.';
                return;
            }
            const base = Math.floor(totals.intelligence / 2);
            const total = base + d6;
            damageIntValue.textContent = `${base} + d6(${d6}) = ${total}`;
            statusMessage.textContent = 'Урон по формуле интеллекта рассчитан.';
        } else {
            if (totals.charisma === null || totals.luck === null) {
                statusMessage.textContent = 'Сначала бросьте Харизму и Удачу.';
                return;
            }
            const base = Math.floor(totals.charisma / 2) + Math.floor(totals.luck / 4);
            const total = base + d6;
            damageChaValue.textContent = `${base} + d6(${d6}) = ${total}`;
            statusMessage.textContent = 'Урон по формуле харизмы рассчитан.';
        }
    }

    function validateForm() {
        const missingFields = [];
        if (!nameInput.value.trim()) missingFields.push('Имя персонажа');
        if (!backgroundInput.value.trim()) missingFields.push('Предыстория');
        if (!classSelect.value) missingFields.push('Класс');
        if (!groupInput.value.trim()) missingFields.push('Номер группы');
        const unrolled = statKeys.filter(({ key }) => state.stats[key].base === null).map(s => s.label);

        if (missingFields.length || unrolled.length) {
            const messages = [];
            if (missingFields.length) messages.push(`Заполните: ${missingFields.join(', ')}`);
            if (unrolled.length) messages.push(`Бросьте характеристики: ${unrolled.join(', ')}`);
            validationMessage.textContent = messages.join(' • ');
            return false;
        }
        validationMessage.textContent = '';
        return true;
    }

    function saveCharacter() {
        if (!validateForm()) return;
        const payload = {
            name: nameInput.value.trim(),
            background: backgroundInput.value.trim(),
            classId: classSelect.value,
            group: groupInput.value.trim(),
            level: state.level,
            stats: state.stats,
        };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
        statusMessage.textContent = 'Персонаж сохранён в листе (LocalStorage).';
    }

    function loadCharacter() {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) return;
        try {
            const data = JSON.parse(raw);
            nameInput.value = data.name || '';
            backgroundInput.value = data.background || '';
            classSelect.value = data.classId || '';
            groupInput.value = data.group || '';
            state.level = data.level || 1;
            state.classId = data.classId || '';
            levelValue.textContent = state.level;
            if (data.stats) {
                state.stats = Object.fromEntries(statKeys.map(({ key }) => [
                    key,
                    { base: data.stats[key]?.base ?? null, total: data.stats[key]?.total ?? null },
                ]));
            }
            updateClassCallout();
            renderStats();
            statusMessage.textContent = 'Загружен сохранённый персонаж.';
        } catch {
            statusMessage.textContent = 'Не удалось прочитать сохранённый лист.';
        }
    }

    function resetCharacter() {
        nameInput.value = '';
        backgroundInput.value = '';
        classSelect.value = '';
        groupInput.value = '';
        state.classId = '';
        state.stats = Object.fromEntries(statKeys.map(({ key }) => [key, { base: null, total: null }]));
        state.freeRoll = null;
        if (freeRollValue) freeRollValue.textContent = '—';
        updateClassCallout();
        renderStats();
        validationMessage.textContent = '';
        statusMessage.textContent = 'Создан пустой лист. Все поля обязательны перед сохранением.';
    }

    function handleClassChange() {
        state.classId = classSelect.value;
        updateClassCallout();
        renderStats();
    }

    function bindEvents() {
        statsGrid.querySelectorAll('.mini-roll').forEach(btn => {
            btn.addEventListener('click', () => {
                const statKey = btn.dataset.roll;
                if (statKey) rollStat(statKey);
            });
        });
        rollAllBtn?.addEventListener('click', rollAllStats);
        saveBtn?.addEventListener('click', saveCharacter);
        newBtn?.addEventListener('click', resetCharacter);
        classSelect?.addEventListener('change', handleClassChange);
        [nameInput, backgroundInput, classSelect, groupInput].forEach(field => {
            field?.addEventListener('input', validateForm);
        });
        freeRollBtn?.addEventListener('click', rollFree);
        assignBtn?.addEventListener('click', assignFreeRoll);
        rollDamageIntBtn?.addEventListener('click', () => rollDamage('int'));
        rollDamageChaBtn?.addEventListener('click', () => rollDamage('cha'));
    }

    bindEvents();
    updateClassCallout();
    renderStats();
    loadCharacter();
})();
