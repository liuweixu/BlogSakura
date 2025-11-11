import React, { useEffect, useState } from "react";
import { UserOutlined, PictureOutlined } from "@ant-design/icons";
import { Layout, Menu, theme } from "antd";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { sessionLoginUser } from "@/api/userController";
import { getSpaceVoListByPage } from "@/api/spaceController";
const { Sider, Content } = Layout;

const App: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [spaceId, setSpaceId] = useState<number | undefined>(undefined);
  //路由跳转
  const navigate = useNavigate();
  //高亮
  //获取当前路径
  const location = useLocation();
  const selectedKey = location.pathname;

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  //处理未登录时，同样后端界面的情况
  const getIsLogin = async () => {
    const res = await sessionLoginUser();
    setIsLogin(res?.data.message !== "未登录");
    if (res?.data.data && res?.data.data.userRole === "user") {
      const resSpace = await getSpaceVoListByPage({
        currentPage: 1,
        pageSize: 1,
        sortField: "id",
        sortOrder: "descend",
        userId: res.data.data.id,
      });
      const records = resSpace?.data?.data?.records ?? [];
      if (resSpace.data.code === 0 && resSpace.data.data) {
        const spaceId = records[0]?.id ?? undefined;
        setSpaceId(spaceId);
      }
    } else {
      setSpaceId(undefined);
    }
  };
  useEffect(() => {
    getIsLogin();
  }, []);
  return (
    <>
      {isLogin ? (
        <Layout>
          <Sider className="mt-21 bg-white rounded-lg" theme="light">
            <Menu
              // defaultSelectedKeys={['1']}
              selectedKeys={[selectedKey]}
              items={[
                {
                  key: "/personal_space",
                  icon: <PictureOutlined />,
                  label: "公共空间",
                  onClick: () => navigate("/personal_space"),
                },
                {
                  key: "/personal_space/private_pictures",
                  icon: <UserOutlined />,
                  label: "我的空间",
                  onClick: () =>
                    navigate(
                      spaceId
                        ? "/personal_space/private_pictures?id=" + spaceId
                        : "/personal_space/private_pictures"
                    ),
                },
              ]}
            />
          </Sider>
          <Content
            className="mt-21 mx-3 bg-white rounded-lg"
            style={{
              padding: 24,
              minHeight: "100vh",
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </Content>
        </Layout>
      ) : (
        navigate("/backend/login")
      )}
    </>
  );
};

export default App;
