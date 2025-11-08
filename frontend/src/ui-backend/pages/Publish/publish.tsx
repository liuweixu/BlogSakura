/**
 * 发布文章
 * 需要有上传表单
 * 要有上传文章封面 目测用单图和无图即可，其中无图可以用随机图片
 * 必须有文章标题和类别
 * 使用富文本插件，由于react版本为19，与react-quill不兼容，使用react-quill-new
 * 参考链接
 * https://www.npmjs.com/package/react-quill-new
 */

"use client";

// import RichTextEditor from "@/ui-backend/components/Editor";
import { useEffect, useState } from "react";

import { useSearchParams } from "react-router-dom";

import {
  Button,
  Form,
  Input,
  Select,
  Space,
  Breadcrumb,
  Radio,
  Card,
  type UploadFile,
} from "antd";
import ReactQuill from "react-quill-new";
import "./index.css";
import { Upload, notification } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { getChannelVOlist } from "@/api/channelController";
import {
  addArticle,
  getArticleVoById,
  getUploadFile,
  updateArticle,
} from "@/api/articleController";

const { Option } = Select;

const layout = {
  labelCol: { span: 1 },
  wrapperCol: { span: 24 },
};

const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};

export function PublishArticle() {
  const [form] = Form.useForm();
  const [fileValue, setFileValue] = useState<UploadFile[]>([]);
  const [imageType, setImageType] = useState<number>(0);
  const [location, setLocation] = useState<string>("");

  const [channelList, setChannelList] = useState<API.ChannelVO[]>([]);

  const getChannelList = async () => {
    const res = await getChannelVOlist();
    const data = res?.data?.data ?? [];
    setChannelList(data);
  };

  useEffect(() => {
    getChannelList();
  }, []);

  /**
   *
   * 提交表格
   *
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any

  //提交成功提示
  const [notify, contextHolder] = notification.useNotification();
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [formData, setFormData] = useState<API.ArticleVO>({});
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onFinish = (values: any) => {
    const { title, channel, content } = values;
    formData.title = title;
    formData.content = content;
    formData.channel = channel;
    formData.imageType = imageType;
    formData.imageUrl = location;
    if (articleId) {
      formData.id = articleId;
      updateArticle(formData);
    } else {
      addArticle(formData);
    }
    notify.success({
      message: "提交成功",
      description: "文章已保存",
      placement: "bottomRight",
    });
  };

  const onReset = () => {
    form.resetFields();
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onUploadChange = (value: any) => {
    setFileValue(value.fileList);
    // 处理上传状态变化
    if (value.file.status === "done") {
      notify.success({
        message: "上传成功",
        description: "图片已成功上传",
        placement: "bottomRight",
      });
    } else if (value.file.status === "error") {
      notify.error({
        message: "上传失败",
        description: "图片上传失败，请重试",
        placement: "bottomRight",
      });
    }
  };

  // 自定义上传请求
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const customRequest = async (options: any) => {
    const { file, onSuccess, onError } = options;

    try {
      // 调用上传 API
      const formData = new FormData();
      formData.append("image", file);
      const response = await getUploadFile(formData);

      // 根据后端返回的数据结构调整
      // 假设后端返回格式为 { data: { Location: "xxx" } }
      const href = response?.data?.data ?? "";
      setLocation(href);

      // 更新文件列表，添加响应信息
      setFileValue((prevList: UploadFile[]) => {
        return prevList.map((item) => {
          if (item.uid === file.uid) {
            return {
              ...item,
              status: "done",
              response: { Location: location },
            };
          }
          return item;
        });
      });

      // 调用成功回调
      onSuccess?.(response.data, file);
    } catch (error) {
      // 更新文件列表，标记为错误
      setFileValue((prevList: UploadFile[]) => {
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
   *
   * 单图与无图的判断和相应处理方式
   * maxCount是控制图像的添加此数
   * 单图时候，利用条件表达式实现
   * 无图的时候，利用后端处理，添加随机图像就行，或者添加404图
   */

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onImageTypeChange = (e: any) => {
    setImageType(e.target.value);
  };

  //实现编辑文章的逻辑
  //TODO 查询id是否存在-》即是否需要编辑文章
  //回填数据
  //需要使用form.setFieldValue()来实现回填
  //TODO 回调封面类型时（单图 无图）必须用number，如果用string就有可能识别不了
  const [searchParams] = useSearchParams();
  const articleId = searchParams.get("id");
  //获取实例
  useEffect(() => {
    /** TODO 关键修改点：
      1. 添加了channelList加载完成的检查
      2. 确保channel_name在channelList中存在
      3. 添加了channelList作为依赖项
      4. 添加了数据有效性检查
      这样修改后，当刷新页面时，会等待channelList加载完成后再尝试回填数据。
    **/
    async function getArticleDetail() {
      if (articleId && channelList.length > 0) {
        // 确保channelList已加载
        const res = await getArticleVoById({ id: Number(articleId) });
        if (res.data.data) {
          form.setFieldsValue({
            title: res.data.data.title,
            content: res.data.data.content,
            channel: res.data.data.channel,
            imageType: res.data.data.imageType,
          });
          const imageTypeValue = res.data.data.imageType ?? 0;
          setImageType(imageTypeValue);
          if (res.data.data.imageUrl) {
            // 构建回显 fileList
            const fileList: UploadFile[] = [
              {
                uid: "-1",
                name: res.data.data.imageUrl.split("/").pop() || "image.jpg",
                status: "done",
                url: `${res.data.data.imageUrl}`,
                response: { Location: res.data.data.imageUrl }, // 保持和上传成功一致
              },
            ];
            setFileValue(fileList);
          }
          // console.log(res.data.data.imageUrl);
          form.setFieldValue("imageUrl", res.data.data.imageUrl);
        }
      }
    }
    getArticleDetail();
  }, [articleId, form, channelList]); // 添加channelList依赖

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
            title: `${articleId ? "编辑文章" : "发布文章"}`,
            href: "/backend/publish",
          },
        ]}
        style={{ marginBottom: "36px" }}
      />
      <Card
        style={{
          width: "100%",
          marginTop: 20,
          borderRadius: 12,
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
        }}
      >
        <Form
          {...layout}
          form={form}
          name="control-hooks"
          onFinish={onFinish}
          labelCol={{ span: 3 }}
          wrapperCol={{ span: 20 }}
        >
          <Form.Item name="title" label="标题" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="channel" label="类别" rules={[{ required: true }]}>
            <Select allowClear placeholder="请选择文章类别">
              {channelList.map((channel) => (
                <Option key={channel.id} value={channel.channel}>
                  {channel.channel}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="content" label="内容" rules={[{ required: true }]}>
            <ReactQuill
              theme="snow"
              className="publish-quill"
              value={form.getFieldValue("content") || ""}
              onChange={(value) => form.setFieldValue("content", value)}
            />
          </Form.Item>
          <Form.Item label="封面">
            <Form.Item name="imageType">
              <Radio.Group onChange={onImageTypeChange}>
                <Radio value={1}>单图</Radio>
                <Radio value={0}>无图</Radio>
              </Radio.Group>
            </Form.Item>
            {/**
             * listType: 决定选择文件框的外观样式
             * showUploadList: 是否展示已上传文件列表
             */}
            {imageType > 0 && (
              <Upload
                name="image"
                listType="picture-card"
                accept="image/*"
                customRequest={customRequest}
                showUploadList
                maxCount={imageType}
                onChange={onUploadChange}
                fileList={fileValue}
              >
                <div>
                  <PlusOutlined />
                </div>
              </Upload>
            )}
          </Form.Item>
          <Form.Item {...tailLayout}>
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
      </Card>
    </div>
  );
}
