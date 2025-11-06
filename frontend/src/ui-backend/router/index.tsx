import { Routes, Route } from "react-router-dom";
import { lazy } from "react";
import { Suspense } from "react";

// 懒加载模块
const Error = lazy(() => import("@/components/error"));
const Login = lazy(() => import("@/ui-backend/pages/Login"));
const Layout = lazy(() => import("@/ui-backend/pages/Layout"));
const Home = lazy(() => import("@/ui-backend/pages/Home"));
const ArticleList = lazy(() => import("@/ui-backend/pages/ArticleList"));
const Publish = lazy(() => import("@/ui-backend/pages/Publish"));
const UserManager = lazy(() => import("@/ui-backend/pages/UserManager"));
const ChannelList = lazy(() => import("@/ui-backend/pages/Channel"));
const Logging = lazy(() => import("@/ui-backend/pages/Logging"));

export const RouterBackend = () => {
  return (
    <Routes>
      <Route
        path="/backend/login"
        element={
          <Suspense fallback={"加载中"}>
            <Login />
          </Suspense>
        }
      />
      <Route
        path="/backend/"
        element={
          <Suspense fallback={"加载中"}>
            <Layout />
          </Suspense>
        }
      >
        <Route
          path=""
          element={
            <Suspense fallback={"加载中"}>
              <Home />
            </Suspense>
          }
        />
        <Route
          path="publish"
          element={
            <Suspense fallback={"加载中"}>
              <Publish />
            </Suspense>
          }
        />
        <Route
          path="article/list"
          element={
            <Suspense fallback={"加载中"}>
              <ArticleList />
            </Suspense>
          }
        />
        <Route
          path="user/list"
          element={
            <Suspense fallback={"加载中"}>
              <UserManager />
            </Suspense>
          }
        />
        <Route
          path="channel/list"
          element={
            <Suspense fallback={"加载中"}>
              <ChannelList />
            </Suspense>
          }
        />
        <Route
          path="logging"
          element={
            <Suspense fallback={"加载中"}>
              <Logging />
            </Suspense>
          }
        />
      </Route>
      <Route
        path="*"
        element={
          <Suspense fallback={"加载中"}>
            <Error />
          </Suspense>
        }
      />
    </Routes>
  );
};
