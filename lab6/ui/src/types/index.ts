export interface PointDto {
  x: number;
  y: number;
}

export interface UiFunctionResponse {
  functionId: number;
  name: string;
  creationMethod: string;
  factoryType: string;
  points: PointDto[];
}

export interface MathFunctionOption {
  localizedName: string;
  key: string;
}

export interface ApiErrorResponse {
  status: number;
  message: string;
  timestamp: string;
}
