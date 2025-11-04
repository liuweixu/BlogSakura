// 文章列表接口
export interface ArticleItem {
  id: string;
  title: string;
  content: string;
  channel_id: number;
  channel_name: string;
  image_url: string;
  image_type: string;
  publish_date: string;
  view: number;
}

// 文章内容接口
export interface ArticleContent {
  title: string;
  content: string;
  channel_name: string;
  image_url: string;
}
