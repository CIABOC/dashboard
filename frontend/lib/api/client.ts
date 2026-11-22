export type ApiResponse<T> = {
  timestamp: string;
  success: boolean;
  data: T | null;
  error: string | null;
};

const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export async function apiGet<T>(path: string): Promise<ApiResponse<T>> {
  const response = await fetch(`${baseUrl}${path}`, {
    headers: { Accept: "application/json" },
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`API request failed (${response.status})`);
  }

  return response.json() as Promise<ApiResponse<T>>;
}
