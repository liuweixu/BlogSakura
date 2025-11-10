import { Routes, Route, useLocation } from "react-router-dom";
import { lazy } from "react";
import { Suspense } from "react";

// 懒加载组件
const Home = lazy(() => import("@/ui-frontend/pages/home"));
const Article = lazy(() => import("@/ui-frontend/pages/article"));
const Header = lazy(() => import("@/ui-frontend/pages/header"));
const Error = lazy(() => import("@/components/error"));
const Pictures = lazy(() => import("@/ui-frontend/pages/pictures"));

export const RouterFrontend = () => {
  const location = useLocation();

  // 只有在匹配已知路由时才显示Header
  const showHeader = ["/", "/test", "/article/"].some(
    (path) =>
      location.pathname === path ||
      (path === "/article/" && location.pathname.startsWith(path))
  );

  return (
    <>
      {showHeader && <Header />}
      <Routes>
        <Route
          path="/"
          element={
            <Suspense fallback={"加载中"}>
              <Home />
            </Suspense>
          }
        />
        <Route
          path="/article/:id"
          element={
            <Suspense fallback={"加载中"}>
              <Article />
            </Suspense>
          }
        />
        <Route
          path="/pictures"
          element={
            <Suspense fallback={"加载中"}>
              <Pictures />
            </Suspense>
          }
        />
        <Route
          path="*"
          element={
            <Suspense fallback={"加载中"}>
              <Error />
            </Suspense>
          }
        />
      </Routes>
    </>
  );
};
