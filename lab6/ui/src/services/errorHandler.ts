type Listener = (message: string) => void;

class ErrorHandler {
  private listeners: Listener[] = [];

  subscribe(listener: Listener) {
    this.listeners.push(listener);
    return () => this.unsubscribe(listener);
  }

  unsubscribe(listener: Listener) {
    this.listeners = this.listeners.filter((l) => l !== listener);
  }

  publish(message: string) {
    this.listeners.forEach((listener) => listener(message));
  }
}

export const errorHandler = new ErrorHandler();
