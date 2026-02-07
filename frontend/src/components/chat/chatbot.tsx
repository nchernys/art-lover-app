import "./chatbot.css";
import { useEffect, useState, useRef } from "react";
import { API_BASE } from "../../baseUrl";
import type { ArtworkInterface } from "../../types/artwork";
import ReactMarkdown from "react-markdown";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMinus,
  faCommentDots,
  faPaperPlane,
  faBrush,
  faDownload,
  faRotate,
} from "@fortawesome/free-solid-svg-icons";

type ChatbotProps = {
  chatbotVisible: boolean;
  onChangeChatbotState: () => void;
  userId: string;
  isChatbotRequestFromCard: boolean;
  setIsChatbotRequestFromCard: React.Dispatch<React.SetStateAction<boolean>>;
  data: ArtworkInterface | null;
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
  data,
  isChatbotRequestFromCard,
  setIsChatbotRequestFromCard,
}: ChatbotProps) {
  const messagesEndRef = useRef<HTMLDivElement | null>(null);
  const [userRequest, setUserRequest] = useState<string>("");
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [tempRequest, setTempRequest] = useState<ArtworkInterface | null>(null);

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
    if (chatbotVisible) {
      fetchMessageHistory();
    }
  }, [chatbotVisible]);

  useEffect(() => {
    if (!data) return;
    if (!isChatbotRequestFromCard) return;
    if (data.id !== tempRequest?.id) {
      setTempRequest(data);
    }
  }, [data, isChatbotRequestFromCard, tempRequest]);

  useEffect(() => {
    if (!chatbotVisible) {
      setTempRequest(null);
      setUserRequest("");
    }
  }, [chatbotVisible]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, tempRequest, chatbotVisible]);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setUserRequest(e.target.value);
  };

  const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!userRequest || !userRequest.trim()) return;

    const newMessage = {
      role: "user",
      content: userRequest.trim(),
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
      setUserRequest("");
    } catch (err) {
      console.log(err);
    }
  };

  const downloadConversation = () => {
    const content = messages
      .map((m) => `${m.role.toUpperCase()}: ${m.content}`)
      .join("\n\n");

    const blob = new Blob([content], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = "Conversation with Artsy.txt";
    a.click();

    URL.revokeObjectURL(url);
  };

  const deleteAllMessages = async () => {
    try {
      const response = await fetch(`${API_BASE}/api/chat/delete-all-messages`, {
        method: "DELETE",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Failed to delte messages.");
      }
      await fetchMessageHistory();
      setUserRequest("");
    } catch (err) {
      console.log(err);
    }
  };

  const hasMessages = messages.length > 0;
  const conversationLimitReached = messages.length >= 50;

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
            <div className="chatbot-window-chat-inner-wrapper">
              {hasMessages &&
                messages.map((m, idx) =>
                  m.role === "user" ? (
                    <div key={`user-index-${idx}`} className="chat-user">
                      {m.content}
                    </div>
                  ) : (
                    <div
                      key={`assistant-index-${idx}`}
                      className="chat-assistant"
                    >
                      <ReactMarkdown>{m.content}</ReactMarkdown>
                    </div>
                  ),
                )}

              {(!hasMessages || tempRequest) && (
                <>
                  {tempRequest ? (
                    <div key={tempRequest.id} className="chat-assistant">
                      What would you like to learn about? <br />
                      <button
                        onClick={() =>
                          setUserRequest(
                            `${userRequest} Tell me more about ${tempRequest.artist}.`,
                          )
                        }
                      >
                        {tempRequest.artist}
                      </button>
                      <button
                        onClick={() =>
                          setUserRequest(
                            `${userRequest} Tell me more about ${tempRequest.title}.`,
                          )
                        }
                      >
                        {tempRequest.title}
                      </button>
                      <button
                        onClick={() =>
                          setUserRequest(
                            `${userRequest} Tell me more about ${tempRequest.movement}.`,
                          )
                        }
                      >
                        {tempRequest.movement}
                      </button>
                      <button
                        onClick={() =>
                          setUserRequest(
                            `${userRequest} Tell me a fun fact about ${tempRequest.artist} or ${tempRequest.title} or ${tempRequest.movement}.`,
                          )
                        }
                      >
                        Fun Fact
                      </button>
                    </div>
                  ) : (
                    <div className="chat-assistant">
                      What would you like to learn about art today?
                    </div>
                  )}
                </>
              )}

              <div ref={messagesEndRef} />
            </div>
          </div>
          {conversationLimitReached && (
            <div className="chatbot-limit-message">
              <div>You’ve reached the 100-message limit. </div>
              <div className="chatbot-limit-group">
                <button onClick={downloadConversation}>
                  <FontAwesomeIcon icon={faDownload} /> <br />
                  Download conversation
                </button>
                <button onClick={deleteAllMessages}>
                  <FontAwesomeIcon icon={faRotate} /> <br />
                  Delete all message & start a new chat
                </button>
              </div>
            </div>
          )}
          <div className="chatbot-warning">
            <div className="chatbot-warning-message">
              This conversation stores up to 100 messages. When the limit is
              reached, older messages are deleted automatically.
            </div>
            <button
              className="chatbot-download-btn"
              onClick={downloadConversation}
            >
              <FontAwesomeIcon icon={faDownload} /> Download conversation
            </button>
          </div>
          <div className="chatbot-window-input">
            <form onSubmit={handleSubmitForm}>
              <div className="text-wrapper">
                <textarea
                  placeholder="Start typing ... "
                  value={userRequest}
                  onChange={handleChange}
                ></textarea>
              </div>
              <button disabled={conversationLimitReached}>
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
