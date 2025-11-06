
// 频道展示接口
export interface ChannelVOBackend {
    id?: number,
    channel?: string,
    createTime?: Date,
    updateTime?: Date,
    articleNumber?: number
}

// 后端频道分页列表接口
export interface ChannelVOBackendPage {
    currentPage: number,
    pageSize: number,
    sortField?: string,
    sortOrder?: string,
    id?: number,
    channel?: string,
    createTime?: Date,
    updateTime?: Date,
    articleNumber?: number
}