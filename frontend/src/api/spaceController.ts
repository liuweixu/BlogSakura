// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 GET /backend/space/ */
export async function getSpaceVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSpaceVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSpaceVO>("/backend/space/", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/space/ */
export async function addSpace(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/space/", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/space/ */
export async function deleteSpace(
  body: API.SpaceDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/space/", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/space/list_user */
export async function getSpaceVoListByUserId(
  body: API.SpaceByUserIdRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListSpaceVO>("/backend/space/list_user", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/space/list/level */
export async function getSpaceListLevel(options?: { [key: string]: any }) {
  return request<API.BaseResponseListSpaceLevel>("/backend/space/list/level", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/space/list/page */
export async function getSpaceVoListByPage(
  body: API.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSpaceVO>("/backend/space/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /backend/space/update */
export async function updateSpace(
  body: API.SpaceUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/space/update", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
