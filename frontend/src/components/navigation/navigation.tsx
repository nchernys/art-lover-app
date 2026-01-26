import "./navigation.css";
import { Link, useLocation } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPlus,
  faEye,
  faMagnifyingGlass,
  faCamera,
  faBookmark,
  faUser,
  faArrowRightFromBracket,
} from "@fortawesome/free-solid-svg-icons";

type NavigationProps = {
  userId: string | null;
  onLogout: () => void;
};

function Navigation({ userId, onLogout }: NavigationProps) {
  const location = useLocation();

  const isCamera = location.pathname === "/camera";
  const isUpload = location.pathname === "/upload";
  const isSearch = location.search === "?search=true";
  const isBookmarked = location.search === "?bookmarked=true";
  const isGallery = location.pathname === "/" && location.search === "";

  return (
    <>
      <nav>
        <div className="header">Art Lover</div>

        <div className="navIcons">
          <div>
            <Link to="/camera" className={isCamera ? "active" : ""}>
              <FontAwesomeIcon icon={faCamera} className="navIcon" />
            </Link>
            <Link to="/upload" className={isUpload ? "active" : ""}>
              <FontAwesomeIcon icon={faPlus} className="navIcon" />
            </Link>
            <Link to="/?search=true" className={isSearch ? "active" : ""}>
              <FontAwesomeIcon icon={faMagnifyingGlass} className="navIcon" />
            </Link>
          </div>
          <div>
            <Link to="/" className={isGallery ? "active" : ""}>
              <FontAwesomeIcon icon={faEye} className="navIcon" />
            </Link>
            <Link
              to="/?bookmarked=true"
              className={isBookmarked ? "active" : ""}
            >
              <FontAwesomeIcon icon={faBookmark} />
            </Link>

            {!userId ? (
              <Link to="/login">
                <FontAwesomeIcon icon={faUser} />
              </Link>
            ) : (
              <button
                type="button"
                onClick={onLogout}
                className="navIconButton"
              >
                <FontAwesomeIcon icon={faArrowRightFromBracket} />
              </button>
            )}
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navigation;
