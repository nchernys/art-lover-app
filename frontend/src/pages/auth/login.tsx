import "./login.css";
import { useState } from "react";
import type { User } from "../../types/user";
import { useNavigate } from "react-router-dom";
import { API_BASE } from "../../baseUrl";
import { apiFetch } from "../../utils/apiFetch";

type LoginProps = {
  onLogin: () => void;
};

export function Login({ onLogin }: LoginProps) {
  const navigate = useNavigate();
  const [userCreds, setUserCreds] = useState<User>({
    email: "",
    password: "",
  });
  const [errorMessage, setErrorMessage] = useState<string>("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setErrorMessage("");

    setUserCreds((prev) => {
      return {
        ...prev,
        [name]: value,
      };
    });
  };

  const handleSubmit = async (
    e: React.FormEvent<HTMLFormElement>,
  ): Promise<void> => {
    e.preventDefault();
    const response: Response = await apiFetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userCreds),
    });
    if (!response.ok) {
      setErrorMessage("Login failed. Please try again.");
      throw new Error("Login failed");
    }
    navigate("/");
    onLogin();
  };

  return (
    <div className="login-form">
      <form onSubmit={handleSubmit}>
        <div className="login-form-header">Login</div>
        <div className="login-error">{errorMessage}</div>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="text"
            name="email"
            value={userCreds.email}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="text"
            name="password"
            value={userCreds.password}
            onChange={handleChange}
          />
        </div>
        <button>Submit</button>
      </form>
      <div className="login-signup-message">
        Don't have an account? <a href="/sign-up">Sign Up</a>
      </div>
    </div>
  );
}
