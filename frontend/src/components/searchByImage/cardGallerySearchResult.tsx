import "./cardGallerySearchResult.css";
import { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../../types/artworkSearchResult";
import type { UploadImageData } from "../../types/uploadImageData";

function CardGallerySearchResult({
  data,
  imageUploaded,
  onSuccess,
}: {
  data: ArtworkSearchResultInterface;
  imageUploaded: UploadImageData;
  onSuccess: () => void;
}) {
  const [imageSelected, setImageSelected] = useState<string>("");
  const [imageSrc, setImageSrc] = useState<string | undefined>(undefined);

  useEffect(() => {
    let canceled = false;
    let url: string | undefined;

    if (imageUploaded.image instanceof File) {
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
        if (!canceled) setImageSrc(undefined);
      });
    }
  }, [imageUploaded.image]);

  const handleSubmit = async () => {
    if (!imageUploaded.image) return;
    const newData = new FormData();
    newData.append("title", data.title);
    newData.append("artist", data.artist);
    newData.append("year", data.year);
    newData.append("continent", data.continent);
    newData.append("country", data.country);
    newData.append("movement", data.movement);
    newData.append("description", data.description);
    if (imageSelected.includes("blob")) {
      newData.append("imageFile", imageUploaded.image);
    } else {
      newData.append("imageUrl", imageSelected);
    }

    const response = await fetch("http://localhost:8080/api/add", {
      method: "POST",
      credentials: "include",
      body: newData,
    });
    if (!response.ok) {
      throw new Error("Upload failed");
    }
    onSuccess();
  };

  if (!imageSrc) return;

  return (
    <>
      <div className="search-gallery-card-wrapper">
        <div className="search-gallery-save" onClick={handleSubmit}>
          <span className="search-gallery-save-label">Save to Gallery</span>{" "}
          <FontAwesomeIcon icon={faFloppyDisk} />
        </div>

        <div className="search-gallery-card-content-wrapper">
          <div className="search-gallery-card-images">
            <div
              className="search-gallary-card-image-wrapper"
              style={{
                border:
                  imageSrc === imageSelected
                    ? "#E80C8F 8px solid"
                    : "transparent 8px solid",
              }}
              onClick={() => (
                setImageSelected(imageSrc),
                console.log(imageSrc)
              )}
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
