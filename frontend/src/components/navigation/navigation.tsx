import "./navigation.css";
import { useEffect } from "react";
import { NavLink } from "react-router-dom";
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
  useEffect(() => {
    console.log(userId);
  }, [userId]);

  return (
    <>
      <nav>
        <div className="header">Art Lover</div>

        <div className="navIcons">
          <div>
            <NavLink to="/camera">
              <FontAwesomeIcon icon={faCamera} className="navIcon" />
            </NavLink>
            <NavLink to="/upload">
              <FontAwesomeIcon icon={faPlus} className="navIcon" />
            </NavLink>
            <NavLink to="/?search=true">
              <FontAwesomeIcon icon={faMagnifyingGlass} className="navIcon" />
            </NavLink>
          </div>
          <div>
            <NavLink to="/">
              <FontAwesomeIcon icon={faEye} className="navIcon" />
            </NavLink>
            <NavLink to="/bookmarked">
              <FontAwesomeIcon icon={faBookmark} />
            </NavLink>

            {!userId ? (
              <NavLink to="/login">
                <FontAwesomeIcon icon={faUser} />
              </NavLink>
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
