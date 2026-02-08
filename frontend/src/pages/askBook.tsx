import { Rag } from "../components/chat/rag";

export function AskBook() {
  return (
    <>
      <div className="ask-book-header">Ask The Book</div>
      <div className="ask-book-content">
        <Rag />
      </div>
    </>
  );
}
