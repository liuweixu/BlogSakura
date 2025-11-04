import type { ArticleItem } from "@/ui-backend/interface/Article";
import { getArticleHomeAPI } from "@/ui-frontend/apis/home";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./list_articles.css";
import { Pagination } from "antd";

export function ListWrapper() {
  //处理分页信息
  const [currentPage, setCurrentPage] = useState(1);
  const onPageChange = (page: number) => {
    setCurrentPage(page);
  };
  const [count, setCount] = useState(0);
  const pageSize = 10;

  // 获取文章列表
  const [data, setData] = useState<ArticleItem[]>([]);
  useEffect(() => {
    const getArticleList = async () => {
      try {
        const res = await getArticleHomeAPI();
        // 确保data是数组，否则使用空数组
        setCount(res.data.data.length);
        setData(
          res.data.data.slice(
            (currentPage - 1) * pageSize,
            currentPage * pageSize
          )
        ); // 注意这个，后台上因为添加拦截中，加上res.data，而这个是没加上，所以要多一个data
      } catch (error) {
        console.error("获取文章列表失败:", error);
      }
    };
    getArticleList();
  }, [currentPage]);

  const Class = [
    "blog-item post-list-show left",
    "blog-item post-list-show right",
  ];

  //对数据库的图像信息进行一定的处理
  const imageGet = (image_url: string) => {
    if (image_url == null) {
      return `https://api.r10086.com/樱道随机图片api接口.php?图片系列=风景系列${
        Math.floor(Math.random() * 10) + 1
      }`;
    } else {
      return image_url;
    }
  };

  function list_articles() {
    return (
      // /*tailwindcss */
      <div id="blog-list">
        {data.map((invoice, index) => {
          return (
            <div className={Class[index % Class.length]} key={invoice.id}>
              <div
                className={`${
                  index % 2 === 0
                    ? "float-right w-[55%] overflow-hidden"
                    : "float-left w-[55%] overflow-hidden"
                }`}
              >
                {/**对图片处理 */}
                <Link
                  to={"/article/" + invoice.id}
                  className="relative h-75 block bg-no-repeat bg-cover 
                    hover:scale-110 transition-all
                    duration-600 hover:rotate-3 rounded-r-lg rounded-l-none"
                >
                  <img
                    className="w-full h-full object-cover
                      pointer-events-none transition-all duration-600 blur-0"
                    src={imageGet(invoice.image_url)}
                    alt=""
                  />
                </Link>
              </div>
              <div
                className={`relative inline-block w-2/5 mt-7.5 mr-2.5 mb-2.5 ${
                  index % 2 === 0
                    ? "float-right pr-8 pl-0 text-left"
                    : "float-left pl-8 pr-0 text-right"
                }`}
              >
                <div className="text-[#888] text-sm">
                  <i className="iconfont icon-time mr-1.5 text-[#989898] text-sm" />
                  发布于 : {invoice.publish_date}
                </div>
                <Link to={"/article/" + invoice.id} className="block my-4.5">
                  <h3 className="line-clamp-2 overflow-hidden break-words font-bold text-[#504e4e] transition-colors duration-200 ease-out hover:text-[#fe9600]">
                    {invoice.title}
                  </h3>
                </Link>
                {/**text-xs  12px  */}
                <div className="text-[#888] text-xs">
                  <span>
                    <i className="iconfont icon-attention_light mr-1.5 text-[#989898] text-xs" />
                    热度 : {invoice.view}
                  </span>
                  {/* <span className="mx-2.5">
                    <i className="iconfont icon-icon_mark mr-1.5 text-[#989898] text-xs" />
                    评论
                  </span> */}
                  {invoice.channel_name && (
                    <span>
                      <i className="iconfont icon-icon_file mr-1.5 text-[#989898] text-xs" />
                      {invoice.channel_name}
                    </span>
                  )}
                </div>
                {/**显示文字内容 */}
                <div className="relative w-full my-2 z-50 text-black/66">
                  <p
                    className="overflow-hidden my-5 leading-6 line-clamp-3"
                    style={{ fontSize: "16px" }}
                  >
                    {invoice.content.replace(/<[^>]+>/g, "")}
                  </p>
                  <div>
                    <Link to={"/article/" + invoice.id}>
                      <i
                        className="iconfont icon-icon_caidan text-[#666] hover:text-[#fe9600]"
                        style={{ fontSize: "30px" }}
                      />
                    </Link>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  return (
    <div className="w-full">
      <div className="w-full h-auto mt-14 inline-block">
        <h1 className="text-[#666] font-bold mt-2.5 mb-7.5 border-b border-dashed border-[#ececec]">
          <i className="iconfont icon-envira" />
          <span> Discovery</span>
        </h1>
      </div>
      {list_articles()}
      <div style={{ display: "flex", justifyContent: "center", marginTop: 16 }}>
        <Pagination
          simple
          current={currentPage}
          pageSize={pageSize}
          total={count}
          onChange={onPageChange}
        />
      </div>
    </div>
  );
}
