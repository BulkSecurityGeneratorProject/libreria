/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { LibreriaTestModule } from '../../../test.module';
import { PrestamoDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/prestamo/prestamo-delete-dialog.component';
import { PrestamoService } from '../../../../../../main/webapp/app/entities/prestamo/prestamo.service';

describe('Component Tests', () => {

    describe('Prestamo Management Delete Component', () => {
        let comp: PrestamoDeleteDialogComponent;
        let fixture: ComponentFixture<PrestamoDeleteDialogComponent>;
        let service: PrestamoService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [LibreriaTestModule],
                declarations: [PrestamoDeleteDialogComponent],
                providers: [
                    PrestamoService
                ]
            })
            .overrideTemplate(PrestamoDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PrestamoDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PrestamoService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
