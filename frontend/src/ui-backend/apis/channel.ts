import { request } from "@/ui-backend/utils";
import type { ChannelVOBackendPage } from "../interface/Channel";

//1. 获取频道列表
export function getChannelListPageAPI(searchParams: ChannelVOBackendPage) {
  //下面是创建请求配置
  return request({
    url: "/api/backend/channel/list/page/vo",
    method: "POST",
    data: searchParams
  });
}

//7. 从id中在频道列表中获取相应的频道名字
export function getChannelById(
    id: string
  ) {
    return request({
      url: `/api/backend/channel/${id}`,
      method: "GET"
    });
}

export function getChannelListAPI() {
  return request({
    url: "/api/backend/channel/list",
    method: "GET"
  });
}
