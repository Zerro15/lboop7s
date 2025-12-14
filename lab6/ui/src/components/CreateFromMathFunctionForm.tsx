import React, { useEffect, useState } from 'react';
import { apiClient } from '../api/apiClient';
import { MathFunctionOption, UiFunctionResponse } from '../types';
import { showError } from '../services/errorHandler';

interface FormState {
  name: string;
  userId: number;
  factoryType: string;
  localizedName: string;
  pointsCount: number;
  leftBound: number;
  rightBound: number;
}

export const CreateFromMathFunctionForm: React.FC = () => {
  const [options, setOptions] = useState<MathFunctionOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState<FormState>({
    name: 'Функция по формуле',
    userId: 1,
    factoryType: 'math',
    localizedName: '',
    pointsCount: 10,
    leftBound: 0,
    rightBound: 10
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    apiClient
      .get<MathFunctionOption[]>('/api/functions/math-functions')
      .then((data) => {
        setOptions(data);
        if (data.length > 0) {
          setForm((prev) => ({ ...prev, localizedName: data[0].localizedName }));
        }
      })
      .catch(() => {
        showError('Не удалось загрузить список функций');
      })
      .finally(() => setLoading(false));
  }, []);

  const validate = () => {
    if (!form.localizedName) {
      showError('Выберите математическую функцию');
      return false;
    }
    if (form.pointsCount < 2) {
      showError('Количество точек должно быть не менее 2');
      return false;
    }
    if (form.leftBound >= form.rightBound) {
      showError('Левая граница должна быть меньше правой');
      return false;
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
        localizedName: form.localizedName,
        pointsCount: form.pointsCount,
        leftBound: form.leftBound,
        rightBound: form.rightBound
      };
      await apiClient.post<UiFunctionResponse>('/api/functions/create-from-math-function', payload);
      alert('Функция успешно создана');
    } catch (error) {
      // ошибки уже показаны обработчиком
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="card">
      <h2>Создание из MathFunction</h2>
      {loading ? (
        <p>Загрузка функций…</p>
      ) : (
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
            <label>Выберите функцию</label>
            <select
              value={form.localizedName}
              onChange={(e) => setForm({ ...form, localizedName: e.target.value })}
            >
              {options.map((opt) => (
                <option key={opt.localizedName} value={opt.localizedName}>
                  {opt.localizedName}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Количество точек</label>
            <input
              type="number"
              min={2}
              value={form.pointsCount}
              onChange={(e) => setForm({ ...form, pointsCount: Number(e.target.value) })}
            />
          </div>

          <div className="form-group">
            <label>Начало интервала</label>
            <input
              type="number"
              value={form.leftBound}
              onChange={(e) => setForm({ ...form, leftBound: Number(e.target.value) })}
            />
          </div>

          <div className="form-group">
            <label>Конец интервала</label>
            <input
              type="number"
              value={form.rightBound}
              onChange={(e) => setForm({ ...form, rightBound: Number(e.target.value) })}
            />
          </div>

          <div className="inline-actions">
            <button type="submit" className="primary" disabled={isSubmitting}>
              {isSubmitting ? 'Создание…' : 'Создать функцию'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
};
