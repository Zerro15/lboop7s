let onError: ((message: string) => void) | null = null;

export function subscribeToErrors(handler: (message: string) => void) {
  onError = handler;
}

export function showError(message: string) {
  if (onError) {
    onError(message);
  }
}
