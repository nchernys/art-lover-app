import "./cardGallery.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCirclePlay, faXmark } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkInterface } from "../types/artwork";
import Bookmark from "./bookmark";

function CardGallery({
  data,
  onDelete,
  onSelect,
  onBookmarkUpdate,
}: {
  data: ArtworkInterface;
  onDelete: (id: string) => Promise<void>;
  onSelect: (id: string) => void;
  onBookmarkUpdate: (id: string) => Promise<void>;
}) {
  return (
    <>
      <div className="gallery-card-wrapper">
        <div className="gallery-card-delete" onClick={() => onDelete(data.id)}>
          <FontAwesomeIcon icon={faXmark} />
        </div>
        <Bookmark data={data} onBookmarkUpdate={onBookmarkUpdate} />

        <div className="gallery-card-content-wrapper">
          <div className="gallery-card-image">
            {data.imageKey !== null ? (
              <img
                src={`https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.imageKey}`}
                alt={`${data.imageKey}`}
              />
            ) : (
              <img src={data.imageUrl} alt={`${data.imageUrl}`} />
            )}
          </div>
          <div className="gallery-card-details">
            <div className="gallery-card-detailes-wrapper">
              <div className="gallery-card-title">{data.title}</div>
              <div className="gallery-card-artist">{data.artist}</div>
              <div className="gallery-card-year">{data.year}</div>
            </div>
            <div
              className="gallery-card-details-play"
              onClick={() => onSelect(data.id)}
            >
              <FontAwesomeIcon icon={faCirclePlay} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default CardGallery;
