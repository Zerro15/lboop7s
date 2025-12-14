import { ApiErrorResponse } from '../types';
import { showError } from '../services/errorHandler';

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function handleResponse(response: Response) {
  if (response.ok) {
    const contentType = response.headers.get('Content-Type') || '';
    if (contentType.includes('application/json')) {
      return response.json();
    }
    return null;
  }

  let errorMessage = 'Произошла ошибка при обращении к серверу';
  try {
    const data: ApiErrorResponse = await response.json();
    errorMessage = data.message || errorMessage;
    throw new ApiError(data.status || response.status, errorMessage);
  } catch (err) {
    if (err instanceof ApiError) {
      throw err;
    }
    throw new ApiError(response.status, errorMessage);
  }
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  try {
    const response = await fetch(`${baseUrl}${url}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      ...options
    });

    return (await handleResponse(response)) as T;
  } catch (error) {
    if (error instanceof ApiError) {
      showError(error.message);
    } else {
      showError('Не удалось выполнить запрос');
    }
    throw error;
  }
}

export const apiClient = {
  get: <T>(url: string) => request<T>(url),
  post: <T>(url: string, body: unknown) =>
    request<T>(url, {
      method: 'POST',
      body: JSON.stringify(body)
    })
};
