import "./index.css";
import { useState, useEffect } from "react";

import { Routes, Route, useNavigate } from "react-router-dom";
import UploadArtwork from "./pages/addNewArtwork/addArtworkManually";
import Navigation from "./components/navigation/navigation";
import Gallery from "./pages/gallery/gallery";
import SearchByImage from "./pages/addNewArtwork/addArtworkWithAi";
import { Login } from "./pages/auth/login";
import { Signup } from "./pages/auth/signup";
import ProtectedRoute from "./security/protectedRoute";
import { API_BASE } from "./baseUrl";

export default function App() {
  const [userId, setUserId] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchMe = async () => {
    try {
      const response = await fetch(`${API_BASE}/api/auth/me`, {
        credentials: "include",
      });

      if (!response.ok) throw new Error("Login failed");

      const data = await response.text(); // NOT json()
      setUserId(data);
    } catch (error) {
      setUserId(null);
      console.log("Error: ", error);
    }
  };

  useEffect(() => {
    fetchMe();
  }, []);

  const handleLogout = async () => {
    try {
      const response = await fetch(`${API_BASE}/api/auth/logout`, {
        method: "POST",
        credentials: "include",
      });

      if (!response.ok) throw new Error("Logout failed.");

      navigate("/login");
      fetchMe();
    } catch (error) {
      console.log("Error: ", error);
    }
  };

  return (
    <>
      <Navigation userId={userId} onLogout={handleLogout} />
      <Routes>
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Gallery userId={userId} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/upload"
          element={
            <ProtectedRoute>
              <UploadArtwork />
            </ProtectedRoute>
          }
        />
        <Route
          path="/camera"
          element={
            <ProtectedRoute>
              <SearchByImage />
            </ProtectedRoute>
          }
        />
        <Route path="/login" element={<Login onLogin={fetchMe} />} />
        <Route path="/sign-up" element={<Signup />} />
      </Routes>
    </>
  );
}
