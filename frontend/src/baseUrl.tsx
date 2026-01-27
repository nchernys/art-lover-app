export const API_BASE =
  import.meta.env.REACT_APP_ENV === "development"
    ? "http://localhost:8080"
    : import.meta.env.VITE_API_BASE;
