import "./index.css";
import { Routes, Route } from "react-router-dom";
import UploadArtwork from "./pages/uploadArtwork";
import Navigation from "./components/navigation";
import Gallery from "./pages/gallery";
import GalleryBookmarked from "./pages/galleryBookmarked";
import SearchByImage from "./pages/searchByImage";
import { Login } from "./pages/login";

export default function App() {
  return (
    <>
      <Navigation />
      <Routes>
        <Route path="/" element={<Gallery />} />
        <Route path="/upload" element={<UploadArtwork />} />
        <Route path="/bookmarked" element={<GalleryBookmarked />} />
        <Route path="/camera" element={<SearchByImage />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </>
  );
}
