"use client";

export default function ErrorPage({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <main className="ct">
      <section className="lc" role="alert">
        <h2>Dashboard unavailable</h2>
        <p className="sub">{error.message || "An unexpected application error occurred."}</p>
        <button className="btn" type="button" onClick={reset}>
          Retry
        </button>
      </section>
    </main>
  );
}
