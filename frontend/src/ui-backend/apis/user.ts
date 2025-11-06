//用户所有请求
import { request } from "@/ui-backend/utils";
import type { UserQueryRequest } from "@/ui-backend/pages/UserManager/user";

//登录请求
export function loginAPI(formData: {
  userAccount: string;
  userPassword: string;
}) {
  return request({
    url: "/api/backend/user/login",
    method: "POST",
    data: formData,
  });
}

// 判断是否登录
export function isLoginAPI() {
  return request({
    url: "/api/backend/user/session/login",
    method: "GET",
  })
}

//退出请求
export function logoutAPI() {
  return request({
    url: "/api/backend/user/logout",
    method: "GET",
  })
}


// 获取用户分页
export function getUserListAPI(formData: UserQueryRequest) {
  return request({
    url: "/api/backend/user/list/page/vo",
    method: "POST",
    data: formData
  })
}


// 删除用户
export function deleteUserByIdAPI(id: number) {
  return request({
    url: `/api/backend/user/${id}`,
    method: "DELETE"
  })
}

export function getUserInfoAPI() {
  return request({
    url: "/api/backend/user/session/login",
    method: "GET"
  })
}