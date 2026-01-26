import "./addArtworkWithAi.css";
import { useState, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCamera, faMagnifyingGlass } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../../types/artworkSearchResult";
import CardGallerySearchResult from "../../components/searchByImage/cardGallerySearchResult";
import type { UploadImageData } from "../../types/uploadImageData";
import { Toast } from "../../components/notifications/toast";
import { Loader } from "../../components/notifications/loader";

function isMobileDevice(): boolean {
  return (
    typeof navigator !== "undefined" &&
    /Android|iPhone|iPad|iPod/i.test(navigator.userAgent)
  );
}

interface Keywords {
  keywords: string;
}

function SearchByImage() {
  const [loading, setLoading] = useState<boolean>(false);
  const [options, setOptions] = useState<ArtworkSearchResultInterface[]>([]);
  const [imageUploaded, setImageUploaded] = useState<UploadImageData>({
    image: null,
  });
  const [showToast, setShowToast] = useState<boolean>(false);
  const [keywords, setKeywords] = useState<Keywords>({ keywords: "" });
  const fileInputRef = useRef<HTMLInputElement>(null);
  const cameraInputRef = useRef<HTMLInputElement>(null);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) return;
    setOptions([]);
    setLoading(true);
    const file = e.target.files[0];
    setImageUploaded({ image: file });
    const data = new FormData();
    data.append("image", file);
    const response = await fetch(`/api/recognize`, {
      method: "POST",
      credentials: "include",
      body: data,
    });

    if (!response.ok) throw new Error("Failed to recognize the image.");

    const result = await response.json();
    setLoading(false);
    setOptions(result);
  };

  const clearForm = () => {
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleChangeKeywords = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    const { name, value } = e.target;

    setKeywords((prev) => {
      return {
        ...prev,
        [name]: value,
      };
    });
  };

  const handleSubmitKeywords = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setOptions([]);
    setLoading(true);
    const data = new FormData();
    data.append("keywords", keywords.keywords);
    const response = await fetch(`/api/recognize-keywords`, {
      method: "POST",
      credentials: "include",
      body: data,
    });
    if (!response.ok) throw new Error("Failed to recognize the image.");

    const result = await response.json();
    setLoading(false);
    setOptions(result);
    console.log("RESULT", result);
  };

  const isMobile = isMobileDevice();

  return (
    <>
      {showToast && <Toast message="Saved successfully!" />}
      <div className="search-forms">
        <form className="search-image-form">
          <div className="search-header">
            Want to know more about an artwork, a landmark, or a famous
            photograph? Upload an image!
          </div>
          <div className="search-input-group">
            <input
              ref={fileInputRef}
              type="file"
              name="image"
              onChange={handleFileUpload}
            />
          </div>
        </form>
        {isMobile && (
          <form className="search-image-form">
            <div className="search-header">Take a photo:</div>
            <div className="search-input-group camera">
              <div onClick={() => cameraInputRef.current?.click()}>
                <FontAwesomeIcon
                  icon={faCamera}
                  className="search-take-photo-icon"
                />
                <input
                  ref={cameraInputRef}
                  type="file"
                  name="image"
                  accept="image/*"
                  capture="environment"
                  style={{ display: "none" }}
                  onChange={handleFileUpload}
                />
              </div>
            </div>
          </form>
        )}

        <form className="search-keywords-form" onSubmit={handleSubmitKeywords}>
          <div className="search-header">Or use keywords:</div>
          <div className="search-input-group keywords">
            <input
              ref={fileInputRef}
              type="text"
              name="keywords"
              placeholder="Van Gogh Starry Night"
              onChange={handleChangeKeywords}
              required
            />
            <button>
              <FontAwesomeIcon icon={faMagnifyingGlass} />
            </button>
          </div>
        </form>
      </div>
      <div className="search-image-content">
        {loading && <Loader />}
        {options.length >= 1 &&
          options.map((op, index) => (
            <CardGallerySearchResult
              key={`${op.title.split(" ").join("-")}-${index}`}
              data={op}
              setLoading={setLoading}
              imageUploaded={imageUploaded}
              onSuccess={() => {
                setShowToast(true);
                setTimeout(() => {
                  setOptions([]);
                  clearForm();
                }, 2000);
              }}
            />
          ))}
      </div>
    </>
  );
}

export default SearchByImage;
