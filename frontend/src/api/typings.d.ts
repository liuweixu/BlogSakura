declare namespace API {
  type ArticleQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    title?: string;
    content?: string;
    channel?: string;
    imageType?: number;
    imageUrl?: string;
    publishDate?: string;
    editDate?: string;
    view?: number;
    like?: number;
  };

  type ArticleVO = {
    id?: number;
    title?: string;
    content?: string;
    imageType?: number;
    imageUrl?: string;
    publishDate?: string;
    editDate?: string;
    view?: number;
    like?: number;
    channel?: string;
  };

  type BaseResponseArticleVO = {
    code?: number;
    data?: ArticleVO;
    message?: string;
  };

  type BaseResponseBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseChannelVO = {
    code?: number;
    data?: ChannelVO;
    message?: string;
  };

  type BaseResponseListArticleVO = {
    code?: number;
    data?: ArticleVO[];
    message?: string;
  };

  type BaseResponseListChannelVO = {
    code?: number;
    data?: ChannelVO[];
    message?: string;
  };

  type BaseResponseListOperateLog = {
    code?: number;
    data?: OperateLog[];
    message?: string;
  };

  type BaseResponseListPictureVO = {
    code?: number;
    data?: PictureVO[];
    message?: string;
  };

  type BaseResponseListSpaceLevel = {
    code?: number;
    data?: SpaceLevel[];
    message?: string;
  };

  type BaseResponseLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseOperateLog = {
    code?: number;
    data?: OperateLog;
    message?: string;
  };

  type BaseResponsePageArticleVO = {
    code?: number;
    data?: PageArticleVO;
    message?: string;
  };

  type BaseResponsePageChannelVO = {
    code?: number;
    data?: PageChannelVO;
    message?: string;
  };

  type BaseResponsePageOperateLog = {
    code?: number;
    data?: PageOperateLog;
    message?: string;
  };

  type BaseResponsePagePicture = {
    code?: number;
    data?: PagePicture;
    message?: string;
  };

  type BaseResponsePagePictureVO = {
    code?: number;
    data?: PagePictureVO;
    message?: string;
  };

  type BaseResponsePageSpace = {
    code?: number;
    data?: PageSpace;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponsePictureTagCategory = {
    code?: number;
    data?: PictureTagCategory;
    message?: string;
  };

  type BaseResponsePictureVO = {
    code?: number;
    data?: PictureVO;
    message?: string;
  };

  type BaseResponseSpaceVO = {
    code?: number;
    data?: SpaceVO;
    message?: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type Channel = {
    id?: number;
    channel?: string;
    isDelete?: number;
    createTime?: string;
    updateTime?: string;
  };

  type ChannelQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    channel?: string;
    createTime?: string;
    updateTime?: string;
  };

  type ChannelVO = {
    id?: number;
    channel?: string;
    createTime?: string;
    updateTime?: string;
    articleNumbers?: number;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getArticleVOByIdParams = {
    id: number;
  };

  type getChannelVOByIdParams = {
    id: number;
  };

  type getFrontendArticleVOByIdParams = {
    id: number;
  };

  type getFrontendPictureVOByIdParams = {
    id: number;
  };

  type getOperateLogByIdParams = {
    id: number;
  };

  type getPictureVOByIdParams = {
    id: number;
  };

  type getSpaceVOByIdParams = {
    id: number;
  };

  type getUploadPictureParams = {
    pictureUploadRequest: PictureUploadRequest;
  };

  type getUserByIdParams = {
    id: number;
  };

  type getUserVOByIdParams = {
    id: number;
  };

  type LoginUserVO = {
    id?: number;
    userAccount?: string;
    userName?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
  };

  type OperateLog = {
    id?: number;
    operateTime?: string;
    operateName?: string;
    costTime?: number;
    isDelete?: number;
  };

  type OperateLogQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    operateTime?: string;
    operateName?: string;
    costTime?: number;
  };

  type PageArticleVO = {
    records?: ArticleVO[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PageChannelVO = {
    records?: ChannelVO[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PageOperateLog = {
    records?: OperateLog[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PagePicture = {
    records?: Picture[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PagePictureVO = {
    records?: PictureVO[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PageSpace = {
    records?: Space[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type PageUserVO = {
    records?: UserVO[];
    pageNumber?: number;
    pageSize?: number;
    totalPage?: number;
    totalRow?: number;
    optimizeCountQuery?: boolean;
  };

  type Picture = {
    id?: number;
    url?: string;
    name?: string;
    introduction?: string;
    category?: string;
    tags?: string;
    picSize?: number;
    picWidth?: number;
    picHeight?: number;
    picScale?: number;
    picFormat?: string;
    userId?: number;
    createTime?: string;
    editTime?: string;
    updateTime?: string;
    spaceId?: number;
    isDelete?: number;
  };

  type PictureQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    name?: string;
    introduction?: string;
    category?: string;
    tags?: string[];
    picSize?: number;
    picWidth?: number;
    picHeight?: number;
    picScale?: number;
    picFormat?: string;
    searchText?: string;
    userId?: number;
    spaceId?: number;
    nullSpaceId?: boolean;
  };

  type PictureTagCategory = {
    tagList?: string[];
    categoryList?: string[];
  };

  type PictureUpdateRequest = {
    id?: number;
    name?: string;
    introduction?: string;
    category?: string;
    tags?: string[];
    spaceId?: number;
  };

  type PictureUploadRequest = {
    id?: number;
    fileUrl?: string;
    spaceId?: number;
  };

  type PictureVO = {
    id?: number;
    url?: string;
    name?: string;
    introduction?: string;
    tags?: string[];
    category?: string;
    picSize?: number;
    picWidth?: number;
    picHeight?: number;
    picScale?: number;
    picFormat?: string;
    userId?: number;
    createTime?: string;
    editTime?: string;
    updateTime?: string;
    spaceId?: number;
    user?: UserVO;
  };

  type Space = {
    id?: number;
    spaceName?: string;
    spaceLevel?: number;
    maxSize?: number;
    maxCount?: number;
    totalSize?: number;
    totalCount?: number;
    userId?: number;
    createTime?: string;
    editTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type SpaceAddRequest = {
    spaceName?: string;
    spaceLevel?: number;
  };

  type SpaceDeleteRequest = {
    id?: number;
  };

  type SpaceLevel = {
    value?: number;
    text?: string;
    maxCount?: number;
    maxSize?: number;
  };

  type SpaceQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    userId?: number;
    spaceName?: string;
    spaceLevel?: number;
  };

  type SpaceUpdateRequest = {
    id?: number;
    spaceName?: string;
    spaceLevel?: number;
    maxSize?: number;
    maxCount?: number;
  };

  type SpaceVO = {
    id?: number;
    spaceName?: string;
    spaceLevel?: number;
    maxSize?: number;
    maxCount?: number;
    totalSize?: number;
    totalCount?: number;
    userId?: number;
    createTime?: string;
    editTime?: string;
    updateTime?: string;
    user?: UserVO;
  };

  type updateViewsParams = {
    id: number;
  };

  type User = {
    id?: number;
    userAccount?: string;
    userPassword?: string;
    userName?: string;
    userRole?: string;
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
  };

  type UserAddRequest = {
    userName?: string;
    userAccount?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    currentPage?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    userName?: string;
    userAccount?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    userAccount?: string;
    userPassword?: string;
    checkPassword?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userName?: string;
    userRole?: string;
  };

  type UserVO = {
    id?: number;
    userAccount?: string;
    userName?: string;
    userRole?: string;
    createTime?: string;
  };
}
