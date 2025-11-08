import React, { useMemo, useState } from "react";
import { Input, List, Spin, Empty } from "antd";
import { useNavigate } from "react-router-dom";
import { searchArticleByTitleOrContent } from "@/api/esController";

const { Search } = Input;

// 仅在本文件内实现搜索框与结果展示
const SimpleSearch = () => {
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [results, setResults] = useState<any[]>([]);
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const hasResults = useMemo(() => results && results.length > 0, [results]);

  // 触发搜索（调用后端 ES 全文检索）
  const onSearch = async (value: string) => {
    const keyword = value?.trim();
    setQuery(value);

    if (!keyword) {
      setResults([]);
      setOpen(false);
      return;
    }

    setLoading(true);
    try {
      // 后端为 GET 映射且从 RequestBody 读取 keyword，这里通过 axios 的 data 传递
      const res = await searchArticleByTitleOrContent({ keyword });
      console.log("搜索结果：", res);
      const data = res?.data?.data ?? [];
      // 尽量容错：data 可能是数组或对象
      const list = Array.isArray(data) ? data : [];
      setResults(list);
      setOpen(true);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      setResults([]);
      setOpen(true);
    } finally {
      setLoading(false);
    }
  };

  const onChange: React.ChangeEventHandler<HTMLInputElement> = (e) => {
    const value = e.target.value;
    setQuery(value);
    if (!value) {
      setResults([]);
      setOpen(false);
    }
  };

  const onBlur: React.FocusEventHandler<HTMLInputElement> = () => {
    // 轻微延迟，避免点击下拉项时被立刻关闭
    setTimeout(() => setOpen(false), 150);
  };

  const onFocus: React.FocusEventHandler<HTMLInputElement> = () => {
    if (results.length > 0) setOpen(true);
  };

  return (
    <div className="h-19 flex items-center relative" style={{ minWidth: 300 }}>
      <Search
        placeholder="搜索内容"
        allowClear
        value={query}
        onChange={onChange}
        onSearch={onSearch}
        onBlur={onBlur}
        onFocus={onFocus}
        style={{ width: 300 }}
      />

      {open && (
        <div className="absolute right-0 top-[48px] w-[360px] bg-white shadow-xl rounded-md border border-gray-100 z-50">
          {loading ? (
            <div className="py-6 flex justify-center items-center">
              <Spin />
            </div>
          ) : hasResults ? (
            <List
              size="small"
              dataSource={results}
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              renderItem={(item: any) => {
                // 尝试适配可能的结构：优先 title，其次 content 片段，否则 JSON 串
                const title =
                  item?.title ?? item?.articleTitle ?? item?.name ?? "结果";
                const snippet =
                  item?.highlight ?? item?.content ?? item?.articleContent;
                return (
                  <List.Item
                    className="px-3 hover:bg-gray-50 cursor-pointer"
                    onClick={() => navigate(`/article/${item.id}`)}
                  >
                    <div className="w-full py-2">
                      <div
                        className="text-[14px] text-gray-900 font-medium truncate"
                        title={title}
                      >
                        {title}
                      </div>
                      {snippet && (
                        <div
                          className="text-[12px] text-gray-500 mt-0.5 line-clamp-2"
                          title={
                            typeof snippet === "string"
                              ? snippet
                              : JSON.stringify(snippet)
                          }
                        >
                          {typeof snippet === "string"
                            ? snippet
                            : JSON.stringify(snippet)}
                        </div>
                      )}
                    </div>
                  </List.Item>
                );
              }}
            />
          ) : (
            <div className="py-6">
              <Empty
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description="无搜索结果"
              />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default SimpleSearch;
