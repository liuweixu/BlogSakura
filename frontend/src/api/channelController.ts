// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 PUT /backend/channel/ */
export async function updateChannel(
  body: API.Channel,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/channel/", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/channel/ */
export async function addChannel(
  body: API.Channel,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/channel/", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/channel/${param0} */
export async function getChannelVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getChannelVOByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseChannelVO>(`/backend/channel/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/channel/${param0} */
export async function removeChannelById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeChannelByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean>(`/backend/channel/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/channel/list */
export async function getChannelVOlist(options?: { [key: string]: any }) {
  return request<API.BaseResponseListChannelVO>("/backend/channel/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/channel/list/page/vo */
export async function getChannelVoListByPage(
  body: API.ChannelQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChannelVO>(
    "/backend/channel/list/page/vo",
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
