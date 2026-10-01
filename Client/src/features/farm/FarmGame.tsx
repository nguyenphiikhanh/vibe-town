"use client";

import { useEffect, useMemo, useState } from "react";
import type { FarmUser } from "./farm.types";
import { FarmBoard } from "./FarmBoard";
import { FarmSprite } from "./FarmSprite";
import { useFarmRoom } from "./useFarmRoom";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001";

type FarmGameProps = {
  token: string;
  user: FarmUser;
  onLogout: () => void;
};

export function FarmGame({ token, user, onLogout }: FarmGameProps) {
  const { snapshot, connection, error, plant, harvest } = useFarmRoom(token, user.id);
  const [selectedCropId, setSelectedCropId] = useState("watermelon");
  const [now, setNow] = useState(Date.now());
  const [notice, setNotice] = useState<string | null>(null);
  const inventoryByCrop = useMemo(
    () => new Map(snapshot?.inventory.map((item) => [item.cropId, item]) ?? []),
    [snapshot],
  );

  useEffect(() => {
    const interval = window.setInterval(() => setNow(Date.now()), 1000);
    return () => window.clearInterval(interval);
  }, []);

  function handlePlotClick(plot: NonNullable<typeof snapshot>["plots"][number]) {
    if (plot.phase === "empty") {
      const seedCount = inventoryByCrop.get(selectedCropId)?.seeds ?? 0;
      if (seedCount < 1) {
        setNotice("Bạn đã dùng hết hạt giống này.");
        return;
      }
      plant(plot.index, selectedCropId);
      setNotice("Đã gieo hạt. Cây sẽ lớn theo thời gian.");
      return;
    }

    if (plot.readyAt && now >= plot.readyAt) {
      harvest(plot.index);
      setNotice("Đang thu hoạch...");
    }
  }

  return (
    <main className="farm-page">
      <header className="farm-topbar">
        <a aria-label="Nông trại Avatar" className="brand-mark" href="/">
          <span className="brand-leaf">✿</span>
          <span>Avatar <b>Nông trại</b></span>
        </a>
        <div className="topbar-user">
          <span className="online-dot" />
          <span>{user.username}</span>
          <button className="quiet-button" onClick={onLogout} type="button">Thoát</button>
        </div>
      </header>

      <section className="farm-content">
        <div className="farm-heading">
          <div>
            <p className="eyebrow">KHU VƯỜN CỦA BẠN</p>
            <h1>Chăm vườn mỗi ngày</h1>
            <p className="farm-subtitle">Gieo hạt, chờ cây lớn, rồi thu hoạch mùa màng.</p>
          </div>
          <div className="weather-pill"><span aria-hidden="true">☀</span><span>Nắng nhẹ</span><i>•</i><span>Ngày mới</span></div>
        </div>

        <div className="farm-layout">
          <section aria-label="Bản đồ nông trại" className="scene-shell">
            <div className="scene-header">
              <div><span className="scene-icon">⌂</span><span>Vườn nhà</span></div>
              <span className={`connection-status connection-status--${connection}`}>
                <i />{connection === "connected" ? "Đang trực tuyến" : connection === "connecting" ? "Đang vào vườn" : connection === "error" ? "Mất kết nối" : "Đang chờ"}
              </span>
            </div>
            <div className="meadow">
              <div aria-hidden="true" className="farm-decoration farm-decoration--left" style={{ backgroundImage: `url("${apiUrl}/assets/hd/farm/42.png")` }} />
              <div aria-hidden="true" className="farm-decoration farm-decoration--right" style={{ backgroundImage: `url("${apiUrl}/assets/hd/farm/45.png")` }} />
              {snapshot ? (
                <FarmBoard
                  crops={snapshot.crops}
                  now={now}
                  onPlotClick={handlePlotClick}
                  plots={snapshot.plots}
                  selectedCropId={selectedCropId}
                />
              ) : (
                <div aria-live="polite" className="farm-loading">
                  <span className="loading-seed" />
                  <span>{connection === "error" ? "Không tải được nông trại" : "Đang chuẩn bị khu vườn…"}</span>
                </div>
              )}
            </div>
            <div className="scene-footer">
              <span>Chọn hạt giống, sau đó bấm vào ô đất trống.</span>
              <span>{snapshot?.plots.filter((plot) => plot.phase === "ready" || (plot.readyAt && now >= plot.readyAt)).length ?? 0} cây sẵn sàng</span>
            </div>
          </section>

          <aside className="farm-sidebar">
            <section className="side-panel crop-panel">
              <div className="panel-heading">
                <div><p className="eyebrow">KHO HẠT GIỐNG</p><h2>Chọn cây trồng</h2></div>
                <span className="seed-bag">✳</span>
              </div>
              <div className="crop-list">
                {snapshot?.crops.map((crop) => {
                  const inventory = inventoryByCrop.get(crop.id);
                  return (
                    <button
                      aria-pressed={selectedCropId === crop.id}
                      className={`crop-choice${selectedCropId === crop.id ? " crop-choice--selected" : ""}`}
                      disabled={!inventory || inventory.seeds < 1}
                      key={crop.id}
                      onClick={() => setSelectedCropId(crop.id)}
                      type="button"
                    >
                      <span className="crop-choice-mark"><FarmSprite compact crop={crop} phase="ready" /></span>
                      <span className="crop-choice-copy"><b>{crop.name}</b><small>Lớn trong {crop.growthSeconds} giây</small></span>
                      <span className="crop-count">×{inventory?.seeds ?? 0}</span>
                    </button>
                  );
                }) ?? <p className="muted-copy">Đang tải hạt giống…</p>}
              </div>
              <p className="helper-note">Hạt giống ban đầu đã có sẵn trong kho.</p>
            </section>

            <section className="side-panel harvest-panel">
              <div className="panel-heading"><div><p className="eyebrow">MÙA VỤ</p><h2>Đã thu hoạch</h2></div><span className="harvest-total">{snapshot?.inventory.reduce((sum, item) => sum + item.harvested, 0) ?? 0}</span></div>
              <div className="harvest-list">
                {snapshot?.crops.map((crop) => {
                  const count = inventoryByCrop.get(crop.id)?.harvested ?? 0;
                  return <div className="harvest-row" key={crop.id}><span>{crop.name}</span><b>{count}</b></div>;
                }) ?? <p className="muted-copy">Chưa có mùa vụ.</p>}
              </div>
            </section>

            <div className="farm-tip"><span aria-hidden="true">✦</span><p><b>Mẹo nhỏ</b><br />Cây lớn ngay cả khi bạn rời khỏi vườn. Quay lại khi biểu tượng thu hoạch xuất hiện nhé.</p></div>
          </aside>
        </div>

        {(error || notice) && <div aria-live="polite" className={`farm-toast${error ? " farm-toast--error" : ""}`}>{error ?? notice}<button aria-label="Đóng thông báo" onClick={() => setNotice(null)} type="button">×</button></div>}
      </section>
      <footer className="farm-footer">Nông trại Avatar <span>•</span> Một khu vườn nhỏ, một ngày vui</footer>
    </main>
  );
}
