export interface PointDto {
  x: number;
  y: number;
}

export interface CreateFromArraysRequest {
  points: PointDto[];
}

export interface CreateFromMathFunctionRequest {
  functionName: string;
  pointsCount: number;
  start: number;
  end: number;
}

export interface TabulatedFunctionResponse {
  points: PointDto[];
}

export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string;
}
