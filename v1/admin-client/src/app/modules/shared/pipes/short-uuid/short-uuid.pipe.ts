import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "shortUUID"
})
export class ShortUuidPipe implements PipeTransform {

    transform(uuid: string): string {
        return `${uuid.slice(-3)}...${uuid.slice(0, 3)}`;
    }

}
