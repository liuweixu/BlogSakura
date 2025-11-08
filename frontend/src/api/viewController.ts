// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 GET /article/views/${param0} */
export async function updateViews(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateViewsParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseLong>(`/article/views/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}
