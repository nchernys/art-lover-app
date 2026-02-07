import "./cardGalleryFullView.css";
import { useEffect, useRef, useState } from "react";
import Bookmark from "../../bookmark/bookmark";
import type { ArtworkInterface } from "../../../types/artwork";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCircleXmark,
  faVolumeHigh,
  faComments,
  faFaceLaughWink,
  faArrowsSpin,
  faPause,
  faStop,
  faComment,
  faBrush,
} from "@fortawesome/free-solid-svg-icons";
import { CardCornerAction } from "../../cardCornerActionButton/cardCornerAction";

type ReadAction = "read" | "stop" | "pause";

function CardGalleryFullView({
  data,
  onClose,
  onBookmarkUpdate,
  onImageFullView,
  onOpenChatbot,
  isChatbotRequestFromCard,
  setIsChatbotRequestFromCard,
}: {
  data: ArtworkInterface;
  onClose: () => void;
  onBookmarkUpdate: (id: string) => Promise<void>;
  onImageFullView: () => void;
  onOpenChatbot: () => void;
  isChatbotRequestFromCard: boolean;
  setIsChatbotRequestFromCard: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const descriptionRef = useRef<HTMLDivElement | null>(null);
  const [playing, setPlaying] = useState<boolean>(false);

  useEffect(() => {
    return () => {
      window.speechSynthesis.cancel();
    };
  }, []);

  const image =
    data.imageKey !== null
      ? `https://pub-222ffb7a0765466cba73cd4826463187.r2.dev/${data.imageKey}`
      : `${data.imageUrl}`;

  const readDescription = (action: ReadAction) => {
    if (!descriptionRef.current) return;
    const text = descriptionRef.current.textContent;
    if (!text) return;
    const synth = window.speechSynthesis;
    if (action === "read") {
      const utterance = new SpeechSynthesisUtterance(text);
      if (!synth.paused) {
        synth.cancel();
        synth.speak(utterance);
      } else {
        synth.resume();
      }
      setPlaying(true);
    } else if (action === "pause" && synth.speaking) {
      synth.pause();
      setPlaying(false);
    } else if (action === "stop" && synth.speaking) {
      synth.cancel();
      setPlaying(false);
    }
  };

  const handleAskChatbot = () => {
    onOpenChatbot();
    setIsChatbotRequestFromCard(true);
    console.log("clicked open chatbot");
  };

  useEffect(() => {
    return () => {
      setIsChatbotRequestFromCard(false);
    };
  }, []);

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
              <div className="gallery-card-defails-full-view-btns">
                <div className="gallery-card-details-full-view-btns-outloud">
                  <FontAwesomeIcon
                    icon={faVolumeHigh}
                    onClick={() => readDescription("read")}
                  />
                  <FontAwesomeIcon
                    icon={faPause}
                    onClick={() => readDescription("pause")}
                  />
                  <FontAwesomeIcon
                    icon={faStop}
                    onClick={() => readDescription("stop")}
                  />
                </div>
                <div className="gallery-card-details-full-view-btns-gap">|</div>
                <div
                  className="gallery-card-details-full-view-btns-ask-artsy"
                  onClick={handleAskChatbot}
                >
                  <div>ASK ARTSY</div>
                  <div>
                    <FontAwesomeIcon icon={faBrush} />
                  </div>
                </div>
              </div>
              <div ref={descriptionRef} className="gallery-card-description">
                {data.description}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CardGalleryFullView;
