import "./error.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";

interface ErrorProps {
  error: string;
  setErrorMessage: React.Dispatch<React.SetStateAction<string>>;
}

export function ErrorModal({ error, setErrorMessage }: ErrorProps) {
  return (
    <div className="error-modal-message">
      <div className="error-modal-close" onClick={() => setErrorMessage("")}>
        <FontAwesomeIcon icon={faXmark} />
      </div>
      <div className="error-modal-text">{error}</div>
    </div>
  );
}
