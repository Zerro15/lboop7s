import { useEffect } from 'react';
import './Modal.css';

type Props = {
  message: string | null;
  onClose: () => void;
};

export function ErrorModal({ message, onClose }: Props) {
  useEffect(() => {
    function onKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') onClose();
    }
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [onClose]);

  if (!message) return null;

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true">
      <div className="modal-window">
        <h2>Ошибка</h2>
        <p>{message}</p>
        <button type="button" onClick={onClose} className="primary">
          Понятно
        </button>
      </div>
    </div>
  );
}
