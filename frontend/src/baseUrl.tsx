export const API_BASE =
  import.meta.env.REACT_APP_ENV === "production"
    ? import.meta.env.VITE_API_BASE
    : "http://localhost:8080";
