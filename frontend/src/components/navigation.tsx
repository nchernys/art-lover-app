import "./navigation.css";
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
  userEmail: string | null;
};

function Navigation({ userEmail }: NavigationProps) {
  console.log("USER EMAIL ", userEmail);
  return (
    <>
      <nav>
        <div className="header">Art Lover</div>

        <div className="navIcons">
          <div>
            <a href="/camera">
              <FontAwesomeIcon icon={faCamera} className="navIcon" />
            </a>
            <a href="/upload">
              <FontAwesomeIcon icon={faPlus} className="navIcon" />
            </a>
            <a href="/search">
              <FontAwesomeIcon icon={faMagnifyingGlass} className="navIcon" />
            </a>
          </div>
          <div>
            <a href="/">
              <FontAwesomeIcon icon={faEye} className="navIcon" />
            </a>
            <a href="/bookmarked">
              <FontAwesomeIcon icon={faBookmark} />
            </a>

            {!userEmail ? (
              <a href="/login">
                <FontAwesomeIcon icon={faUser} />
              </a>
            ) : (
              <a href="/logout">
                <FontAwesomeIcon icon={faArrowRightFromBracket} />
              </a>
            )}
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navigation;
