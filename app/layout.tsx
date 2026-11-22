import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "CIABOC Management Dashboard",
  description: "CIABOC Integrated Institutional Management Dashboard",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
