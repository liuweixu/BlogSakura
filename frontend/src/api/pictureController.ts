// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 PUT /backend/picture/ */
export async function updatePicture(
  body: API.PictureUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/picture/", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /backend/picture/ */
export async function addPicture(
  body: API.PictureVO,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/picture/", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/picture/ */
export async function deletePicture(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/picture/", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/picture/${param0} */
export async function getPictureVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPictureVOByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponsePictureVO>(`/backend/picture/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /backend/picture/deep */
export async function deleteDeepPicture(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/backend/picture/deep", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/picture/list */
export async function getPictureVoList(options?: { [key: string]: any }) {
  return request<API.BaseResponseListPictureVO>("/backend/picture/list", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/picture/list/page */
export async function getPictureListByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPictureListByPageParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePicture>("/backend/picture/list/page", {
    method: "GET",
    params: {
      ...params,
      pictureQueryRequest: undefined,
      ...params["pictureQueryRequest"],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /backend/picture/list/page/vo */
export async function getPictureVoListByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPictureVOListByPageParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePictureVO>(
    "/backend/picture/list/page/vo",
    {
      method: "GET",
      params: {
        ...params,
        pictureQueryRequest: undefined,
        ...params["pictureQueryRequest"],
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /backend/picture/tag_category */
export async function getPictureListTagCategory(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponsePictureTagCategory>(
    "/backend/picture/tag_category",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /backend/picture/upload */
export async function getUploadPicture(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUploadPictureParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePictureVO>("/backend/picture/upload", {
    method: "POST",
    params: {
      ...params,
      pictureUploadRequest: undefined,
      ...params["pictureUploadRequest"],
    },
    data: body,
    ...(options || {}),
  });
}
