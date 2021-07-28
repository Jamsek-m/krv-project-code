import { BaseType } from "./common.types";

export enum SettingsValueType {
    BOOLEAN = "BOOLEAN",
    STRING = "STRING",
    INTEGER = "INTEGER",
    NUMBER = "NUMBER",
    FLOAT = "FLOAT",
    JSON = "JSON",
}

export interface Settings extends BaseType {
    key: string;
    value: string;
    type: SettingsValueType;
}

export interface SettingsResponse {
    [key: string]: Settings;
}
