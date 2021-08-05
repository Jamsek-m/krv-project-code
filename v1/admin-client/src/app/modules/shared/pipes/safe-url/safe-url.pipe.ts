import { Pipe, PipeTransform, SecurityContext } from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";

@Pipe({
    name: "safeUrl"
})
export class SafeUrlPipe implements PipeTransform {

    constructor(private sanitizer: DomSanitizer) {
    }

    transform(value: string): string | null {
        return this.sanitizer.sanitize(SecurityContext.URL, value);
    }

}
