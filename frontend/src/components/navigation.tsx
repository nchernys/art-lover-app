import "./navigation.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPlus,
  faEye,
  faMagnifyingGlass,
  faCamera,
  faBookmark,
  faUser,
} from "@fortawesome/free-solid-svg-icons";

function Navigation() {
  return (
    <>
      <nav>
        <div className="header">Art Lover</div>
        <div className="navIcons">
          <a href="/camera">
            <FontAwesomeIcon icon={faCamera} className="navIcon" />
          </a>
          <a href="/upload">
            <FontAwesomeIcon icon={faPlus} className="navIcon" />
          </a>
          <a href="/search">
            <FontAwesomeIcon icon={faMagnifyingGlass} className="navIcon" />
          </a>
          <a href="/">
            <FontAwesomeIcon icon={faEye} className="navIcon" />
          </a>
          <a href="/bookmarked">
            <FontAwesomeIcon icon={faBookmark} />
          </a>
          <a href="/login">
            <FontAwesomeIcon icon={faUser} />
          </a>
        </div>
      </nav>
    </>
  );
}

export default Navigation;
