import { useEffect, useState } from "react";
import { Button, Card, Form, List, Pagination, Progress, Tag } from "antd";
import { getFrontendPictureVoListByPage } from "@/api/pictureFrontendController";
import { getPictureListTagCategory } from "@/api/pictureController";
import { useNavigate, useSearchParams } from "react-router-dom";
import { PlusOutlined } from "@ant-design/icons";
import { getSpaceVoById } from "@/api/spaceController";

function App() {
  const [data, setData] = useState<API.PictureVO[]>([]);
  const [total, setTotal] = useState(0);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [searchId] = useSearchParams();
  const spaceId = searchId.get("id");
  const [progress, setProgress] = useState(0);
  const [spaceInfo, setSpaceInfo] = useState<API.SpaceVO>({});

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

  // 计算进度条
  const calculateProgress = async () => {
    const res = await getSpaceVoById({ id: spaceId });
    if (res.data.code === 0 && res.data.data) {
      setSpaceInfo(res.data.data);
      const usedSize = res.data.data?.totalSize ?? 0;
      const totalSize = res.data.data?.maxSize ?? 0;
      const result = (usedSize / totalSize) * 100;
      setProgress(Number(result.toFixed(2)));
    }
  };

  useEffect(() => {
    calculateProgress();
  }, []);

  return (
    <div className="mx-4">
      {spaceId && (
        <div className="flex justify-end mb-4 gap-4 items-center">
          <span>
            <Button
              type="primary"
              onClick={() => navigate("/personal_space/private_pictures/add")}
              icon={<PlusOutlined />}
            >
              创建图像
            </Button>
          </span>
          <span>
            <Progress type="dashboard" percent={progress} size={40} />
          </span>
        </div>
      )}
      <div>
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
                      navigate(`/picture/${item.id}?spaceId=${item.spaceId}`);
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
        <div
          style={{ display: "flex", justifyContent: "center", marginTop: 16 }}
        >
          <Pagination
            current={searchParams.currentPage}
            pageSize={searchParams.pageSize}
            total={total}
            onChange={onPageChange}
            showQuickJumper
            defaultCurrent={searchParams.currentPage}
            showTotal={
              spaceId
                ? (total) =>
                    `图片总数 ${total} 张 / 总空间 ${spaceInfo.maxCount} 张`
                : undefined
            }
          />
        </div>
      </div>
    </div>
  );
}

export default App;
