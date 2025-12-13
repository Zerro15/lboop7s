import { useEffect, useState } from 'react';
import { ErrorModal } from './components/ErrorModal';
import { CreateFromArraysForm } from './components/CreateFromArraysForm';
import { CreateFromMathFunctionForm } from './components/CreateFromMathFunctionForm';
import { errorHandler } from './services/errorHandler';
import './main.css';

function App() {
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    useEffect(() => {
        const unsubscribe = errorHandler.subscribe((message) => setErrorMessage(message));
        return unsubscribe;
    }, []);

    return (
        <div className="page">
            <header>
                <h1>Лабораторная работа №7</h1>
                <p className="subtitle">Два способа создания табулированной функции.</p>
            </header>

            <main>
                <div className="forms-container">
                    <section className="form-section">
                        <h2>Создание из массива точек</h2>
                        <CreateFromArraysForm />
                    </section>

                    <section className="form-section">
                        <h2>Создание из математической функции</h2>
                        <CreateFromMathFunctionForm />
                    </section>
                </div>
            </main>

            <ErrorModal
                message={errorMessage}
                onClose={() => setErrorMessage(null)}
            />
        </div>
    );
}

export default App;