import "./cardGallerySearchResult.css";
import { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../../types/artworkSearchResult";
import type { UploadImageData } from "../../types/uploadImageData";
import { API_BASE } from "../../baseUrl";

function CardGallerySearchResult({
  data,
  imageUploaded,
  onSuccess,
  setLoading,
}: {
  data: ArtworkSearchResultInterface;
  imageUploaded: UploadImageData | null;
  onSuccess: () => void;
  setLoading: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const [imageSelected, setImageSelected] = useState<string>("");
  const [imageSrc, setImageSrc] = useState<string>("");

  useEffect(() => {
    let canceled = false;
    let url: string;

    if (imageUploaded && imageUploaded.image instanceof File) {
      url = URL.createObjectURL(imageUploaded.image);
      Promise.resolve().then(() => {
        if (!canceled) setImageSrc(url);
      });
      return () => {
        canceled = true;
        if (url) URL.revokeObjectURL(url);
      };
    } else {
      Promise.resolve().then(() => {
        if (!canceled) setImageSrc("");
      });
    }
  }, [imageUploaded]);

  const handleSubmit = async () => {
    setLoading(true);
    const newData = new FormData();
    newData.append("title", data.title);
    newData.append("artist", data.artist);
    newData.append("year", data.year);
    newData.append("continent", data.continent);
    newData.append("country", data.country);
    newData.append("movement", data.movement);
    newData.append("description", data.description);
    newData.append("box", JSON.stringify(data.box));
    if (
      imageUploaded &&
      imageUploaded.image &&
      imageSelected.includes("blob")
    ) {
      newData.append("imageFile", imageUploaded.image);
    } else {
      newData.append("imageUrl", imageSelected);
    }

    const response = await fetch(`${API_BASE}/api/save`, {
      method: "POST",
      credentials: "include",
      body: newData,
    });
    if (!response.ok) {
      throw new Error("Upload failed");
    }
    setLoading(false);
    onSuccess();
  };

  return (
    <>
      <div className="search-gallery-card-wrapper">
        <div className="search-gallery-save" onClick={handleSubmit}>
          <span className="search-gallery-save-label">Save to Gallery</span>{" "}
          <FontAwesomeIcon icon={faFloppyDisk} />
        </div>

        <div className="search-gallery-card-content-wrapper">
          <div className="search-gallery-card-images">
            {imageSrc !== "" && (
              <div
                className="search-gallary-card-image-wrapper"
                style={{
                  border:
                    imageSrc === imageSelected
                      ? "#E80C8F 8px solid"
                      : "transparent 8px solid",
                }}
                onClick={() => setImageSelected(imageSrc)}
              >
                <img
                  src={imageSrc}
                  alt="image"
                  loading="lazy"
                  decoding="async"
                  referrerPolicy="no-referrer"
                  width={300}
                  height={300}
                />
              </div>
            )}
            {data.imageUrls &&
              data.imageUrls.map((item, index) => (
                <div
                  key={`image-${index}`}
                  onClick={() => setImageSelected(item)}
                  className="search-gallary-card-image-wrapper"
                  style={{
                    border:
                      item === imageSelected
                        ? "#E80C8F 8px solid"
                        : "transparent 8px solid",
                  }}
                >
                  <img src={item} alt="image" />
                </div>
              ))}
          </div>
          <div className="search-gallery-card-details">
            <div className="search-gallery-card-detailes-wrapper">
              <div className="search-gallery-card-title">{data.title}</div>
              <div className="search-gallery-card-artist">{data.artist}</div>
              <div className="search-gallery-card-year">{data.year}</div>
              <div className="search-gallery-card-description">
                {data.description}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default CardGallerySearchResult;
