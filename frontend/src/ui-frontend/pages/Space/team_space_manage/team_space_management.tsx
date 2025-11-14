import { deleteSpace } from "@/api/spaceController";
import {
  addSpaceUser,
  editSpaceUser,
  getSpaceUserVoList,
} from "@/api/spaceUserController";
import { getUserVoListByPage } from "@/api/userController";
import {
  Button,
  Table,
  type TableProps,
  Space,
  message,
  Select,
  Form,
  AutoComplete,
} from "antd";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function App() {
  // 在组件中定义角色映射
  const roleOptions = [
    { label: "浏览者", value: "viewer" },
    { label: "编辑者", value: "editor" },
    { label: "管理员", value: "admin" },
  ];

  const [form] = Form.useForm();

  const [userList, setUserList] = useState<API.UserVO[]>([]);
  const getUserList = async () => {
    const resUserList = await getUserVoListByPage({
      currentPage: 1,
      pageSize: 1000,
    });
    const data = resUserList?.data?.data?.records ?? [];
    setUserList(data as API.UserVO[]);
  };
  useEffect(() => {
    getUserList();
  }, []);

  const handleRoleChange = async (record: API.SpaceUserVO, value: string) => {
    try {
      const res = await editSpaceUser({
        id: record.id,
        spaceRole: value,
      });
      if (res.data.code === 0 && res.data.data) {
        message.success("角色更新成功");
        getSpaceList(); // 刷新列表
      } else {
        message.error("角色更新失败");
      }
    } catch (error) {
      console.error(error);
      message.error("角色更新失败");
    }
  };
  const [spaceList, setSpaceList] = useState<API.SpaceUserVO[]>([]);
  const params = useParams();
  //   const navigate = useNavigate();
  const columns: TableProps<API.SpaceVO>["columns"] = [
    {
      title: "姓名",
      dataIndex: "user",
      key: "userName",
      render: (text) => {
        return text?.userName ?? "无姓名";
      },
    },
    {
      title: "角色",
      dataIndex: "spaceRole",
      key: "spaceRole",
      render: (text: string, record: API.SpaceUserVO) => (
        <Select
          value={text}
          options={roleOptions}
          onChange={(value) => handleRoleChange(record, value)}
          style={{ width: 120 }}
        />
      ),
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
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      render: (_, record) => (
        <Space size="middle">
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
    const resSpaceList = await getSpaceUserVoList({
      spaceId: params.id ?? undefined,
    });
    const data = resSpaceList?.data?.data ?? [];
    console.log(data);
    setSpaceList(
      data.sort((a, b) => a.spaceRole.localeCompare(b.spaceRole ?? ""))
    );
  };
  useEffect(() => {
    getSpaceList();
  }, []);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleAddUser = async (values: any) => {
    const { userId } = values;
    console.log(userId);
    const res = await addSpaceUser({
      spaceId: params.id ?? undefined,
      userId: userId,
      spaceRole: "viewer",
    });
    if (res.data.code === 0 && res.data.data) {
      message.success("添加用户成功");
      getSpaceList();
    } else {
      message.error("添加用户失败");
    }
  };
  return (
    <div>
      <div>
        <Form onFinish={handleAddUser} form={form}>
          <div className="flex gap-2 items-end">
            <Form.Item name="userId">
              <AutoComplete
                options={userList.map((user) => ({
                  label: user.userName,
                  value: user.id,
                  key: user.id,
                }))}
                placeholder="请选择用户"
              />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                添加用户
              </Button>
            </Form.Item>
          </div>
        </Form>
      </div>
      <div>
        <Table<API.UserVO>
          className="mt-5"
          columns={columns}
          dataSource={spaceList}
        />
      </div>
    </div>
  );
}

export default App;
