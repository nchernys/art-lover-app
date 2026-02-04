import { Navigate } from "react-router-dom";
import { useEffect, useState, type ReactNode } from "react";
import { API_BASE } from "../baseUrl";
import { apiFetch } from "../utils/apiFetch";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const [allowed, setAllowed] = useState<boolean | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const res = await apiFetch(`${API_BASE}/api/auth/me`, {
          method: "GET",
          credentials: "include",
        });
        setAllowed(res.ok);
      } catch {
        setAllowed(false);
      }
    };

    checkAuth();
  }, []);

  if (allowed === null) {
    return <div>Loading...</div>;
  }

  return allowed ? <>{children}</> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
