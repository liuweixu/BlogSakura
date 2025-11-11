import { getFrontendPictureVoById } from "@/api/pictureFrontendController";
import {
  Button,
  Card,
  Col,
  Descriptions,
  Image,
  message,
  Row,
  type DescriptionsProps,
} from "antd";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AntDesignOutlined } from "@ant-design/icons";
import { deletePicture } from "@/api/pictureController";
import { sessionLoginUser } from "@/api/userController";

function App() {
  const [pictureInfo, setPictureInfo] = useState<API.PictureVO>({});
  const [spaceId, setSpaceId] = useState<number | undefined>();
  const [userRole, setUserRole] = useState<string | undefined>();
  const [isEditDelete, setIsEditDelete] = useState(false);
  const navigate = useNavigate();
  const params = useParams();
  const id = params.id;
  const getPictureInfo = async () => {
    const res = await getFrontendPictureVoById({ id: id });
    if (res.data.code !== 0 || !res.data.data) {
      // 判断id是否存在，如果不存在就跳转到404页面
      navigate("/error");
      return;
    }
    if (res.data.data.spaceId) {
      setSpaceId(res.data.data.spaceId);
    }
    setPictureInfo(res.data.data);
    const resUser = await sessionLoginUser();
    if (resUser.data.code === 0 && resUser.data.data) {
      setUserRole(resUser.data.data.userRole);
    }
  };
  useEffect(() => {
    getPictureInfo();
  }, []);

  // 根据用户角色和 spaceId 判断是否显示编辑删除按钮
  useEffect(() => {
    if (userRole === "admin" && spaceId === undefined) {
      setIsEditDelete(true);
    } else if (userRole === "user" && spaceId !== undefined) {
      setIsEditDelete(true);
    } else {
      setIsEditDelete(false);
    }
  }, [userRole, spaceId]);
  // 描述列表信息
  const items: DescriptionsProps["items"] = [
    {
      key: "name",
      label: "图片名称",
      span: "filled",
      children: <p>{pictureInfo.name}</p>,
    },
    {
      key: "introduction",
      label: "图片简介",
      span: "filled",
      children: <p>{pictureInfo.introduction}</p>,
    },
    {
      key: "category",
      label: "图片类别",
      span: "filled",
      children: <p>{pictureInfo.category}</p>,
    },
    {
      key: "tags",
      label: "图片标签",
      span: "filled",
      children: (
        <span className="flex flex-wrap gap-2">
          {pictureInfo.tags?.map((item) => {
            return <Button key={item}>{item}</Button>;
          })}
        </span>
      ),
    },
    {
      key: "5",
      label: "图片大小",
      children: (
        <p>
          {pictureInfo.picSize
            ? (pictureInfo.picSize / 1024 / 1024).toFixed(2) + "MB"
            : "0MB"}
        </p>
      ),
      span: "filled",
    },
    {
      key: "6",
      label: "图片宽度",
      children: <p>{pictureInfo.picWidth}</p>,
      span: "filled",
    },
    {
      key: "7",
      label: "图片高度",
      children: <p>{pictureInfo.picHeight}</p>,
      span: "filled",
    },
    {
      key: "8",
      label: "图片宽高比",
      children: <p>{pictureInfo.picScale}</p>,
      span: "filled",
    },
    {
      key: "9",
      label: "图片格式",
      children: <p>{pictureInfo.picFormat}</p>,
      span: "filled",
    },
    {
      key: "10",
      label: "图片创建时间",
      children: (
        <p>{new Date(pictureInfo.createTime ?? "").toLocaleString()}</p>
      ),
      span: "filled",
    },
    {
      key: "11",
      label: "图片更新时间",
      children: <p>{new Date(pictureInfo.editTime ?? "").toLocaleString()}</p>,
      span: "filled",
    },
  ];

  // 删除图像
  const handleDelete = async (id: number) => {
    const res = await deletePicture({ id: id });
    if (res.data.code === 0) {
      message.success("删除成功");
      navigate("/pictures");
    } else {
      message.error("删除失败");
    }
  };
  // 编辑图像
  const handleEdit = (id: number) => {
    if (spaceId) {
      navigate(
        `/personal_space/private_pictures/add?id=${id}&spaceId=${spaceId}`
      );
    } else {
      navigate(`/backend/picture?id=${id}`);
    }
  };
  return (
    <div className="mt-20 mx-4">
      <Row>
        <Col sm={24} md={16} xl={12}>
          <Image src={pictureInfo.url} />
        </Col>
        <Col sm={24} md={8} xl={12}>
          <Card style={{ marginLeft: 16 }}>
            <Descriptions
              title="图片信息"
              bordered
              items={items}
              size="middle"
            />
            {isEditDelete && (
              <div className="flex flex-row justify-center items-center gap-4">
                <Button
                  type="primary"
                  onClick={() => handleEdit(pictureInfo.id ?? 0)}
                  className="mt-4"
                  icon={<AntDesignOutlined />}
                >
                  编辑
                </Button>
                <Button
                  danger
                  onClick={async () => {
                    await handleDelete(pictureInfo.id ?? 0);
                  }}
                  className="mt-4"
                  icon={<AntDesignOutlined />}
                >
                  删除
                </Button>
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}
export default App;
