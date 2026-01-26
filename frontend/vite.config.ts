import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // allow network access
    strictPort: true, // optional, keeps port fixed
    proxy: {
      "/api": {
        target: "http://3.236.116.165:8080",
        changeOrigin: true,
      },
    },
    allowedHosts: ["http://localhost:5173"],
  },
});
