import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';
import { Prestamo } from './prestamo.model';
import { PrestamoService } from './prestamo.service';

@Injectable()
export class PrestamoPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private prestamoService: PrestamoService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.prestamoService.find(id).subscribe((prestamo) => {
                    prestamo.fechaPrestado = this.datePipe
                        .transform(prestamo.fechaPrestado, 'yyyy-MM-ddTHH:mm:ss');
                    prestamo.fechasDevuelto = this.datePipe
                        .transform(prestamo.fechasDevuelto, 'yyyy-MM-ddTHH:mm:ss');
                    this.ngbModalRef = this.prestamoModalRef(component, prestamo);
                    resolve(this.ngbModalRef);
                });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.prestamoModalRef(component, new Prestamo());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    prestamoModalRef(component: Component, prestamo: Prestamo): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.prestamo = prestamo;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
