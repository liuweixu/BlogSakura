import { request } from "@/ui-backend/utils";
import type { LoggingVOBackendPage } from "../interface/Logging";

// 1. 获取日志分页信息
export function getLoggingPageAPI(searchParams: LoggingVOBackendPage){
    return request({
        url: "/api/backend/logging/list/page/vo",
        method: "GET",
        data: searchParams
    })
}

// 2. 全部删除日志记录
export function deleteLoggingAPI(){
    return request({
        url: "/api/backend/logging/all",
        method: "DELETE"
    })
}