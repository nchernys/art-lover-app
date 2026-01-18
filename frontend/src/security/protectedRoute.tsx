import { Navigate } from "react-router-dom";
import { useEffect, useState, type ReactNode } from "react";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const [allowed, setAllowed] = useState<boolean | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/auth/me", {
          method: "GET",
          credentials: "include",
        });

        console.log("RES OK? ", res.ok);
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
