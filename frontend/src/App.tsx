import "./index.css";
import { useState, useEffect } from "react";
import { Routes, Route } from "react-router-dom";
import UploadArtwork from "./pages/uploadArtwork";
import Navigation from "./components/navigation";
import Gallery from "./pages/gallery";
import GalleryBookmarked from "./pages/galleryBookmarked";
import SearchByImage from "./pages/searchByImage";
import { Login } from "./pages/login";
import { Signup } from "./pages/signup";
import ProtectedRoute from "./security/protectedRoute";

export default function App() {
  const [userEmail, setUserEmail] = useState<string | null>(null);
  useEffect(() => {
    fetch("http://localhost:8080/api/auth/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error();
        return res.text(); // NOT json()
      })
      .then((email) => setUserEmail(email))
      .catch(() => setUserEmail(null));
  }, []);

  return (
    <>
      <Navigation userEmail={userEmail} />
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
        <Route path="/login" element={<Login />} />
        <Route path="/sign-up" element={<Signup />} />
      </Routes>
    </>
  );
}
