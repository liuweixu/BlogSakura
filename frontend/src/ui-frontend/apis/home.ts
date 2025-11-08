import type { ArticleVOBackendPage } from "@/ui-backend/interface/Article";
import { request } from "@/ui-frontend/utils";

//1. 获取文章分页列表
export function getArticleHomeAPI(searchParams: ArticleVOBackendPage) {
  return request({
    url: "/api/article/list/page/vo",
    method: "POST",
    data: searchParams
  });
}


export function getArticleFeaturesAPI() {
  return request({
    url: "/api/article/list/features",
    method: "GET"
  });
}