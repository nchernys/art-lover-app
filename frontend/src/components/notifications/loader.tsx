import "./loader.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";

export function Loader() {
    return (
      <div className="loader-screen">
        <div className="loader-group">
          <FontAwesomeIcon icon={faSpinner} className="loader" />
        </div>
      </div>
    );
}
