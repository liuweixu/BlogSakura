import { request } from "@/ui-backend/utils";

//登录请求
export function uploadAPI(formData: FormData) {
    return request({
      url: "/api/backend/article/upload/image",
      method: "POST",
      data: formData
    });
  }