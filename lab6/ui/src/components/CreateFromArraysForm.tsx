import React, { useMemo, useState } from 'react';
import { apiClient } from '../api/apiClient';
import { PointDto, UiFunctionResponse } from '../types';
import { showError } from '../services/errorHandler';

interface FormState {
  pointsCount: number;
  name: string;
  factoryType: string;
  userId: number;
}

export const CreateFromArraysForm: React.FC = () => {
  const [form, setForm] = useState<FormState>({
    pointsCount: 3,
    name: 'Функция из точек',
    factoryType: 'array',
    userId: 1
  });
  const [tablePoints, setTablePoints] = useState<PointDto[]>([
    { x: 0, y: 0 },
    { x: 1, y: 1 },
    { x: 2, y: 4 }
  ]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const rowsToRender = useMemo(() => {
    return tablePoints.map((point, index) => (
      <tr key={index}>
        <td>
          <input
            type="number"
            value={point.x}
            onChange={(e) => updatePoint(index, 'x', e.target.value)}
            required
          />
        </td>
        <td>
          <input
            type="number"
            value={point.y}
            onChange={(e) => updatePoint(index, 'y', e.target.value)}
            required
          />
        </td>
      </tr>
    ));
  }, [tablePoints]);

  const updatePoint = (index: number, key: keyof PointDto, value: string) => {
    setTablePoints((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [key]: value === '' ? Number.NaN : Number(value) } as PointDto;
      return next;
    });
  };

  const regenerateTable = () => {
    if (form.pointsCount > 500) {
      const confirmed = window.confirm(
        'Вы запросили создание очень большой таблицы. Продолжить? Это может потребовать времени.'
      );
      if (!confirmed) {
        return;
      }
    }

    if (tablePoints.some((p) => p.x !== undefined || p.y !== undefined)) {
      const confirmed = window.confirm('Текущие значения будут сброшены. Продолжить?');
      if (!confirmed) {
        return;
      }
    }

    const freshPoints: PointDto[] = Array.from({ length: form.pointsCount }, (_, idx) => ({
      x: idx,
      y: 0
    }));
    setTablePoints(freshPoints);
  };

  const validate = () => {
    if (tablePoints.length < 2) {
      showError('Нужно указать минимум две точки');
      return false;
    }
    const xs = new Set<number>();
    for (const point of tablePoints) {
      if (!Number.isFinite(point.x) || !Number.isFinite(point.y)) {
        showError('Все значения X и Y должны быть заполнены');
        return false;
      }
      if (xs.has(point.x)) {
        showError('Значения X не должны повторяться');
        return false;
      }
      xs.add(point.x);
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsSubmitting(true);
    try {
      const payload = {
        name: form.name,
        userId: form.userId,
        factoryType: form.factoryType,
        points: tablePoints
      };

      await apiClient.post<UiFunctionResponse>('/api/functions/create-from-arrays', payload);
      alert('Функция успешно создана');
    } catch (error) {
      // Ошибка уже показана через обработчик
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="card">
      <h2>Создание из массива точек</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Название функции</label>
          <input
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Количество точек</label>
          <input
            type="number"
            min={2}
            value={form.pointsCount}
            onChange={(e) => setForm({ ...form, pointsCount: Number(e.target.value) })}
          />
          {form.pointsCount > 200 && (
            <span className="badge warning">Большое количество строк. Возможны задержки.</span>
          )}
        </div>

        <div className="inline-actions">
          <button type="button" className="secondary" onClick={regenerateTable}>
            Создать таблицу
          </button>
          <button type="submit" className="primary" disabled={isSubmitting}>
            {isSubmitting ? 'Создание…' : 'Создать функцию'}
          </button>
        </div>

        <div className="table-wrapper" aria-label="Точки функции">
          <table>
            <thead>
              <tr>
                <th>X</th>
                <th>Y</th>
              </tr>
            </thead>
            <tbody>{rowsToRender}</tbody>
          </table>
        </div>
      </form>
    </div>
  );
};
