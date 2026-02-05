import "./cardGalleryFullView.css";
import Bookmark from "../../bookmark/bookmark";
import type { ArtworkInterface } from "../../../types/artwork";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCircleXmark,
  faVolumeHigh,
  faComments,
  faFaceLaughWink,
  faArrowsSpin,
} from "@fortawesome/free-solid-svg-icons";
import { CardCornerAction } from "../../cardCornerActionButton/cardCornerAction";

function CardGalleryFullView({
  data,
  onClose,
  onBookmarkUpdate,
  onImageFullView,
}: {
  data: ArtworkInterface;
  onClose: () => void;
  onBookmarkUpdate: (id: string) => Promise<void>;
  onImageFullView: () => void;
}) {
  const image =
    data.imageKey !== null
      ? `https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.imageKey}`
      : `${data.imageUrl}`;

  return (
    <div className="gallary-card-screen-full-view">
      <div className="gallery-card-wrapper-full-view">
        <div className="gallery-card-nav-full-view">
          <div
            className="gallery-card-close-full-view"
            onClick={() => onClose()}
          >
            <FontAwesomeIcon icon={faCircleXmark} />
          </div>
          <Bookmark data={data} onBookmarkUpdate={onBookmarkUpdate} />
        </div>
        <div className="gallery-card-content-wrapper-full-view">
          <div className="gallery-card-image-full-view">
            {image && (
              <CardCornerAction
                onAction={onImageFullView}
                payload={data.imageKey !== null ? data.imageKey : data.imageUrl}
                corner={"bottomLeft"}
                icon={"zoom"}
              />
            )}
            {image ? (
              <img src={image} alt={`${data.title}`} />
            ) : (
              <div className="gallery-card-full-view-no-image">NO IMAGE</div>
            )}
          </div>
          <div className="gallery-card-details gallery-card-details-full-view">
            <div className="gallery-card-detailes-wrapper-full-view">
              <div className="gallery-card-title">{data.title}</div>
              <div className="gallery-card-artist">{data.artist}</div>
              <div className="gallery-card-movement">{data.movement}</div>
              <div className="gallery-card-year">{data.year}</div>
              <div className="gallery-card-description">{data.description}</div>
            </div>
            <div className="gallery-card-defails-full-view-btns">
              <div title="Listen">
                <FontAwesomeIcon icon={faVolumeHigh} />
              </div>
              <div title="Read more">
                <FontAwesomeIcon icon={faArrowsSpin} />
              </div>
              <div title="Fun facts">
                <FontAwesomeIcon icon={faFaceLaughWink} />
              </div>

              <div title="Chat with Artsy">
                <FontAwesomeIcon icon={faComments} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CardGalleryFullView;
