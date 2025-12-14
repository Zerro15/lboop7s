import React from 'react';

interface ErrorModalProps {
  open: boolean;
  message: string;
  onClose: () => void;
}

export const ErrorModal: React.FC<ErrorModalProps> = ({ open, message, onClose }) => {
  if (!open) return null;

  return (
    <div className="modal-backdrop" role="alertdialog" aria-modal="true">
      <div className="modal">
        <h3>Ошибка</h3>
        <p>{message}</p>
        <div className="modal-actions">
          <button className="primary" onClick={onClose} autoFocus>
            Понятно
          </button>
        </div>
      </div>
    </div>
  );
};
