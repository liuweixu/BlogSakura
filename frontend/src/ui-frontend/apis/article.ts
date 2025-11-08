import { request } from "@/ui-frontend/utils";

//1. 按照id获取文章内容
export function getArticleById(id: string) {
  return request({
    url: `/api/article/${id}`,
    method: "GET",
  });
}

// //2. 按照id获取文章现有阅读数
// export function getArticleViewsById(id: string) {
//   return request({
//     url: `/api/article/views/${id}`,
//     method: "GET"
//   });
// }

//3. 更新文章id的阅读数
export function updateArticleViewsById(id: string) {
  return request({
    url: `/api/article/views/${id}`,
    method: "GET"
  })
}

//4. 获取文章的ES全文查询结果
export function searchArticleDoc(keyword: string) {
  return request({
    url: '/api/search',
    method: 'POST',
    data: {keyword: keyword}
  })
}