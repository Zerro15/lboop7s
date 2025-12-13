import { useEffect, useMemo, useState } from 'react';
import { createFromMathFunction, fetchMathFunctions } from '../services/apiClient';
import { errorHandler } from '../services/errorHandler';
import './Forms.css';

interface FormState {
  functionName: string;
  pointsCount: number;
  start: number;
  end: number;
}

export function CreateFromMathFunctionForm() {
  const [functions, setFunctions] = useState<string[]>([]);
  const [state, setState] = useState<FormState>({
    functionName: '',
    pointsCount: 5,
    start: 0,
    end: 5
  });
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    fetchMathFunctions().then((list) => setFunctions(list.sort((a, b) => a.localeCompare(b))));
  }, []);

  const isValidInterval = useMemo(() => state.start < state.end, [state]);

  const onChange = (field: keyof FormState, value: string) => {
    setState((prev) => ({ ...prev, [field]: field === 'functionName' ? value : Number(value) }));
    setSuccessMessage(null);
  };

  const validate = (): string | null => {
    if (!state.functionName) return 'Выберите функцию';
    if (!Number.isFinite(state.pointsCount) || state.pointsCount < 2) return 'Минимум две точки';
    if (!Number.isFinite(state.start) || !Number.isFinite(state.end)) return 'Границы интервала должны быть числами';
    if (state.start >= state.end) return 'Начало интервала должно быть меньше конца';
    return null;
  };

  const onSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const error = validate();
    if (error) {
      errorHandler.publish(error);
      return;
    }
    try {
      await createFromMathFunction(state);
      setSuccessMessage('Функция создана на основе выбранной формулы');
    } catch (e) {
      setSuccessMessage(null);
    }
  };

  return (
    <section className="card">
      <header>
        <h2>Создание из математической функции</h2>
        <p className="subtitle">Выберите функцию и задайте интервал табулирования.</p>
      </header>

      <form onSubmit={onSubmit} className="table-form">
        <label>
          Выберите функцию
          <select value={state.functionName} onChange={(e) => onChange('functionName', e.target.value)}>
            <option value="" disabled>
              -- выберите функцию --
            </option>
            {functions.map((fn) => (
              <option key={fn} value={fn}>
                {fn}
              </option>
            ))}
          </select>
        </label>

        <div className="grid-row" style={{ marginTop: '12px' }}>
          <label>
            Количество точек
            <input
              type="number"
              min={2}
              value={state.pointsCount}
              onChange={(e) => onChange('pointsCount', e.target.value)}
            />
          </label>
          <label>
            Начало интервала
            <input type="number" value={state.start} onChange={(e) => onChange('start', e.target.value)} />
          </label>
          <label>
            Конец интервала
            <input type="number" value={state.end} onChange={(e) => onChange('end', e.target.value)} />
          </label>
        </div>
        {!isValidInterval && <div className="warning">Начало интервала должно быть меньше конца.</div>}

        <div className="actions">
          <button type="submit" className="primary">Создать функцию</button>
          {successMessage && <span className="success">{successMessage}</span>}
        </div>
      </form>
    </section>
  );
}
