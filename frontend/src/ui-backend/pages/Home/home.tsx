import { Breadcrumb, Card } from "antd";
import { BarChartOutlined } from "@ant-design/icons";
import CountUp from "react-countup";
import { Col, Row, Statistic } from "antd";
import { useEffect, useState } from "react";
import { getArticleHomeAPI } from "@/ui-frontend/apis/home";
import { getChannelAPI } from "@/ui-backend/apis/article";
import { useNavigate } from "react-router-dom";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const formatter = (value: any) => <CountUp end={value} separator="," />;

const App = () => {
  // 统计文章数量和频道数量
  const [articleCount, setArticleCount] = useState(0);
  const [channelCount, setChannelCount] = useState(0);
  const navigate = useNavigate();
  useEffect(() => {
    const getCount = async () => {
      const resArticle = await getArticleHomeAPI();
      setArticleCount(resArticle?.data.data.length);
      const resChannel = await getChannelAPI();
      setChannelCount(resChannel?.data.data.length);
    };
    getCount();
  }, []);
  return (
    <div>
      <div>
        <Breadcrumb
          separator=">"
          items={[
            {
              title: "首页",
              href: "/backend/",
            },
          ]}
        />
        <Card
          style={{
            width: "100%",
            marginTop: 20,
            borderRadius: 12,
            boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          }}
          title={
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <BarChartOutlined style={{ color: "#1890ff", fontSize: 20 }} />
              <span
                style={{
                  fontSize: 18,
                  fontWeight: 600,
                  color: "#333",
                  fontFamily: "'KaiTi', 'KaiTi_GB2312', 'STKaiti', serif",
                }}
              >
                统计信息
              </span>
            </div>
          }
        >
          <Row gutter={16}>
            <Col span={12}>
              <Statistic
                title="文章数量"
                value={articleCount}
                formatter={formatter}
              />
            </Col>
            <Col span={12}>
              <Statistic
                title="频道数量"
                value={channelCount}
                precision={2}
                formatter={formatter}
              />
            </Col>
          </Row>
        </Card>
      </div>
      <div className="flex flex-col justify-center items-center h-full p-8">
        <div className="flex items-center gap-8">
          <div style={{ marginTop: 0 }}>
            <Card
              hoverable
              style={{ width: 240 }}
              cover={
                <img
                  draggable={true}
                  alt="example"
                  src="https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列1"
                />
              }
              onClick={() => navigate("/backend/publish")}
            >
              <Card.Meta title="发布文章" description="发布和更新文章" />
            </Card>
          </div>
          <div style={{ marginTop: "100px" }}>
            <Card
              hoverable
              style={{ width: 240 }}
              cover={
                <img
                  draggable={true}
                  alt="example"
                  src="https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列4"
                />
              }
              onClick={() => navigate("/backend/articlelist")}
            >
              <Card.Meta title="文章列表" description="查看和管理文章列表" />
            </Card>
          </div>
          <div style={{ marginTop: 0 }}>
            <Card
              hoverable
              style={{ width: 240 }}
              cover={
                <img
                  draggable={true}
                  alt="example"
                  src="https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列7"
                />
              }
              onClick={() => navigate("/backend/channellist")}
            >
              <Card.Meta title="频道列表" description="查看和管理频道列表" />
            </Card>
          </div>
          <div style={{ marginTop: "100px" }}>
            <Card
              hoverable
              style={{ width: 240 }}
              cover={
                <img
                  draggable={true}
                  alt="example"
                  src="https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列6"
                />
              }
              onClick={() => navigate("/backend/logging")}
            >
              <Card.Meta title="日志记录" description="管理日志" />
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default App;
