// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 PUT /backend/article/ */
export async function updateArticle(
  body: API.ArticleVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/article/", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/article/ */
export async function addArticle(
  body: API.ArticleVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/article/", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/article/ */
export async function deleteArticle(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/article/", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/article/${param0} */
export async function getArticleVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getArticleVOByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseArticleVO>(`/backend/article/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/article/list */
export async function getArticleVoList(options?: { [key: string]: any }) {
  return request<API.BaseResponseListArticleVO>("/backend/article/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/article/list/page/vo */
export async function getArticleVoListByPage(
  body: API.ArticleQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticleVO>(
    "/backend/article/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /backend/article/upload/image */
export async function getUploadFile(
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString>("/backend/article/upload/image", {
    method: "POST",
    // headers: {
    //   "Content-Type": "application/json",
    // },
    data: body,
    ...(options || {}),
  });
}
