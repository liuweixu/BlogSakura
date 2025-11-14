/* eslint-disable @typescript-eslint/no-unused-vars */
import { addSpace, getSpaceVoById, updateSpace } from "@/api/spaceController";
import { getUserVoListByPage } from "@/api/userController";
import { Breadcrumb, Button, Card, Form, Input, message, Select } from "antd";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

function App() {
  const [form] = Form.useForm();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [spaceCreateRequest, setSpaceCreateRequest] =
    useState<API.SpaceAddRequest>({});
  const [searchParams] = useSearchParams();
  const searchParamsId = searchParams.get("id");
  const [userList, setUserList] = useState<API.UserVO[]>([]);

  const createSpace = async () => {
    const res = await addSpace(spaceCreateRequest);
    const data = res?.data?.data;
    if (data) {
      message.success("创建空间成功");
    } else {
      message.error("创建空间失败");
    }
  };

  const editSpace = async () => {
    console.log(spaceCreateRequest);
    const res = await updateSpace(spaceCreateRequest);
    const data = res?.data?.data;
    if (data) {
      message.success("修改空间成功");
    } else {
      message.error("修改空间失败");
    }
  };
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onFinish = (values: any) => {
    const { spaceName, spaceLevel, spaceType, userId } = values;
    spaceCreateRequest.spaceName = spaceName;
    spaceCreateRequest.spaceLevel = spaceLevel;
    spaceCreateRequest.spaceType = spaceType;
    spaceCreateRequest.userId = userId;
    console.log(spaceCreateRequest);
    if (searchParamsId) {
      spaceCreateRequest.id = searchParamsId;
      editSpace();
    } else {
      createSpace();
    }
  };

  const getOldSpace = async () => {
    const id = searchParamsId ?? "";
    if (id) {
      const res = await getSpaceVoById({ id: id.toString() });
      if (res.data.code === 0 && res.data.data) {
        form.setFieldsValue({
          spaceName: res.data.data.spaceName,
          spaceLevel: res.data.data.spaceLevel,
          spaceType: res.data.data.spaceType,
          userId: res?.data.data?.user?.id,
        });
      }
    }
  };

  // 获取用户信息
  const getUserList = async () => {
    const res = await getUserVoListByPage({
      currentPage: 1,
      pageSize: 1000,
    });
    if (res.data.code === 0 && res.data.data?.records) {
      setUserList(res.data.data.records);
    }
  };

  useEffect(() => {
    getOldSpace();
    getUserList();
  }, []);
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
            title: "创建空间",
            href: "/backend/space",
          },
        ]}
      />
      <div className="text-4xl font-bold mb-4 font-serif">创建或修改空间</div>
      <Form form={form} onFinish={onFinish}>
        <Form.Item name="spaceName" label="空间名称" layout="vertical">
          <Input />
        </Form.Item>
        <Form.Item name="spaceLevel" label="空间层次" layout="vertical">
          <Select
            options={[
              { label: "普通版", key: 0, value: 0 },
              { label: "专业版", key: 1, value: 1 },
              { label: "旗舰版", key: 2, value: 2 },
            ]}
          />
        </Form.Item>
        <Form.Item name="spaceType" label="空间类型" layout="vertical">
          <Select
            options={[
              { label: "私有空间", key: 0, value: 0 },
              { label: "团队空间", key: 1, value: 1 },
            ]}
          />
        </Form.Item>
        <Form.Item name="userId" label="用户" layout="vertical">
          <Select
            options={userList.map((user) => ({
              label: user.userName,
              key: user.id,
              value: user.id,
            }))}
          />
        </Form.Item>
        <Form.Item className="flex justify-center">
          <Button type="primary" htmlType="submit">
            提交
          </Button>
        </Form.Item>
      </Form>
      <Card title="空间级别介绍">
        <div>普通版：大小100MB，数量100</div>
        <div>专业版：大小1000MB，数量1000</div>
        <div>旗舰版：大小100000MB，数量100000</div>
      </Card>
    </div>
  );
}

export default App;
