import { deleteSpace, getSpaceVoListByPage } from "@/api/spaceController";
import {
  Form,
  Input,
  Button,
  Table,
  Breadcrumb,
  type TableProps,
  Space,
  message,
} from "antd";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function App() {
  const [form] = Form.useForm();
  const [spaceList, setSpaceList] = useState<API.SpaceVO[]>([]);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [searchParams, setSearchParams] = useState<API.SpaceQueryRequest>({
    currentPage: 1,
    pageSize: 5,
  });
  const [total, setTotal] = useState(0);
  const navigate = useNavigate();

  const columns: TableProps<API.SpaceVO>["columns"] = [
    {
      title: "id",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "空间名称",
      dataIndex: "spaceName",
      key: "spaceName",
    },
    {
      title: "空间层次",
      dataIndex: "spaceLevel",
      key: "spaceLevel",
      render: (text) => {
        return text === 0
          ? "普通版"
          : text === 1
          ? "专业版"
          : text === 2
          ? "旗舰版"
          : "未知";
      },
    },
    {
      title: "最大容量",
      dataIndex: "maxSize",
      key: "maxSize",
      render: (text) => {
        return text ? text / 1024 / 1024 + " MB" : 0;
      },
    },
    {
      title: "最大数量",
      dataIndex: "maxCount",
      key: "maxCount",
    },
    {
      title: "总容量",
      dataIndex: "totalSize",
      key: "totalSize",
      render: (text) => {
        return text ? (text / 1024 / 1024).toFixed(2) + " MB" : 0;
      },
    },
    {
      title: "总数量",
      dataIndex: "totalCount",
      key: "totalCount",
    },
    {
      title: "用户",
      dataIndex: "user",
      key: "userName",
      render: (text) => {
        return text?.userName ?? "未分配";
      },
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      render: (text) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "编辑时间",
      dataIndex: "editTime",
      key: "editTime",
      render: (text) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "空间类型",
      dataIndex: "spaceType",
      key: "spaceType",
      render: (text) => {
        return text === 0 ? "私有空间" : text === 1 ? "团队空间" : "未知";
      },
    },
    {
      title: "操作",
      key: "operation",
      dataIndex: "operation",
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="primary"
            onClick={() => navigate(`/backend/space?id=${record.id}`)}
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

  const handleDelete = async (id: number) => {
    const res = await deleteSpace({ id: id.toString() });
    if (res.data.code === 0 && res.data.data) {
      message.success("删除成功");
      getSpaceList();
    } else {
      message.error("删除失败");
    }
  };

  const getSpaceList = async () => {
    const resSpaceList = await getSpaceVoListByPage(searchParams);
    const records = resSpaceList?.data?.data?.records ?? [];
    console.log(records);
    setSpaceList(records);
    const newTotal = resSpaceList?.data?.data?.totalRow ?? 0;
    setTotal(newTotal);
  };
  useEffect(() => {
    getSpaceList();
  }, [searchParams.currentPage, searchParams.pageSize, searchParams.spaceType]);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSearch = (values: any) => {
    console.log(values);
    searchParams.currentPage = 1;
    searchParams.spaceName = values.spaceName;
    getSpaceList();
  };
  const handleTableChange = (page: number, size: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = size;
    getSpaceList();
  };
  return (
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
            title: "空间列表",
            href: "/backend/space/list",
          },
        ]}
      />
      <Form layout="inline" onFinish={handleSearch} form={form}>
        <div className="flex gap-10 items-center">
          {/* <Form.Item name="userAccount" label="请输入账号">
            <Input placeholder="请输入账号" />
          </Form.Item> */}
          <Form.Item name="spaceName" label="请输入空间名称">
            <Input placeholder="请输入空间名称" />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
        </div>
      </Form>
      <Table<API.UserVO>
        className="mt-5"
        columns={columns}
        dataSource={spaceList}
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
}

export default App;
