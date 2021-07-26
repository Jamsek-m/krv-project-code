import { BaseType } from "./common.types";

export enum AttributeType {
    STRING = "STRING",
    JSON = "JSON",
    ARRAY = "ARRAY",
}

export class UserAttribute extends BaseType {
    public key: string;
    public value: string;
    public type: AttributeType;
    public userId: string;
}

export class User extends BaseType {
    public username: string;
    public email: string;
    public firstName: string;
    public lastName: string;
    public attributes: UserAttribute[];
}
