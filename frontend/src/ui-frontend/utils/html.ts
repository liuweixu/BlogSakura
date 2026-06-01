/** 将 HTML 转为纯文本，用于搜索摘要等场景 */
export function stripHtml(html: string): string {
  if (!html) return "";
  const text =
    new DOMParser().parseFromString(html, "text/html").body.textContent ?? "";
  return text.replace(/\s+/g, " ").trim();
}

export function truncateText(text: string, maxLength = 120): string {
  if (text.length <= maxLength) return text;
  return `${text.slice(0, maxLength)}...`;
}

/** 提取搜索结果摘要：去 HTML 并截断 */
export function toSearchSnippet(raw: unknown, maxLength = 120): string {
  if (raw == null) return "";
  const source = typeof raw === "string" ? raw : JSON.stringify(raw);
  return truncateText(stripHtml(source), maxLength);
}
