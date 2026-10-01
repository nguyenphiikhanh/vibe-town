"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import type { FarmUser } from "./farm.types";
import type { TownCatalogResponse, TownSnapshot } from "./town.types";
import { FarmSprite } from "./FarmSprite";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001";
const money = new Intl.NumberFormat("vi-VN");

type TownGameProps = { token: string; user: FarmUser; onLogout: () => void };

export function TownGame({ token, user, onLogout }: TownGameProps) {
  const [town, setTown] = useState<TownSnapshot | null>(null);
  const [cropCatalog, setCropCatalog] = useState<TownCatalogResponse["crops"]>([]);
  const [selectedShopId, setSelectedShopId] = useState("grocery");
  const [selectedProductId, setSelectedProductId] = useState("watermelon");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const loadTown = useCallback(async () => {
    const [townResponse, catalogResponse] = await Promise.all([
      fetch(`${apiUrl}/town/state`, { headers: { authorization: `Bearer ${token}` } }),
      fetch(`${apiUrl}/farm/catalog`),
    ]);
    const townPayload = (await townResponse.json()) as TownSnapshot | { error?: string };
    const catalogPayload = (await catalogResponse.json()) as TownCatalogResponse;
    if (!townResponse.ok || !("economy" in townPayload)) {
      throw new Error("error" in townPayload ? townPayload.error ?? "Không tải được thị trấn." : "Không tải được thị trấn.");
    }
    setTown(townPayload);
    setCropCatalog(catalogPayload.crops ?? []);
  }, [token]);

  useEffect(() => {
    let active = true;
    void loadTown().catch((cause: unknown) => {
      if (active) setError(cause instanceof Error ? cause.message : "Không tải được thị trấn.");
    });
    return () => { active = false; };
  }, [loadTown]);

  const selectedShop = town?.shops.find((shop) => shop.id === selectedShopId) ?? town?.shops[0];
  const selectedProduct = town?.products.find((product) => product.id === selectedProductId) ?? town?.products[0];
  const cropById = useMemo(() => new Map(cropCatalog.map((crop) => [crop.id, crop])), [cropCatalog]);

  async function act(path: "restock" | "serve" | "unlock", payload: Record<string, string | number>) {
    setBusy(true);
    setError(null);
    setNotice(null);
    try {
      const response = await fetch(`${apiUrl}/town/${path}`, {
        method: "POST",
        headers: { authorization: `Bearer ${token}`, "content-type": "application/json" },
        body: JSON.stringify(payload),
      });
      const result = await response.json() as TownSnapshot | { snapshot: TownSnapshot; earned: number } | { error?: string };
      if (!response.ok) throw new Error("error" in result ? result.error ?? "Thao tác không thành công." : "Thao tác không thành công.");
      if (path === "serve" && "snapshot" in result) {
        setTown(result.snapshot);
        setNotice(`Khách đã mua hàng • +${money.format(result.earned)} xu`);
      } else if ("economy" in result) {
        setTown(result);
        setNotice(path === "restock" ? "Đã nhập thêm hàng cho cửa tiệm." : "Cửa hàng mới đã mở cửa!");
      }
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Thao tác không thành công.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="town-page">
      <header className="town-topbar">
        <a className="town-brand" href="/" aria-label="Phố Nhỏ Avatar">
          <span className="town-brand-icon">P</span>
          <span>PHỐ NHỎ <b>AVATAR</b></span>
        </a>
        <div className="town-topbar-right">
          <div className="town-wallet"><span>Xu thị trấn</span><b>{town ? money.format(town.economy.coins) : "…"}</b></div>
          <span className="town-user"><i />{user.username}</span>
          <button className="town-logout" onClick={onLogout} type="button">Thoát</button>
        </div>
      </header>

      <section className="town-main">
        <div className="town-intro">
          <div>
            <p className="town-kicker">THỊ TRẤN ĐANG THỨC GIẤC</p>
            <h1>Ngày mới ở <em>Phố Nhỏ</em></h1>
            <p>Gây dựng cửa tiệm, phục vụ bà con và mở rộng con phố của bạn.</p>
          </div>
          <div className="town-day"><span className="day-sun">☼</span><span><b>Ngày 1</b><small>Trời nắng đẹp</small></span></div>
        </div>

        <div className="town-stats">
          <div className="town-stat town-stat--cash"><span className="stat-icon">Xu</span><span><small>VỐN KINH DOANH</small><b>{town ? money.format(town.economy.coins) : "—"} <i>xu</i></b></span></div>
          <div className="town-stat"><span className="stat-icon stat-icon--heart">♥</span><span><small>THIỆN CẢM</small><b>{town?.economy.reputation ?? 0} <i>điểm</i></b></span></div>
          <div className="town-stat"><span className="stat-icon stat-icon--sales">↗</span><span><small>ĐƠN ĐÃ BÁN</small><b>{town?.economy.totalSales ?? 0} <i>món</i></b></span></div>
          <div className="town-goal"><span className="goal-star">✦</span><span><small>MỤC TIÊU HÔM NAY</small><b>Phục vụ 5 vị khách</b></span><span className="goal-progress">{Math.min(5, town?.economy.totalSales ?? 0)}/5</span></div>
        </div>

        <div className="town-layout">
          <section className="town-map-panel">
            <div className="town-map-heading">
              <div><span className="map-pin">⌖</span><span><b>Phố Mộc Miên</b><small>Chạm vào cửa tiệm để quản lý</small></span></div>
              <span className="town-weather"><i />Thị trấn yên bình</span>
            </div>
            <div className="town-map">
              <div className="town-map-cloud town-map-cloud--one" />
              <div className="town-map-cloud town-map-cloud--two" />
              <div className="town-map-hill town-map-hill--back" />
              <div className="town-map-hill town-map-hill--front" />
              <div className="town-road town-road--horizontal" />
              <div className="town-road town-road--vertical" />
              <div className="town-crossing" />
              <div className="town-plaza"><span>QUẢNG TRƯỜNG</span><b>✿</b></div>
              <div aria-hidden="true" className="town-water" />
              {[0, 1, 2, 3, 4, 5, 6].map((tree, index) => <span aria-hidden="true" className={`town-tree town-tree--${tree + 1}`} key={index} />)}
              {town?.shops.map((shop, index) => (
                <button
                  aria-label={`${shop.name}${shop.unlocked ? "" : `, mở với ${money.format(shop.price)} xu`}`}
                  aria-pressed={selectedShopId === shop.id}
                  className={`town-building town-building--${shop.id}${selectedShopId === shop.id ? " town-building--selected" : ""}${shop.unlocked ? "" : " town-building--locked"}`}
                  key={shop.id}
                  onClick={() => {
                    setSelectedShopId(shop.id);
                    if (shop.unlocked) setError(null);
                  }}
                  style={{ "--building-index": index } as React.CSSProperties}
                  type="button"
                >
                  <span className="building-art"><img alt="" draggable="false" src={`${apiUrl}/assets/hd/object/${shop.objectId}.png`} /></span>
                  <span className="building-sign">{shop.name}</span>
                  {shop.unlocked ? <span className="building-level">Cấp {shop.level}</span> : <span className="building-lock">{money.format(shop.price)} xu</span>}
                </button>
              ))}
              <div className="town-townhall"><img alt="" src={`${apiUrl}/assets/hd/object/1032.png`} /><span>NHÀ VĂN HÓA</span></div>
              <div className="town-map-sign"><span>BIỂN CHỈ ĐƯỜNG</span><b>✦</b></div>
              <div className="town-map-people"><span>●</span><span>●</span><span>●</span><b>Đang dạo phố</b></div>
              <div className="town-map-caption"><span>✿</span> Một góc phố đang chờ bạn làm nên chuyện</div>
            </div>
            <div className="town-map-footer"><span><i className="map-legend map-legend--open" />Đang kinh doanh</span><span><i className="map-legend map-legend--locked" />Sắp mở cửa</span><b>Phố Nhỏ • 08:00 sáng</b></div>
          </section>

          <aside className="shop-management">
            {selectedShop?.unlocked ? (
              <>
                <div className="shop-title-row">
                  <div className="shop-title-art"><img alt="" src={`${apiUrl}/assets/hd/object/${selectedShop.objectId}.png`} /></div>
                  <div><p className="town-kicker">CỬA TIỆM CỦA BẠN</p><h2>{selectedShop.name}</h2><span className="shop-open-label"><i />Đang mở cửa</span></div>
                </div>
                <div className="shop-customer"><span className="customer-avatar">{town?.economy.totalSales ? "♥" : "…"}</span><span><b>{town?.economy.totalSales ? "Khách quen ghé thăm" : "Khách đầu tiên sắp tới"}</b><small>Chọn một món để phục vụ vị khách tiếp theo.</small></span><span className="customer-bubble">Xin chào!</span></div>
                <div className="stock-heading"><span><b>Quầy hàng</b><small>Hàng hóa đang có trong tiệm</small></span><button className="restock-all" disabled={busy || !selectedProduct} onClick={() => selectedProduct && void act("restock", { productId: selectedProduct.id, quantity: 3 })} type="button">+ Nhập 3</button></div>
                <div className="town-product-list">
                  {town?.products.map((product) => {
                    const crop = cropById.get(product.id);
                    return <button aria-pressed={selectedProductId === product.id} className={`town-product${selectedProductId === product.id ? " town-product--selected" : ""}`} key={product.id} onClick={() => setSelectedProductId(product.id)} type="button">
                      <span className="product-art">{crop && <FarmSprite compact crop={crop} phase="ready" />}</span>
                      <span className="product-name"><b>{product.name}</b><small>Bán {money.format(product.sellPrice)} xu</small></span>
                      <span className="product-quantity">×{product.stock}</span>
                    </button>;
                  })}
                </div>
                <button className="serve-customer" disabled={busy || !selectedProduct} onClick={() => selectedProduct && void act("serve", { productId: selectedProduct.id })} type="button">
                  <span>Phục vụ khách</span><b>+{selectedProduct ? money.format(selectedProduct.sellPrice) : 0} xu</b>
                </button>
                <p className="shop-tip">Mua thêm hàng bằng nút <b>+ Nhập 3</b>. Mỗi lượt bán sẽ tăng thiện cảm cho cửa tiệm.</p>
              </>
            ) : (
              <div className="locked-shop-panel">
                <div className="locked-shop-art"><img alt="" src={`${apiUrl}/assets/hd/object/${selectedShop?.objectId ?? 833}.png`} /></div>
                <p className="town-kicker">CƠ HỘI KINH DOANH MỚI</p>
                <h2>{selectedShop?.name}</h2>
                <p>Mở thêm một điểm đến mới để con phố nhộn nhịp hơn và thu hút thêm nhiều khách.</p>
                <div className="unlock-price"><small>CHI PHÍ MỞ TIỆM</small><b>◉ {money.format(selectedShop?.price ?? 0)} xu</b></div>
                <button className="unlock-shop" disabled={busy} onClick={() => selectedShop && void act("unlock", { shopId: selectedShop.id })} type="button">Mở cửa hàng <span>→</span></button>
              </div>
            )}
          </aside>
        </div>

        <section className="town-business-strip">
          <div><p className="town-kicker">HÀNH TRÌNH LẬP NGHIỆP</p><h2>Con phố của bạn</h2></div>
          <div className="business-steps">
            <span className="business-step business-step--done"><b>01</b><i>✓</i><small>Mở tiệm đầu tiên</small></span><span className="step-line" />
            <span className={`business-step${(town?.economy.totalSales ?? 0) >= 5 ? " business-step--done" : " business-step--current"}`}><b>02</b><i>{(town?.economy.totalSales ?? 0) >= 5 ? "✓" : "2"}</i><small>Phục vụ 5 khách</small></span><span className="step-line" />
            <span className="business-step"><b>03</b><i>3</i><small>Mở tiệm thứ hai</small></span>
          </div>
          <div className="town-note"><span>✦</span><p><b>Mẹo của bác Tư</b><small>Bán hàng để gom xu, rồi ghé mặt bằng trống mở thêm tiệm mới nhé.</small></p></div>
        </section>
      </section>

      {(error || notice) && <div aria-live="polite" className={`town-toast${error ? " town-toast--error" : ""}`}>{error ?? notice}<button aria-label="Đóng thông báo" onClick={() => { setError(null); setNotice(null); }} type="button">×</button></div>}
      <footer className="town-footer">PHỐ NHỎ AVATAR <span>•</span> Mỗi cửa tiệm là một câu chuyện</footer>
    </main>
  );
}
