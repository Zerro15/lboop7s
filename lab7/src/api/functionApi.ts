import apiClient from './axiosConfig';
import {
  FactoryType,
  MathFunctionInfo,
  Point,
  TabulatedFunctionRequest,
  TabulatedFunctionResponse
} from '../types/function.types';

interface ApiError extends Error {
  type?: string;
  status?: number;
}

const normalizeError = (error: any): ApiError => {
  if (error?.response) {
    const apiError: ApiError = new Error(
      error.response.data?.message || error.message || 'Сервер вернул ошибку'
    );
    apiError.type = error.response.data?.type || 'SERVER_ERROR';
    apiError.status = error.response.status;
    return apiError;
  }

  if (error instanceof Error) {
    return error as ApiError;
  }

  return new Error('Неизвестная ошибка при обращении к серверу');
};

export const mathFunctionApi = {
  // Получение всех доступных математических функций с информацией
  getAllMathFunctions: async (): Promise<MathFunctionInfo[]> => {
    try {
      const response = await apiClient.get('/math-functions/all');
      return response.data as MathFunctionInfo[];
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Получение Map функций для фронтенда
  getMathFunctionMap: async (): Promise<Record<string, {
    label: string;
    instance: any;
  }>> => {
    try {
      const response = await apiClient.get('/math-functions/map');
      return response.data as Record<string, { label: string; instance: any }>;
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Создание конкретной функции по ключу
  createMathFunctionInstance: async (functionKey: string): Promise<any> => {
    try {
      const response = await apiClient.post('/math-functions/create', { functionKey });
      return response.data.instance;
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Предпросмотр функции
  previewMathFunction: async (
    mathFunctionKey: string,
    pointsCount: number,
    leftBound: number,
    rightBound: number
  ): Promise<Point[]> => {
    try {
      const response = await apiClient.post('/math-functions/preview', {
        mathFunctionKey,
        pointsCount,
        leftBound,
        rightBound,
      });
      return response.data.points as Point[];
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Получение функции по локализованному названию
  getFunctionByLocalizedName: async (localizedName: string): Promise<any> => {
    try {
      const response = await apiClient.get(`/math-functions/by-name/${encodeURIComponent(localizedName)}`);
      return response.data;
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Получение локализованного названия по ключу
  getLocalizedName: async (functionKey: string): Promise<string> => {
    try {
      const response = await apiClient.get(`/math-functions/localized-name/${functionKey}`);
      return response.data.name as string;
    } catch (error) {
      throw normalizeError(error);
    }
  },

  // Получение ключа по локализованному названию
  getKeyByLocalizedName: async (localizedName: string): Promise<string> => {
    try {
      const response = await apiClient.get(`/math-functions/key-by-name/${encodeURIComponent(localizedName)}`);
      return response.data.key as string;
    } catch (error) {
      throw normalizeError(error);
    }
  },
};

export const functionApi = {
  /**
   * Создание табулированной функции из массивов точек
   */
  createFromArrays: async (
    points: Point[],
    factoryType: FactoryType,
    name: string
  ): Promise<TabulatedFunctionResponse> => {
    const payload: TabulatedFunctionRequest = {
      name,
      factoryType,
      points,
    };

    try {
      const response = await apiClient.post('/functions/tabulated/arrays', payload);
      return response.data as TabulatedFunctionResponse;
    } catch (error) {
      throw normalizeError(error);
    }
  },

  /**
   * Создание табулированной функции из математической функции
   */
  createFromMathFunction: async (
    mathFunctionKey: string,
    pointsCount: number,
    leftBound: number,
    rightBound: number,
    factoryType: FactoryType,
    name: string
  ): Promise<TabulatedFunctionResponse> => {
    const payload: TabulatedFunctionRequest = {
      name,
      factoryType,
      mathFunctionKey,
      pointsCount,
      leftBound,
      rightBound,
    };

    try {
      const response = await apiClient.post('/functions/tabulated/math-function', payload);
      return response.data as TabulatedFunctionResponse;
    } catch (error) {
      throw normalizeError(error);
    }
  },
};
