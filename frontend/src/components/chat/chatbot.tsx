import "./chatbot.css";
import { useEffect, useState } from "react";
import { API_BASE } from "../../baseUrl";
import type { ArtworkInterface } from "../../../types/artwork";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMinus,
  faCommentDots,
  faPaperPlane,
  faBrush,
} from "@fortawesome/free-solid-svg-icons";

type ChatbotProps = {
  chatbotVisible: boolean;
  onChangeChatbotState: () => void;
  userId: string;
  selectedArtworkId: string | null;
  data: ArtworkInterface;
};

type Role = "user" | "assistant";

type ChatMessage = {
  role: Role;
  content: string;
  userId: string;
  createdAt: Date;
};

export function Chatbot({
  onChangeChatbotState,
  chatbotVisible,
  userId,
  selectedArtworkId,
  data,
}: ChatbotProps) {
  const [userRequest, setUserRequest] = useState<string | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);

  const fetchMessageHistory = async () => {
    try {
      const response = await fetch(`${API_BASE}/api/chat/messages`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Failed to fetch messages.");
      }

      const data = await response.json();
      setMessages(data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchMessageHistory();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setUserRequest(e.target.value);
  };

  const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!userRequest || !userRequest.trim()) return;

    const newMessage = {
      role: "user",
      content: userRequest,
      userId: userId,
      createdAt: new Date(),
    };
    try {
      const response = await fetch(`${API_BASE}/api/chat/save`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },

        body: JSON.stringify(newMessage),
      });

      if (!response.ok) {
        throw new Error("Failed to send new message.");
      }
      await fetchMessageHistory();
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <div className="chatbot-wrapper">
      {chatbotVisible && (
        <div className="chatbot-window">
          <div className="chatbot-window-header">
            <div>
              Ask Artsy
              <FontAwesomeIcon icon={faBrush} className="header-icon" />
            </div>
            <FontAwesomeIcon icon={faMinus} onClick={onChangeChatbotState} />
          </div>
          <div className="chatbot-window-chat">
            {selectedArtworkId ? (
              <div className="chat-assistant">
                What would you like to learn about? <br />
                <button>{data.artist}</button> <button>{data.title}</button>
                <button>{data.movement}</button>
                <button>Something else</button>
              </div>
            ) : (
              <div className="chat-assistant">
                What would you like to learn about art?
              </div>
            )}
            <div className="chat-user"></div>
          </div>
          <div className="chatbot-warning">
            This conversation stores up to 50 messages. When the limit is
            reached, older messages are deleted automatically.
          </div>
          <div className="chatbot-window-input">
            <form onSubmit={handleSubmitForm}>
              <div className="text-wrapper">
                <textarea
                  placeholder="Start typing ... "
                  onChange={handleChange}
                ></textarea>
              </div>
              <button>
                <FontAwesomeIcon icon={faPaperPlane} />
              </button>
            </form>
          </div>
        </div>
      )}
      <div className="chatbot-icon" onClick={onChangeChatbotState}>
        <FontAwesomeIcon icon={faCommentDots} />
      </div>
    </div>
  );
}
