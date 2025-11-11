import { useEffect, useState, useRef } from "react";
import { getFrontendPictureVoListByPage } from "@/api/pictureFrontendController";
import Masonry from "react-masonry-css";
import "./masonry.css";

function App() {
  const [data, setData] = useState<API.PictureVO[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [total, setTotal] = useState(0);
  const loadingRef = useRef(false);

  const pageSize = 12;

  // 初始加载
  useEffect(() => {
    const loadData = async () => {
      if (loadingRef.current) return;
      loadingRef.current = true;

      const params: API.PictureQueryRequest = {
        currentPage: 1,
        pageSize,
        sortField: "id",
        sortOrder: "descend",
      };

      try {
        const res = await getFrontendPictureVoListByPage(params);
        if (res.data.code !== 0 || !res.data.data) {
          return;
        }
        const records = res.data.data.records ?? [];
        const totalRow = res.data.data.totalRow ?? 0;
        setTotal(totalRow);
        setData(records);
        setCurrentPage(1);
      } finally {
        loadingRef.current = false;
      }
    };

    loadData();
  }, []);

  // 加载更多数据
  useEffect(() => {
    if (currentPage <= 1) return;

    const loadMore = async () => {
      if (loadingRef.current) return;
      loadingRef.current = true;

      const params: API.PictureQueryRequest = {
        currentPage,
        pageSize,
        sortField: "id",
        sortOrder: "descend",
      };

      try {
        const res = await getFrontendPictureVoListByPage(params);
        if (res.data.code !== 0 || !res.data.data) {
          return;
        }
        const records = res.data.data.records ?? [];
        setData((prev) => [...prev, ...records]);
      } finally {
        loadingRef.current = false;
      }
    };

    loadMore();
  }, [currentPage, pageSize]);

  // 滚动监听
  useEffect(() => {
    const handleScroll = () => {
      // 距离底部100px时加载更多
      if (
        window.innerHeight + window.scrollY >=
        document.documentElement.scrollHeight - 100
      ) {
        const hasMore = data.length < total;
        if (hasMore && !loadingRef.current) {
          setCurrentPage((prev) => prev + 1);
        }
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [data.length, total]);

  const breakpointColumnsObj = {
    default: 3,
    1100: 2,
    700: 1,
    500: 1,
  };
  return (
    <div className="mt-23 mx-4">
      <Masonry
        breakpointCols={breakpointColumnsObj}
        className="flex myMasonryGrid mt-2 gap-2 w-full max-w-225 mx-auto"
        columnClassName="myMasonryGridColumn"
      >
        {data.map((item, index) => {
          return (
            <div key={index} className="mb-1.5">
              <img
                src={item.url ?? ""}
                alt={item.name ?? ""}
                style={{ width: "100%", display: "block" }}
              />
            </div>
          );
        })}
      </Masonry>
    </div>
  );
}

export default App;
