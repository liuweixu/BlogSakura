import { useEffect, useState } from "react";
import { PlusOutlined } from "@ant-design/icons";
import {
  AutoComplete,
  Breadcrumb,
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
  getUploadPictureByUrl,
  updatePicture,
} from "@/api/pictureController";
import "./picture.css";
import { useNavigate, useSearchParams } from "react-router-dom";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

const getBase64 = (file: FileType): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });

export function PictureManagement() {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [form] = Form.useForm();
  //提交成功提示
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [notify, contextHolder] = notification.useNotification();
  const [picData, setPicData] = useState<API.PictureVO>({});
  const [pictureId, setPictureId] = useState<number | undefined>(undefined);
  const [searchParams] = useSearchParams();
  const searchParamsId = searchParams.get("id");
  const [title, setTitle] = useState<string>("");
  const navigate = useNavigate();

  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as FileType);
    }
  };

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
            id: searchParamsId ?? "",
          },
        },
        formData
      );

      // 根据后端返回的数据结构调整
      const pictureResponse = response?.data?.data as API.PictureVO;
      setPicData(pictureResponse as API.PictureVO);
      setPictureId(pictureResponse?.id ?? 0);
      setTitle(pictureResponse?.name ?? "");
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

  // Url上传图像
  const handleUrlSearch = async (value: string) => {
    const res = await getUploadPictureByUrl({ fileUrl: value });
    if (res.data.code === 0 && res.data.data) {
      setPicData(res.data.data as API.PictureVO);
      setPictureId(res.data.data?.id ?? 0);
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
    pictureUpdateRequest.id = pictureId ? pictureId : searchParamsId;
    console.log("pictureUpdateRequest", pictureUpdateRequest);
    const res = await updatePicture(pictureUpdateRequest);
    if (res?.data?.code === 0 && res?.data?.data) {
      message.success("图片更新成功");
      navigate(`/backend/picture?id=${pictureUpdateRequest.id}`);
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
    const id = searchParamsId ?? "";
    if (id) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
  }, []);

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
    {
      key: "url",
      label: "URL上传",
      children: (
        <Form onFinish={onFinish} form={form}>
          <Form.Item>
            <Input.Search
              placeholder="input search text"
              onSearch={handleUrlSearch}
              enterButton
            />
          </Form.Item>
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
      <Breadcrumb
        separator=">"
        items={[
          {
            title: "首页",
            href: "/backend/",
          },
          {
            title: `${searchParamsId ? "编辑图片" : "创建图片"}`,
            href: `${
              searchParamsId
                ? `/backend/picture?id=${searchParamsId}`
                : "/backend/picture"
            }`,
          },
        ]}
        style={{ marginBottom: "36px" }}
      />
      <div className="picture-upload">
        <Tabs items={itemTabs} defaultActiveKey="file" />
      </div>
    </div>
  );
}
