import { LoginPage } from "./login-page";

function Login() {
  return (
    <div className="grid min-h-svh lg:grid-cols-[4fr_1fr]">
      <div className="relative hidden bg-muted lg:block">
        <img
          src="https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列1"
          alt="Image"
          className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
        />
      </div>
      <div className="flex flex-1 items-center justify-center">
        <div className="w-full max-w-xs">
          <LoginPage />
        </div>
      </div>
    </div>
  );
}

export default Login;
