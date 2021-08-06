export type DifferenceFilter<T> = (value1: T, value2: T) => boolean;

export enum DataFetchStatus {
    LOADING = "LOADING",
    OK = "OK",
    ERROR = "ERROR",
}

export type TemplateData<E, S = DataFetchStatus> = {
    status: S;
    data: E;
    errorMessage?: string;
}
