// @ts-ignore
/* eslint-disable */
import request from "@/ui-backend/utils";

/** 此处后端没有提供注释 POST /search */
export async function searchArticleByTitleOrContent(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListArticleVO>("/search", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
