// 后端文章分页列表接口
export interface ArticleVOBackendPage{
  currentPage: number,
  pageSize: number,
  sortField?: string,
  sortOrder?: string,
  id?: number,
  title?: string,
  content?: string,
  channel?: string,
  imageType?: number,
  imageUrl?: string,
  publishDate?: Date,
  editDate?: Date,
  view?: number,
  like?: number
}

// 后端文章信息接口
export interface ArticleVOBackend {
  id?: string,
  title?: string,
  content?: string,
  channel?: string,
  imageType?: number,
  imageUrl?: string,
  publishDate?: Date,
  editDate?: Date,
  view?: number,
  like?: number
};


