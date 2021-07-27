import { BaseType } from "./common.types";

export enum AttributeType {
    STRING = "STRING",
    JSON = "JSON",
    ARRAY = "ARRAY",
}

export interface UserAttribute extends BaseType {
    key: string;
    value: string;
    type: AttributeType;
    userId: string;
}

export interface User extends BaseType {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    attributes: UserAttribute[];
}
