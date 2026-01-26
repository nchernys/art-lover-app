import "./findNewAndSaveToGallery.css";
import { useState, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../../types/artworkSearchResult";
import CardGallerySearchResult from "../../components/searchByImage/cardGallerySearchResult";
import type { UploadImageData } from "../../types/uploadImageData";
import { Toast } from "../../components/notifications/toast";

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

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) return;
    setOptions([]);
    setLoading(true);
    const file = e.target.files[0];
    setImageUploaded({ image: file });
    const data = new FormData();
    data.append("image", file);
    const response = await fetch(`http://localhost:8080/api/recognize`, {
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
    const response = await fetch(
      `http://localhost:8080/api/recognize-keywords`,
      {
        method: "POST",
        credentials: "include",
        body: data,
      },
    );
    if (!response.ok) throw new Error("Failed to recognize the image.");

    const result = await response.json();
    setLoading(false);
    setOptions(result);
    console.log("RESULT", result);
  };

  return (
    <>
      {showToast && <Toast message="Saved successfully!" />}
      <div className="search-forms">
        <form className="search-image-form">
          <div className="search-header">
            Want to know more about an artwork, a landmark, or a famous
            photograph? Upload an image!
          </div>
          <input
            ref={fileInputRef}
            type="file"
            name="image"
            onChange={handleFileUpload}
          />
        </form>
        <form className="search-keywords-form" onSubmit={handleSubmitKeywords}>
          <div className="search-header">Or use keywords:</div>
          <div className="search-input-group">
            <input
              ref={fileInputRef}
              type="text"
              name="keywords"
              placeholder="Van Gogh Starry Night"
              onChange={handleChangeKeywords}
              required
            />
            <input type="submit" value="Submit" />
          </div>
        </form>
      </div>
      <div className="search-image-content">
        {loading && (
          <div className="loader-group">
            <FontAwesomeIcon icon={faSpinner} className="loader" />
          </div>
        )}
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
