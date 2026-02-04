import "./cardGallery.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCirclePlay } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkInterface } from "../../../types/artwork";
import Bookmark from "../../bookmark/bookmark";
import { CardCornerAction } from "../../cardCornerActionButton/cardCornerAction";

function CardGallery({
  data,
  onDelete,
  onSelect,
  onBookmarkUpdate,
}: {
  data: ArtworkInterface;
  onDelete: (id: string) => void;
  onSelect: (id: string) => void;
  onBookmarkUpdate: (id: string) => Promise<void>;
}) {
  const imageUrl = data.previewKey
    ? `https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.previewKey}`
    : data.imageKey
      ? `https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.imageKey}`
      : `${data.imageUrl}`;

  return (
    <>
      <div className="gallery-card-wrapper">
        <CardCornerAction
          onAction={onDelete}
          payload={data.id}
          corner={"topLeft"}
          icon={"delete"}
        />
        <Bookmark data={data} onBookmarkUpdate={onBookmarkUpdate} />

        <div className="gallery-card-content-wrapper">
          <div className="gallery-card-image">
            {imageUrl ? (
              <img
                src={imageUrl}
                alt={`${data.imageKey}`}
                loading="lazy"
                decoding="async"
                referrerPolicy="no-referrer"
                width={300}
                height={300}
              />
            ) : (
              <div className="gallery-card-no-image">NO IMAGE</div>
            )}
          </div>
          <div className="gallery-card-details">
            <div className="gallery-card-details-wrapper">
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
