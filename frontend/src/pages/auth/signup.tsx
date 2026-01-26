import "./signup.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { User } from "../../types/user";

export function Signup() {
  const navigate = useNavigate();
  const API_BASE = import.meta.env.VITE_API_BASE;
  const [userCreds, setUserCreds] = useState<User>({
    email: "",
    password: "",
  });
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [showPasswordErrorMessage, setShowPasswordErrorMessage] =
    useState<boolean>(false);
  const [passwordMatchError, setPasswordMatchError] = useState<boolean>(false);
  const [errorSubmittingForm, setErrorSubmittingForm] =
    useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");

  function validatePassword(pass: string) {
    const regex =
      /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=.])[A-Za-z\d!@#$%^&*()_+\-=.]{8,}$/;

    return regex.test(pass);
  }

  function testMatch(pass1: string, pass2: string) {
    const testMatch = pass1 === pass2;
    if (!testMatch) {
      setPasswordMatchError(true);
    } else {
      setPasswordMatchError(false);
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name === "password") {
      const test = validatePassword(value);
      if (!test) {
        setShowPasswordErrorMessage(true);
      } else {
        setShowPasswordErrorMessage(false);
      }
      testMatch(confirmPassword, value);
    }

    if (name === "confirm-password") {
      setConfirmPassword(value);
      testMatch(userCreds.password, value);
    }

    if (name === "email" || name === "password") {
      setUserCreds((prev) => {
        return {
          ...prev,
          [name]: value,
        };
      });
    }
  };

  const handleSubmit = async (
    e: React.FormEvent<HTMLFormElement>,
  ): Promise<void> => {
    e.preventDefault();
    if (passwordMatchError || showPasswordErrorMessage) {
      setErrorSubmittingForm(true);
      return;
    }

    const response = await fetch(`${API_BASE}/api/auth/signup`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userCreds),
    });
    if (!response.ok) {
      const error = await response.json();

      if (response.status === 409) {
        setErrorSubmittingForm(true);
        setErrorMessage(error.message); // "Email already exists"
        return;
      }

      throw new Error("Sign Up failed.");
    }

    navigate("/login");
  };

  return (
    <div className="login-form">
      <form onSubmit={handleSubmit}>
        <div className="login-form-header">Register</div>
        {errorSubmittingForm && (
          <div className="error-message">
            {errorMessage !== ""
              ? errorMessage
              : "Error submitting the form. Please try again..."}
          </div>
        )}
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            name="email"
            value={userCreds.email}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="text"
            name="password"
            value={userCreds.password}
            onChange={handleChange}
          />
          {showPasswordErrorMessage && (
            <div className="error-message">
              <div> Password must contain</div>
              <div>
                at least 8 characters, <br />
                at least one uppercase, <br />
                at least one digit, <br />
                and at least one special character (!@#$%^&*()_+-=.)
              </div>
            </div>
          )}
        </div>
        <div className="form-group">
          <label htmlFor="password">Confirm Password</label>
          <input
            type="text"
            name="confirm-password"
            value={confirmPassword}
            onChange={handleChange}
          />
          {passwordMatchError && (
            <div className="error-message">Passwords must match!</div>
          )}
        </div>
        <button>Submit</button>
      </form>
      <div className="login-signup-message">
        Already have an account? <a href="/login">Log in</a>
      </div>
    </div>
  );
}
