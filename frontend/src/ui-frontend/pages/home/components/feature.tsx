import { Col, Row } from "antd";
import type { ArticleItem } from "@/ui-backend/interface/Article";
import { getArticleHomeAPI } from "@/ui-frontend/apis/home";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
export function Feature() {
  const [data, setData] = useState<ArticleItem[]>([]);
  useEffect(() => {
    const getArticleList = async () => {
      try {
        const res = await getArticleHomeAPI();
        // 确保data是数组，否则使用空数组
        setData(res.data.data); // 注意这个，后台上因为添加拦截中，加上res.data，而这个是没加上，所以要多一个data
      } catch (error) {
        console.error("获取文章列表失败:", error);
      }
    };
    getArticleList();
  }, []);

  const features = data.slice(-3);

  function featureList() {
    return (
      <Row gutter={16}>
        {features.map((invoice) => (
          <Col
            id="feature"
            key={invoice.id}
            xs={24}
            sm={24}
            md={8}
            lg={8}
            xl={8}
          >
            <div
              id="feature-item"
              className="relative h-40 shadow-[1px_3px_3px_rgba(0,0,0,0.3)] overflow-hidden rounded-2xl"
            >
              <Link
                to={"/article/" + invoice.id}
                className="h-full block group"
              >
                <div
                  id="img-box"
                  className="transition-all duration-350 ease-in-out scale-100 h-full group-hover:scale-120"
                >
                  <img
                    src={
                      invoice.image_url ||
                      `https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列${
                        Math.floor(Math.random() * 10) + 1
                      }`
                    }
                    alt=""
                    className="w-full h-full"
                  />
                </div>
                <div
                  id="info"
                  className="absolute inset-0 text-center invisible backface-hidden bg-black/60 opacity-0 group-hover:visible group-hover:opacity-100 transition-all duration-350 ease-in-out"
                >
                  <h3 className="uppercase text-white text-center text-xl p-2.5 bg-[#111]/80 mt-10 transition-all duration-350 ease-in-out -translate-x-full group-hover:translate-x-0 text-ellipsis">
                    {invoice.title}
                  </h3>
                  <p className="italic text-xs relative text-gray-300 text-center transtion-all duration-300 ease-linear delay-100 translate-x-full mt-4 h-10 leading-5 group-hover:translate-x-0 ellipsis-two">
                    {invoice.content.replace(/<[^>]+>/g, "").slice(0, 10) +
                      "..."}
                  </p>
                </div>
              </Link>
            </div>
          </Col>
        ))}
      </Row>
    );
  }

  return (
    <div id="feature-wrapper" className="max-md:hidden">
      <div id="feature-title" className="w-full h-auto mt-14 inline-block">
        <h1 className="text-[#666] font-bold mt-2.5 leading-6 pb-1.5 mb-8 border-b border-dashed border-[#ececec]">
          <i className="iconfont icon-anchor" />
          <span> START:DASH!!</span>
        </h1>
      </div>
      {featureList()}
    </div>
  );
}
