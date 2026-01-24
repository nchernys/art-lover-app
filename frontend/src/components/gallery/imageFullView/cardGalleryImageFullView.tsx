import "./cardGalleryImageFullView.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkInterface } from "../../../types/artwork";

type CardGalleryImageFullViewProps = {
  onImageFullView: () => void;
  imageFullView: boolean;
  data: ArtworkInterface;
};

export function CardGalleryImageFullView({
  onImageFullView,
  imageFullView,
  data,
}: CardGalleryImageFullViewProps) {
  if (!imageFullView) return;
  return (
    <div className="card-gallery-image-full-view-wrapper">
      <div
        className="card-gallery-image-full-view-close"
        onClick={onImageFullView}
      >
        <FontAwesomeIcon icon={faXmark} />
      </div>
      {data.imageKey !== null ? (
        <img
          src={`https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.imageKey}`}
          alt={`${data.imageKey}`}
        />
      ) : (
        <img src={data.imageUrl} alt={`${data.imageUrl}`} />
      )}
    </div>
  );
}
