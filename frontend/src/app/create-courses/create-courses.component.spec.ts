import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CreateCoursesComponent } from './create-courses.component';

describe('CreateCoursesComponent', () => {
  let component: CreateCoursesComponent;
  let fixture: ComponentFixture<CreateCoursesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateCoursesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCoursesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
