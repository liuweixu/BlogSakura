import { useEffect, useState } from "react";
import { Card, Row, Col, Statistic, Button, Space } from "antd";
import {
  FileTextOutlined,
  UserOutlined,
  PictureOutlined,
  AppstoreOutlined,
  InboxOutlined,
  DockerOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import { getArticleVoListByPage } from "@/api/articleController";
import { getUserVoListByPage } from "@/api/userController";
import { getPictureVoListByPage } from "@/api/pictureController";
import { getChannelVoListByPage } from "@/api/channelController";
import { getSpaceVoListByPage } from "@/api/spaceController";

function App() {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    articles: 0,
    users: 0,
    pictures: 0,
    channels: 0,
    spaces: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const [articlesRes, usersRes, picturesRes, channelsRes, spacesRes] =
        await Promise.all([
          getArticleVoListByPage({ currentPage: 1, pageSize: 1 }),
          getUserVoListByPage({ currentPage: 1, pageSize: 1 }),
          getPictureVoListByPage({ currentPage: 1, pageSize: 1 }),
          getChannelVoListByPage({ currentPage: 1, pageSize: 1 }),
          getSpaceVoListByPage({ currentPage: 1, pageSize: 1 }),
        ]);

      setStats({
        articles: articlesRes?.data?.data?.totalRow ?? 0,
        users: usersRes?.data?.data?.totalRow ?? 0,
        pictures: picturesRes?.data?.data?.totalRow ?? 0,
        channels: channelsRes?.data?.data?.totalRow ?? 0,
        spaces: spacesRes?.data?.data?.totalRow ?? 0,
      });
    } catch (error) {
      console.error("获取统计数据失败:", error);
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      title: "文章总数",
      value: stats.articles,
      icon: <FileTextOutlined style={{ fontSize: 32, color: "#1890ff" }} />,
      color: "#1890ff",
      path: "/backend/article/list",
    },
    {
      title: "用户总数",
      value: stats.users,
      icon: <UserOutlined style={{ fontSize: 32, color: "#52c41a" }} />,
      color: "#52c41a",
      path: "/backend/user/list",
    },
    {
      title: "图片总数",
      value: stats.pictures,
      icon: <PictureOutlined style={{ fontSize: 32, color: "#faad14" }} />,
      color: "#faad14",
      path: "/backend/picture/list",
    },
    {
      title: "频道总数",
      value: stats.channels,
      icon: <AppstoreOutlined style={{ fontSize: 32, color: "#722ed1" }} />,
      color: "#722ed1",
      path: "/backend/channel/list",
    },
    {
      title: "空间总数",
      value: stats.spaces,
      icon: <InboxOutlined style={{ fontSize: 32, color: "#eb2f96" }} />,
      color: "#eb2f96",
      path: "/backend/space/list",
    },
  ];

  return (
    <div style={{ padding: "24px" }}>
      {/* 欢迎区域 */}
      <Card
        style={{
          marginBottom: 24,
          borderRadius: 12,
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          background: "linear-gradient(135deg, #067eea 0%, #764ba2 100%)",
          border: "none",
        }}
        bodyStyle={{ padding: "32px" }}
      >
        <div style={{ color: "#fff" }}>
          <h1
            style={{
              fontSize: 32,
              margin: 0,
              marginBottom: 8,
              fontWeight: 600,
              color: "#fff",
            }}
          >
            ようこそ！！！
          </h1>
          <p style={{ fontSize: 16, margin: 0, opacity: 0.9 }}>
            欢迎来到 BlogSakura 管理台
          </p>
        </div>
      </Card>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]}>
        {statCards.map((item, index) => (
          <Col xs={24} sm={12} lg={8} xl={8} key={index}>
            <Card
              hoverable
              style={{
                borderRadius: 12,
                boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                cursor: "pointer",
                transition: "all 0.3s",
              }}
              bodyStyle={{ padding: "24px" }}
              onClick={() => navigate(item.path)}
            >
              <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
                <div>{item.icon}</div>
                <div style={{ flex: 1 }}>
                  <Statistic
                    title={item.title}
                    value={item.value}
                    loading={loading}
                    valueStyle={{ fontSize: 28, fontWeight: 600 }}
                  />
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      {/* 快速操作 */}
      <Card
        title="快速操作"
        style={{
          marginTop: 24,
          borderRadius: 12,
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
        }}
      >
        <Space wrap size="large">
          <Button
            icon={<FileTextOutlined />}
            onClick={() => navigate("/backend/publish")}
          >
            发布文章
          </Button>
          <Button
            icon={<PictureOutlined />}
            onClick={() => navigate("/backend/picture")}
          >
            上传图片
          </Button>
          <Button
            icon={<InboxOutlined />}
            onClick={() => navigate("/backend/space")}
          >
            创建空间
          </Button>
          <Button
            icon={<AppstoreOutlined />}
            onClick={() => navigate("/backend/channel/list")}
          >
            管理频道
          </Button>
          <Button
            icon={<DockerOutlined />}
            onClick={() => navigate("/backend/logging")}
          >
            系统日志
          </Button>
        </Space>
      </Card>
    </div>
  );
}

export default App;
