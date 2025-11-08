// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 GET /article/${param0} */
export async function getFrontendArticleVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFrontendArticleVOByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseArticleVO>(`/article/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /article/list */
export async function getFrontendArticleVoList(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListArticleVO>("/article/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /article/list/features */
export async function getFrontendArticleVoListFeatures(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListArticleVO>("/article/list/features", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /article/list/page/vo */
export async function getFrontendArticleVoListByPage(
  body: API.ArticleQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageArticleVO>("/article/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
