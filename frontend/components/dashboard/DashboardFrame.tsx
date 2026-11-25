"use client";

import { useEffect, useRef } from "react";

const apiBase = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function sendReportConfig(frame: HTMLIFrameElement | null) {
  frame?.contentWindow?.postMessage(
    { type: "ciaboc:vmr-api", apiBase },
    window.location.origin,
  );
}

export default function DashboardFrame() {
  const frameRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    function onFrameReady(event: MessageEvent) {
      const frame = frameRef.current;
      if (frame && event.origin === window.location.origin &&
          event.source === frame.contentWindow && event.data?.type === "ciaboc:vmr-ready") {
        sendReportConfig(frame);
      }
    }

    window.addEventListener("message", onFrameReady);
    sendReportConfig(frameRef.current);
    return () => window.removeEventListener("message", onFrameReady);
  }, []);

  return (
    <iframe
      ref={frameRef}
      title="CIABOC Management Dashboard wireframe"
      src="/prototype.html#authenticated"
      className="block h-screen w-full border-0"
      onLoad={(event) => sendReportConfig(event.currentTarget)}
    />
  );
}
