/**
 * 此处使用新的组件：Table Popconfirm DatePicker
 * 注意掌握日期的设置方式
 * 注意Table的dataIndex
 *
 */
import { useNavigate } from "react-router-dom";
import {
  Breadcrumb,
  Card,
  Form,
  Select,
  Button,
  Popconfirm,
  Space,
} from "antd";
import { useEffect, useState } from "react";
import { delArticleAPI, getChannelAPI } from "@/ui-backend/apis/article";
import type { ChannelItem } from "@/ui-backend/interface/Publish";
// 引入汉化包 时间选择器显示中文
// import locale from 'antd/es/date-picker/locale/zh_CN'

import { Table } from "antd";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";

import { getArticleListAPI } from "@/ui-backend/apis/article";

const { Option } = Select;
// const { RangePicker } = DatePicker;

export const Article = () => {
  //获取频道信息
  const [channelList, setChannelList] = useState<ChannelItem[]>([]);
  const navigate = useNavigate();
  useEffect(() => {
    const getChannelList = async () => {
      const res = await getChannelAPI();
      setChannelList(res.data.data);
    };
    getChannelList();
  }, []);

  //获取文章列表
  const [articleList, setArticleList] = useState([]);
  const [count, setCount] = useState(0);

  /**
   * 筛选功能的实现：先获取对应的表单数据，然后传递给后端，后端根据数据进行数据库的筛选返回结果数据即可
   */
  const [reqData, setReqData] = useState({
    channel_name: "",
  });
  //获取筛选的数据
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onSelectChange = (formValue: any) => {
    setReqData({
      ...reqData,
      channel_name: formValue.channel_id,
    });
  };
  useEffect(() => {
    const getArticleList = async () => {
      const res = await getArticleListAPI(reqData);
      setArticleList(res.data.data);
      setCount(res.data.data.length);
    };
    getArticleList();
  }, [reqData]);

  // console.log(articleList);

  // 删除文章(删除后要更新文章列表)
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onConfirm = async (data: any) => {
    await delArticleAPI(data.id);
    setReqData({
      ...reqData,
    });
  };
  // 列表头
  // 准备列数据
  // 定义状态枚举
  const columns = [
    {
      title: "封面",
      dataIndex: "image_url",
      width: 120,
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      render: (cover: any) => {
        if (cover == null) {
          // return <img src={`https://www.loliapi.com/acg/?id=${Math.floor(Math.random() * 10 + 1)}`} width={80} height={60} alt=""/>
          return (
            <img src="/statics/images/404.png" width={80} height={60} alt="" />
          );
        } else {
          return <img src={cover || ""} width={80} height={60} alt="" />;
        }
      },
    },
    {
      title: "标题",
      dataIndex: "title",
      width: 220,
    },
    {
      title: "发布时间",
      dataIndex: "publish_date",
    },
    {
      title: "阅读数",
      dataIndex: "read_count",
    },
    {
      title: "评论数",
      dataIndex: "comment_count",
    },
    {
      title: "点赞数",
      dataIndex: "like_count",
    },
    {
      title: "操作",
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      render: (data: any) => {
        return (
          <Space size="middle">
            <Button
              type="primary"
              shape="circle"
              icon={<EditOutlined />}
              onClick={() => navigate(`/backend/publish?id=${data.id}`)}
            />
            <Popconfirm
              title="删除文章"
              description="确认要删除当前文章吗?"
              onConfirm={() => onConfirm(data)}
              okText="Yes"
              cancelText="No"
            >
              <Button
                type="primary"
                danger
                shape="circle"
                icon={<DeleteOutlined />}
              />
            </Popconfirm>
          </Space>
        );
      },
    },
  ];
  return (
    <div>
      <Breadcrumb
        separator=">"
        items={[
          {
            title: "首页",
            href: "/backend/",
          },
          {
            title: "文章列表",
            href: "/backend/articlelist",
          },
        ]}
      />
      <Card
        style={{
          width: "100%",
          marginTop: 20,
          borderRadius: 12,
          boxShadow: "0 4px 8px rgba(0,0,0,0.05)",
          fontFamily: "'KaiTi', 'KaiTi_GB2312', 'STKaiti', serif",
        }}
        title="文章筛选"
      >
        <Form layout="inline" onFinish={onSelectChange}>
          <Form.Item label="频道" name="channel_id">
            <Select placeholder="请选择文章频道" style={{ width: 120 }}>
              {channelList.map((channel) => (
                <Option key={channel.id} value={channel.name}>
                  {channel.name}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" style={{ marginLeft: 40 }}>
              筛选
            </Button>
          </Form.Item>
        </Form>
      </Card>
      {/**表格区域 */}
      <Card
        style={{
          width: "100%",
          marginTop: 20,
          borderRadius: 12,
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          fontFamily: "'KaiTi', 'KaiTi_GB2312', 'STKaiti', serif",
        }}
        title={`根据筛选条件共查询到 ${count} 条结果：`}
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={articleList}
          pagination={{
            total: count,
            pageSize: 5,
          }}
        />
      </Card>
    </div>
  );
};
