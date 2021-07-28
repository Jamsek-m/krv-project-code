import { Component, OnDestroy, OnInit } from "@angular/core";
import { KeysService } from "../../../../services/keys.service";
import { Observable, Subject } from "rxjs";
import { JsonWebKey, KeyType, PublicSigningKey, WellKnownConfig } from "../../../../models";
import { startWith, switchMap, take, takeUntil } from "rxjs/operators";
import { ProviderContext } from "../../../../context/provider.context";
import { FormBuilder, FormGroup } from "@angular/forms";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-keys-list-page",
    templateUrl: "./keys-list-page.component.html",
    styleUrls: ["./keys-list-page.component.scss"]
})
export class KeysListPageComponent implements OnInit, OnDestroy {

    public wellKnown$: Observable<WellKnownConfig>;
    private reloadJwks$: Subject<void> = new Subject<void>();
    public jwks$: Observable<PublicSigningKey[]>;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    public keyForm: FormGroup;
    public keyTypes = KeyType;

    public showFullId = false;

    constructor(private keysService: KeysService,
                private provider: ProviderContext,
                private toastr: ToastrService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.jwks$ = this.reloadJwks$.pipe(
            startWith(null),
            switchMap(() => {
                return this.keysService.getKeys();
            }),
            takeUntil(this.destroy$)
        )
        this.wellKnown$ = this.provider.getWellKnownConfig().pipe(
            takeUntil(this.destroy$)
        );
        this.keyForm = this.fb.group({
            algorithm: this.fb.control("")
        });
    }

    public createNewKey() {
        const {algorithm} = this.keyForm.getRawValue();
        this.keysService.createKey(algorithm).pipe(
            take(1)
        ).subscribe((createdKey: JsonWebKey) => {
            this.toastr.success("Key was created", "Created!");
            this.reloadJwks$.next();
        })
    }

    ngOnDestroy() {
        this.destroy$.next(true);
    }

    public getVerificationKey(keyId: string): void {
        this.keysService.getPlainKey(keyId).subscribe(plain => {
            console.log("PLAIN: ", plain);
        });
    }

    public toggleShowFullId() {
        this.showFullId = !this.showFullId;
    }

    public getKeyIdentifier(index: number, key: PublicSigningKey): string {
        return key.id;
    }

    public shortUUID(uuid: string): string {
        return `${uuid.slice(-3)}...${uuid.slice(0, 3)}`;
    }

}
