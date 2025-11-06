import { Breadcrumb, Card } from "antd";
import { useEffect, useState } from "react";
import { getChannelListPageAPI } from "@/ui-backend/apis/channel";
import type { ChannelVOBackendPage } from "@/ui-backend/interface/Channel";

import { Table } from "antd";

export const Channel = () => {
  //获取频道信息
  const [channelList, setChannelList] = useState<ChannelVOBackendPage[]>([]);
  const [searchParams, setSearchParams] = useState<ChannelVOBackendPage>({
    currentPage: 1,
    pageSize: 10,
  });
  const [total, setTotal] = useState(0);

  const getChannelList = async () => {
    const res_channel = await getChannelListPageAPI(searchParams);
    console.log(res_channel.data.data.records);
    setChannelList(res_channel.data.data.records);
    const total = res_channel.data.data.totalRow;
    setTotal(total);
  };
  useEffect(() => {
    getChannelList();
  }, []);
  // 列表头
  // 准备列数据
  // 定义状态枚举
  const columns = [
    {
      title: "标题",
      dataIndex: "channel",
      width: 220,
    },
    {
      title: "文章个数",
      dataIndex: "articleNumbers",
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      render: (text: Date) => {
        return new Date(text).toLocaleString();
      },
    },
    {
      title: "更新时间",
      dataIndex: "updateTime",
      render: (text: Date) => {
        return new Date(text).toLocaleString();
      },
    },
    // {
    //   title: "操作",
    //   // eslint-disable-next-line @typescript-eslint/no-explicit-any
    //   render: (data: any) => {
    //     return (
    //       <Space size="middle">
    //         <Button
    //           type="primary"
    //           shape="circle"
    //           icon={<EditOutlined />}
    //           onClick={() => navigate(`/backend/publish?id=${data.id}`)}
    //         />
    //         <Popconfirm
    //           title="删除频道"
    //           description="确认要删除当前频道吗?"
    //           onConfirm={() => onConfirm(data)}
    //           okText="Yes"
    //           cancelText="No"
    //         >
    //           <Button
    //             type="primary"
    //             danger
    //             shape="circle"
    //             icon={<DeleteOutlined />}
    //           />
    //         </Popconfirm>
    //       </Space>
    //     );
    //   },
    // },
  ];

  // 处理分页变化
  const handleTableChange = (page: number, size: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = size;
    getChannelList();
  };
  return (
    <div>
      <Breadcrumb
        separator=">"
        items={[
          {
            title: "首页",
            href: "/backend/",
          },
          {
            title: "频道列表",
            href: "/backend/channellist",
          },
        ]}
      />
      {/**表格区域 */}
      <Card
        style={{
          width: "100%",
          marginTop: 20,
          borderRadius: 12,
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          fontFamily: "'KaiTi', 'KaiTi_GB2312', 'STKaiti', serif",
        }}
        title={`共计 ${total} 个频道`}
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={channelList}
          pagination={{
            total: total,
            pageSize: searchParams.pageSize,
            current: searchParams.currentPage,
            onChange: handleTableChange,
            onShowSizeChange: handleTableChange,
          }}
        />
      </Card>
    </div>
  );
};
