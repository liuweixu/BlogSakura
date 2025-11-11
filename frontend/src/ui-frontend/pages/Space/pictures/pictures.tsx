import { useEffect, useState } from "react";
import {
  Button,
  Card,
  Flex,
  Form,
  Input,
  List,
  Pagination,
  Space,
  Tabs,
  Tag,
} from "antd";
import { getFrontendPictureVoListByPage } from "@/api/pictureFrontendController";
import { getPictureListTagCategory } from "@/api/pictureController";
import React from "react";
import { useNavigate } from "react-router-dom";

function App() {
  const [data, setData] = useState<API.PictureVO[]>([]);
  const [total, setTotal] = useState(0);
  const [form] = Form.useForm();
  const [category, setCategory] = useState<string[]>([]);
  const [tagsList, setTagsList] = useState<string[]>([]);
  const navigate = useNavigate();
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [searchParams, setSearchParams] = useState<API.PictureQueryRequest>({
    currentPage: 1,
    pageSize: 12,
    sortField: "id",
    sortOrder: "descend",
  });
  // 获取分页信息
  const getPictureVOList = async () => {
    const res = await getFrontendPictureVoListByPage(searchParams);
    if (res.data.code !== 0 || !res.data.data) {
      return;
    }
    setData(res.data.data.records ?? []);
    setTotal(res.data.data.totalRow ?? 0);
  };
  useEffect(() => {
    getPictureVOList();
  }, [searchParams.currentPage, searchParams.pageSize]);

  // 处理分页信息
  const onPageChange = (page: number, pageSize: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = pageSize;
    getPictureVOList();
  };

  // 分类和标签查询
  const getCategoryTagsList = async () => {
    const res = await getPictureListTagCategory();
    if (res.data.code !== 0 || !res.data.data) {
      return;
    }
    setCategory(res.data.data.categoryList ?? []);
    setTagsList(res.data.data.tagList ?? []);
  };

  useEffect(() => {
    getCategoryTagsList();
  }, []);

  // 得到分类列表
  const categoryItems = [];
  categoryItems.push({
    label: "全部",
    key: "all",
  });
  categoryItems.push(
    ...category.map((item) => {
      return {
        label: item,
        key: item.toString(),
      };
    })
  );

  // 查询
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSearch = (values: any) => {
    searchParams.currentPage = 1;
    searchParams.name = values.keywords;
    searchParams.introduction = values.keywords;
    getPictureVOList();
  };

  // 查询分类
  const handleCategoryChange = (key: string) => {
    if (key !== "all") {
      searchParams.category = key;
    } else {
      searchParams.category = "";
    }
    getPictureVOList();
  };

  const [selectedTags, setSelectedTags] = React.useState<string[]>([]);
  const handleChange = (tag: string, checked: boolean) => {
    const nextSelectedTags = checked
      ? [...selectedTags, tag]
      : selectedTags.filter((t) => t !== tag);
    setSelectedTags(nextSelectedTags);
    searchParams.tags = nextSelectedTags;
    getPictureVOList();
  };

  return (
    <div className="mt-23 mx-4">
      <Form form={form} onFinish={handleSearch}>
        <Form.Item
          name={"keywords"}
          className="flex gap-10 items-center justify-center"
        >
          <Space className="w-full justify-center">
            <Input placeholder="请输入关键词" style={{ width: 300 }} />
            <Button type="primary" htmlType="submit" onClick={handleSearch}>
              查询
            </Button>
          </Space>
        </Form.Item>
        <Form.Item name="categorytags">
          <Tabs
            defaultActiveKey={searchParams.category}
            items={categoryItems}
            onChange={handleCategoryChange}
          />
          <Flex gap={4} wrap align="center">
            <span>标签：</span>
            {tagsList.map<React.ReactNode>((tag) => (
              <Tag.CheckableTag
                key={tag}
                checked={selectedTags.includes(tag)}
                onChange={(checked) => handleChange(tag, checked)}
              >
                {tag}
              </Tag.CheckableTag>
            ))}
          </Flex>
        </Form.Item>
        <Form.Item name="pictures">
          <List
            grid={{
              gutter: 16,
              xs: 1,
              sm: 2,
              md: 4,
              lg: 4,
              xl: 6,
              xxl: 6,
            }}
            dataSource={data}
            renderItem={(item) => (
              <List.Item>
                <Card
                  cover={
                    <div className="w-full h-40 overflow-hidden flex items-center justify-center">
                      <img
                        src={item.url}
                        alt={item.name}
                        title={item.name}
                        className="w-full h-full object-cover object-center"
                      />
                    </div>
                  }
                  onClick={() => {
                    navigate(`/picture/${item.id}`);
                  }}
                >
                  <Card.Meta
                    title={item.name}
                    description={
                      <div className="flex flex-nowrap overflow-hidden text-ellipsis">
                        <Tag color="#2db7f5">{item.category || "默认"}</Tag>
                        {item.tags?.map((tag) => {
                          return <Tag key={tag}>{tag}</Tag>;
                        })}
                      </div>
                    }
                  />
                </Card>
              </List.Item>
            )}
          />
        </Form.Item>
      </Form>
      <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
        <Pagination
          current={searchParams.currentPage}
          pageSize={searchParams.pageSize}
          total={total}
          onChange={onPageChange}
          showQuickJumper
          defaultCurrent={searchParams.currentPage}
        />
      </div>
    </div>
  );
}

export default App;
