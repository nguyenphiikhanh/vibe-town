import type { Metadata } from "next";
import "./styles.css";

export const metadata: Metadata = {
  title: "Phố Nhỏ Avatar",
  description: "Mở cửa tiệm, phục vụ khách và gây dựng con phố của riêng bạn.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="vi">
      <body>{children}</body>
    </html>
  );
}
