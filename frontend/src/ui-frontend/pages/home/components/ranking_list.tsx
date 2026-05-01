import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getFrontendRankingList } from "@/api/articleFrontendController";

export function RankingList() {
  const [data, setData] = useState<API.ArticleVO[]>([]);

  const getRankingData = async () => {
    try {
      const res = await getFrontendRankingList();
      setData(res.data.data ?? []);
    } catch (error) {
      console.error("获取排行榜失败:", error);
    }
  };

  useEffect(() => {
    getRankingData();
  }, []);

  return (
    <aside className="w-full rounded-lg border border-[#ececec] p-4 bg-white/80">
      <h3 className="text-[#666] font-bold mb-3">排行榜</h3>
      <div className="flex flex-col gap-2">
        {data.length === 0 && (
          <span className="text-sm text-[#999]">暂无排行数据</span>
        )}
        {data.map((item, index) => (
          <Link
            key={item.id}
            to={`/article/${item.id}`}
            className="flex items-center justify-between text-sm hover:text-[#fe9600] transition-colors"
          >
            <span className="mr-2 truncate">
              {index + 1}. {item.title}
            </span>
            <span className="text-[#999] shrink-0">热度 {item.view ?? 0}</span>
          </Link>
        ))}
      </div>
    </aside>
  );
}
