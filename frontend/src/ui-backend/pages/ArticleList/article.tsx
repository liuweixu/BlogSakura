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
  Button,
  Popconfirm,
  Space,
  message,
  Input,
} from "antd";
import { useEffect, useState } from "react";
import { deleteArticleByIdAPI } from "@/ui-backend/apis/article";

import { Table } from "antd";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";

import { getArticleListPageAPI } from "@/ui-backend/apis/article";
import type { ArticleVOBackendPage } from "@/ui-backend/interface/Article";

export const Article = () => {
  const navigate = useNavigate();

  //获取文章列表
  const [articleList, setArticleList] = useState([]);
  const [total, setTotal] = useState(0);
  const [searchParams, setSearchParams] = useState<ArticleVOBackendPage>({
    currentPage: 1,
    pageSize: 5,
    sortField: "id",
    sortOrder: "descend",
  });
  const [form] = Form.useForm();
  form.setFieldsValue(searchParams);
  const getArticleList = async () => {
    const res = await getArticleListPageAPI(searchParams);
    setArticleList(res.data.data.records);
    console.log(res.data.data.records);
    setTotal(res.data.data.totalRow);
  };
  useEffect(() => {
    getArticleList();
  }, [searchParams.currentPage, searchParams.pageSize]);

  // 删除文章(删除后要更新文章列表)
  const handleDelete = async (id: number) => {
    const res = await deleteArticleByIdAPI(id);
    if (res.data.code === 0) {
      message.success("删除成功");
      // 检查删除后当前页是否为空
      const currentPageItemCount = articleList.length;
      // 如果当前页只有一条数据（删除后为空），且不是第一页，则跳转到前一页
      if (currentPageItemCount === 1 && searchParams.currentPage > 1) {
        setSearchParams({
          ...searchParams,
          currentPage: searchParams.currentPage - 1,
        });
      } else {
        // 否则直接刷新当前页
        getArticleList();
      }
    } else {
      message.error("删除失败");
    }
  };

  // 处理分页变化
  const handleTableChange = (page: number, size: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = size;
    getArticleList();
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSearch = (values: any) => {
    console.log(values);
    searchParams.currentPage = 1;
    searchParams.title = values.title;
    searchParams.channel = values.channel;
    getArticleList();
  };
  // 列表头
  // 准备列数据
  // 定义状态枚举
  const columns = [
    {
      title: "封面",
      dataIndex: "imageUrl",
      width: 120,
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      render: (cover: any) => {
        if (cover == "" || cover == null) {
          return (
            <img
              src={`https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列${
                Math.floor(Math.random() * 10) + 1
              }`}
              width={80}
              height={60}
              alt=""
            />
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
      title: "频道",
      dataIndex: "channel",
      width: 120,
    },
    {
      title: "发布时间",
      dataIndex: "publishDate",
      render: (text: Date) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "阅读数",
      dataIndex: "view",
    },
    {
      title: "点赞数",
      dataIndex: "like",
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
              onConfirm={() => handleDelete(data.id)}
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
        <Form layout="inline" onFinish={handleSearch} form={form}>
          <div className="flex gap-10 items-center">
            <Form.Item name="title" label="请输入标题">
              <Input placeholder="请输入标题" />
            </Form.Item>
            <Form.Item name="channel" label="请输入频道">
              <Input placeholder="请输入频道" />
            </Form.Item>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
          </div>
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
        title={`根据筛选条件共查询到 ${total} 条结果：`}
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={articleList}
          pagination={{
            total: total,
            pageSize: searchParams.pageSize,
            current: searchParams.currentPage,
            onChange: handleTableChange,
            onShowSizeChange: handleTableChange,
          }}
        />
      </Card>
    </div>
  );
};
