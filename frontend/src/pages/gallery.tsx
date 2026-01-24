import "./gallery.css";
import { useState, useEffect } from "react";
import CardGallery from "../components/gallery/card/cardGallery";
import CardGalleryFullView from "../components/gallery/cardFullView/cardGalleryFullView";
import type { ArtworkInterface } from "../types/artwork";
import { CardGalleryImageFullView } from "../components/gallery/imageFullView/cardGalleryImageFullView";
import { DeleteModal } from "../components/gallery/modals/deleteModal";

interface DeleteModalInterface {
  id: string;
  title: string;
}

function Gallery() {
  const [artworks, setArtworks] = useState<ArtworkInterface[]>([]);
  const [selectedArtworkId, setSelectedArtworkId] = useState<string | null>(
    null,
  );
  const [imageFullView, setImageFullView] = useState<boolean>(false);
  const [deleteModal, setDeleteModal] = useState<DeleteModalInterface>({
    id: "",
    title: "",
  });

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
    setArtworks(result);
  };

  const handleDeleteModal = (id: string, title: string) => {
    console.log("DELETE HIT!", id, title);
    setDeleteModal({ id: id, title: title });
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
      handleDeleteModal("", "");
    } catch (error) {
      console.error("Delete failed:", error);
      throw error;
    }
  };

  const handleSelect = (id: string) => {
    const selected = artworks.find((a) => a.id === id);
    if (selected) setSelectedArtworkId(selected.id);
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

  const handleFullViewClose = () => {
    setSelectedArtworkId(null);
  };

  const handleImageFullView = () => {
    console.log("FULL VIEW IMAGE!");
    setImageFullView(!imageFullView);
  };

  const selectedArtwork = artworks.find((aw) => aw.id === selectedArtworkId);

  return (
    <div className="wrapper">
      <div className="gallery-wrapper">
        {artworks.map((aw, index) => (
          <CardGallery
            key={index}
            data={aw}
            onDelete={() => handleDeleteModal(aw.id, aw.title)}
            onSelect={handleSelect}
            onBookmarkUpdate={handleUpdateBookmark}
          />
        ))}
      </div>
      {selectedArtwork && (
        <div className="gallery-full-view">
          <CardGalleryFullView
            data={selectedArtwork}
            onClose={handleFullViewClose}
            onBookmarkUpdate={handleUpdateBookmark}
            onImageFullView={handleImageFullView}
          />
        </div>
      )}
      {selectedArtwork && imageFullView && (
        <CardGalleryImageFullView
          onImageFullView={handleImageFullView}
          imageFullView={imageFullView}
          data={selectedArtwork}
        />
      )}
      {deleteModal.id !== "" && (
        <DeleteModal
          onDeleteModal={handleDeleteModal}
          onDelete={handleDelete}
          title={deleteModal.title}
          id={deleteModal.id}
        />
      )}
    </div>
  );
}

export default Gallery;
