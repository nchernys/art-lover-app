import "./uploadArtwork.css";
import { useState } from "react";
import type { ArtworkInterface } from "../types/artwork";
import { ArtworkInitialState } from "../types/artwork";

function UploadArtwork() {
  const [formData, setFormData] =
    useState<ArtworkInterface>(ArtworkInitialState);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;

    setFormData((prev) => {
      return {
        ...prev,
        [name]: value,
      };
    });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;

    setFormData((prev) => ({
      ...prev,
      image: file,
    }));
  };
  const handleSubmit = async () => {
    if (!formData.image) {
      return;
    }

    const data = new FormData();
    data.append("title", formData.title);
    data.append("artist", formData.artist);
    data.append("year", formData.year);
    data.append("continent", formData.continent);
    data.append("country", formData.country);
    data.append("movement", formData.movement);
    data.append("description", formData.description);
    data.append("imageFile", formData.image);

    const response = await fetch("http://localhost:8080/api/add", {
      method: "POST",
      credentials: "include",
      body: data,
    });
    if (!response.ok) {
      throw new Error("Upload failed");
    }

    const result = await response.json();
    console.log(result);
    setFormData(ArtworkInitialState);
  };

  return (
    <>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
      >
        <div className="form-group">
          <label htmlFor="image">Upload Image</label>
          <input type="file" name="image" onChange={handleFileChange} />
        </div>
        <div className="form-group">
          <label htmlFor="title">Title</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="artist">Artist</label>
          <input
            type="text"
            name="artist"
            value={formData.artist}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="year">Year</label>
          <input
            type="text"
            name="year"
            value={formData.year}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="movement">Movement</label>
          <input
            type="text"
            name="movement"
            value={formData.movement}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="continent">Continent</label>
          <input
            type="text"
            name="continent"
            value={formData.continent}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="country">Country</label>
          <input
            type="text"
            name="country"
            value={formData.country}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            name="description"
            rows={8}
            value={formData.description}
            onChange={handleChange}
          />
        </div>
        <button>Submit</button>
      </form>
    </>
  );
}

export default UploadArtwork;
