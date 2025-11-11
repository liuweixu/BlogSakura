import { useEffect, useState } from "react";
import { Breadcrumb, Button, Form, Input, message, Space, Table } from "antd";
import type { TableProps } from "antd";
import { deletePicture, getPictureListByPage } from "@/api/pictureController";
import { useNavigate } from "react-router-dom";
import { AntDesignOutlined } from "@ant-design/icons";

export const App = () => {
  const navigate = useNavigate();
  const columns: TableProps<API.Picture>["columns"] = [
    // {
    //   title: "id",
    //   dataIndex: "id",
    //   key: "id",
    // },
    {
      title: "图片",
      dataIndex: "url",
      key: "url",
      width: 160,
      render: (text) => <img src={text} width={160} height={120} alt="图片" />,
    },
    {
      title: "图片名称",
      dataIndex: "name",
      key: "name",
      width: 160,
    },
    // {
    //   title: "图片简介",
    //   dataIndex: "introduction",
    //   key: "introduction",
    //   width: 80,
    // },
    {
      title: "图片类别",
      dataIndex: "category",
      key: "category",
      width: 80,
    },
    {
      title: "图片标签",
      dataIndex: "tags",
      key: "tags",
      width: 180,
    },
    {
      title: "图片信息",
      dataIndex: "pictureInfo",
      key: "pictureInfo",
      width: 160,
      render: (_, record) => (
        <div className="flex flex-col gap-1">
          <div>图片格式: {record.picFormat}</div>
          <div>图片宽度: {record.picWidth}</div>
          <div>图片高度: {record.picHeight}</div>
          <div>图片宽高比: {record.picScale}</div>
          <div>图片大小: {record.picSize}</div>
        </div>
      ),
    },
    {
      title: "用户id",
      dataIndex: "userId",
      key: "userId",
      width: 40,
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      width: 80,
      render: (text) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "编辑时间",
      dataIndex: "editTime",
      key: "editTime",
      width: 80,
      render: (text) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "操作",
      key: "operation",
      dataIndex: "operation",
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="primary"
            onClick={() => navigate(`/backend/picture?id=${record.id}`)}
          >
            修改
          </Button>
          <Button
            type="primary"
            danger
            onClick={() => handleDelete(record.id ?? 0)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const [pictureList, setPictureList] = useState<API.Picture[]>([]);
  const [total, setTotal] = useState(0);
  const [searchParams, setSearchParams] = useState<API.PictureQueryRequest>({
    currentPage: 1,
    pageSize: 5,
    sortField: "createTime",
    sortOrder: "descend",
  });
  const [form] = Form.useForm();
  form.setFieldsValue(searchParams);

  const getPictureList = async () => {
    const resUserList = await getPictureListByPage(
      searchParams as API.PictureQueryRequest
    );
    const records = resUserList?.data?.data?.records ?? [];
    setPictureList(records);
    const newTotal = resUserList?.data?.data?.totalRow ?? 0;
    setTotal(newTotal);
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleDelete = async (id: any) => {
    const res = await deletePicture({ id: id });
    if (res.data.code === 0) {
      message.success("删除成功");
      // 检查删除后当前页是否为空
      const currentPageItemCount = pictureList.length;
      // 如果当前页只有一条数据（删除后为空），且不是第一页，则跳转到前一页
      const currentPage = searchParams.currentPage ?? 1;
      if (currentPageItemCount === 1 && currentPage > 1) {
        setSearchParams({
          ...searchParams,
          currentPage: currentPage - 1,
        });
      } else {
        // 否则直接刷新当前页
        getPictureList();
      }
    } else {
      message.error("删除失败");
    }
  };

  // 处理分页变化
  const handleTableChange = (page: number, size: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = size;
    getPictureList();
  };

  useEffect(() => {
    getPictureList();
  }, [searchParams.currentPage, searchParams.pageSize]);

  //eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSearch = (values: any) => {
    console.log(values);
    searchParams.currentPage = 1;
    searchParams.name = values.keywords;
    searchParams.introduction = values.keywords;
    searchParams.category = values.category;
    searchParams.tags = values.tags;
    getPictureList();
  };

  return (
    <div>
      <div className="flex justify-between items-center">
        <div>
          <Breadcrumb
            style={{ marginBottom: "36px" }}
            separator=">"
            items={[
              {
                title: "首页",
                href: "/backend/",
              },
              {
                title: "图像列表",
                href: "/backend/picture/list",
              },
            ]}
          />
        </div>
        <div>
          <Button
            type="primary"
            onClick={() => navigate("/backend/picture")}
            icon={<AntDesignOutlined />}
          >
            创建图像
          </Button>
        </div>
      </div>
      <Form layout="inline" onFinish={handleSearch} form={form}>
        <div className="flex gap-10 items-center">
          <Form.Item name="keywords" label="请输入关键词">
            <Input placeholder="从名称和简介中搜索" />
          </Form.Item>
          <Form.Item name="category" label="请输入图像类型">
            <Input placeholder="请输入图像类型" />
          </Form.Item>
          <Form.Item name="tags" label="请输入图像标签">
            <Input placeholder="请输入图像标签" />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
        </div>
      </Form>
      <Table<API.UserVO>
        className="mt-5"
        columns={columns}
        dataSource={pictureList}
        pagination={{
          pageSize: searchParams.pageSize,
          current: searchParams.currentPage,
          total: total,
          onChange: handleTableChange,
          onShowSizeChange: handleTableChange,
        }}
      />
    </div>
  );
};
