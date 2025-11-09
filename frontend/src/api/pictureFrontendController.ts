// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 PUT /picture/ */
export async function updateFrontendPicture(
  body: API.PictureUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/picture/", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /picture/${param0} */
export async function getFrontendPictureVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFrontendPictureVOByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponsePictureVO>(`/picture/${param0}`, {
    method: "GET",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /picture/list/page/vo */
export async function getFrontendPictureVoListByPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFrontendPictureVOListByPageParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePictureVO>("/picture/list/page/vo", {
    method: "GET",
    params: {
      ...params,
      pictureQueryRequest: undefined,
      ...params["pictureQueryRequest"],
    },
    ...(options || {}),
  });
}
