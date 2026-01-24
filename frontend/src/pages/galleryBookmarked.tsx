import "./gallery.css";
import { useState, useEffect } from "react";
import CardGallery from "../components/gallery/card/cardGallery";
import CardGalleryFullView from "../components/gallery/cardFullView/cardGalleryFullView";
import type { ArtworkInterface } from "../types/artwork";
import { ArtworkInitialState } from "../types/artwork";

function GalleryBookmarked() {
  const [artworks, setArtworks] = useState<ArtworkInterface[]>([]);
  const [selectedArtwork, setSelectedArtwork] =
    useState<ArtworkInterface>(ArtworkInitialState);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const response = await fetch("http://localhost:8080/api/show", {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("Failed to fetch artworks.");
    }

    const result = await response.json();
    const bookmarked = result.filter(
      (aw: ArtworkInterface) => aw.bookmark === true,
    );
    setArtworks(bookmarked);
    console.log(bookmarked);
  };

  const handleDelete = async (id: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/delete/${id}`, {
        method: "DELETE",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Failed to delete record");
      }
      fetchData();
    } catch (error) {
      console.error("Delete failed:", error);
      throw error;
    }
  };

  const handleSelect = (id: string) => {
    const selected = artworks.find((a) => a.id === id);
    if (selected) setSelectedArtwork(selected);
  };

  const handleUpdateBookmark = async (id: string) => {
    const artwork = artworks.find((aw) => aw.id === id);
    if (!artwork) throw new Error("Artwork not found.");
    const updateBookmark = new FormData();
    updateBookmark.append("bookmark", `${!artwork.bookmark}`);
    try {
      const response = await fetch(
        `http://localhost:8080/api/update/bookmark/${id}`,
        {
          method: "PATCH",
          credentials: "include",
          body: updateBookmark,
        },
      );
      if (!response.ok) {
        throw new Error("Failed to update bookmark");
      }
      fetchData();
    } catch (error) {
      console.error("Bookmark update failed:", error);
      throw error;
    }
  };

  return (
    <div className="wrapper">
      <div className="gallery-wrapper">
        {artworks.map((aw, index) => (
          <CardGallery
            key={index}
            data={aw}
            onDelete={handleDelete}
            onSelect={handleSelect}
            onBookmarkUpdate={handleUpdateBookmark}
          />
        ))}
      </div>
      {selectedArtwork && selectedArtwork.id !== "" && (
        <div className="gallery-full-view">
          <CardGalleryFullView
            data={selectedArtwork}
            onClose={() => setSelectedArtwork(ArtworkInitialState)}
            onBookmarkUpdate={handleUpdateBookmark}
          />
        </div>
      )}
    </div>
  );
}

export default GalleryBookmarked;
