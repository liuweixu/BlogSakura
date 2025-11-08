// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 PUT /backend/logging/ */
export async function updateOperateLog(
  body: API.OperateLog,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/logging/", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/logging/ */
export async function addOperateLog(
  body: API.OperateLog,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/logging/", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/logging/${param0} */
export async function getOperateLogById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOperateLogByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseOperateLog>(`/backend/logging/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/logging/${param0} */
export async function removeOperateLogById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeOperateLogByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean>(`/backend/logging/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/logging/all */
export async function deleteOperateLogs(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>("/backend/logging/all", {
    method: "DELETE",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/logging/list */
export async function getOperateLogList(options?: { [key: string]: any }) {
  return request<API.BaseResponseListOperateLog>("/backend/logging/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/logging/list/page/vo */
export async function getOperateLogListByPage(
  body: API.OperateLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageOperateLog>(
    "/backend/logging/list/page/vo",
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
