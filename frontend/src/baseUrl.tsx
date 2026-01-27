export const API_BASE =
  import.meta.env.VITE_ENV === "development"
    ? "http://localhost:8080"
    : import.meta.env.VITE_API_BASE;
