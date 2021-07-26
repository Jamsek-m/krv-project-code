import { EntityList } from "@mjamsek/prog-utils";
import { HttpResponse } from "@angular/common/http";

export function mapToEntityList<T>(response: HttpResponse<T[]>) {
    if (response.body !== null) {
        let totalCount = response.headers.get("x-total-count");
        if (totalCount === null) {
            totalCount = response.body.length.toString(10);
        }
        return EntityList.of(response.body, parseInt(totalCount, 10));
    }
    throw new Error("");
}
