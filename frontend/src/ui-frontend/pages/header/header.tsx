import { Affix } from "antd";
import { Link, useLocation } from "react-router-dom";
import {
  HomeOutlined,
  FolderOutlined,
  UserOutlined,
  AndroidOutlined,
} from "@ant-design/icons";
import SimpleSearch from "./search";

function App() {
  const location = useLocation();
  const currentPath = location.pathname;

  const navItems = [
    { path: "/", label: "首页", icon: HomeOutlined },
    { path: "/pictures", label: "公共图库", icon: FolderOutlined },
    { path: "/personal_space", label: "私有空间", icon: AndroidOutlined },
    { path: "/backend", label: "后台", icon: AndroidOutlined },
  ];

  const isActive = (path: string) => {
    if (path === "/") {
      return currentPath === "/";
    }
    return currentPath.startsWith(path);
  };

  return (
    <div>
      <Affix offsetTop={0}>
        <div
          id="nav-wrapper"
          className="fixed w-full h-19 px-7.5 transition-all duration-400 ease-in-out bg-white/90 shadow-[0_1px_40px_-8px_rgba(0,0,0,.5)]"
        >
          <div className="flex justify-between items-center">
            <div id="nav-left" className="h-19 leading-19 flex items-center">
              <Link
                to="/"
                className="text-[#464646] text-[20px] font-extrabold hover:text-[#fe9600]"
              >
                weixuliu's blog
              </Link>
            </div>
            <div
              id="nav-center"
              className="flex-3 flex justify-center items-center"
            >
              <ul id="nav" className="flex items-center space-x-6">
                {navItems.map((item) => {
                  const Icon = item.icon;
                  const active = isActive(item.path);
                  return (
                    <li key={item.path}>
                      <Link
                        to={item.path}
                        className={`flex items-center text-base h-14.5 leading-20 relative transition-colors ${
                          active
                            ? "text-[#fe9600]"
                            : "text-[#666666] hover:text-[#fe9600]"
                        }`}
                      >
                        <Icon className="mr-1.5" />
                        <span>{item.label}</span>
                        {/* {active && (
                          <span className="absolute -bottom-4.5 left-0 right-0 h-1.5 bg-[#fe9600]"></span>
                        )} */}
                      </Link>
                    </li>
                  );
                })}
              </ul>
            </div>
            <div id="nav-right" className="flex items-center space-x-4">
              <SimpleSearch mode="icon" />
              <UserOutlined className="text-[#666666] text-lg cursor-pointer hover:text-[#fe9600]" />
            </div>
          </div>
        </div>
      </Affix>
    </div>
  );
}

export default App;
