import "./searchByImage.css";
import { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkSearchResultInterface } from "../types/artworkSearchResult";
import CardGallerySearchResult from "../components/cardGallerySearchResult";
import type { UploadImageData } from "../types/uploadImageData";

function SearchByImage() {
  const [loading, setLoading] = useState<boolean>(false);
  const [options, setOptions] = useState<ArtworkSearchResultInterface[]>([]);
  const [imageUploaded, setImageUploaded] = useState<UploadImageData>({
    image: null,
  });

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) return;
    setLoading(true);
    const file = e.target.files[0];
    setImageUploaded({ image: file });
    const data = new FormData();
    data.append("image", file);
    const response = await fetch(`http://localhost:8080/api/recognize`, {
      method: "POST",
      body: data,
    });

    if (!response.ok) throw new Error("Failed to recognize the image.");

    const result = await response.json();
    console.log(result);
    setLoading(false);
    setOptions(result);
  };

  return (
    <>
      <form className="search-image-form">
        <div className="search-image-header">
          Want to know more about an artwork, a landmark, or a famous
          photograph? Upload an image!
        </div>
        <input type="file" name="image" onChange={handleFileUpload} />
      </form>
      <div className="search-image-content">
        {loading ? (
          <div className="loader-group">
            <div className="searching">Searching . . .</div>
            <div>
              <FontAwesomeIcon icon={faSpinner} className="loader" />
            </div>
          </div>
        ) : (
          options.map((op, index) => (
            <CardGallerySearchResult
              key={index}
              data={op}
              imageUploaded={imageUploaded}
            />
          ))
        )}
      </div>
    </>
  );
}

export default SearchByImage;
