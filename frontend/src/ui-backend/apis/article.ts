import { request } from "@/ui-backend/utils";
import type { ArticleVOBackend, ArticleVOBackendPage } from "../interface/Article";

//2. 添加文章
export function addArticleAPI(formData: ArticleVOBackend) {
  return request({
    url: "/api/backend/article/",
    method: "POST",
    data: formData
  });
}

//3. 获取文章分页列表
export function getArticleListPageAPI(searchParams: ArticleVOBackendPage) {
  return request({
    url: "/api/backend/article/list/page/vo",
    method: 'POST',
    data: searchParams
  });
}

//4. 后台删除文章
export function deleteArticleByIdAPI(id: number) {
  return request({
    url: `/api/backend/article/${id}`,
    method: "DELETE",
  });
}

//5. 获取id的文章
export function getArticleById(id: string) {
  return request({
    url: `/api/backend/article/${id}`,
    method: "GET",
  });
}

//6. 对id的文章删改（类似添加文章逻辑）
export function editArticleAPI(formData: ArticleVOBackend) {
  return request({
    url: "/api/backend/article/",
    method: "PUT",
    data: formData,
  });
}

