import { useState, useEffect } from "react";
import type { ArtworkInterface } from "../types/artwork";
import { ArtworkInitialState } from "../types/artwork";

interface Artist {
  id: string;
  name: string;
}

function UploadArtwork() {
  const [formData, setFormData] =
    useState<ArtworkInterface>(ArtworkInitialState);
  const [artists, setArtists] = useState<Artist[]>([]);
  const [showInput, showOtherInput] = useState<boolean>(false);

  const fetchArtists = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/artists", {
        method: "GET",
        credentials: "include",
      });
      if (!response.ok) throw new Error("Failed to fetch artists");
      const data = await response.json();
      setArtists(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchArtists();
  }, []);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    const { name, value } = e.target;

    const nextValue = value;

    if (name === "artistId") {
      if (value === "__OTHER__") {
        showOtherInput(true);
      } else {
        showOtherInput(false);
      }
    }

    setFormData((prev) => {
      return {
        ...prev,
        [name]: nextValue,
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
    if (formData.artistId !== "__OTHER__") {
      data.append("artistId", formData.artistId);
    } else {
      data.append("artistId", "");
    }
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

    console.log(data);
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
          <label htmlFor="image">
            Upload Image <span className="required">*</span>
          </label>
          <input
            type="file"
            name="image"
            onChange={handleFileChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="title">
            Title <span className="required">*</span>
          </label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="artist">
            Artist <span className="required">*</span>
          </label>
          <select
            name="artistId"
            value={formData.artistId}
            onChange={handleChange}
            required
          >
            <option value="" disabled selected>
              Select an artist:
            </option>
            {artists &&
              artists.map((a, i) => (
                <option key={i} value={a.id}>
                  {a.name}
                </option>
              ))}
            <option value="__OTHER__">Other</option>
          </select>
          {showInput && (
            <input
              type="text"
              name="artist"
              value={formData.artist}
              onChange={handleChange}
            />
          )}
        </div>
        <div className="form-group">
          <label htmlFor="year">
            Year or Period <span className="required">*</span>
          </label>
          <input
            placeholder="1823, 1st century CE, Paleolithic"
            type="text"
            name="year"
            value={formData.year}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="movement">
            Movement <span className="required">*</span>
          </label>
          <input
            type="text"
            name="movement"
            value={formData.movement}
            onChange={handleChange}
            required
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
          <label htmlFor="description">
            Description <span className="required">*</span>
          </label>
          <textarea
            name="description"
            rows={8}
            value={formData.description}
            onChange={handleChange}
            required
          />
        </div>
        <button>Submit</button>
      </form>
    </>
  );
}

export default UploadArtwork;
