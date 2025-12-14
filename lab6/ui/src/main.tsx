import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom/client';
import './styles.css';
import { CreateFromArraysForm } from './components/CreateFromArraysForm';
import { CreateFromMathFunctionForm } from './components/CreateFromMathFunctionForm';
import { ErrorModal } from './components/ErrorModal';
import { subscribeToErrors } from './services/errorHandler';

const App: React.FC = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    subscribeToErrors((message) => {
      setErrorMessage(message);
    });
  }, []);

  return (
    <div className="app-container">
      <h1>Лабораторная работа №7: табулирование функций</h1>
      <p>
        Выберите удобный способ создания табулированной функции: вручную через таблицу точек или на основе
        математической функции.
      </p>
      <div className="section-grid">
        <CreateFromArraysForm />
        <CreateFromMathFunctionForm />
      </div>

      <ErrorModal open={!!errorMessage} message={errorMessage || ''} onClose={() => setErrorMessage(null)} />
    </div>
  );
};

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(<App />);
