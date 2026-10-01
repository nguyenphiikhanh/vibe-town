"use client";

import { useEffect, useState, type FormEvent } from "react";
import { TownGame } from "./TownGame";
import { login } from "./farm.api";
import type { FarmUser, LoginResponse } from "./farm.types";

export function FarmApp() {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<FarmUser | null>(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    const savedToken = window.sessionStorage.getItem("avatar-farm-token");
    const savedUser = window.sessionStorage.getItem("avatar-farm-user");
    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser) as FarmUser);
      } catch {
        window.sessionStorage.removeItem("avatar-farm-token");
        window.sessionStorage.removeItem("avatar-farm-user");
      }
    }
  }, []);

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setBusy(true);
    try {
      const result: LoginResponse = await login(username, password);
      window.sessionStorage.setItem("avatar-farm-token", result.token);
      window.sessionStorage.setItem("avatar-farm-user", JSON.stringify(result.user));
      setToken(result.token);
      setUser(result.user);
      setPassword("");
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Đăng nhập thất bại.");
    } finally {
      setBusy(false);
    }
  }

  function handleLogout() {
    window.sessionStorage.removeItem("avatar-farm-token");
    window.sessionStorage.removeItem("avatar-farm-user");
    setToken(null);
    setUser(null);
  }

  if (token && user) return <TownGame onLogout={handleLogout} token={token} user={user} />;

  return (
    <main className="login-page">
      <div aria-hidden="true" className="login-decoration login-decoration--sun" />
      <div aria-hidden="true" className="login-decoration login-decoration--hill" />
      <section className="login-card">
        <div className="login-emblem"><img alt="" src={`${process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001"}/assets/hd/object/831.png`} /></div>
        <p className="eyebrow">MỘT THỊ TRẤN NHỎ, BAO ĐIỀU HAY</p>
        <h1>Phố Nhỏ đang chờ bạn</h1>
        <p className="login-copy">Đăng nhập bằng tài khoản Avatar để mở cửa tiệm đầu tiên và bắt đầu hành trình kinh doanh.</p>
        <form className="login-form" onSubmit={handleLogin}>
          <label htmlFor="username">Tên đăng nhập</label>
          <input autoComplete="username" id="username" maxLength={20} onChange={(event) => setUsername(event.target.value)} required value={username} />
          <label htmlFor="password">Mật khẩu</label>
          <input autoComplete="current-password" id="password" onChange={(event) => setPassword(event.target.value)} required type="password" value={password} />
          {error && <p aria-live="polite" className="form-error">{error}</p>}
          <button className="login-submit" disabled={busy} type="submit">{busy ? "Đang vào thị trấn…" : "Đến Phố Nhỏ"}<span aria-hidden="true">→</span></button>
        </form>
        <p className="login-footnote">Dùng tài khoản đã có trong cơ sở dữ liệu Avatar.</p>
      </section>
      <footer className="login-footer">Mở cửa tiệm đầu tiên • Gặp những người hàng xóm thân quen</footer>
    </main>
  );
}
