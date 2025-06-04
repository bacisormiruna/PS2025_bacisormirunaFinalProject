import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GeneralLayoutComponent } from './component/general-layout/general-layout.component';
import { HomeComponent } from './component/pages/home/home.component';
import { LoginComponent } from './component/pages/login/login.component';
import { RegisterComponent } from './component/pages/register/register.component';
import { AuthGuard } from './auth.guard';
import {LogoutComponent} from "./component/pages/logout/logout.component";
import {CreateCoursesComponent} from "./create-courses/create-courses.component";
import {CoursesComponent} from "./courses/courses.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: GeneralLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'logout', component: LogoutComponent },
      { path: 'login', component: LoginComponent },
      { path: 'createCourse', component: CreateCoursesComponent },
      { path: 'courses', component: CoursesComponent },
      { path: 'home', component: HomeComponent }]
  },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
