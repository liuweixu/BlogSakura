import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  // 用作路径别名
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  // 解决本地开发的跨域问题
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, ""),
        secure: false,
      },
    },
  },
});
