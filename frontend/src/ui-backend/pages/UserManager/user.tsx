import { useEffect, useState } from "react";
import { Button, Form, Input, message, Space, Table } from "antd";
import type { TableProps } from "antd";
import { getUserVoListByPage, deleteUser } from "@/api/userController";

export const User = () => {
  const columns: TableProps<API.UserVO>["columns"] = [
    {
      title: "id",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "账号",
      dataIndex: "userAccount",
      key: "userAccount",
    },
    {
      title: "用户昵称",
      dataIndex: "userName",
      key: "userName",
    },
    {
      title: "用户角色",
      dataIndex: "userRole",
      key: "userRole",
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
      title: "操作",
      key: "operation",
      dataIndex: "operation",
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="primary"
            danger
            onClick={() => handleDelete(record.id ?? 0)}
          >
            删除
          </Button>
          <Button type="primary">修改</Button>
        </Space>
      ),
    },
  ];

  const [userList, setUserList] = useState<API.UserVO[]>([]);
  const [total, setTotal] = useState(0);
  const [searchParams, setSearchParams] = useState<API.UserQueryRequest>({
    currentPage: 1,
    pageSize: 5,
  });
  const [form] = Form.useForm();
  form.setFieldsValue(searchParams);

  const getUserList = async () => {
    const resUserList = await getUserVoListByPage(searchParams);
    const records = resUserList?.data?.data?.records ?? [];
    setUserList(records);
    const newTotal = resUserList?.data?.data?.totalRow ?? 0;
    setTotal(newTotal);
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleDelete = async (id: any) => {
    const res = await deleteUser({ id: id });
    if (res.data.code === 0) {
      message.success("删除成功");
      // 检查删除后当前页是否为空
      const currentPageItemCount = userList.length;
      // 如果当前页只有一条数据（删除后为空），且不是第一页，则跳转到前一页
      const currentPage = searchParams.currentPage ?? 1;
      if (currentPageItemCount === 1 && currentPage > 1) {
        setSearchParams({
          ...searchParams,
          currentPage: currentPage - 1,
        });
      } else {
        // 否则直接刷新当前页
        getUserList();
      }
    } else {
      message.error("删除失败");
    }
  };

  // 处理分页变化
  const handleTableChange = (page: number, size: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = size;
    getUserList();
  };

  useEffect(() => {
    getUserList();
  }, [searchParams.currentPage, searchParams.pageSize]);

  //eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSearch = (values: any) => {
    console.log(values);
    searchParams.currentPage = 1;
    searchParams.userAccount = values.userAccount;
    searchParams.userName = values.userName;
    getUserList();
  };

  return (
    <div>
      <Form layout="inline" onFinish={handleSearch} form={form}>
        <div className="flex gap-10 items-center">
          <Form.Item name="userAccount" label="请输入账号">
            <Input placeholder="请输入账号" />
          </Form.Item>
          <Form.Item name="userName" label="请输入用户昵称">
            <Input placeholder="请输入用户昵称" />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
        </div>
      </Form>
      <Table<API.UserVO>
        className="mt-5"
        columns={columns}
        dataSource={userList}
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
