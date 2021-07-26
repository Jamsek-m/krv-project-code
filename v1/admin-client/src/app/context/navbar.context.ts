import { BehaviorSubject, Observable, Subject } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: "root"
})
export class NavbarContext {

    private opened$: Subject<boolean> = new BehaviorSubject<boolean>(false);

    public isOpened(): Observable<boolean> {
        return this.opened$.asObservable();
    }

    public toggle(open: boolean): void {
        this.opened$.next(open);
    }

}
