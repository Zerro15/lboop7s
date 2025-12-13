import { useMemo, useState } from 'react';
import { createFromArrays } from '../services/apiClient';
import { errorHandler } from '../services/errorHandler';
import { PointDto } from '../types';
import './Forms.css';

const MAX_POINTS_WITHOUT_CONFIRMATION = 200;

export function CreateFromArraysForm() {
  const [pointsCount, setPointsCount] = useState<number>(3);
  const [grid, setGrid] = useState<PointDto[]>([{ x: 0, y: 0 }, { x: 1, y: 1 }, { x: 2, y: 4 }]);
  const [lastCreatedCount, setLastCreatedCount] = useState<number>(3);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const tooManyRows = useMemo(() => pointsCount > MAX_POINTS_WITHOUT_CONFIRMATION, [pointsCount]);

  const regenerateGrid = () => {
    if (grid.length && pointsCount !== lastCreatedCount) {
      const proceed = window.confirm('Текущая таблица будет перезаписана. Продолжить?');
      if (!proceed) return;
    }

    if (tooManyRows) {
      const proceed = window.confirm(`Будет создано ${pointsCount} строк. Продолжить?`);
      if (!proceed) return;
    }

    const fresh: PointDto[] = Array.from({ length: pointsCount }, (_, idx) => ({ x: idx, y: 0 }));
    setGrid(fresh);
    setLastCreatedCount(pointsCount);
    setSuccessMessage(null);
  };

  const updateCell = (idx: number, field: keyof PointDto, value: string) => {
    const numeric = Number(value);
    const updated = grid.map((row, i) => (i === idx ? { ...row, [field]: numeric } : row));
    setGrid(updated);
  };

  const validate = (): string | null => {
    if (!grid.length) return 'Добавьте хотя бы одну точку';
    const seen = new Set<number>();
    for (const row of grid) {
      if (!Number.isFinite(row.x) || !Number.isFinite(row.y)) {
        return 'Все X и Y должны быть числами';
      }
      if (seen.has(row.x)) {
        return 'Значения X должны быть уникальными';
      }
      seen.add(row.x);
    }
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
      await createFromArrays({ points: grid });
      setSuccessMessage('Функция создана из таблицы');
    } catch (e) {
      setSuccessMessage(null);
    }
  };

  return (
    <section className="card">
      <header>
        <div>
          <h2>Создание из массивов X и Y</h2>
          <p className="subtitle">Введите количество точек и заполните таблицу значений.</p>
        </div>
        <div className="count-row">
          <label>
            Количество точек
            <input
              type="number"
              min={1}
              value={pointsCount}
              onChange={(e) => setPointsCount(Math.max(1, Number(e.target.value)))}
            />
          </label>
          <button type="button" onClick={regenerateGrid} className="secondary">
            Создать таблицу
          </button>
        </div>
        {tooManyRows && <div className="warning">Большое количество строк может замедлить работу. Таблица создается только после подтверждения.</div>}
      </header>

      <form onSubmit={onSubmit} className="table-form">
        <div className="grid-header">
          <span>X</span>
          <span>Y</span>
        </div>
        <div className="grid-body" style={{ maxHeight: '320px', overflow: 'auto' }}>
          {grid.map((row, index) => (
            <div className="grid-row" key={index}>
              <input
                type="number"
                value={row.x}
                onChange={(e) => updateCell(index, 'x', e.target.value)}
              />
              <input
                type="number"
                value={row.y}
                onChange={(e) => updateCell(index, 'y', e.target.value)}
              />
            </div>
          ))}
        </div>
        <div className="actions">
          <button type="submit" className="primary">Создать функцию</button>
          {successMessage && <span className="success">{successMessage}</span>}
        </div>
      </form>
    </section>
  );
}
