import "./cardGalleryImageFullView.css";
import type { ArtworkInterface } from "../types/artwork";

type CardGalleryImageFullViewProps = {
  onImageFullView: boolean;
  data: ArtworkInterface;
};

export function CardGalleryImageFullView({
  onImageFullView,
  data,
}: CardGalleryImageFullViewProps) {
  if (!onImageFullView) return null;
  return (
    <div className="card-gallery-image-full-view-wrapper">
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
