export interface LoggingVOBackendPage {
    currentPage: number;
    pageSize: number;
    operateTime?: Date;
    operateName?: string;
    costTime?: number;
    sortField?: string;
    sortOrder?: string;
}