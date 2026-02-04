export async function apiFetch(
  input: RequestInfo | URL,
  init: RequestInit = {},
): Promise<Response> {
  const response = await fetch(input, {
    ...init,
    credentials: "include",
  });

  const url = typeof input === "string" ? input : input.toString();

  if (
    response.status === 401 &&
    !url.includes("/api/auth/login") &&
    !url.includes("/api/auth/signup")
  ) {
    if (!window.location.pathname.startsWith("/login")) {
      window.location.href = "/login";
    }

    return Promise.reject(new Error("Session expired"));
  }

  return response;
}
