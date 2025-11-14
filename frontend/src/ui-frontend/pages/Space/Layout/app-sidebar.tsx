import React, { useEffect, useState } from "react";
import { UserOutlined, PictureOutlined, TeamOutlined } from "@ant-design/icons";
import { Layout, Menu, theme } from "antd";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { sessionLoginUser } from "@/api/userController";
import { getSpaceVoListByUserId } from "@/api/spaceController";
import { getMyTeamSpaceList } from "@/api/spaceUserController";
const { Sider, Content } = Layout;

const App: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [spaceListPrimary, setSpaceListPrimary] = useState<API.SpaceVO[]>([]);
  const [spaceListTeam, setSpaceListTeam] = useState<API.SpaceUserVO[]>([]);
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
    if (res?.data.data) {
      const resSpacePrimary = await getSpaceVoListByUserId({
        userId: res.data.data.id,
        spaceType: 0,
      });
      if (resSpacePrimary.data.code === 0 && resSpacePrimary.data.data) {
        setSpaceListPrimary(resSpacePrimary.data.data);
      }
      const resSpaceTeam = await getMyTeamSpaceList();
      if (resSpaceTeam.data.code === 0 && resSpaceTeam.data.data) {
        setSpaceListTeam(resSpaceTeam.data.data);
      }
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
              mode="inline"
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
                  children: spaceListPrimary.map((space) => {
                    return {
                      key: "/personal_space/private_pictures?id=" + space.id,
                      icon: <UserOutlined />,
                      label: space.spaceName,
                      onClick: () =>
                        navigate(
                          "/personal_space/private_pictures?id=" + space.id
                        ),
                    };
                  }),
                },
                {
                  key: "/personal_space/team_pictures",
                  icon: <TeamOutlined />,
                  label: "团队空间",
                  children: spaceListTeam.map((space) => {
                    return {
                      key: "/personal_space/team_pictures?id=" + space.id,
                      icon: <TeamOutlined />,
                      label: space.space?.spaceName,
                      onClick: () =>
                        navigate(
                          "/personal_space/team_pictures?id=" + space.space?.id
                        ),
                    };
                  }),
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
