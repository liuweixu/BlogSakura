import type { ArticleVOBackend } from "@/ui-backend/interface/Article";
import {
  getArticleById,
  updateArticleViewsById,
} from "@/ui-frontend/apis/article";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { marked } from "marked";
import "github-markdown-css/github-markdown.css";
import "./mainwrapper_style.css";

function App() {
  const location = useLocation();
  const id = location.pathname.split("/")[2];
  const [data, setData] = useState<ArticleVOBackend>();
  // 计算文章阅读数
  const [view, SetView] = useState(0);

  const getArticleContent = async () => {
    try {
      const res = await getArticleById(id);
      setData(res.data.data);
      const res1 = await updateArticleViewsById(id);
      SetView(res1?.data.data);
    } catch (error) {
      console.error("获取文章列表失败:", error);
    }
  };

  useEffect(() => {
    getArticleContent();
  }, []);

  return (
    //ArticleWrapper
    <div>
      <div className="bg-white" />
      {/**ArticleTop */}
      <div className="relative overflow-hidden">
        <div className="bg-no-repeat bg-cover bg-center h-100 bg-origin-border">
          <img
            className="w-full h-full object-cover"
            src={
              data?.imageUrl ||
              `https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列${
                Math.floor(Math.random() * 10) + 1
              }`
            }
            alt=""
          />
        </div>
        <div className="max-w-[850px] p-[0_10px] m-auto text-left top-auto bottom-5 absolute left-0 right-0 text-white text-shadow-[2px_2px_10px_#000] z-[1]">
          <h1 className="text-4xl font-bold">{data?.title}</h1>
          {data && (
            <p className="text-[14px] p-[18px_0_0] leading-[39px]">
              <span>
                <img
                  className="w-[35px] h-[35px] rounded-full float-left mr-3"
                  src="/statics/images/list_01.png"
                ></img>
              </span>
              <span>频道：{data.channel}</span>
              <span className="mx-1.5">·</span>
              <span>阅读数：{view}</span>
              <span className="mx-1.5">·</span>
              <span>
                发布时间：{data.publishDate?.toString().split("T")[0] || ""}
              </span>
            </p>
          )}
        </div>
      </div>
      {/**MainWrapper 正文内容 */}
      <div className="min-h-[600px] max-w-[850px] p-[0_10px] ml-auto mr-auto pt-[50px] bg-white-80">
        {data ? (
          <div className="flex-items">
            <div className="cell">
              <div
                className="entry-content"
                id="content"
                dangerouslySetInnerHTML={{
                  __html: marked(data?.content || ""),
                }}
              />
            </div>
          </div>
        ) : (
          <div>Loading...</div>
        )}
      </div>
    </div>
  );
}

export default App;
