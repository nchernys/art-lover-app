import "./gallery.css";
import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import CardGallery from "../../components/gallery/card/cardGallery";
import CardGalleryFullView from "../../components/gallery/cardFullView/cardGalleryFullView";
import type { ArtworkInterface } from "../../types/artwork";
import { CardGalleryImageFullView } from "../../components/gallery/imageFullView/cardGalleryImageFullView";
import { DeleteModal } from "../../components/gallery/modals/deleteModal";
import { Chatbot } from "../../components/chat/chatbot";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCamera } from "@fortawesome/free-solid-svg-icons";
import { NavLink } from "react-router-dom";
import { API_BASE } from "../../baseUrl";
import { apiFetch } from "../../utils/apiFetch";

interface DeleteModalInterface {
  id: string;
  title: string;
}

function Gallery({ userId }: { userId: string }) {
  const [searchParams] = useSearchParams();
  const isSearchMode = searchParams.get("search") === "true";
  const isBookmarkMode = searchParams.get("bookmarked") === "true";
  const [artworks, setArtworks] = useState<ArtworkInterface[]>([]);
  const [selectedArtworkId, setSelectedArtworkId] = useState<string | null>(
    null,
  );
  const [imageFullView, setImageFullView] = useState<boolean>(false);
  const [deleteModal, setDeleteModal] = useState<DeleteModalInterface>({
    id: "",
    title: "",
  });
  const [query, setQuery] = useState<string>("");
  const [chatbotVisible, setChatbotVisible] = useState<boolean>(false);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (isBookmarkMode) {
      setQuery("");
    }
  }, [isBookmarkMode]);

  const fetchData = async () => {
    const response = await apiFetch(`${API_BASE}/api/show`, {
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
    setDeleteModal({ id: id, title: title });
  };

  const handleDelete = async (id: string) => {
    try {
      const response = await apiFetch(`${API_BASE}/api/delete/${id}`, {
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
      const response = await apiFetch(`${API_BASE}/api/update/bookmark/${id}`, {
        method: "PATCH",
        credentials: "include",
        body: updateBookmark,
      });
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
    setImageFullView(!imageFullView);
  };

  const selectedArtwork = artworks.find((aw) => aw.id === selectedArtworkId);

  const filteredArtworks = artworks.filter(
    (artwork) =>
      artwork.title.toLowerCase().includes(query.toLowerCase()) ||
      artwork.artist.toLowerCase().includes(query.toLowerCase()) ||
      artwork.description.toLowerCase().includes(query.toLowerCase()) ||
      artwork.movement.toLowerCase().includes(query.toLowerCase()),
  );

  const bookmarkedArtworks = artworks.filter(
    (artwork) => artwork.bookmark === true,
  );

  const galleryItems = isBookmarkMode
    ? bookmarkedArtworks
    : isSearchMode && query
      ? filteredArtworks
      : artworks;

  const handleChatbotVisibility = () => {
    setChatbotVisible((prev) => !prev);
    console.log(chatbotVisible);
  };

  const handleOpenChatbot = () => {
    setChatbotVisible(true);
  };

  return (
    <>
      <Chatbot
        onChangeChatbotState={handleChatbotVisibility}
        chatbotVisible={chatbotVisible}
        userId={userId}
        selectedArtworkId={selectedArtworkId}
        data={selectedArtwork}
      />
      {isSearchMode && (
        <input
          type="text"
          className="search-artworks"
          placeholder="Search artworks..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      )}
      <div className="wrapper">
        {galleryItems.length < 1 && (
          <div className="empty-message">
            Your gallery doesn’t have any artworks yet. <br />
            Click{" "}
            <NavLink to="/camera">
              <FontAwesomeIcon icon={faCamera} className="find-art-icon" />
            </NavLink>
            to get started!
          </div>
        )}
        <div className="gallery-wrapper">
          {galleryItems.length >= 1 &&
            galleryItems.map((aw, index) => (
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
              onOpenChatbot={handleOpenChatbot}
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
    </>
  );
}

export default Gallery;
