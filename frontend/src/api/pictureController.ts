// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 GET /picture/getInfo/${param0} */
export async function getInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfoParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.Picture>(`/picture/getInfo/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /picture/list */
export async function list(options?: { [key: string]: any }) {
  return request<API.Picture[]>("/picture/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /picture/page */
export async function page(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pageParams,
  options?: { [key: string]: any }
) {
  return request<API.PagePicture>("/picture/page", {
    method: "GET",
    params: {
      ...params,
      page: undefined,
      ...params["page"],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /picture/remove/${param0} */
export async function remove(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<boolean>(`/picture/remove/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /picture/save */
export async function save(
  body: API.Picture,
  options?: { [key: string]: any }
) {
  return request<boolean>("/picture/save", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /picture/update */
export async function update(
  body: API.Picture,
  options?: { [key: string]: any }
) {
  return request<boolean>("/picture/update", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
