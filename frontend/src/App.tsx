import "./index.css";
import { useState, useEffect } from "react";

import { Routes, Route, useNavigate } from "react-router-dom";
import UploadArtwork from "./pages/uploadArtwork";
import Navigation from "./components/navigation/navigation";
import Gallery from "./pages/gallery";
import GalleryBookmarked from "./pages/galleryBookmarked";
import SearchByImage from "./pages/recognizeByImage";
import { Login } from "./pages/auth/login";
import { Signup } from "./pages/auth/signup";
import ProtectedRoute from "./security/protectedRoute";

export default function App() {
  const [userId, setUserId] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchMe = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/auth/me", {
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
      const response = await fetch("http://localhost:8080/api/auth/logout", {
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
              <Gallery />
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
          path="/bookmarked"
          element={
            <ProtectedRoute>
              <GalleryBookmarked />
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
