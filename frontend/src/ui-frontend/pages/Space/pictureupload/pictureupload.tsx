import { useEffect, useState, useRef } from "react";
import { PlusOutlined } from "@ant-design/icons";
import {
  AutoComplete,
  Button,
  Form,
  Input,
  message,
  notification,
  Select,
  Space,
  Tabs,
  Upload,
} from "antd";
import type { GetProp, TabsProps, UploadFile, UploadProps } from "antd";
import {
  getPictureListTagCategory,
  getPictureVoById,
  getUploadPicture,
} from "@/api/pictureController";
import "./picture.css";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { getSpaceVoListByPage } from "@/api/spaceController";
import { sessionLoginUser } from "@/api/userController";
import { editFrontendPicture } from "@/api/pictureFrontendController";
import CropModal from "./cropModal";
import type { CropModalRef } from "./cropModal";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

const getBase64 = (file: FileType): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });

function App() {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [form] = Form.useForm();
  //提交成功提示
  const [notify, contextHolder] = notification.useNotification();
  const [picData, setPicData] = useState<API.PictureVO>({});
  const [pictureId, setPictureId] = useState<number | undefined>(undefined);
  const [spaceType, setSpaceType] = useState<number | undefined>(undefined);
  const [searchParams] = useSearchParams();
  const searchParamsId = searchParams.get("id");
  const [userId, setUserId] = useState<number | undefined>(undefined);
  const [spaceId, setSpaceId] = useState<number | undefined>(undefined);
  const navigate = useNavigate();
  const cropModalRef = useRef<CropModalRef>(null);

  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as FileType);
    }
  };

  // 获取空间id
  const getUserInfo = async () => {
    const res = await sessionLoginUser();
    if (
      res?.data.code === 0 &&
      res?.data.data &&
      res.data.data.userRole === "user"
    ) {
      setUserId(res.data.data.id);
    }
  };
  useEffect(() => {
    getUserInfo();
  }, []);
  const getSpaceId = async () => {
    const res = await getSpaceVoListByPage({
      currentPage: 1,
      pageSize: 1,
      sortField: "id",
      sortOrder: "descend",
      userId: userId,
    });
    const records = res?.data?.data?.records ?? [];
    if (res?.data.code === 0 && res?.data.data) {
      setSpaceId(records[0]?.id ?? undefined);
      setSpaceType(records[0]?.spaceType ?? undefined);
    }
  };
  useEffect(() => {
    getSpaceId();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const handleChange: UploadProps["onChange"] = ({ fileList: newFileList }) =>
    setFileList(newFileList);

  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>点击或拖拽上传图片</div>
    </button>
  );

  // 校验函数
  const beforeUpload = (file: FileType) => {
    const isJpgOrPng =
      file.type === "image/jpeg" ||
      file.type === "image/png" ||
      file.type === "image/jpg" ||
      file.type === "image/webp" ||
      file.type === "image/gif" ||
      file.type === "image/bmp" ||
      file.type === "image/tiff" ||
      file.type === "image/ico" ||
      file.type === "image/svg+xml" ||
      file.type === "image/heic" ||
      file.type === "image/heif";
    if (!isJpgOrPng) {
      message.error("You can only upload image file!");
    }
    const isLt2M = file.size / 1024 / 1024 < 30;
    if (!isLt2M) {
      message.error("Image must smaller than 30MB!");
    }
    return isJpgOrPng && isLt2M;
  };

  /**
   * 自定义上传图片 涉及到文件上传
   * @param options 上传参数
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const customRequest = async (options: any) => {
    const { file, onSuccess, onError } = options;

    try {
      // 调用上传 API
      const formData = new FormData();
      formData.append("image", file);
      console.log("searchParamsId", searchParamsId);
      const response = await getUploadPicture(
        {
          pictureUploadRequest: {
            id: searchParamsId ? searchParamsId : undefined,
            spaceId: spaceId ?? undefined,
          },
        },
        formData
      );

      // 根据后端返回的数据结构调整
      const pictureResponse = response?.data?.data as API.PictureVO;
      setPicData(pictureResponse as API.PictureVO);
      setPictureId(pictureResponse?.id ?? 0);
      form.setFieldsValue({
        title: pictureResponse?.name ?? "",
      });
      console.log("form.getFieldValue('title')", form.getFieldValue("title"));
      // 更新文件列表，添加响应信息
      setFileList((prevList: UploadFile[]) => {
        return prevList.map((item) => {
          if (item.uid === file.uid) {
            return {
              ...item,
              status: "done",
              response: { imgUrl: picData.url ?? "" },
            };
          }
          return item;
        });
      });

      // 调用成功回调
      onSuccess?.(response.data, file);
    } catch (error) {
      // 更新文件列表，标记为错误
      setFileList((prevList: UploadFile[]) => {
        return prevList.map((item) => {
          if (item.uid === file.uid) {
            return {
              ...item,
              status: "error",
            };
          }
          return item;
        });
      });

      // 调用错误回调
      onError?.(error);

      notify.error({
        message: "上传失败",
        description: "图片上传失败，请重试",
        placement: "bottomRight",
      });
    }
  };

  /**
   * 提交表单
   * @param values
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onFinish = async (values: any) => {
    const { title, profile, category, tags } = values;
    const pictureUpdateRequest: API.PictureUpdateRequest = {};
    pictureUpdateRequest.category = category ?? "";
    pictureUpdateRequest.tags = tags ?? [];
    pictureUpdateRequest.name = title ?? "";
    pictureUpdateRequest.introduction = profile ?? "";
    pictureUpdateRequest.id = pictureId
      ? pictureId
      : searchParamsId
      ? searchParamsId
      : undefined;
    pictureUpdateRequest.spaceId = spaceId;
    console.log("pictureUpdateRequest", pictureUpdateRequest);
    const res = await editFrontendPicture(pictureUpdateRequest);
    if (res?.data?.code === 0 && res?.data?.data) {
      message.success("图片更新成功");
      navigate(`/personal_space/private_pictures?id=${spaceId}`);
    }
  };

  /**
   * 重置
   */
  const onReset = () => {
    setFileList([]);
  };

  //获取旧图像
  const getOldPicture = async () => {
    // 获取数据
    const id = searchParamsId;
    if (id) {
      const res = await getPictureVoById({ id: id });
      if (res.data.code === 0 && res.data.data) {
        form.setFieldsValue({
          title: res.data.data.name,
          profile: res.data.data.introduction,
          category: res.data.data.category,
          tags: res.data.data.tags,
        });
        setFileList([
          {
            uid: "-1",
            name: res.data.data.name ?? "",
            status: "done",
            url: res.data.data.url,
            response: { Location: res.data.data.url },
          },
        ]);
      }
    }
  };

  useEffect(() => {
    getOldPicture();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParamsId]);

  // 获取标签和分类选项
  const [tagList, setTagList] = useState<string[]>([]);
  const [categoryList, setCategoryList] = useState<string[]>([]);
  const getTagCategoryOptions = async () => {
    const res = await getPictureListTagCategory();
    if (res.data.code === 0 && res.data.data) {
      const { tagList, categoryList } = res.data.data;
      setTagList(tagList ?? []);
      setCategoryList(categoryList ?? []);
    }
  };
  useEffect(() => {
    getTagCategoryOptions();
  }, []);

  // 处理编辑图片
  const handleEditPicture = () => {
    const currentFile = fileList.find((file) => file.status === "done");
    const currentPictureId =
      pictureId || (searchParamsId ? searchParamsId : undefined);
    if (currentFile?.url) {
      cropModalRef.current?.open(currentFile.url, currentPictureId);
    } else if (currentFile?.response?.imgUrl) {
      cropModalRef.current?.open(currentFile.response.imgUrl, currentPictureId);
    } else if (currentFile?.response?.Location) {
      cropModalRef.current?.open(
        currentFile.response.Location,
        currentPictureId
      );
    }
  };

  // 处理编辑后的图片上传
  const handleCropConfirm = async (blob: Blob) => {
    try {
      // 将 Blob 转换为 File
      const file = new File([blob], "edited-image.png", { type: "image/png" });

      // 创建 FormData
      const formData = new FormData();
      formData.append("image", file);

      // 调用上传 API
      const response = await getUploadPicture(
        {
          pictureUploadRequest: {
            id: searchParamsId ? searchParamsId : undefined,
            spaceId: spaceId ?? undefined,
          },
        },
        formData
      );

      // 根据后端返回的数据结构调整
      const pictureResponse = response?.data?.data as API.PictureVO;
      setPicData(pictureResponse as API.PictureVO);
      setPictureId(pictureResponse?.id ?? 0);

      // 更新文件列表
      setFileList([
        {
          uid: "-1",
          name: pictureResponse?.name ?? "",
          status: "done",
          url: pictureResponse?.url,
          response: { imgUrl: pictureResponse?.url ?? "" },
        },
      ]);

      message.success("图片编辑并上传成功");
    } catch {
      notify.error({
        message: "上传失败",
        description: "编辑后的图片上传失败，请重试",
        placement: "bottomRight",
      });
    }
  };

  // 检查是否有已上传的图片
  const hasUploadedImage = fileList.some(
    (file) =>
      file.status === "done" &&
      (file.url || file.response?.imgUrl || file.response?.Location)
  );

  // Tabs部分信息
  const itemTabs: TabsProps["items"] = [
    {
      key: "file",
      label: "文件上传",
      children: (
        <Form onFinish={onFinish} form={form}>
          <Form.Item name="picture" layout="vertical">
            <Upload
              action="https://660d2bd96ddfa2943b33731c.mockapi.io/api/upload"
              listType="picture-card"
              fileList={fileList}
              onPreview={handlePreview}
              onChange={handleChange}
              beforeUpload={beforeUpload}
              customRequest={customRequest}
            >
              {fileList.length >= 1 ? null : uploadButton}
            </Upload>
            {hasUploadedImage && (
              <div style={{ marginTop: 16, textAlign: "center" }}>
                <Button type="default" onClick={handleEditPicture}>
                  编辑图片
                </Button>
              </div>
            )}
          </Form.Item>
          <Form.Item name="title" label="名称" layout="vertical">
            <Input />
          </Form.Item>
          <Form.Item name="profile" label="简介" layout="vertical">
            <Input.TextArea />
          </Form.Item>
          <Form.Item name="category" label="分类" layout="vertical">
            <AutoComplete
              options={categoryList.map((category) => ({
                label: category,
                value: category,
              }))}
            />
          </Form.Item>
          <Form.Item name="tags" label="标签" layout="vertical">
            <Select
              options={tagList.map((tag) => ({ label: tag, value: tag }))}
              mode="tags"
            />
          </Form.Item>
          <Form.Item className="flex flex-row justify-center items-center">
            <Space>
              <Button type="primary" htmlType="submit">
                提交
              </Button>
              <Button htmlType="button" onClick={onReset}>
                重置
              </Button>
            </Space>
          </Form.Item>
        </Form>
      ),
    },
  ];

  return (
    <div>
      {contextHolder}
      <div className="picture-upload">
        <div className="mb-4">
          保存图像到空间：
          <Link
            to={
              spaceType === 0
                ? `/personal_space/private_pictures?id=${spaceId}`
                : `/personal_space/team_pictures?id=${spaceId}`
            }
          >
            {spaceId}
          </Link>
        </div>
        <Tabs items={itemTabs} defaultActiveKey="file" />
      </div>
      <CropModal ref={cropModalRef} onConfirm={handleCropConfirm} />
    </div>
  );
}

export default App;
