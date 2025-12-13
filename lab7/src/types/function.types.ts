export enum FactoryType {
  ARRAY = 'ARRAY',
  LINKED_LIST = 'LINKED_LIST'
}

export interface Point {
  x: number;
  y: number;
}

export type ErrorType =
  | 'INVALID_NUMBER'
  | 'NEGATIVE_SIZE'
  | 'TOO_LARGE_SIZE'
  | 'INVALID_INTERVAL'
  | 'DUPLICATE_X'
  | 'EMPTY_FIELD'
  | 'SERVER_ERROR'
  | 'FUNCTION_NOT_FOUND'
  | 'PREVIEW_ERROR'
  | 'PREVIEW_LIMIT';

export interface MathFunctionInfo {
  key: string;
  label: string;           // Локализованное название
  description: string;
  example: string;
  category: string;
  functionType: string;    // Тип функции (SqrFunction, IdentityFunction и т.д.)
  parameters: any[];       // Параметры конструктора
}

export interface MathFunctionMap {
  [key: string]: {
    label: string;
    instance: any;         // Экземпляр функции
    factory: () => any;    // Фабрика для создания экземпляра
  };
}

export interface TabulatedFunctionRequest {
  name: string;
  factoryType: FactoryType;
  points?: Point[];
  mathFunctionKey?: string;
  pointsCount?: number;
  leftBound?: number;
  rightBound?: number;
}

export interface TabulatedFunctionResponse {
  id?: number;
  name: string;
  factoryType: FactoryType;
  points: Point[];
  createdAt?: string;
}