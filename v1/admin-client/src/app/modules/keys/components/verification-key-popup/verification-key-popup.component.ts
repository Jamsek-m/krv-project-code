import { Component, Input, OnInit } from "@angular/core";
import { BsModalRef } from "ngx-bootstrap/modal";
import { KeyType } from "@lib";

@Component({
    selector: "app-verification-key-popup",
    templateUrl: "./verification-key-popup.component.html",
    styleUrls: ["./verification-key-popup.component.scss"]
})
export class VerificationKeyPopupComponent implements OnInit {

    public keyTypes = KeyType;

    @Input()
    public key: string;

    @Input()
    public keyType: KeyType;

    constructor(public modalRef: BsModalRef) {
    }

    ngOnInit(): void {
    }

}
