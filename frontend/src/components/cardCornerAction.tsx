import "./cardCornerAction.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark, faExpand } from "@fortawesome/free-solid-svg-icons";

type CardActionCornerProps<T> = {
  onAction: (payload: T) => void | Promise<void>;
  payload: T;
  corner: string;
  icon: string;
};

export function CardCornerAction<T>({
  onAction,
  payload,
  corner,
  icon,
}: CardActionCornerProps<T>) {
  return (
    <div
      className={`gallery-card-action-corner ${corner}`}
      onClick={() => onAction(payload)}
    >
      {icon === "zoom" ? (
        <FontAwesomeIcon icon={faExpand} />
      ) : (
        <FontAwesomeIcon icon={faXmark} />
      )}
    </div>
  );
}
