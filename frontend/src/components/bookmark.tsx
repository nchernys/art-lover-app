import "./bookmark.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBookmark as faBookmarkRegular } from "@fortawesome/free-regular-svg-icons";
import { faBookmark as faBookmarkSolid } from "@fortawesome/free-solid-svg-icons";
import type { ArtworkInterface } from "../types/artwork";

function Bookmark({
  data,
  onBookmarkUpdate,
}: {
  data: ArtworkInterface;
  onBookmarkUpdate: (id: string) => Promise<void>;
}) {
  return (
    <div
      className="gallery-card-bookmark"
      onClick={() => onBookmarkUpdate(data.id)}
    >
      {data.bookmark && data.bookmark === true ? (
        <FontAwesomeIcon icon={faBookmarkSolid} />
      ) : (
        <FontAwesomeIcon icon={faBookmarkRegular} />
      )}
    </div>
  );
}

export default Bookmark;
