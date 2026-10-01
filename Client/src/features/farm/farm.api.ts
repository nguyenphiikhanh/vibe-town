import type { LoginResponse } from "./farm.types";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001";

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await fetch(`${apiUrl}/auth/login`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  const result = (await response.json()) as LoginResponse | { error?: string };
  if (!response.ok || !("token" in result)) {
    throw new Error("error" in result ? result.error ?? "Không thể đăng nhập." : "Không thể đăng nhập.");
  }
  return result;
}
