import { useState, useEffect } from "react";

interface BlurImageProps {
  src: string;
  alt: string;
}

function ImageStyles() {
  return (
    <style>
      {`
        @keyframes imageLoading {
          from {
            width: 0;
          }
          to {
            width: 100%;
          }
        }
      `}
    </style>
  );
}

export function BlurImage({ src, alt, ...props }: BlurImageProps) {
  const [readySrc, setReadySrc] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const img = new Image();
    img.src = src;

    img
      .decode()
      .then(() => {
        if (!cancelled) setReadySrc(src);
      })
      .catch(() => {
        if (!cancelled) setReadySrc(src); // fallback
      });

    return () => {
      cancelled = true;
    };
  }, [src]);

  return (
    <div
      style={{
        position: "relative",
        height: "100%",
        width: "100%",
        backgroundColor: "rgb(176, 193, 201)",
      }}
    >
      <ImageStyles />
      <div
        style={{
          position: "absolute",
          inset: 0,
          background: "rgb(220, 220, 220)",
          filter: "blur(40px)",
          opacity: readySrc ? 0 : 1,
          transition: "opacity 400ms ease",
          animation: "imageLoading 1.5s infinite ease",
        }}
      />
      {readySrc && (
        <img
          src={src}
          alt={alt}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "cover",
          }}
          {...props}
        />
      )}
    </div>
  );
}
