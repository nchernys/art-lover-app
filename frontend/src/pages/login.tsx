import "./login.css";
import { useState } from "react";
import type { User, LoginResponse } from "../types/user";

export function Login() {
  const [userCreds, setUserCreds] = useState<User>({
    username: "",
    password: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setUserCreds((prev) => {
      return {
        ...prev,
        [name]: value,
      };
    });
  };

  const handleSubmit = async (
    e: React.FormEvent<HTMLFormElement>
  ): Promise<void> => {
    e.preventDefault();
    const response: Response = await fetch("http://localhost:8080/api/login", {
      method: "POST",
      body: JSON.stringify(userCreds),
    });
    if (!response.ok) {
      throw new Error("Upload failed");
    }

    const result: LoginResponse = await response.json();
    console.log(result);
  };

  return (
    <div className="login-form">
      <form onSubmit={handleSubmit}>
        <div className="login-form-header">Login</div>
        <div className="form-group">
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            name="username"
            value={userCreds.username}
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
    </div>
  );
}
