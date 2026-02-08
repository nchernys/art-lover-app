import "./rag.css";
import { useState } from "react";
import ReactMarkdown from "react-markdown";
import { Loader } from "../../components/notifications/loader";

type RagData = {
  question: string;
  response: string;
};

export function Rag() {
  const [loading, setLoading] = useState<boolean>(false);
  const [rag, setRag] = useState<RagData>({ question: "", response: "" });

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/rag/query`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          question: rag?.question,
        }),
      });

      if (!response.ok) throw Error;
      const answer = await response.text();
      setRag((prev) => ({ ...prev, response: answer }));
      setLoading(false);
    } catch (err) {
      setLoading(false);
      console.log(err);
    }
  };

  return (
    <>
      <div className="">{loading && <Loader />} </div>
      <div className="rag-wrapper">
        <form className="rag-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="rag-question">Question: </label>
            <textarea
              rows={5}
              value={rag.question}
              onChange={(e) =>
                setRag((prev) => ({
                  ...prev,
                  question: e.target.value,
                }))
              }
            />
          </div>
          <button>Ask</button>
        </form>
        <div className="rag-response">
          <ReactMarkdown>{rag?.response}</ReactMarkdown>
        </div>
      </div>
    </>
  );
}
