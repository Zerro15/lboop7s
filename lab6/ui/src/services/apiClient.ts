import { CreateFromArraysRequest, CreateFromMathFunctionRequest, TabulatedFunctionResponse } from '../types';
import { errorHandler } from './errorHandler';

const BASE_URL = '/api/functions';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({ message: 'Неизвестная ошибка сервера' }));
    const message = errorBody.message || 'Неизвестная ошибка сервера';
    errorHandler.publish(message);
    throw new Error(message);
  }
  return response.json();
}

export async function fetchMathFunctions(): Promise<string[]> {
  const response = await fetch(`${BASE_URL}/math-functions`);
  return handleResponse<string[]>(response);
}

export async function createFromArrays(payload: CreateFromArraysRequest): Promise<TabulatedFunctionResponse> {
  const response = await fetch(`${BASE_URL}/create-from-arrays`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  return handleResponse<TabulatedFunctionResponse>(response);
}

export async function createFromMathFunction(payload: CreateFromMathFunctionRequest): Promise<TabulatedFunctionResponse> {
  const response = await fetch(`${BASE_URL}/create-from-math-function`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  return handleResponse<TabulatedFunctionResponse>(response);
}
