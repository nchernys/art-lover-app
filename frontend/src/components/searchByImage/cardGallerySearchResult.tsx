import "./cardGallerySearchResult.css";
import { useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../../types/artworkSearchResult";
import type { UploadImageData } from "../../types/uploadImageData";
import { API_BASE } from "../../baseUrl";
import { apiFetch } from "../../utils/apiFetch";
import { BlurImage } from "../styling/blurImagePlaceholderOnLoad";

interface CardGallerySearchResultProps {
  data: ArtworkSearchResultInterface;
  imageUploaded: UploadImageData | null;
  onSuccess: () => void;
  setLoading: React.Dispatch<React.SetStateAction<boolean>>;
}

const isBlobUrl = (value: string): value is `blob:${string}` =>
  value.startsWith("blob:");

function CardGallerySearchResult({
  data,
  imageUploaded,
  onSuccess,
  setLoading,
}: CardGallerySearchResultProps) {
  const [allImages, setAllImages] = useState<string[] | null>(null);
  const [imageSelected, setImageSelected] = useState<string | null>(null);
  const [imageSrc, setImageSrc] = useState<string | null>(null);

  useEffect(() => {
    if (!imageUploaded || !(imageUploaded.image instanceof File)) {
      setImageSrc(null);
      return;
    }

    const url = URL.createObjectURL(imageUploaded.image);
    setImageSrc(url);

    return () => URL.revokeObjectURL(url);
  }, [imageUploaded]);

  useEffect(() => {
    const availableImages: string[] = [
      ...(data.imageUrls ?? []),
      ...(imageSrc ? [imageSrc] : []),
    ];
    setAllImages(availableImages);
    setImageSelected(availableImages[0]);
  }, [data.imageUrls, imageSrc]);

  const handleSubmit = async () => {
    try {
      setLoading(true);

      const formData = new FormData();
      formData.append("title", data.title);
      formData.append("artist", data.artist);
      formData.append("year", data.year);
      formData.append("continent", data.continent);
      formData.append("country", data.country);
      formData.append("movement", data.movement);
      formData.append("description", data.description);
      formData.append("box", JSON.stringify(data.box));

      if (!data.imageUrls || data.imageUrls?.length < 1) {
        formData.append("imageUrl", "");
      } else if (
        imageUploaded?.image instanceof File &&
        imageSelected &&
        isBlobUrl(imageSelected)
      ) {
        formData.append("imageFile", imageUploaded.image);
      } else if (imageSelected) {
        formData.append("imageUrl", imageSelected);
      }

      const response = await apiFetch(`${API_BASE}/api/save`, {
        method: "POST",
        credentials: "include",
        body: formData,
      });

      if (!response.ok) {
        throw new Error("Upload failed");
      }

      onSuccess();
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const hasImages = allImages !== null && allImages.length > 0;
  const noImage = hasImages && imageSelected === null;

  return (
    <div className="search-gallery-card-wrapper">
      <button
        className="search-gallery-save"
        onClick={handleSubmit}
        disabled={noImage}
      >
        <span className="search-gallery-save-label">Save to Gallery</span>
        <FontAwesomeIcon icon={faFloppyDisk} />
      </button>

      <div className="search-gallery-card-content-wrapper">
        <div className="search-gallery-card-images">
          {allImages?.map((item, index) => (
            <div
              key={`image-${index}-${item}`}
              className={`search-gallery-card-image-wrapper ${
                item === imageSelected ? "selected" : ""
              }`}
              onClick={() => setImageSelected(item)}
            >
              <BlurImage src={item} alt={`${data.title} by ${data.artist}`} />
            </div>
          ))}
        </div>

        <div className="search-gallery-card-details">
          <div className="search-gallery-card-details-wrapper">
            <div className="search-gallery-card-title">{data.title}</div>
            <div className="search-gallery-card-artist">{data.artist}</div>
            <div className="search-gallery-card-year">{data.year}</div>
            <div className="search-gallery-card-movement">{data.movement}</div>
            <div className="search-gallery-card-description">
              {data.description}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CardGallerySearchResult;
