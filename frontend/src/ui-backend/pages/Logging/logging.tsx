import {
  deleteOperateLogs,
  getOperateLogListByPage,
} from "@/api/operateLogController";
import { Breadcrumb, Button, Card } from "antd";

import { Table } from "antd";
import { useEffect, useState } from "react";

export const LoggingPage = () => {
  // 获取日志记录信息
  const [logging, setLogging] = useState<API.OperateLog[]>([]);
  const [total, setTotal] = useState(0);
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [searchParams, setSearchParams] = useState<API.OperateLogQueryRequest>({
    currentPage: 1,
    pageSize: 10,
  });
  // 修改以适应 getOperateLogListByPage 需要的参数格式
  const getLoggingList = async () => {
    const resLogging = await getOperateLogListByPage(searchParams);
    const records = resLogging?.data?.data?.records ?? [];
    const totalRow = resLogging?.data?.data?.totalRow ?? 0;
    setLogging(records);
    setTotal(totalRow);
  };
  useEffect(() => {
    getLoggingList();
  }, []);

  const columns = [
    {
      title: "操作时间",
      dataIndex: "operateTime",
      width: 220,
    },
    {
      title: "操作名称",
      dataIndex: "operateName",
      width: 220,
    },
    {
      title: "消耗时间（单位：毫秒）",
      dataIndex: "costTime",
      width: 220,
    },
  ];

  const onClearLog = async () => {
    await deleteOperateLogs();
    searchParams.currentPage = 1;
    setLogging([]);
  };

  const handleTableChange = (page: number, pageSize: number) => {
    searchParams.currentPage = page;
    searchParams.pageSize = pageSize;
    getLoggingList();
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
            title: "日志记录",
            href: "/backend/logging",
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
        title={
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <span
              style={{
                fontSize: 18,
                fontWeight: 600,
                color: "#333",
                fontFamily: "'KaiTi', 'KaiTi_GB2312', 'STKaiti', serif",
              }}
            >
              日志列表
            </span>
            <div className="ml-auto">
              <Button danger onClick={onClearLog}>
                Clear
              </Button>
            </div>
          </div>
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={logging}
          pagination={{
            current: searchParams.currentPage,
            pageSize: searchParams.pageSize,
            total: total,
            onChange: handleTableChange,
            onShowSizeChange: handleTableChange,
          }}
        />
      </Card>
    </div>
  );
};
