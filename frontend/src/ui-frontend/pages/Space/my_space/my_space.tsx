import { useEffect, useState } from "react";
import { Card, Form, List, Pagination, Tag } from "antd";
import { getFrontendPictureVoListByPage } from "@/api/pictureFrontendController";
import { getPictureListTagCategory } from "@/api/pictureController";
import { useNavigate, useSearchParams } from "react-router-dom";

function App() {
  const [data, setData] = useState<API.PictureVO[]>([]);
  const [total, setTotal] = useState(0);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [searchId] = useSearchParams();
  const spaceId = searchId.get("id");

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [searchParams, setSearchParams] = useState<API.PictureQueryRequest>({
    currentPage: 1,
    pageSize: 12,
    sortField: "id",
    sortOrder: "descend",
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    spaceId: spaceId ? spaceId : undefined,
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
  };

  useEffect(() => {
    getCategoryTagsList();
  }, []);

  return (
    <div className="mx-4">
      <Form form={form}>
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
